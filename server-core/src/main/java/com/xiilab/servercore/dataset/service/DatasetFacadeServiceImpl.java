package com.xiilab.servercore.dataset.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.FileType;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.dto.NginxFilesDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatasetFacadeServiceImpl implements DatasetFacadeService{
	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.dataset.dockerImage.name}")
	private String dockerImage;
	@Value("${astrago.dataset.dockerImage.hostPath}")
	private String hostPath;
	private final DatasetService datasetService;
	private final StorageService storageService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final WebClientService webClientService;

	@Override
	@Transactional
	public void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files) {
		//storage 조회
		StorageEntity storageEntity = storageService.findById(createDatasetDTO.getStorageId());

		//dataset 저장
		AstragoDatasetEntity astragoDataset = AstragoDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.storageEntity(storageEntity)
			.build();

		datasetService.insertAstragoDataset(astragoDataset, files);
	}

	@Override
	public DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId) {
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetService.getDatasetWithStorage(datasetId);
		Long id = datasetWithStorage.getDatasetId();
		List<WorkloadResDTO.UsingDatasetDTO> usingDatasetDTOS = workloadModuleFacadeService.workloadsUsingDataset(id);
		datasetWithStorage.addUsingDatasets(usingDatasetDTOS);
		return datasetWithStorage;
	}

	@Override
	@Transactional
	public void insertLocalDataset(DatasetDTO.CreateLocalDataset createDatasetDTO) {
		CreateLocalDatasetDTO createDto = CreateLocalDatasetDTO.builder()
			.namespace(namespace)
			.datasetName(createDatasetDTO.getDatasetName())
			.ip(createDatasetDTO.getIp())
			.storagePath(createDatasetDTO.getStoragePath())
			.dockerImage(dockerImage)
			.hostPath(hostPath)
			.build();
		//1. nginx deployment, pvc, pv, svc 생성
		CreateLocalDatasetResDTO createLocalDatasetResDTO = workloadModuleFacadeService.createLocalDataset(createDto);
		//2. 디비 인서트
		LocalDatasetEntity localDatasetEntity = LocalDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.ip(createDatasetDTO.getIp())
			.storageType(StorageType.NFS)
			.storagePath(createDatasetDTO.getStoragePath())
			.dns(createLocalDatasetResDTO.getDns())
			.deploymentName(createLocalDatasetResDTO.getDeploymentName())
			.pvcName(createLocalDatasetResDTO.getPvcName())
			.pvName(createLocalDatasetResDTO.getPvName())
			.svcName(createLocalDatasetResDTO.getSvcName())
			.build();
		datasetService.insertLocalDataset(localDatasetEntity);
	}

	@Override
	@Transactional
	public void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId, UserInfoDTO userInfoDTO) {
		//division 확인 후 astrago 데이터 셋이면 디비만 변경
		Dataset dataset = datasetService.findById(datasetId);

		if(checkAccessDataset(userInfoDTO, dataset)){
			//local 데이터 셋이면 디비 + deployment label 변경
			if(dataset.isLocalDataset()){
				LocalDatasetEntity localDatasetEntity = (LocalDatasetEntity)dataset;
				ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO = ModifyLocalDatasetDeploymentDTO
					.builder()
					.deploymentName(localDatasetEntity.getDeploymentName())
					.modifyDatasetName(modifyDataset.getDatasetName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.modifyLocalDatasetDeployment(modifyLocalDatasetDeploymentDTO);
			}
			datasetService.modifyDataset(modifyDataset, datasetId);
		}else{
			throw new RuntimeException("데이터 셋 수정 권한이 없습니다.");
		}
	}
	@Override
	@Transactional
	public void deleteDataset(Long datasetId, UserInfoDTO userInfoDTO) {
		Dataset dataset = datasetService.findById(datasetId);
		if(checkAccessDataset(userInfoDTO, dataset)){
			boolean isUse = workloadModuleFacadeService.isUsedDataset(datasetId);
			//true = 사용중인 데이터 셋
			if(isUse){
				throw new RuntimeException("사용중인 데이터 셋은 삭제할 수 없습니다.");
			}
			//astrago 데이터 셋은 db 삭제(astragodataset, workspacedatasetmapping
			if(dataset.isAstargoDataset()){
				//workspace mapping 삭제
				datasetService.deleteDatasetWorkspaceMappingById(datasetId);
				//dataset 삭제
				datasetService.deleteDatasetById(datasetId);
			}else if(dataset.isLocalDataset()){
				//pv, pvc, deployment, svc 삭제
				LocalDatasetEntity localDatasetEntity = (LocalDatasetEntity)dataset;
				DeleteLocalDatasetDTO deleteLocalDatasetDTO = DeleteLocalDatasetDTO.builder()
					.deploymentName(localDatasetEntity.getDeploymentName())
					.svcName(localDatasetEntity.getSvcName())
					.pvcName(localDatasetEntity.getPvcName())
					.pvName(localDatasetEntity.getPvName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.deleteLocalDataset(deleteLocalDatasetDTO);
				//workspace mapping 삭제
				datasetService.deleteDatasetWorkspaceMappingById(datasetId);
				//db 삭제 - TB_localDataset
				datasetService.deleteDatasetById(datasetId);
			}
		}else{
			throw new RuntimeException("데이터 셋 수정 권한이 없습니다.");
		}
	}

	@Override
	public DirectoryDTO getLocalDatasetFiles(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		//local dataset 조회
		List<DirectoryDTO.ChildrenDTO> fileList = new ArrayList<>();
		LocalDatasetEntity dataset = (LocalDatasetEntity) datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + reqFilePathDTO.getPath();
		List<NginxFilesDTO> files = webClientService.getObjectsFromUrl(httpUrl, NginxFilesDTO.class);

		for (NginxFilesDTO file : files) {
			DirectoryDTO.ChildrenDTO children = DirectoryDTO.ChildrenDTO
				.builder()
				.name(file.getName())
				.type(file.getFileType())
				.path(FileType.D == file.getFileType() ? reqFilePathDTO.getPath() + file.getName() + File.separator :
					reqFilePathDTO.getPath() + file.getName())
				.build();
			fileList.add(children);
		}
		return DirectoryDTO.builder().children(fileList).build();
	}

	@Override
	public DownloadFileResDTO DownloadLocalDatasetFile(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		LocalDatasetEntity dataset = (LocalDatasetEntity) datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + reqFilePathDTO.getPath();
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		MediaType contentType = fileInfo.getContentType();
		byte[] fileContent = webClientService.downloadFile(httpUrl, contentType);
		ByteArrayResource resource = new ByteArrayResource(fileContent);
		String fileName = webClientService.retrieveFileName(httpUrl);

		return DownloadFileResDTO.builder()
			.byteArrayResource(resource)
			.fileName(fileName)
			.mediaType(contentType)
			.build();
	}

	@Override
	public DatasetDTO.FileInfo getLocalDatasetFileInfo(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		LocalDatasetEntity dataset = (LocalDatasetEntity) datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + reqFilePathDTO.getPath();
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String size = webClientService.formatFileSize(fileInfo.getContentLength());
		String lastModifiedTime = webClientService.formatLastModifiedTime(fileInfo.getLastModified());
		String contentPath = reqFilePathDTO.getPath();

		return DatasetDTO.FileInfo.builder()
			.fileName(fileName)
			.size(size)
			.lastModifiedTime(lastModifiedTime)
			.contentPath(contentPath)
			.build();
	}

	@Override
	public DownloadFileResDTO getLocalDatasetFile(Long datasetId, String filePath) {
		LocalDatasetEntity dataset = (LocalDatasetEntity) datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		MediaType contentType = fileInfo.getContentType();
		byte[] fileContent = webClientService.downloadFile(httpUrl, contentType);
		ByteArrayResource resource = new ByteArrayResource(fileContent);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String type = fileInfo.getFirst(HttpHeaders.CONTENT_TYPE);
		if (type != null) {
			if (type.startsWith("image/") || type.startsWith("text/")) {
				return DownloadFileResDTO.builder()
					.byteArrayResource(resource)
					.fileName(fileName)
					.mediaType(contentType)
					.build();
			}else {
				throw new RuntimeException("미리보기를 지원하지 않는 포맷입니다.");
			}
		}else{
			throw new RuntimeException("미리보기를 지원하지 않는 포맷입니다.");
		}
	}

	private static boolean checkAccessDataset(UserInfoDTO userInfoDTO, Dataset dataset) {
		return userInfoDTO.getAuth() == AuthType.ROLE_ADMIN ||
			(userInfoDTO.getAuth() == AuthType.ROLE_USER && userInfoDTO.getId().equals(dataset.getRegUser().getRegUserId()));
	}

}
