package com.xiilab.servercore.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelErrorCode;
import com.xiilab.modulecommon.util.CompressUtils;
import com.xiilab.modulecommon.util.DecompressUtils;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.common.enums.RepositorySortType;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceDatasetDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceModelDTO;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.moduleuser.dto.UserDTO;
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
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;

	@Override
	@Transactional
	public void insertAstragoModel(AstragoModelEntity astragoModel, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = astragoModel.getStorageEntity().getHostPath();
		String saveDirectoryName = astragoModel.getModelName().replace(" ", "") + "-" + UUID.randomUUID().toString().substring(6);
		String modelPath = storageRootPath + "/" +  saveDirectoryName;
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
			astragoModel.setSaveDirectoryName(saveDirectoryName);
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
	public ModelDTO.ResModels getModels(PageInfo pageInfo, RepositorySearchCondition repositorySearchCondition, UserDTO.UserInfo userInfoDTO) {
		PageRequest pageRequest = PageRequest.of(pageInfo.getPageNo() - 1, pageInfo.getPageSize());
		Page<Model> models = modelRepository.findByAuthorityWithPaging(pageRequest, userInfoDTO.getId(), userInfoDTO.getAuth(), repositorySearchCondition);
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
		model.modifyModelDefaultPath(modifyModel.getDefaultPath());
	}

	@Override
	@Transactional
	public void deleteModelWorkspaceMappingById(Long modelId) {
		modelWorkspaceRepository.deleteModelWorkspaceMappingById(modelId);
	}

	@Override
	@Transactional
	public void deleteModelById(Long modelId) {
		modelRepository.deleteById(modelId);
	}

	@Override
	public DirectoryDTO getAstragoModelFiles(Long modelId, String filePath) {
		modelRepository.findById(modelId).orElseThrow(()-> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		return CoreFileUtils.getAstragoFiles(filePath);
	}

	@Override
	@Transactional
	public void astragoModelUploadFile(Long modelId, String path, List<MultipartFile> files) {
		AstragoModelEntity model = (AstragoModelEntity) modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		long size = CoreFileUtils.datasetUploadFiles(path, files);
		model.setModelSize(size);
	}

	@Override
	@Transactional
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
	@Transactional
	public DownloadFileResDTO downloadAstragoModelFile(Long modelId, String filePath) {
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
	public void compressAstragoModelFiles(Long modelId, List<String> filePaths, CompressFileType compressFileType) {
		modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		List<Path> pathList = filePaths.stream().map(Path::of).toList();
		CompressUtils.saveCompressFile(pathList, null, compressFileType);
	}

	@Override
	public void deCompressAstragoModelFile(Long modelId, String filePath) {
		modelRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));
		DecompressUtils.saveDecompressFile(Path.of(filePath), null);
	}

	@Override
	public ModelDTO.ModelsInWorkspace getModelsByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO) {
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
	@Transactional
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
			.modelDefaultMountPath(insertWorkspaceModelDTO.getDefaultPath())
			.build();

		modelWorkspaceRepository.save(datasetWorkSpaceMappingEntity);
	}

	@Override
	@Transactional
	public void deleteWorkspaceModel(String workspaceResourceName, Long modelId, UserDTO.UserInfo userInfoDTO) {
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

	@Override
	@Transactional
	public void deleteModelWorkloadMapping(Long jobId) {
		modelWorkLoadMappingRepository.deleteByWorkloadId(jobId);
	}

	@Override
	@Transactional
	public void updateWorkspaceModel(UpdateWorkspaceModelDTO updateWorkspaceModelDTO, String workspaceResourceName,
		Long modelId, UserDTO.UserInfo userInfoDTO) {
		ModelWorkSpaceMappingEntity workSpaceMappingEntity = modelWorkspaceRepository.findByWorkspaceResourceNameAndModelId(
			workspaceResourceName, modelId);
		if(workSpaceMappingEntity == null){
			throw new RestApiException(ModelErrorCode.MODEL_NOT_FOUND);
		}
		//owner or 본인 체크
		if(!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(workSpaceMappingEntity.getRegUser().getRegUserId().equalsIgnoreCase(userInfoDTO.getId()))){
			throw new RestApiException(ModelErrorCode.MODEL_FIX_FORBIDDEN);
		}
		workSpaceMappingEntity.modifyDefaultPath(updateWorkspaceModelDTO.getDefaultPath());
	}
}
