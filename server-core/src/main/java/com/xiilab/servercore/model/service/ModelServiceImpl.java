package com.xiilab.servercore.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelErrorCode;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.RepositoryType;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.entity.ModelWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkspaceRepository;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceModelDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelServiceImpl implements ModelService{

	private final ModelRepository modelRepository;
	private final ModelWorkspaceRepository modelWorkspaceRepository;

	@Override
	@Transactional
	public void insertAstragoModel(AstragoModelEntity astragoModel, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = astragoModel.getStorageEntity().getHostPath();
		String modelPath = storageRootPath + "/" + astragoModel.getModelName().replace(" ", "");
		long size = 0;
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(modelPath);
		try {
			Files.createDirectories(uploadPath);
			// 업로드된 각 파일에 대해 작업 수행
			if(files != null){
				for (MultipartFile file : files) {
					Path targetPath = uploadPath.resolve(file.getOriginalFilename().replace(" ", "_"));
					Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
					size += file.getSize();
				}
			}
			//model 저장
			astragoModel.setModelSize(size);
			astragoModel.setModelPath(modelPath);
			modelRepository.save(astragoModel);
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}

	@Override
	@Transactional
	public void insertLocalModel(LocalModelEntity localModelEntity) {
		modelRepository.save(localModelEntity);
	}

	@Override
	public ModelDTO.ResModels getModels(int pageNo, int pageSize, UserInfoDTO userInfoDTO) {
		PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
		Page<Model> models = modelRepository.findByAuthorityWithPaging(pageRequest, userInfoDTO.getId(), userInfoDTO.getAuth());
		List<Model> entities = models.getContent();
		long totalCount = models.getTotalElements();

		return ModelDTO.ResModels.entitiesToDtos(entities, totalCount);
	}

	@Override
	public ModelDTO.ResModelWithStorage getModelWithStorage(Long modelId) {
		Model modelWithStorage = modelRepository.getModelWithStorage(modelId);
		if(modelWithStorage == null){
			throw new RestApiException(ModelErrorCode.MODEL_NOT_FOUND);
		}
		return ModelDTO.ResModelWithStorage.toDto(modelWithStorage);
	}

	@Override
	public Model findById(Long modelId) {
		Model model = modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		return model;
	}

	@Override
	@Transactional
	public void modifyModel(ModelDTO.ModifyModel modifyModel, Long modelId) {
		Model model = modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		model.modifyModelName(modifyModel.getModelName());
	}

	@Override
	public void deleteModelWorkspaceMappingById(Long modelId) {
		modelWorkspaceRepository.deleteModelWorkspaceMappingById(modelId);
	}

	@Override
	public void deleteModelById(Long modelId) {
		modelRepository.deleteById(modelId);
	}

	@Override
	public DirectoryDTO getAstragoModelFiles(Long modelId, String filePath) {
		modelRepository.findById(modelId).orElseThrow(()-> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		return CoreFileUtils.getAstragoFiles(filePath);
	}

	@Override
	public void astragoModelUploadFile(Long modelId, String path, List<MultipartFile> files) {
		AstragoModelEntity model = (AstragoModelEntity) modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		long size = CoreFileUtils.datasetUploadFiles(path, files);
		model.setModelSize(size);
	}

	@Override
	public void astragoModelCreateDirectory(Long modelId, ModelDTO.ReqFilePathDTO reqFilePathDTO) {
		modelRepository.findById(modelId).orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		Path dirPath = Path.of(reqFilePathDTO.getPath());
		// 디렉토리가 존재하지 않으면 생성
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				throw new RestApiException(ModelErrorCode.MODEL_DIRECTORY_CREATE_FAIL);
			}
		} else {
			throw new RestApiException(ModelErrorCode.MODEL_DIRECTORY_CREATE_ALREADY);
		}
	}

	@Override
	public void astragoModelDeleteFiles(Long modelId, ModelDTO.ReqFilePathsDTO reqFilePathsDTO) {
		modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		String[] targetPaths = reqFilePathsDTO.getPaths();
		for (String targetPath : targetPaths) {
			CoreFileUtils.deleteFileOrDirectory(targetPath);
		}
	}

	@Override
	public DownloadFileResDTO DownloadAstragoModelFile(Long modelId, String filePath) {
		modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		Path targetPath = Path.of(filePath);
		// 파일이 존재하는지 확인
		if (Files.exists(targetPath)) {
			if (Files.isDirectory(targetPath)) {
				// 디렉토리일 경우, 디렉토리와 하위 파일들을 압축하여 다운로드
				try {
					byte[] zipFileContent = CoreFileUtils.zipDirectory(targetPath);
					ByteArrayResource resource = new ByteArrayResource(zipFileContent);
					String zipFileName = targetPath.getFileName() + ".zip";
					return DownloadFileResDTO.builder()
						.byteArrayResource(resource)
						.fileName(zipFileName)
						.mediaType(MediaType.parseMediaType("application/zip"))
						.build();
				} catch (IOException e) {
					throw new RestApiException(ModelErrorCode.MODEL_ZIP_DOWNLOAD_FAIL);
				}
			}else{
				String fileName = CoreFileUtils.getFileName(filePath);
				// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
				byte[] fileContent;
				try {
					fileContent = Files.readAllBytes(targetPath);
				} catch (IOException e) {
					throw new RestApiException(ModelErrorCode.MODEL_FILE_DOWNLOAD_FAIL);
				}
				ByteArrayResource resource = new ByteArrayResource(fileContent);
				MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);
				return DownloadFileResDTO.builder()
					.byteArrayResource(resource)
					.fileName(fileName)
					.mediaType(mediaType)
					.build();
			}
		}else{
			throw new RestApiException(CommonErrorCode.FILE_NOT_FOUND);
		}
	}

	@Override
	public ModelDTO.ModelsInWorkspace getModelsByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserInfoDTO userInfoDTO) {
		if(repositoryType == RepositoryType.WORKSPACE){
			List<ModelWorkSpaceMappingEntity> models = modelWorkspaceRepository.findByWorkspaceResourceName(
				workspaceResourceName);
			if(models != null || models.size() != 0){
				return ModelDTO.ModelsInWorkspace.mappingEntitiesToDtos(models);
			}
		}else{
			List<Model> modelsByAuthority = modelRepository.findByAuthority(userInfoDTO.getId(), userInfoDTO.getAuth());
			return ModelDTO.ModelsInWorkspace.entitiesToDtos(modelsByAuthority);
		}
		return null;
	}

	@Override
	public void insertWorkspaceModel(InsertWorkspaceModelDTO insertWorkspaceModelDTO) {
		String workspaceResourceName = insertWorkspaceModelDTO.getWorkspaceResourceName();
		Long modelId = insertWorkspaceModelDTO.getModelId();

		ModelWorkSpaceMappingEntity workSpaceMappingEntity = modelWorkspaceRepository.findByWorkspaceResourceNameAndModelId(
			workspaceResourceName, modelId);
		if(workSpaceMappingEntity != null){
			throw new RestApiException(ModelErrorCode.MODEL_WORKSPACE_MAPPING_ALREADY);
		}

		//dataset entity 조회
		Model model = modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		//datasetWorkspaceMappingEntity 생성 및 model entity 추가
		ModelWorkSpaceMappingEntity datasetWorkSpaceMappingEntity = ModelWorkSpaceMappingEntity.builder()
			.workspaceResourceName(workspaceResourceName)
			.model(model)
			.build();

		modelWorkspaceRepository.save(datasetWorkSpaceMappingEntity);
	}

	@Override
	public void deleteWorkspaceModel(String workspaceResourceName, Long modelId, UserInfoDTO userInfoDTO) {
		ModelWorkSpaceMappingEntity workSpaceMappingEntity = modelWorkspaceRepository.findByWorkspaceResourceNameAndModelId(
			workspaceResourceName, modelId);
		if(workSpaceMappingEntity == null){
			throw new RestApiException(ModelErrorCode.MODEL_NOT_FOUND);
		}
		//owner or 본인 체크
		if(!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(workSpaceMappingEntity.getRegUser().getRegUserId().equalsIgnoreCase(userInfoDTO.getId()))){
			throw new RestApiException(ModelErrorCode.MODEL_DELETE_FORBIDDEN);
		}
		modelWorkspaceRepository.deleteByModelIdAndWorkspaceResourceName(modelId, workspaceResourceName);
	}

	@Override
	public ModelDTO.ModelsInWorkspace getModelsByWorkspaceResourceName(String workspaceResourceName) {
		List<ModelWorkSpaceMappingEntity> models = modelWorkspaceRepository.findByWorkspaceResourceName(
			workspaceResourceName);
		if(models != null || models.size() != 0){
			return ModelDTO.ModelsInWorkspace.mappingEntitiesToDtos(models);
		}
		return null;
	}
}
