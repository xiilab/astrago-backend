package com.xiilab.servercore.dataset.service;

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

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulecommon.util.CompressUtils;
import com.xiilab.modulecommon.util.DecompressUtils;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkspaceRepository;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceDatasetDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceDatasetDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DatasetServiceImpl implements DatasetService {

	private final DatasetRepository datasetRepository;
	private final DatasetWorkspaceRepository datasetWorkspaceRepository;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;

	@Override
	@Transactional
	public void insertAstragoDataset(AstragoDatasetEntity astragoDatasetEntity, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = astragoDatasetEntity.getStorageEntity().getHostPath();
		String saveDirectoryName =
			astragoDatasetEntity.getDatasetName().replace(" ", "") + "-" + UUID.randomUUID().toString().substring(6);
		String datasetPath = storageRootPath + "/" + saveDirectoryName;
		long size = 0;
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(datasetPath);
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
			//dataset 저장
			astragoDatasetEntity.setDatasetSize(size);
			astragoDatasetEntity.setDatasetPath(datasetPath);
			astragoDatasetEntity.setSaveDirectoryName(saveDirectoryName);
			datasetRepository.save(astragoDatasetEntity);
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}

	@Override
	public DatasetDTO.ResDatasets getDatasets(PageInfo pageInfo, RepositorySearchCondition repositorySearchCondition,
		UserDTO.UserInfo userInfoDTO, PageMode pageMode) {
		PageRequest pageRequest = PageRequest.of(pageInfo.getPageNo() - 1, pageInfo.getPageSize());
		Page<Dataset> datasets = datasetRepository.findByAuthorityWithPaging(pageRequest, userInfoDTO.getId(),
			userInfoDTO.getAuth(), repositorySearchCondition, pageMode);
		List<Dataset> entities = datasets.getContent();
		long totalCount = datasets.getTotalElements();

		return DatasetDTO.ResDatasets.entitiesToDtos(entities, totalCount);
	}

	@Override
	public DatasetDTO.ResDatasetWithStorage getDatasetWithStorage(Long datasetId) {
		Dataset datasetWithStorage = datasetRepository.getDatasetWithStorage(datasetId);
		if (datasetWithStorage == null) {
			throw new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND);
		}
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
		dataset.modifyDatasetDefaultPath(modifyDataset.getDefaultPath());
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
	public DirectoryDTO getAstragoDatasetFiles(Long datasetId, String filePath) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		return CoreFileUtils.getAstragoFiles(filePath);
	}

	@Override
	@Transactional
	public void astragoDatasetUploadFile(Long datasetId, String path, List<MultipartFile> files) {
		AstragoDatasetEntity dataset = (AstragoDatasetEntity)datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		long size = CoreFileUtils.datasetUploadFiles(path, files);
		dataset.setDatasetSize(size);
	}

	@Override
	public void astragoDatasetDeleteFiles(Long datasetId, DatasetDTO.ReqFilePathsDTO reqFilePathsDTO) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		String[] targetPaths = reqFilePathsDTO.getPaths();
		for (String targetPath : targetPaths) {
			CoreFileUtils.deleteFileOrDirectory(targetPath);
		}
	}

	@Override
	@Transactional
	public DownloadFileResDTO downloadAstragoDatasetFile(Long datasetId, String filePath) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
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
					log.error("io exception: " + e);
					throw new RestApiException(DatasetErrorCode.DATASET_ZIP_DOWNLOAD_FAIL);
				}
			} else {
				String fileName = CoreFileUtils.getFileName(filePath);
				// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
				byte[] fileContent;
				try {
					fileContent = Files.readAllBytes(targetPath);
				} catch (IOException e) {
					log.error("io exception: " + e);
					throw new RestApiException(DatasetErrorCode.DATASET_FILE_DOWNLOAD_FAIL);
				}
				ByteArrayResource resource = new ByteArrayResource(fileContent);
				MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);
				return DownloadFileResDTO.builder()
					.byteArrayResource(resource)
					.fileName(fileName)
					.mediaType(mediaType)
					.build();
			}
		} else {
			log.error("path :" + targetPath);
			throw new RestApiException(CommonErrorCode.FILE_NOT_FOUND);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public void compressAstragoDatasetFiles(Long datasetId, DatasetDTO.ReqCompressDTO reqCompressDTO) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		List<Path> pathList = reqCompressDTO.getFilePaths().stream().map(Path::of).toList();
		CompressUtils.saveCompressFile(pathList, null, reqCompressDTO.getCompressFileType());
	}

	@Override
	@Transactional(readOnly = true)
	public void deCompressAstragoDatasetFile(Long datasetId, String filePath) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		DecompressUtils.saveDecompressFile(Path.of(filePath), null);
	}

	@Override
	@Transactional
	public void astragoDatasetCreateDirectory(Long datasetId, DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		Path dirPath = Path.of(reqFilePathDTO.getPath());
		// 디렉토리가 존재하지 않으면 생성
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				throw new RestApiException(DatasetErrorCode.DATASET_DIRECTORY_CREATE_FAIL);
			}
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_DIRECTORY_CREATE_ALREADY);
		}
	}

	@Override
	public DatasetDTO.DatasetsInWorkspace getDatasetsByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO) {
		if (repositoryType == RepositoryType.WORKSPACE) {
			List<DatasetWorkSpaceMappingEntity> datasets = datasetWorkspaceRepository.findByWorkspaceResourceName(
				workspaceResourceName);
			if (datasets != null || datasets.size() != 0) {
				return DatasetDTO.DatasetsInWorkspace.mappingEntitiesToDtos(datasets);
			}
		} else {
			List<Dataset> datasetsByAuthority = datasetRepository.findByAuthority(userInfoDTO.getId(),
				userInfoDTO.getAuth());
			return DatasetDTO.DatasetsInWorkspace.entitiesToDtos(datasetsByAuthority);
		}

		return null;
	}

	@Override
	@Transactional
	public void insertWorkspaceDataset(InsertWorkspaceDatasetDTO insertWorkspaceDatasetDTO) {
		String workspaceResourceName = insertWorkspaceDatasetDTO.getWorkspaceResourceName();
		Long datasetId = insertWorkspaceDatasetDTO.getDatasetId();

		DatasetWorkSpaceMappingEntity workSpaceMappingEntity = datasetWorkspaceRepository.findByWorkspaceResourceNameAndDatasetId(
			workspaceResourceName, datasetId);
		if (workSpaceMappingEntity != null) {
			throw new RestApiException(DatasetErrorCode.DATASET_WORKSPACE_MAPPING_ALREADY);
		}

		//dataset entity 조회
		Dataset dataset = datasetRepository.findById(datasetId)
			.orElseThrow(() -> new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND));
		//datasetWorkspaceMappingEntity 생성 및 dataset entity 추가
		DatasetWorkSpaceMappingEntity datasetWorkSpaceMappingEntity = DatasetWorkSpaceMappingEntity.builder()
			.workspaceResourceName(workspaceResourceName)
			.dataset(dataset)
			.datasetDefaultMountPath(insertWorkspaceDatasetDTO.getDefaultPath())
			.build();

		datasetWorkspaceRepository.save(datasetWorkSpaceMappingEntity);
	}

	@Override
	@Transactional
	public void deleteWorkspaceDataset(String workspaceResourceName, Long datasetId, UserDTO.UserInfo userInfoDTO) {
		DatasetWorkSpaceMappingEntity workSpaceMappingEntity = datasetWorkspaceRepository.findByWorkspaceResourceNameAndDatasetId(
			workspaceResourceName, datasetId);
		if (workSpaceMappingEntity == null) {
			throw new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND);
		}
		//owner or 본인 체크
		if (!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(workSpaceMappingEntity.getRegUser()
			.getRegUserId()
			.equalsIgnoreCase(userInfoDTO.getId()))) {
			throw new RestApiException(DatasetErrorCode.DATASET_DELETE_FORBIDDEN);
		}
		datasetWorkspaceRepository.deleteByDatasetIdAndWorkspaceResourceName(datasetId, workspaceResourceName);
	}

	@Override
	public DatasetDTO.DatasetsInWorkspace getDatasetsByWorkspaceResourceName(String workspaceResourceName) {
		List<DatasetWorkSpaceMappingEntity> datasets = datasetWorkspaceRepository.findByWorkspaceResourceName(
			workspaceResourceName);
		if (datasets != null || datasets.size() != 0) {
			return DatasetDTO.DatasetsInWorkspace.mappingEntitiesToDtos(datasets);
		}
		return null;
	}

	@Override
	@Transactional
	public void deleteDatasetWorkloadMapping(Long jobId) {
		datasetWorkLoadMappingRepository.deleteByWorkloadId(jobId);
	}

	@Override
	@Transactional
	public void updateWorkspaceDataset(UpdateWorkspaceDatasetDTO updateWorkspaceDatasetDTO,
		String workspaceResourceName, Long datasetId, UserDTO.UserInfo userInfoDTO) {
		DatasetWorkSpaceMappingEntity workSpaceMappingEntity = datasetWorkspaceRepository.findByWorkspaceResourceNameAndDatasetId(
			workspaceResourceName, datasetId);
		if (workSpaceMappingEntity == null) {
			throw new RestApiException(DatasetErrorCode.DATASET_NOT_FOUND);
		}
		//owner or 본인 체크
		if (!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(workSpaceMappingEntity.getRegUser()
			.getRegUserId()
			.equalsIgnoreCase(userInfoDTO.getId()))) {
			throw new RestApiException(DatasetErrorCode.DATASET_FIX_FORBIDDEN);
		}
		workSpaceMappingEntity.modifyDefaultPath(updateWorkspaceDatasetDTO.getDefaultPath());
	}

	public void deleteDatasetWorkloadMappingById(Long datasetId) {
		datasetWorkLoadMappingRepository.deleteByDatasetId(datasetId);
	}

}
