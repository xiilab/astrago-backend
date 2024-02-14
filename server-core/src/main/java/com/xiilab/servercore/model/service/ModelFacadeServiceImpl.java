package com.xiilab.servercore.model.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelErrorCode;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalModelResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalModelDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.FileType;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.dto.NginxFilesDTO;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;
import com.xiilab.servercore.dataset.service.WebClientService;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.servercore.model.entity.AstragoModelEntity;
import com.xiilab.servercore.model.entity.LocalModelEntity;
import com.xiilab.servercore.model.entity.Model;
import com.xiilab.servercore.storage.entity.StorageEntity;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ModelFacadeServiceImpl implements ModelFacadeService{
	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.dataset.dockerImage.name}")
	private String dockerImage;
	@Value("${astrago.dataset.dockerImage.hostPath}")
	private String hostPath;
	private final StorageService storageService;
	private final ModelService modelService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final WebClientService webClientService;

	@Override
	@Transactional
	public void insertAstragoDataset(ModelDTO.CreateAstragoModel createModelDTO, List<MultipartFile> files) {
		StorageEntity storageEntity = storageService.findById(createModelDTO.getStorageId());

		AstragoModelEntity astragoModel = AstragoModelEntity.builder()
			.modelName(createModelDTO.getModelName())
			.storageEntity(storageEntity)
			.build();

		modelService.insertAstragoDataset(astragoModel, files);
	}

	@Override
	@Transactional
	public void insertLocalModel(ModelDTO.CreateLocalModel createLocalModel) {
		CreateLocalModelDTO createDto = CreateLocalModelDTO.builder()
			.namespace(namespace)
			.modelName(createLocalModel.getModelName())
			.ip(createLocalModel.getIp())
			.storagePath(createLocalModel.getStoragePath())
			.dockerImage(dockerImage)
			.hostPath(hostPath)
			.build();

		CreateLocalModelResDTO createLocalModelResDTO = workloadModuleFacadeService.createLocalModel(createDto);

		LocalModelEntity localModelEntity = LocalModelEntity.builder()
			.modelName(createLocalModel.getModelName())
			.ip(createLocalModel.getIp())
			.storageType(StorageType.NFS)
			.storagePath(createLocalModel.getStoragePath())
			.dns(createLocalModelResDTO.getDns())
			.deploymentName(createLocalModelResDTO.getDeploymentName())
			.pvcName(createLocalModelResDTO.getPvcName())
			.pvName(createLocalModelResDTO.getPvName())
			.svcName(createLocalModelResDTO.getSvcName())
			.build();
		modelService.insertLocalModel(localModelEntity);
	}

	@Override
	public ModelDTO.ResModelWithStorage getModel(Long modelId) {
		ModelDTO.ResModelWithStorage modelWithStorage = modelService.getModelWithStorage(modelId);
		Long id = modelWithStorage.getModelId();
		List<WorkloadResDTO.UsingModelDTO> usingModelDTOS = workloadModuleFacadeService.workloadsUsingModel(id);
		modelWithStorage.addUsingModels(usingModelDTOS);
		return modelWithStorage;
	}

	@Override
	@Transactional
	public void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId, UserInfoDTO userInfoDTO) {
		//division 확인 후 astrago 데이터 셋이면 디비만 변경
		Model model = modelService.findById(modelId);

		if (checkAccessDataset(userInfoDTO, model)) {
			//local 데이터 셋이면 디비 + deployment label 변경
			if (model.isLocalModel()) {
				LocalModelEntity localModelEntity = (LocalModelEntity)model;
				ModifyLocalModelDeploymentDTO modifyLocalDatasetDeploymentDTO = ModifyLocalModelDeploymentDTO
					.builder()
					.deploymentName(localModelEntity.getDeploymentName())
					.modifyModelName(modifyModel.getModelName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.modifyLocalModelDeployment(modifyLocalDatasetDeploymentDTO);
			}
			modelService.modifyModel(modifyModel, modelId);
		} else{
			throw new RestApiException(ModelErrorCode.MODEL_FIX_FORBIDDEN);
		}
	}

	@Override
	@Transactional
	public void deleteModel(Long modelId, UserInfoDTO userInfoDTO) {
		Model model = modelService.findById(modelId);
		if (checkAccessDataset(userInfoDTO, model)) {
			boolean isUse = workloadModuleFacadeService.isUsedModel(modelId);
			//true = 사용중인 데이터 셋
			if (isUse) {
				throw new RestApiException(ModelErrorCode.MODEL_NOT_DELETE_IN_USE);
			}
			//astrago 데이터 셋은 db 삭제(astragodataset, workspacedatasetmapping
			if (model.isAstargoModel()) {
				//workspace mapping 삭제
				modelService.deleteModelWorkspaceMappingById(modelId);
				//dataset 삭제
				modelService.deleteModelById(modelId);
			} else if (model.isLocalModel()) {
				//pv, pvc, deployment, svc 삭제
				LocalModelEntity localModelEntity = (LocalModelEntity)model;
				DeleteLocalModelDTO deleteLocalModelDTO = DeleteLocalModelDTO.builder()
					.deploymentName(localModelEntity.getDeploymentName())
					.svcName(localModelEntity.getSvcName())
					.pvcName(localModelEntity.getPvcName())
					.pvName(localModelEntity.getPvName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.deleteLocalModel(deleteLocalModelDTO);
				//workspace mapping 삭제
				modelService.deleteModelWorkspaceMappingById(modelId);
				//db 삭제 - TB_localDataset
				modelService.deleteModelById(modelId);
			}
		} else {
			throw new RestApiException(ModelErrorCode.MODEL_FIX_FORBIDDEN);
		}
	}

	@Override
	public ModelDTO.FileInfo getAstragoModelFileInfo(Long modelId, String filePath) {
		modelService.findById(modelId);
		File file = new File(filePath);
		if (file.exists()) {
			String size = webClientService.formatFileSize(file.length());
			String lastModifiedTime = webClientService.formatLastModifiedTime(file.lastModified());
			String contentPath = filePath;
			return ModelDTO.FileInfo.builder()
				.fileName(file.getName())
				.size(size)
				.lastModifiedTime(lastModifiedTime)
				.contentPath(contentPath)
				.build();
		} else {
			throw new RestApiException(ModelErrorCode.MODEL_FILE_NOT_FOUND);
		}
	}

	@Override
	public DownloadFileResDTO getAstragoModelFile(Long modelId, String filePath) {
		modelService.findById(modelId);
		Path targetPath = Path.of(filePath);
		// 파일이 존재하는지 확인
		if (Files.exists(targetPath)) {
			String fileName = CoreFileUtils.getFileName(filePath);
			// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
			byte[] fileContent;
			try {
				fileContent = Files.readAllBytes(targetPath);
			} catch (IOException e) {
				throw new RestApiException(ModelErrorCode.MODEL_PREVIEW_FAIL);
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
						throw new RestApiException(ModelErrorCode.MODEL_PREVIEW_FAIL);
				}
			} else {
				throw new RestApiException(ModelErrorCode.MODEL_PREVIEW_FAIL);
			}
			ByteArrayResource resource = new ByteArrayResource(fileContent);
			MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);

			return DownloadFileResDTO.builder()
				.byteArrayResource(resource)
				.fileName(fileName)
				.mediaType(mediaType)
				.build();
		}else{
			throw new RestApiException(ModelErrorCode.MODEL_FILE_NOT_FOUND);
		}
	}

	@Override
	public DirectoryDTO getLocalModelFiles(Long modelId, String filePath) {
		//local dataset 조회
		List<DirectoryDTO.ChildrenDTO> fileList = new ArrayList<>();
		LocalModelEntity model = (LocalModelEntity)modelService.findById(modelId);
		String httpUrl = model.getDns() + filePath;
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
	public DownloadFileResDTO DownloadLocalModelFile(Long modelId, String filePath) {
		LocalModelEntity model = (LocalModelEntity)modelService.findById(modelId);
		String httpUrl = model.getDns() + filePath;
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
	public ModelDTO.FileInfo getLocalModelFileInfo(Long modelId, String filePath) {
		LocalModelEntity model = (LocalModelEntity)modelService.findById(modelId);
		String httpUrl = model.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String size = webClientService.formatFileSize(fileInfo.getContentLength());
		String lastModifiedTime = webClientService.formatLastModifiedTime(fileInfo.getLastModified());
		String contentPath = filePath;

		return ModelDTO.FileInfo.builder()
			.fileName(fileName)
			.size(size)
			.lastModifiedTime(lastModifiedTime)
			.contentPath(contentPath)
			.build();
	}

	@Override
	public DownloadFileResDTO getLocalModelFile(Long modelId, String filePath) {
		LocalModelEntity model = (LocalModelEntity)modelService.findById(modelId);
		String httpUrl = model.getDns() + filePath;
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
				throw new RestApiException(ModelErrorCode.MODEL_PREVIEW_FAIL);
			}
		} else {
			throw new RestApiException(ModelErrorCode.MODEL_PREVIEW_FAIL);
		}
	}

	private static boolean checkAccessDataset(UserInfoDTO userInfoDTO, Model model) {
		return userInfoDTO.getAuth() == AuthType.ROLE_ADMIN ||
			(userInfoDTO.getAuth() == AuthType.ROLE_USER && userInfoDTO.getId()
				.equals(model.getRegUser().getRegUserId()));
	}
}
