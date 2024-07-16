package com.xiilab.servercore.modelrepo.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.LabelErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelRepoErrorCode;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModelRepoFacadeServiceImpl implements ModelRepoFacadeService {

	private final ModelRepoRepository modelRepoRepository;
	private final LabelRepository labelRepository;
	private final StorageService storageService;

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

			return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(saveModel);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(ModelRepoErrorCode.MODEL_REPO_SAVE_FAIL);
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
	public ModelRepoDTO.ResponseDTO getModelRepoById(String workspaceResourceName, long modelRepoId) {
		ModelRepoEntity modelRepoEntity = getModelRepoEntityById(modelRepoId);
		return ModelRepoDTO.ResponseDTO.convertModelRepoDTO(modelRepoEntity);
	}

	@Override
	@Transactional
	public void deleteModelRepoById(long modelRepoId) {
		modelRepoRepository.deleteById(modelRepoId);
	}

	@Override
	@Transactional
	public void modifyModelRepo(long modelRepoId, ModelRepoDTO.RequestDTO modelRepoReqDTO) {

		ModelRepoEntity getModelRepo = getModelRepoEntityById(modelRepoId);

		StorageEntity storageEntity = storageService.findById(modelRepoReqDTO.getStorageId());

		getModelRepo.modifyModelRepo(modelRepoReqDTO.getModelName(), modelRepoReqDTO.getDescription(),
			storageEntity);

		if (Objects.nonNull(modelRepoReqDTO.getLabelIds())) {
			List<LabelEntity> labelEntityList = modelRepoReqDTO.getLabelIds()
				.stream()
				.map(this::getLabelEntityById)
				.toList();
			getModelRepo.modifyModelLabel(labelEntityList);
		}
	}

	private LabelEntity getLabelEntityById(long id) {
		return labelRepository.findById(id).orElseThrow(() -> new RestApiException(LabelErrorCode.LABEL_NOT_FOUND));
	}

	private List<ModelRepoEntity> getModelRepoEntityListByWorkspaceResourceName(String workspaceResourceName) {
		return modelRepoRepository.findAllByWorkspaceResourceName(workspaceResourceName);
	}

	private ModelRepoEntity getModelRepoEntityById(long modelRepoId) {
		return modelRepoRepository.findById(modelRepoId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_NOT_FOUND));
	}

}