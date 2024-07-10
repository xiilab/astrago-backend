package com.xiilab.servercore.modelrepo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.exception.errorcode.ModelRepoErrorCode;
import com.xiilab.modulek8sdb.modelrepo.entity.LabelEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.repository.LabelRepository;
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
	public void createModelRepo(ModelRepoDTO.RequestDTO modelRepoReqDTO, List<MultipartFile> files) {
		try {
			// 스토리지 조회
			StorageEntity storageEntity = storageService.findById(modelRepoReqDTO.getStorageId());
			// ModelRepoEntity 생성
			ModelRepoEntity modelRepoEntity = modelRepoReqDTO.convertEntity(storageEntity);
			// ModelRepoEntity save
			ModelRepoEntity saveModel = modelRepoRepository.save(modelRepoEntity);
			// 라벨 등록
			if (Objects.nonNull(modelRepoReqDTO.getLabels())) {
				List<LabelEntity> labelEntityList = modelRepoReqDTO.getLabels().stream().map(label ->
					getLabelEntityById(label.getLabelId())
				).toList();
				saveModel.addModelLabelEntity(labelEntityList);
			}
			saveModel.addModelVersionEntity();
			// 파일 저장
			createFile(saveModel, files);

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

		if (Objects.nonNull(modelRepoReqDTO.getLabels())) {
			List<LabelEntity> labelEntityList = modelRepoReqDTO.getLabels().stream().map(label ->
				getLabelEntityById(label.getLabelId())
			).toList();
			getModelRepo.modifyModelLabel(labelEntityList);
		}
	}

	private LabelEntity getLabelEntityById(long id) {
		return labelRepository.findById(id).orElseThrow(() -> new RestApiException(ModelRepoErrorCode.LABEL_NOT_FOUND));
	}

	private List<ModelRepoEntity> getModelRepoEntityListByWorkspaceResourceName(String workspaceResourceName) {
		return modelRepoRepository.findAllByWorkspaceResourceName(workspaceResourceName);
	}

	private ModelRepoEntity getModelRepoEntityById(long modelRepoId) {
		return modelRepoRepository.findById(modelRepoId)
			.orElseThrow(() -> new RestApiException(ModelRepoErrorCode.MODEL_NOT_FOUND));
	}

	private void createFile(ModelRepoEntity saveModel, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = saveModel.getStorageEntity().getHostPath();
		String saveDirectoryName =
			saveModel.getModelName().replace(" ", "") + "/"
				+ saveModel.getModelVersionList().get(0).getVersion();
		String modelPath = storageRootPath + "/" + saveModel.getWorkspaceResourceName() + "/" + saveDirectoryName;

		long size = 0;
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(modelPath);
		try {
			Files.createDirectories(uploadPath);
			// 업로드된 각 파일에 대해 작업 수행
			if (files != null) {
				for (MultipartFile file : files) {
					Path targetPath = uploadPath.resolve(file.getOriginalFilename().replace(" ", "_"));
					Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
					size += file.getSize();
				}
			}
			//model 저장
			saveModel.setModelSize(size);
			saveModel.setModelPath(modelPath);
			saveModel.setSaveDirectoryName(saveDirectoryName);
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}

}