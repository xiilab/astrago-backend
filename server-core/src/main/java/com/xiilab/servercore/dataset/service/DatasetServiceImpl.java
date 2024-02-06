package com.xiilab.servercore.dataset.service;

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
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;
import com.xiilab.servercore.dataset.repository.DatasetRepository;
import com.xiilab.servercore.dataset.repository.DatasetWorkspaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DatasetServiceImpl implements DatasetService {

	private final DatasetRepository datasetRepository;
	private final DatasetWorkspaceRepository datasetWorkspaceRepository;

	@Override
	@Transactional
	public void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = astragoDatasetEntity.getStorageEntity().getHostPath();
		String datasetPath = storageRootPath + "/" + astragoDatasetEntity.getDatasetName().replace(" ", "");
		//dataset 저장
		astragoDatasetEntity.setDatasetPath(datasetPath);
		datasetRepository.save(astragoDatasetEntity);
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(datasetPath);
		try {
			Files.createDirectories(uploadPath);
			// 업로드된 각 파일에 대해 작업 수행
			for (MultipartFile file : files) {
				Path targetPath = uploadPath.resolve(file.getOriginalFilename());
				Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}

	@Override
	public DatasetDTO.ResDatasets getDatasets(int pageNo, int pageSize, UserInfoDTO userInfoDTO) {
		PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
		Page<Dataset> datasets = datasetRepository.findByAuthority(pageRequest, userInfoDTO);
		List<Dataset> entities = datasets.getContent();
		long totalCount = datasets.getTotalElements();

		return DatasetDTO.ResDatasets.entitiesToDtos(entities, totalCount);
	}

	@Override
	public DatasetDTO.ResDatasetWithStorage getDatasetWithStorage(Long datasetId) {
		Dataset datasetWithStorage = datasetRepository.getDatasetWithStorage(datasetId);
		return DatasetDTO.ResDatasetWithStorage.toDto(datasetWithStorage);
	}

	@Override
	@Transactional
	public void insertLocalDataset(LocalDatasetEntity localDatasetEntity) {
		datasetRepository.save(localDatasetEntity);
	}

	@Override
	public Dataset findById(Long datasetId) {
		Dataset dataset = datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		return dataset;
	}

	@Override
	@Transactional
	public void modifyDataset(DatasetDTO.ModifyDatset modifyDataset, Long datasetId) {
		Dataset dataset = datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		dataset.modifyDatasetName(modifyDataset.getDatasetName());
	}

	@Override
	@Transactional
	public void deleteDatasetById(Long datasetId) {
		datasetRepository.deleteById(datasetId);
	}

	@Override
	@Transactional
	public void deleteDatasetWorkspaceMappingById(Long datasetId) {
		datasetWorkspaceRepository.deleteByDatasetId(datasetId);
	}

	@Override
	public DirectoryDTO getDatasetFiles(DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		String targetPath = reqFilePathDTO.getPath();
		return CoreFileUtils.getFileList(targetPath);
	}

	@Override
	public void astragoDatasetUploadFile(String path, List<MultipartFile> files) {
		CoreFileUtils.datasetUploadFiles(path, files);
	}

	@Override
	public void astragoDatasetDeleteFiles(DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		String targetPath = reqFilePathDTO.getPath();
		CoreFileUtils.deleteFileOrDirectory(targetPath);
	}

	@Override
	public DownloadFileResDTO astragoDatasetDownloadFile(DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		Path targetPath = Path.of(reqFilePathDTO.getPath());
		// 파일이 존재하는지 확인
		if (Files.exists(targetPath)) {
			String fileName = CoreFileUtils.getFileName(reqFilePathDTO.getPath());
			// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
			byte[] fileContent;
			try {
				fileContent = Files.readAllBytes(targetPath);
			} catch (IOException e) {
				throw new RestApiException(CommonErrorCode.FILE_DOWNLOAD_FAIL);
			}
			ByteArrayResource resource = new ByteArrayResource(fileContent);
			MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);

			return DownloadFileResDTO.builder()
				.byteArrayResource(resource)
				.fileName(fileName)
				.mediaType(mediaType)
				.build();
		}else{
			throw new RestApiException(CommonErrorCode.FILE_NOT_FOUND);
		}
	}
}
