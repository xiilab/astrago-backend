package com.xiilab.servercore.modelrepo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LabelErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelRepoErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
import com.xiilab.modulek8sdb.modelrepo.enums.ModelRepoType;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoRepository;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoVersionRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ModelRepoFacadeServiceImpl implements ModelRepoFacadeService {

	private final ModelRepoRepository modelRepoRepository;
	private final LabelRepository labelRepository;
	private final StorageService storageService;
	private final ModelRepoVersionRepository versionRepository;

	private static void copyModelToStorage(String wlModelPath, String storagePath) {
		// 모델 경로 설정
		Path sourcePath = Paths.get(wlModelPath);
		Path targetPath = Paths.get(storagePath);
		// 모델 복사
		try {
			if (Files.isDirectory(sourcePath)) {
				FileUtils.copyDirectory(sourcePath, targetPath);
			} else {
				FileUtils.copyFile(sourcePath, targetPath);
			}
			log.info("모델 등록 성공");
		} catch (IOException e) {
			log.info("모델 등록 실패");
		}
	}

	@Override
	public List<ModelRepoDTO.ResponseDTO> getModelRepoList(String workspaceResourceName) {
		// 해당 워크스페이스에 등록된 Model List 조회
		List<ModelRepoEntity> modelRepoEntityList = getModelRepoEntityListByWorkspaceResourceName(
			workspaceResourceName);
		return modelRepoEntityList.stream().map(ModelRepoDTO.ResponseDTO::convertModelRepoDTO).toList();
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
			// 라벨 등록
			if (Objects.nonNull(modelRepoReqDTO.getLabelIds())) {
				List<LabelEntity> labelEntityList = modelRepoReqDTO.getLabelIds()
					.stream()
					.map(this::getLabelEntityById)
					.toList();
				saveModel.addModelLabelEntity(labelEntityList);
			}
			saveModel.addModelVersionEntity();
			// 해당 모델이 저장 되는 경로
			String modelPath = storageEntity.getStoragePath() + "/workspace/" + saveModel.getWorkspaceResourceName() + "/model/" +
				saveModel.getModelRepoRealName().replace(" ", "");
			saveModel.setModelPath(modelPath);

			return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(saveModel);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ModelRepoErrorCode.MODEL_REPO_SAVE_FAIL);
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
		// 모델 Entity 조회
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(modelRepoId);
		// 모델 삭제
		modelRepoRepository.deleteById(modelRepoId);
		// 해당 모델 파일 삭제
		String modelPath = modelRepoEntity.getModelPath();
		// 모델이 저장된 폴더 삭제
		FileUtils.deleteDirectory(modelPath);
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
		String versionPath = modelRepoEntity.getModelPath() + versionEntity.getVersion();
		// 모델이 저장된 폴더 삭제
		FileUtils.deleteDirectory(versionPath);
	}

	@Override
	@Transactional
	public void registerOrVersionUpModelRepo(ModelRepoDTO.wlModelRepoDTO wlModelRepoDTO) {
		// WL 모델 경로
		String storagePath = "";
		if (wlModelRepoDTO.getModelType().equals(ModelRepoType.NEW_MODEL)) {
			// 모델 신규 등록
			storagePath = createNewModelRepo(wlModelRepoDTO);
		} else {
			// 기존 모델에 추가
			storagePath = versionUpModelRepo(wlModelRepoDTO);
		}
		for(String wlModelPath : wlModelRepoDTO.getWlModelPaths()){
			copyModelToStorage(wlModelPath, storagePath);
		}
	}

	private String versionUpModelRepo(ModelRepoDTO.wlModelRepoDTO wlModelRepoDTO) {
		String storagePath;
		// 기존 모델 조회
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(wlModelRepoDTO.getModelRepoId());
		// 해당 모델의 다음 버전 조회
		int version = modelRepoEntity.getModelVersionList().size() + 1;
		// 다음 버전 저장
		modelRepoEntity.updateModelRepoVersion(version);
		// 옮길 경로
		storagePath = modelRepoEntity.getModelPath() + "/v" + version;
		return storagePath;
	}

	private String createNewModelRepo(ModelRepoDTO.wlModelRepoDTO wlModelRepoDTO) {
		// 스토리지 조회
		StorageEntity storage = storageService.findById(wlModelRepoDTO.getStorageId());
		ModelRepoDTO.RequestDTO requestDTO = wlModelRepoDTO.convertEntity(storage);
		// model repo 저장
		ModelRepoDTO.ResponseDTO modelRepo = createModelRepo(requestDTO);
		// 옮길 경로
		return modelRepo.getModelPath() + "/v1";
	}

	private LabelEntity getLabelEntityById(long id) {
		return labelRepository.findById(id).orElseThrow(() -> new RestApiException(LabelErrorCode.LABEL_NOT_FOUND));
	}

	private List<ModelRepoEntity> getModelRepoEntityListByWorkspaceResourceName(String workspaceResourceName) {
		return modelRepoRepository.findAllByWorkspaceResourceName(workspaceResourceName);
	}

	private ModelRepoEntity getModelRepoEntityById(long modelRepoId) {
		return modelRepoRepository.findById(modelRepoId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_REPO_NOT_FOUND));
	}

	private ModelVersionEntity getModelRepoVersionById(long versionId){
		return versionRepository.findById(versionId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_REPO_VERSION_NOT_FOUND));
	}


}