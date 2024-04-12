package com.xiilab.servercore.dataset.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.dto.NginxFilesDTO;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DatasetFacadeServiceImpl implements DatasetFacadeService {
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
	private final NetworkRepository networkRepository;

	@Override
	@Transactional
	public void insertAstragoDataset(DatasetDTO.CreateAstragoDataset createDatasetDTO, List<MultipartFile> files) {
		//storage 조회
		StorageEntity storageEntity = storageService.findById(createDatasetDTO.getStorageId());

		//dataset 저장
		AstragoDatasetEntity astragoDataset = AstragoDatasetEntity.builder()
			.datasetName(createDatasetDTO.getDatasetName())
			.storageEntity(storageEntity)
			.defaultPath(createDatasetDTO.getDefaultPath())
			.build();

		datasetService.insertAstragoDataset(astragoDataset, files);
	}

	@Override
	public DatasetDTO.ResDatasetWithStorage getDataset(Long datasetId) {
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetService.getDatasetWithStorage(datasetId);
		return datasetWithStorage;
	}

	@Override
	@Transactional
	public void insertLocalDataset(DatasetDTO.CreateLocalDataset createDatasetDTO) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		log.info("폐쇄망 : " + network.getNetworkCloseYN());
		CreateLocalDatasetDTO createDto = CreateLocalDatasetDTO.builder()
			.namespace(namespace)
			.datasetName(createDatasetDTO.getDatasetName())
			.ip(createDatasetDTO.getIp())
			.storagePath(createDatasetDTO.getStoragePath())
			// .dockerImage(dockerImage)
			.dockerImage(network.getLocalVolumeURL())
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
			.defaultPath(createDatasetDTO.getDefaultPath())
			.build();
		localDatasetEntity.setDatasetSize(50l);
		datasetService.insertLocalDataset(localDatasetEntity);
	}

	@Override
	@Transactional
	public void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId, UserInfoDTO userInfoDTO) {
		//division 확인 후 astrago 데이터 셋이면 디비만 변경
		Dataset dataset = datasetService.findById(datasetId);

		if (checkAccessDataset(userInfoDTO, dataset)) {
			//local 데이터 셋이면 디비 + deployment label 변경
			if (dataset.isLocalDataset()) {
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
		} else{
			throw new RestApiException(DatasetErrorCode.DATASET_FIX_FORBIDDEN);
		}
	}

	@Override
	@Transactional
	public void deleteDataset(Long datasetId, UserInfoDTO userInfoDTO) {
		Dataset dataset = datasetService.findById(datasetId);
		if (checkAccessDataset(userInfoDTO, dataset)) {
			boolean isUse = workloadModuleFacadeService.isUsedDataset(datasetId);
			//true = 사용중인 데이터 셋
			if (isUse) {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_DELETE_IN_USE);
			}
			//astrago 데이터 셋은 db 삭제(astragodataset, workspacedatasetmapping
			if (dataset.isAstragoDataset()) {
				//workspace mapping 삭제
				datasetService.deleteDatasetWorkspaceMappingById(datasetId);
				//dataset 삭제
				datasetService.deleteDatasetById(datasetId);
			} else if (dataset.isLocalDataset()) {
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
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_FIX_FORBIDDEN);
		}
	}

	@Override
	public DirectoryDTO getLocalDatasetFiles(Long datasetId, String filePath) {
		//local dataset 조회
		List<DirectoryDTO.ChildrenDTO> fileList = new ArrayList<>();
		LocalDatasetEntity dataset = (LocalDatasetEntity)datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + filePath;
		List<NginxFilesDTO> files = webClientService.getObjectsFromUrl(httpUrl, NginxFilesDTO.class);
		int directoryCnt = 0;
		int fileCnt = 0;
		for (NginxFilesDTO file : files) {
			DirectoryDTO.ChildrenDTO children = DirectoryDTO.ChildrenDTO
				.builder()
				.name(file.getName())
				.type(file.getFileType())
				.size(CoreFileUtils.formatFileSize(file.getSize() == null ? 0 : Long.parseLong(file.getSize())))
				.path(FileType.D == file.getFileType() ? filePath + file.getName() + File.separator :
					filePath + file.getName())
				.build();
			if(file.getFileType() == FileType.D){
				directoryCnt += 1;
			}else{
				fileCnt += 1;
			}
			fileList.add(children);
		}
		return DirectoryDTO.builder().children(fileList).directoryCnt(directoryCnt).fileCnt(fileCnt).build();
	}

	@Override
	public DownloadFileResDTO DownloadLocalDatasetFile(Long datasetId, String filePath) {
		LocalDatasetEntity dataset = (LocalDatasetEntity)datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + filePath;
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
	public FileInfoDTO getLocalDatasetFileInfo(Long datasetId, String filePath) {
		LocalDatasetEntity dataset = (LocalDatasetEntity)datasetService.findById(datasetId);
		String httpUrl = dataset.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String size = webClientService.formatFileSize(fileInfo.getContentLength());
		String lastModifiedTime = webClientService.formatLastModifiedTime(fileInfo.getLastModified());
		String contentPath = filePath;

		return FileInfoDTO.builder()
			.fileName(fileName)
			.size(size)
			.lastModifiedTime(lastModifiedTime)
			.contentPath(contentPath)
			.build();
	}

	@Override
	public DownloadFileResDTO getLocalDatasetFile(Long datasetId, String filePath) {
		LocalDatasetEntity dataset = (LocalDatasetEntity)datasetService.findById(datasetId);
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
			} else {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
			}
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
		}
	}

	@Override
	public FileInfoDTO getAstragoDatasetFileInfo(Long datasetId, String filePath) {
		datasetService.findById(datasetId);
		File file = new File(filePath);
		if (file.exists()) {
			String size = webClientService.formatFileSize(file.length());
			String lastModifiedTime = webClientService.formatLastModifiedTime(file.lastModified());
			String contentPath = filePath;
			return FileInfoDTO.builder()
				.fileName(file.getName())
				.size(size)
				.lastModifiedTime(lastModifiedTime)
				.contentPath(contentPath)
				.build();
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_FILE_NOT_FOUND);
		}
	}

	@Override
	public DownloadFileResDTO getAstragoDatasetFile(Long datasetId, String filePath) {
		datasetService.findById(datasetId);
		Path targetPath = Path.of(filePath);
		// 파일이 존재하는지 확인
		if (Files.exists(targetPath)) {
			String fileName = CoreFileUtils.getFileName(filePath);
			// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
			byte[] fileContent;
			try {
				fileContent = Files.readAllBytes(targetPath);
			} catch (IOException e) {
				throw new RestApiException(DatasetErrorCode.DATASET_PREVIEW_FAIL);
			}
			String fileExtension = CoreFileUtils.getFileExtension(targetPath);
			if (fileExtension != null) {
				switch (fileExtension.toLowerCase()) {
					case "txt":
						break;
					case "jpg":
					case "jpeg":
					case "png":
						break;
					default:
						throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
				}
			} else {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
			}
			ByteArrayResource resource = new ByteArrayResource(fileContent);
			MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);

			return DownloadFileResDTO.builder()
				.byteArrayResource(resource)
				.fileName(fileName)
				.mediaType(mediaType)
				.build();
		}else{
			throw new RestApiException(DatasetErrorCode.DATASET_FILE_NOT_FOUND);
		}
	}

	@Override
	public WorkloadResDTO.PageUsingDatasetDTO getWorkloadsUsingDataset(PageInfo pageInfo, Long datasetId) {
		return workloadModuleFacadeService.workloadsUsingDataset(pageInfo.getPageNo(), pageInfo.getPageSize(), datasetId);
	}

	private static boolean checkAccessDataset(UserInfoDTO userInfoDTO, Dataset dataset) {
		return userInfoDTO.getAuth() == AuthType.ROLE_ADMIN ||
			(userInfoDTO.getAuth() == AuthType.ROLE_USER && userInfoDTO.getId()
				.equals(dataset.getRegUser().getRegUserId()));
	}

}
