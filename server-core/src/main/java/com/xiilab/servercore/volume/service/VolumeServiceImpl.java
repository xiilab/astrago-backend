package com.xiilab.servercore.volume.service;

import java.io.File;
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
import com.xiilab.modulecommon.exception.errorcode.VolumeErrorCode;
import com.xiilab.modulecommon.util.CompressUtils;
import com.xiilab.modulecommon.util.DecompressUtils;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.volume.entity.VolumeWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.volume.repository.VolumeRepository;
import com.xiilab.modulek8sdb.volume.repository.VolumeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.volume.repository.VolumeWorkspaceRepository;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceVolumeDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceDatasetDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.volume.dto.VolumeReqDTO;
import com.xiilab.servercore.volume.dto.VolumeResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class VolumeServiceImpl implements VolumeService {
	private final VolumeRepository volumeRepository;
	private final VolumeWorkspaceRepository volumeWorkspaceRepository;
	private final VolumeWorkLoadMappingRepository volumeWorkLoadMappingRepository;

	@Override
	@Transactional
	public void insertAstragoVolume(AstragoVolumeEntity astragoVolumeEntity, List<MultipartFile> files) {
		//파일 업로드
		String storageRootPath = astragoVolumeEntity.getStorageEntity().getHostPath();
		String saveDirectoryName =
			astragoVolumeEntity.getVolumeName().replace(" ", "") + "-" + UUID.randomUUID().toString().substring(6);
		String volumePath = storageRootPath + File.separator + saveDirectoryName;
		long size = 0;
		// 업로드된 파일을 저장할 경로 설정
		Path uploadPath = Paths.get(volumePath);
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
			astragoVolumeEntity.setVolumeSize(size);
			astragoVolumeEntity.setVolumePath(volumePath);
			astragoVolumeEntity.setSaveDirectoryName(saveDirectoryName);
			volumeRepository.save(astragoVolumeEntity);
		} catch (IOException e) {
			throw new RestApiException(CommonErrorCode.FILE_UPLOAD_FAIL);
		}
	}

	@Override
	public VolumeResDTO.ResVolumes getVolumes(PageInfo pageInfo, RepositorySearchCondition repositorySearchCondition,
		UserDTO.UserInfo userInfoDTO, PageMode pageMode) {
		PageRequest pageRequest = PageRequest.of(pageInfo.getPageNo() - 1, pageInfo.getPageSize());
		Page<Volume> volumes = volumeRepository.findByAuthorityWithPaging(pageRequest, userInfoDTO.getId(),
			userInfoDTO.getAuth(), repositorySearchCondition, pageMode);
		List<Volume> entities = volumes.getContent();
		long totalCount = volumes.getTotalElements();

		return VolumeResDTO.ResVolumes.entitiesToDtos(entities, totalCount);
	}

	@Override
	public VolumeResDTO.ResVolumeWithStorage getVolumeWithStorage(Long volumeId) {
		Volume volumeWithStorage = volumeRepository.getVolumeWithStorage(volumeId);
		if (volumeWithStorage == null) {
			throw new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND);
		}
		return VolumeResDTO.ResVolumeWithStorage.toDto(volumeWithStorage);
	}

	@Override
	@Transactional
	public void insertLocalVolume(LocalVolumeEntity localVolumeEntity) {
		volumeRepository.save(localVolumeEntity);
	}

	@Override
	public Volume findById(Long volumeId) {
		return volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
	}

	@Override
	@Transactional
	public void modifyVolume(VolumeReqDTO.Edit.ModifyVolume modifyVolumeDTO, Long volumeId) {
		Volume volume = volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		volume.modifyVolumeName(modifyVolumeDTO.getVolumeName());
		volume.modifyVolumeDefaultPath(modifyVolumeDTO.getDefaultPath());
		volume.modifyVolumeAccessType(modifyVolumeDTO.getVolumeAccessType());
	}

	@Override
	@Transactional
	public void deleteVolumeById(Long volumeId) {
		volumeRepository.deleteById(volumeId);
	}

	@Override
	@Transactional
	public void deleteVolumeWorkspaceMappingById(Long volumeId) {
		volumeWorkspaceRepository.deleteByVolumeId(volumeId);
	}

	@Override
	public DirectoryDTO getAstragoVolumeFiles(Long volumeId, String filePath) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		return CoreFileUtils.getAstragoFiles(filePath);
	}

	@Override
	public void astragoVolumeUploadFile(Long volumeId, String path, List<MultipartFile> files) {
		AstragoVolumeEntity astragoVolumeEntity = (AstragoVolumeEntity)volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		long size = CoreFileUtils.datasetUploadFiles(path, files);
		astragoVolumeEntity.setVolumeSize(size);
	}

	@Override
	public void astragoVolumeDeleteFiles(Long volumeId, VolumeReqDTO.FilePaths reqFilePathDTO) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		String[] targetPaths = reqFilePathDTO.getPaths();
		for (String targetPath : targetPaths) {
			CoreFileUtils.deleteFileOrDirectory(targetPath);
		}
	}

	@Override
	@Transactional
	public DownloadFileResDTO downloadAstragoVolumeFile(Long volumeId, String filePath) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
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
					throw new RestApiException(VolumeErrorCode.VOLUME_ZIP_DOWNLOAD_FAIL);
				}
			} else {
				String fileName = CoreFileUtils.getFileName(filePath);
				// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
				byte[] fileContent;
				try {
					fileContent = Files.readAllBytes(targetPath);
				} catch (IOException e) {
					log.error("io exception: " + e);
					throw new RestApiException(VolumeErrorCode.VOLUME_FILE_DOWNLOAD_FAIL);
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
	public void compressAstragoVolumeFiles(Long volumeId, VolumeReqDTO.Compress compress) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		List<Path> pathList = compress.getFilePaths().stream().map(Path::of).toList();
		CompressUtils.saveCompressFile(pathList, null, compress.getCompressFileType());
	}

	@Override
	public void deCompressAstragoVolumeFile(Long volumeId, String filePath) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		DecompressUtils.saveDecompressFile(Path.of(filePath), null);
	}

	@Override
	@Transactional
	public void astragoVolumeCreateDirectory(Long volumeId, VolumeReqDTO.FilePath filePath) {
		volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		Path dirPath = Path.of(filePath.getPath());
		// 디렉토리가 존재하지 않으면 생성
		if (!Files.exists(dirPath)) {
			try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				throw new RestApiException(VolumeErrorCode.VOLUME_DIRECTORY_CREATE_FAIL);
			}
		} else {
			throw new RestApiException(VolumeErrorCode.VOLUME_DIRECTORY_CREATE_ALREADY);
		}
	}

	@Override
	public VolumeResDTO.VolumesInWorkspace getVolumeByRepositoryType(String workspaceResourceName,
		RepositoryType repositoryType, UserDTO.UserInfo userInfoDTO) {
		if (repositoryType == RepositoryType.WORKSPACE) {
			List<VolumeWorkSpaceMappingEntity> datasets = volumeWorkspaceRepository.findByWorkspaceResourceName(
				workspaceResourceName);
			if (datasets != null || datasets.size() != 0) {
				return VolumeResDTO.VolumesInWorkspace.mappingEntitiesToDtos(datasets);
			}
		} else {
			List<Volume> datasetsByAuthority = volumeRepository.findByAuthority(userInfoDTO.getId(),
				userInfoDTO.getAuth());
			return VolumeResDTO.VolumesInWorkspace.entitiesToDtos(datasetsByAuthority);
		}

		return null;
	}

	@Override
	public void insertWorkspaceVolume(InsertWorkspaceVolumeDTO insertWorkspaceVolumeDTO) {
		String workspaceResourceName = insertWorkspaceVolumeDTO.getWorkspaceResourceName();
		Long volumeId = insertWorkspaceVolumeDTO.getVolumeId();

		VolumeWorkSpaceMappingEntity workSpaceMappingEntity = volumeWorkspaceRepository.findByWorkspaceResourceNameAndVolumeId(workspaceResourceName, volumeId);
		if (workSpaceMappingEntity != null) {
			throw new RestApiException(VolumeErrorCode.VOLUME_WORKSPACE_MAPPING_ALREADY);
		}

		Volume volume = volumeRepository.findById(volumeId)
			.orElseThrow(() -> new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND));
		//datasetWorkspaceMappingEntity 생성 및 dataset entity 추가
		VolumeWorkSpaceMappingEntity volumeWorkSpaceMappingEntity = VolumeWorkSpaceMappingEntity.builder()
			.workspaceResourceName(workspaceResourceName)
			.volume(volume)
			.volumeDefaultMountPath(insertWorkspaceVolumeDTO.getDefaultPath())
			.build();

		volumeWorkspaceRepository.save(volumeWorkSpaceMappingEntity);
	}

	@Override
	public void deleteWorkspaceDataset(String workspaceResourceName, Long volumeId, UserDTO.UserInfo userInfoDTO) {
		VolumeWorkSpaceMappingEntity volumeWorkSpaceMappingEntity = volumeWorkspaceRepository.findByWorkspaceResourceNameAndVolumeId(
			workspaceResourceName, volumeId);
		if (volumeWorkSpaceMappingEntity == null) {
			throw new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND);
		}
		//owner or 본인 체크
		if (!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(volumeWorkSpaceMappingEntity.getRegUser()
			.getRegUserId()
			.equalsIgnoreCase(userInfoDTO.getId()))) {
			throw new RestApiException(VolumeErrorCode.VOLUME_DELETE_FORBIDDEN);
		}

		volumeWorkspaceRepository.deleteByVolumeIdAndWorkspaceResourceName(volumeId, workspaceResourceName);
	}

	@Override
	public VolumeResDTO.VolumesInWorkspace getVolumesByWorkspaceResourceName(String workspaceResourceName) {
		List<VolumeWorkSpaceMappingEntity> volumes = volumeWorkspaceRepository.findByWorkspaceResourceName(
			workspaceResourceName);
		if (volumes != null || volumes.size() != 0) {
			return VolumeResDTO.VolumesInWorkspace.mappingEntitiesToDtos(volumes);
		}
		return null;
	}

	@Override
	@Transactional
	public void deleteVolumeWorkloadMapping(Long jobId) {
		volumeWorkLoadMappingRepository.deleteByWorkloadId(jobId);
	}

	@Override
	@Transactional
	public void updateWorkspaceVolume(UpdateWorkspaceDatasetDTO updateWorkspaceDatasetDTO, String workspaceResourceName,
		Long volumeId, UserDTO.UserInfo userInfoDTO) {
		VolumeWorkSpaceMappingEntity workSpaceMappingEntity = volumeWorkspaceRepository.findByWorkspaceResourceNameAndVolumeId(
			workspaceResourceName, volumeId);
		if (workSpaceMappingEntity == null) {
			throw new RestApiException(VolumeErrorCode.VOLUME_NOT_FOUND);
		}
		//owner or 본인 체크
		if (!(userInfoDTO.isMyWorkspace(workspaceResourceName)) && !(workSpaceMappingEntity.getRegUser()
			.getRegUserId()
			.equalsIgnoreCase(userInfoDTO.getId()))) {
			throw new RestApiException(VolumeErrorCode.VOLUME_FIX_FORBIDDEN);
		}
		workSpaceMappingEntity.modifyDefaultPath(updateWorkspaceDatasetDTO.getDefaultPath());
	}

	public void deleteVolumeWorkloadMappingById(Long volumeId) {
		volumeWorkLoadMappingRepository.deleteByVolumeId(volumeId);
	}
}
