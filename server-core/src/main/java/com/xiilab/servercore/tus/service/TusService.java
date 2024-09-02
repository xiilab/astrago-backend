package com.xiilab.servercore.tus.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulecommon.exception.errorcode.TusErrorCode;
import com.xiilab.modulecommon.exception.errorcode.VolumeErrorCode;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoRepository;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.volume.repository.VolumeRepository;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;
import com.xiilab.servercore.modelrepo.service.ModelRepoFacadeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class TusService {
	private final TusFileUploadService tusFileUploadService;
	// TODO 삭제 예정
/*	private final ModelRepository modelRepository;
	private final DatasetRepository datasetRepository;*/
	private final VolumeRepository volumeRepository;
	private final ModelRepoFacadeService modelRepoFacadeService;
	private final ModelRepoRepository modelRepoRepository;

	public void tusUpload(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 업로드
			tusFileUploadService.process(request, response);

			// 현재 업로드 정보
			UploadInfo uploadInfo = tusFileUploadService.getUploadInfo(request.getRequestURI());

			// 완료 된 경우 파일 저장
			if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
				// "metadata"로 넘어온 원본 파일명 추출
				String filename = Optional.ofNullable(uploadInfo.getMetadata().get("filename"))
					.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));
				String uploadType = Optional.ofNullable(uploadInfo.getMetadata().get("uploadType"))
					.orElseThrow(() -> new RestApiException(TusErrorCode.UPLOAD_TYPE_ERROR_MESSAGE))
					.toUpperCase();

				// TODO 삭제 예정
				/*if ("DATASET".equals(uploadType)) {
					saveDataset(request, uploadInfo, filename);
				} else if ("MODEL".equals(uploadType)) {
					saveModel(request, uploadInfo, filename);
				} else*/
				if ("MODEL_REPO".equals(uploadType)) {
					saveModelRepo(request, uploadInfo, filename);
				} else if ("VOLUME".equals(uploadType)) {
					saveVolume(request, uploadInfo, filename);
				}

				// 임시 파일 삭제
				tusFileUploadService.deleteUpload(request.getRequestURI());
			}
		} catch (IOException | TusException e) {
			log.error("exception was occurred. message={}", e.getMessage(), e);
			throw new RestApiException(TusErrorCode.UPLOAD_FAILED_MESSAGE);
		}
	}

	private void saveModelRepo(HttpServletRequest request, UploadInfo uploadInfo, String filename) throws
		IOException,
		TusException {
		// model repo DTO 생성
		ModelRepoDTO.RequestDTO modelRepoDTO = getModelRepoDTO(uploadInfo);
		// model repo DB 등록
		ModelRepoDTO.ResponseDTO modelRepo = modelRepoFacadeService.createModelRepo(modelRepoDTO);

		ModelRepoEntity modelRepoEntity = modelRepoRepository.findById(modelRepo.getModelRepoId())
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		log.info("ModelRepoPath : " + modelRepoEntity.getModelPath());
		//파일 업로드 경로
		String filePath = modelRepoEntity.getModelPath() + "/v1/";
		// 파일 저장
		getFilePath(request, filePath, filename);
	}

	private ModelRepoDTO.RequestDTO getModelRepoDTO(UploadInfo uploadInfo) {
		List<Long> labelsIds = new ArrayList<>();
		Long storageId = Optional.ofNullable(uploadInfo.getMetadata().get("storageId"))
			.map(Long::valueOf)
			.orElseThrow(() -> new RestApiException(TusErrorCode.UPLOAD_TYPE_ERROR_MESSAGE));
		String modelName = Optional.ofNullable(uploadInfo.getMetadata().get("modelName"))
			.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));
		String description = Optional.ofNullable(uploadInfo.getMetadata().get("description"))
			.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));
		String workspaceResourceName = Optional.ofNullable(uploadInfo.getMetadata().get("workspaceResourceName"))
			.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));
		String modelPath = Optional.ofNullable(uploadInfo.getMetadata().get("modelPath"))
			.orElseThrow(() -> new RestApiException(TusErrorCode.FILE_NAME_ERROR_MESSAGE));
		if(uploadInfo.getMetadata().get("labelsIds") != null){
			labelsIds = getLabels(uploadInfo.getMetadata().get("labelsIds"));
		}

		return ModelRepoDTO.RequestDTO.builder()
			.modelName(modelName)
			.description(description)
			.workspaceResourceName(workspaceResourceName)
			.labelIds(labelsIds)
			.storageId(storageId)
			.modelPath(modelPath)
			.build();
	}

	// TODO 삭제 예정
	/*private void saveModel(HttpServletRequest request, UploadInfo uploadInfo, String filename) throws
		IOException, TusException {
		Long modelId = Optional.ofNullable(uploadInfo.getMetadata().get("modelId"))
			.map(Long::valueOf)
			.orElseThrow(() -> new RestApiException(TusErrorCode.UPLOAD_TYPE_ERROR_MESSAGE));
		Model findModel = volumeRepository.findById(modelId)
			.orElseThrow(() -> new RestApiException(ModelErrorCode.MODEL_NOT_FOUND));

		// 파일 저장
		Long fileSize = getFilePath(request, uploadInfo.getMetadata().get("filePath"), filename);
		findModel.setModelSize(fileSize);
	}

	private void saveDataset(HttpServletRequest request, UploadInfo uploadInfo, String filename) throws
		IOException, TusException {
		Long datasetId = Optional.ofNullable(uploadInfo.getMetadata().get("datasetId"))
			.map(Long::valueOf)
			.orElseThrow(() -> new RestApiException(TusErrorCode.UPLOAD_TYPE_ERROR_MESSAGE));
		Dataset findDataset = datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));

		// 파일 저장
		Long fileSize = getFilePath(request, uploadInfo.getMetadata().get("filePath"), filename);
		findDataset.setDatasetSize(fileSize);
	}*/

	private void saveVolume(HttpServletRequest request, UploadInfo uploadInfo, String filename) throws
		IOException, TusException {
		Long volumeId = Optional.ofNullable(uploadInfo.getMetadata().get("volumeId"))
			.map(Long::valueOf)
			.orElseThrow(() -> new RestApiException(TusErrorCode.UPLOAD_TYPE_ERROR_MESSAGE));
		Volume findVolume = volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));

		// 파일 저장
		Long fileSize = getFilePath(request, uploadInfo.getMetadata().get("filePath"), filename);
		findVolume.setVolumeSize(fileSize);
	}

	private Long getFilePath(HttpServletRequest request, String filePath, String filename) throws
		IOException,
		TusException {
		return CoreFileUtils.saveInputStreamToFile(filePath,
			filename, tusFileUploadService.getUploadedBytes(request.getRequestURI()));
	}

	private List<Long> getLabels(String labelIds) {
		return Arrays.stream(labelIds.split(","))
			.map(String::trim)
			.map(Long::parseLong)
			.toList();
	}
}
