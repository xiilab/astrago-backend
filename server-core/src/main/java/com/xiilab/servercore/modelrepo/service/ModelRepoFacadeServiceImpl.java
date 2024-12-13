package com.xiilab.servercore.modelrepo.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LabelErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelRepoErrorCode;
import com.xiilab.modulecommon.exception.errorcode.TusErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;
import com.xiilab.modulek8sdb.deploy.repository.DeployRepository;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
import com.xiilab.modulek8sdb.modelrepo.enums.ModelRepoType;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoRepository;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoVersionRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.deploy.dto.ResDeploys;
import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.upload.UploadInfo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ModelRepoFacadeServiceImpl implements ModelRepoFacadeService {

	private final ModelRepoRepository modelRepoRepository;
	private final LabelRepository labelRepository;
	private final StorageService storageService;
	private final ModelRepoVersionRepository versionRepository;
	private final DeployRepository deployRepository;
	private final ModelRepoVersionRepository modelRepoVersionRepository;

	@Override
	public PageDTO<ModelRepoDTO.ResponseDTO> getModelRepoList(String workspaceResourceName, String search, int pageNum,
		int pageSize) {
		// 해당 워크스페이스에 등록된 Model List 조회
		List<ModelRepoEntity> modelRepoEntityList = getModelRepoEntityListByWorkspaceResourceName(
			workspaceResourceName, search);
		List<ModelRepoDTO.ResponseDTO> modelRepoReqDtoList = modelRepoEntityList.stream()
			.map(ModelRepoDTO.ResponseDTO::convertModelRepoDTO)
			.toList();

		return new PageDTO<>(modelRepoReqDtoList, pageNum, pageSize);
	}

	private static FileInfoDTO copyModelToStorage(String wlModelPath, String storagePath) {
		// 모델 경로 설정
		Path sourcePath = Paths.get(wlModelPath);
		Path targetPath = Paths.get(storagePath);
		// 모델 복사
		try {
			log.info("모델 등록 성공");
			return FileUtils.copyFile(sourcePath, targetPath);
		} catch (IOException e) {
			log.info("모델 등록 실패");
			return null;
		}
	}

	@Override
	public ModelRepoDTO.ResponseDTO getModelRepoById(String workspaceResourceName, long modelRepoId) {
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(modelRepoId);

		return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(modelRepoEntity);
	}

	@Override
	@Transactional
	public void deleteModelRepoById(long modelRepoId) {
		//해당 모델을 사용중인 서비스가 있는지 체크 -> 있으면 삭제 X
		List<DeployEntity> deploys = deployRepository.findByModelRepoId(modelRepoId);
		if (deploys.size() != 0) {
			throw new RestApiException(ModelRepoErrorCode.MODEL_REPO_DELETE_FAIL);
		}
		// 모델 Entity 조회
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(modelRepoId);
		// 모델 삭제
		modelRepoRepository.deleteById(modelRepoId);
		// 해당 모델 파일 삭제
		String modelPath = modelRepoEntity.getModelPath();
		String hostPath = modelRepoEntity.getStorageEntity().getHostPath();
		// 모델이 저장된 폴더 삭제
		String directoryPath = hostPath + modelPath;
		FileUtils.deleteAllDirectory(directoryPath);
	}

	@Override
	@Transactional
	public void deleteModelRepoVersion(long versionId) {
		// 해당 ID의 모델 조회
		ModelVersionEntity versionEntity = getModelRepoVersionById(versionId);
		ModelRepoEntity modelRepoEntity = versionEntity.getModelRepoEntity();
		// 해당 ID의 버전 삭제
		versionRepository.deleteById(versionId);
		// 삭제될 버전의 주소
		String versionPath = modelRepoEntity.getModelPath() + "/" + versionEntity.getVersion();
		// 모델이 저장된 폴더 삭제
		String hostPath = modelRepoEntity.getStorageEntity().getHostPath();
		String modelPath = hostPath + versionPath;
		FileUtils.deleteAllDirectory(modelPath);
	}

	@Override
	@Transactional
	public ModelRepoDTO.ResponseDTO createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO) {
		try {
			// 스토리지 조회
			StorageEntity storageEntity = storageService.findById(modelRepoReqDTO.getStorageId());
			// ModelRepoEntity 생성
			ModelRepoEntity modelRepoEntity = modelRepoReqDTO.convertEntity(storageEntity);
			// ModelRepoEntity save
			ModelRepoEntity saveModel = modelRepoRepository.save(modelRepoEntity);

			setModelLabel(modelRepoReqDTO, saveModel);
			String modelPath = "";
			if (modelRepoReqDTO.getModelPath() == null) {
				// 해당 모델이 저장 되는 경로
				modelPath =
					storageEntity.getStoragePath() + "/workspace/" + saveModel.getWorkspaceResourceName() + "/model/" +
						saveModel.getModelRepoRealName().replace(" ", "");
			} else {
				modelPath = storageEntity.getStoragePath() + saveModel.getModelRepoRealName().replace(" ", "");
			}
			saveModel.setModelPath(modelPath);
			return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(saveModel);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ModelRepoErrorCode.MODEL_REPO_SAVE_FAIL);
		}
	}

	@Override
	@Transactional
	public void registerOrVersionUpModelRepo(List<MultipartFile> files,
		ModelRepoDTO.WlModelRepoDTO wlModelRepoDTO) {
		// // WL 모델 경로
		String storagePath = "";
		ModelRepoDTO.ResponseDTO responseDTO = null;
		if (wlModelRepoDTO.getModelType().equals(ModelRepoType.NEW_MODEL)) {
			responseDTO = createNewModelRepo(wlModelRepoDTO);
			// 모델 신규 등록
			storagePath = responseDTO.getModelPath() + "/v1/";
		} else {
			// 기존 모델에 추가
			responseDTO = versionUpModelRepo(wlModelRepoDTO);
			ModelVersionEntity versionEntity = versionRepository.findLatestByModelRepoEntityId(
				responseDTO.getModelRepoId());
			storagePath = responseDTO.getModelPath() + "/" + versionEntity.getVersion() + "/";
		}
		// 모델 파일 복사
		ModelVersionEntity versionEntity = versionRepository.findLatestByModelRepoEntityId(
			responseDTO.getModelRepoId());
		FileInfoDTO modelFile = copyModelToStorage(wlModelRepoDTO.getWlModelPaths(), storagePath);
		if (Objects.nonNull(modelFile)) {
			versionEntity.setModelFile(modelFile.getFileName(), modelFile.getSize());
		}
		// 그외 설정 파일 업로드
		// List<FileInfoDTO> metaFiles = FileUtils.uploadFiles(storagePath, files);
		// if (Objects.nonNull(metaFiles)) {
		// 	versionEntity.setModelMeta(metaFiles);
		// }
	}

	@Override
	@Transactional
	public void updateModelRepoById(long modelRepoId, ModelRepoDTO.UpdateDTO updateDTO) {
		ModelRepoEntity findModelRepo = getModelRepoEntityById(modelRepoId);

		findModelRepo.updateModelRepo(updateDTO.getModelName(), updateDTO.getDescription());
	}

	@Override
	public PageDTO<ModelRepoDTO.VersionDTO> getModelRepoVersionList(long modelRepoId, int pageNum, int pageSize,
		String sort) {
		Sort order = Sort.by(Sort.Direction.fromString(sort), "version");
		List<ModelVersionEntity> getModelRepoEntity = versionRepository.findByModelRepoEntityId(modelRepoId, order);
		List<ModelRepoDTO.VersionDTO> versionDTOS = getModelRepoEntity.stream()
			.map(ModelRepoDTO.VersionDTO::convertVersionDTO)
			.toList();
		return new PageDTO<>(versionDTOS, pageNum, pageSize);
	}

	private ModelRepoDTO.ResponseDTO versionUpModelRepo(ModelRepoDTO.WlModelRepoDTO wlModelRepoDTO) {
		// 기존 모델 조회
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(wlModelRepoDTO.getModelRepoId());
		// 해당 모델의 다음 버전 조회
		int version = modelRepoEntity.getModelVersionList().size() + 1;
		// 다음 버전 저장
		modelRepoEntity.updateModelRepoVersion(version);
		// 옮길 경로
		return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(modelRepoEntity);
	}

	private LabelEntity getLabelEntityById(long id) {
		return labelRepository.findById(id).orElseThrow(() -> new RestApiException(LabelErrorCode.LABEL_NOT_FOUND));
	}

	private List<ModelRepoEntity> getModelRepoEntityListByWorkspaceResourceName(String workspaceResourceName,
		String search) {
		return modelRepoRepository.findAllByWorkspaceResourceName(workspaceResourceName, search);
	}

	@Override
	public ModelRepoEntity getModelRepoEntityById(long modelRepoId) {
		return modelRepoRepository.findById(modelRepoId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_REPO_NOT_FOUND));
	}

	@Override
	public ResDeploys getDeploysUsingModel(Long modelRepoId, int pageNum, int pageSize) {
		PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize);
		PageImpl<DeployEntity> deploysUsingModel = deployRepository.getDeploysUsingModel(pageRequest, modelRepoId);
		List<DeployEntity> content = deploysUsingModel.getContent();
		long totalCount = deploysUsingModel.getTotalElements();
		return ResDeploys.entitiesToDtos(content, totalCount);
	}

	@Override
	public DirectoryDTO getModelFiles(Long modelRepoId, String modelVersion, String filePath) {
		if (filePath == null || filePath.isBlank()) {
			ModelRepoEntity modelRepoEntity = getModelRepoEntityById(modelRepoId);
			String hostPath = modelRepoEntity.getStorageEntity()
				.getHostPath();// /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a
			String modelPath = hostPath + "/" + modelRepoEntity.getModelPath() + "/" + modelVersion;
			return CoreFileUtils.getAstragoFiles(modelPath);
		} else {
			return CoreFileUtils.getAstragoFiles(filePath);
		}
	}

	@Override
	@Transactional
	public ModelRepoDTO.ResponseDTO createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO, UploadInfo uploadInfo) {
		try {
			Long fileSize = uploadInfo.getLength();
			String filename = uploadInfo.getMetadata().get("filename");
			String regUserId = uploadInfo.getMetadata().get("regUserId");
			String regUserName = uploadInfo.getMetadata().get("regUserName");
			String regUserRealName = uploadInfo.getMetadata().get("regUserRealName");
			// 스토리지 조회
			StorageEntity storageEntity = storageService.findById(modelRepoReqDTO.getStorageId());
			// ModelRepoEntity 생성
			ModelRepoEntity modelRepoEntity = modelRepoReqDTO.convertEntity(storageEntity);
			String hostPath = storageEntity.getHostPath();
			String modelRepoRealName = "model-" + UUID.randomUUID().toString().substring(6);
			String modelRepoPath =
				"/workspaces/" + modelRepoReqDTO.getWorkspaceResourceName() + "/models/" + modelRepoRealName;
			modelRepoEntity.setModelPath(modelRepoPath);
			modelRepoEntity.setModelRepoRealName(modelRepoRealName);

			RegUser regUser = new RegUser(regUserId, regUserName, regUserRealName);
			ModelVersionEntity modelVersionEntity = ModelVersionEntity.builder()
				.version("v1")
				.modelFileName(filename)
				.modelFileSize(String.valueOf(fileSize))
				.modelRepoEntity(modelRepoEntity)
				.build();
			modelVersionEntity.setRegUserInfo(regUser, LocalDateTime.now(), LocalDateTime.now());
			modelRepoVersionRepository.save(modelVersionEntity);

			// ModelRepoEntity save
			modelRepoEntity.setRegUserInfo(regUser, LocalDateTime.now(), LocalDateTime.now());
			ModelRepoEntity saveModel = modelRepoRepository.save(modelRepoEntity);
			saveModel.addModelVersionEntity(modelVersionEntity);
			setModelLabel(modelRepoReqDTO, saveModel);
			String modelPath = hostPath + modelRepoPath + "/v1";
			/*if(modelRepoReqDTO.getModelPath() == null) {
				// 해당 모델이 저장 되는 경로
				modelPath = storageEntity.getStoragePath() + "/workspace/" + saveModel.getWorkspaceResourceName() + "/model/" +
					saveModel.getModelRepoRealName().replace(" ", "");
			}else{
				modelPath = saveModel.getModelRepoRealName().replace(" ", "");
			}*/
			// saveModel.setModelPath(modelPath);
			ModelRepoDTO.ResponseDTO responseDTO = ModelRepoDTO.ResponseDTO.convertModelRepoDTO(saveModel);
			responseDTO.setModelPath(modelPath);
			return responseDTO;
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ModelRepoErrorCode.MODEL_REPO_SAVE_FAIL);
		}
	}

	@Override
	public void uploadMetaFiles(Long modelRepoId, String modelVersion, List<MultipartFile> files) {
		ModelRepoEntity modelRepoEntity = modelRepoRepository.findById(modelRepoId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_REPO_NOT_FOUND));
		String modelPath = modelRepoEntity.getStorageEntity().getHostPath()
			+ modelRepoEntity.getModelPath() + "/" + modelVersion;

		FileUtils.uploadFiles(modelPath, files);
	}

	private ModelRepoDTO.ResponseDTO createNewModelRepo(ModelRepoDTO.WlModelRepoDTO wlModelRepoDTO) {
		ModelRepoDTO.RequestDTO requestDTO = wlModelRepoDTO.convertRequestDTO();
		// model repo 저장
		return createModelRepo(requestDTO);
	}

	private ModelVersionEntity getModelRepoVersionById(long versionId) {
		return versionRepository.findById(versionId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_REPO_VERSION_NOT_FOUND));
	}

	private void setModelLabel(ModelRepoDTO.RequestDTO modelRepoReqDTO, ModelRepoEntity saveModel) {
		// 라벨 등록
		if (Objects.nonNull(modelRepoReqDTO.getLabelIds())) {
			List<LabelEntity> labelEntityList = modelRepoReqDTO.getLabelIds()
				.stream()
				.map(this::getLabelEntityById)
				.toList();
			saveModel.addModelLabelEntity(labelEntityList);
		}
	}

}
