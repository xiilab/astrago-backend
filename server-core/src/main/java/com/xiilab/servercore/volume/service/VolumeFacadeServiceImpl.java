package com.xiilab.servercore.volume.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.FileType;
import com.xiilab.modulecommon.enums.OutputVolumeYN;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DatasetErrorCode;
import com.xiilab.modulecommon.exception.errorcode.VolumeErrorCode;
import com.xiilab.modulek8s.facade.dto.CreateLocalVolumeDTO;
import com.xiilab.modulek8s.facade.dto.CreateLocalVolumeResDTO;
import com.xiilab.modulek8s.facade.dto.DeleteLocalVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceVolumeDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.dto.NginxFilesDTO;
import com.xiilab.servercore.dataset.service.WebClientService;
import com.xiilab.servercore.storage.service.StorageService;
import com.xiilab.servercore.volume.dto.VolumeReqDTO;
import com.xiilab.servercore.volume.dto.VolumeResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VolumeFacadeServiceImpl implements VolumeFacadeService {
	private final VolumeService volumeService;
	private final StorageService storageService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final WebClientService webClientService;
	private final NetworkRepository networkRepository;
	@Value("${astrago.namespace}")
	private String namespace;
	@Value("${astrago.dataset.dockerImage.name}")
	private String dockerImage;
	@Value("${astrago.dataset.dockerImage.hostPath}")
	private String hostPath;
	@Value("${astrago.private-registry-url}")
	private String privateRegistryUrl;

	private static boolean checkAccessVolume(UserDTO.UserInfo userInfoDTO, Volume volume) {
		return userInfoDTO.getAuth() == AuthType.ROLE_ADMIN ||
			(userInfoDTO.getAuth() == AuthType.ROLE_USER && userInfoDTO.getId()
				.equals(volume.getRegUser().getRegUserId()));
	}

	@Override
	@Transactional
	public void insertAstragoVolume(VolumeReqDTO.Edit.CreateAstragoVolume createAstragoVolumeDTO,
		List<MultipartFile> files) {
		// storage 조회
		StorageEntity storageEntity = storageService.findById(createAstragoVolumeDTO.getStorageId());

		// Volume 저장
		AstragoVolumeEntity astragoVolume = AstragoVolumeEntity.builder()
			.volumeName(createAstragoVolumeDTO.getVolumeName())
			.storageEntity(storageEntity)
			.defaultPath(createAstragoVolumeDTO.getDefaultPath())
			.outputVolumeYN(OutputVolumeYN.N)
			.build();

		Long saveVolumeId = volumeService.insertAstragoVolume(astragoVolume, files);

		// workspace
		saveWorkspaceVolumeMapping(createAstragoVolumeDTO.getWorkspaceResourceName(), saveVolumeId,
			createAstragoVolumeDTO.getDefaultPath(), createAstragoVolumeDTO.getLabelIds());
	}

	@Override
	@Transactional
	public Long insertAstragoOutputVolume(String volumeName, String workspaceResourceName, String workloadResourceName,
		String defaultPath) {
		// storage 조회
		StorageEntity defaultStorage = storageService.getDefaultStorage();

		// Volume 저장
		AstragoVolumeEntity astragoVolume = AstragoVolumeEntity.builder()
			.volumeName(volumeName)
			.storageEntity(defaultStorage)
			.defaultPath(defaultPath)
			.outputVolumeYN(OutputVolumeYN.Y)
			.build();

		volumeService.insertAstragoOutputVolume(astragoVolume, volumeName, workspaceResourceName, workloadResourceName);
		return astragoVolume.getVolumeId();
	}

	@Override
	public VolumeResDTO.ResVolumeWithStorage getVolume(Long volumeId) {
		VolumeResDTO.ResVolumeWithStorage volumeWithStorage = volumeService.getVolumeWithStorage(volumeId);
		return volumeWithStorage;
	}

	@Override
	@Transactional
	public void insertLocalVolume(VolumeReqDTO.Edit.CreateLocalVolume createLocalVolumeDTO) {
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		log.info("폐쇄망 : " + network.getNetworkCloseYN());
		String volumeImageURL = "";
		if(network.getNetworkCloseYN() == NetworkCloseYN.Y){
			if(isBlankSafe(privateRegistryUrl)){
				volumeImageURL = network.getLocalVolumeImageUrl();
			}else{
				volumeImageURL = privateRegistryUrl + "/" + network.getLocalVolumeImageUrl();
			}
		}else{
			volumeImageURL = network.getLocalVolumeImageUrl();
		}
		CreateLocalVolumeDTO createDto = CreateLocalVolumeDTO.builder()
			.namespace(namespace)
			.volumeName(createLocalVolumeDTO.getVolumeName())
			.ip(createLocalVolumeDTO.getIp())
			.storagePath(createLocalVolumeDTO.getStoragePath())
			.dockerImage(volumeImageURL)
			.hostPath(hostPath)
			.build();
		//1. nginx deployment, pvc, pv, svc 생성
		CreateLocalVolumeResDTO createLocalVolumeResDTO = workloadModuleFacadeService.createLocalVolume(createDto);
		// workloadModuleFacadeService.createLo
		//2. 디비 인서트
		LocalVolumeEntity localVolumeEntity = LocalVolumeEntity.builder()
			.volumeName(createLocalVolumeDTO.getVolumeName())
			.outputVolumeYN(OutputVolumeYN.N)
			.ip(createLocalVolumeDTO.getIp())
			.storageType(StorageType.NFS)
			.storagePath(createLocalVolumeDTO.getStoragePath())
			.dns(createLocalVolumeResDTO.getDns())
			.deploymentName(createLocalVolumeResDTO.getDeploymentName())
			.pvcName(createLocalVolumeResDTO.getPvcName())
			.pvName(createLocalVolumeResDTO.getPvName())
			.svcName(createLocalVolumeResDTO.getSvcName())
			.defaultPath(createLocalVolumeDTO.getDefaultPath())
			.build();
		localVolumeEntity.setVolumeSize(50L);
		Long saveVolumeId = volumeService.insertLocalVolume(localVolumeEntity);

		// workspace
		saveWorkspaceVolumeMapping(createLocalVolumeDTO.getWorkspaceResourceName(), saveVolumeId,
			createLocalVolumeDTO.getDefaultPath(), createLocalVolumeDTO.getLabelIds());
	}

	private void saveWorkspaceVolumeMapping(String workspaceResourceName, Long saveVolumeId,
		String defaultPath, Set<Long> labelIds) {
		if (StringUtils.hasText(workspaceResourceName)) {
			InsertWorkspaceVolumeDTO insertWorkspaceVolumeDTO = InsertWorkspaceVolumeDTO.builder()
				.volumeId(saveVolumeId)
				.workspaceResourceName(workspaceResourceName)
				.defaultPath(defaultPath)
				.labelIds(labelIds)
				.build();
			volumeService.insertWorkspaceVolume(insertWorkspaceVolumeDTO);
		}
	}

	@Override
	@Transactional
	public void modifyVolume(VolumeReqDTO.Edit.ModifyVolume modifyVolumeDTO, Long volumeId,
		UserDTO.UserInfo userInfoDTO) {
		Volume volume = volumeService.findById(volumeId);

		if (checkAccessVolume(userInfoDTO, volume)) {
			//local 데이터 셋이면 디비 + deployment label 변경
			if (volume.isLocalVolume()) {
				// LocalDatasetEntity localDatasetEntity = (LocalDatasetEntity)dataset;
				LocalVolumeEntity localVolumeEntity = (LocalVolumeEntity)volume;
				ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO = ModifyLocalDatasetDeploymentDTO
					.builder()
					.deploymentName(localVolumeEntity.getDeploymentName())
					.modifyDatasetName(localVolumeEntity.getVolumeName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.modifyLocalDatasetDeployment(modifyLocalDatasetDeploymentDTO);
			}
			volumeService.modifyVolume(modifyVolumeDTO, volumeId);
		} else {
			throw new RestApiException(VolumeErrorCode.VOLUME_FIX_FORBIDDEN);
		}
	}

	@Override
	public void deleteVolume(Long volumeId, UserDTO.UserInfo userInfoDTO) {
		Volume volume = volumeService.findById(volumeId);
		if (checkAccessVolume(userInfoDTO, volume)) {
			boolean isUse = workloadModuleFacadeService.isUsedVolume(volumeId);
			//true = 사용중인 데이터 셋
			if (isUse) {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_DELETE_IN_USE);
			}
			//astrago 데이터 셋은 db 삭제(astragodataset, workspacedatasetmapping
			if (volume.isAstragoVolume()) {
				//workspace mapping 삭제
				// datasetService.deleteDatasetWorkspaceMappingById(datasetId);
				volumeService.deleteVolumeWorkspaceMappingById(volumeId);
				//workload mapping 삭제
				// datasetService.deleteDatasetWorkloadMappingById(datasetId);
				volumeService.deleteVolumeWorkloadMapping(volumeId);
				//dataset 삭제
				// datasetService.deletVolumeById(datasetId);
				volumeService.deleteVolumeById(volumeId);
			} else if (volume.isLocalVolume()) {
				//pv, pvc, deployment, svc 삭제
				LocalVolumeEntity localVolumeEntity = (LocalVolumeEntity)volume;
				DeleteLocalVolumeDTO deleteLocalVolumeDTO = DeleteLocalVolumeDTO.builder()
					.deploymentName(localVolumeEntity.getDeploymentName())
					.svcName(localVolumeEntity.getSvcName())
					.pvcName(localVolumeEntity.getPvcName())
					.pvName(localVolumeEntity.getPvName())
					.namespace(namespace)
					.build();
				workloadModuleFacadeService.deleteLocalVolume(deleteLocalVolumeDTO);
				//workspace mapping 삭제
				volumeService.deleteVolumeWorkspaceMappingById(volumeId);
				//workload mapping 삭제
				volumeService.deleteVolumeWorkloadMapping(volumeId);
				//db 삭제 - TB_localDataset
				volumeService.deleteVolumeById(volumeId);
			}
		} else {
			throw new RestApiException(VolumeErrorCode.VOLUME_FIX_FORBIDDEN);
		}
	}

	@Override
	public DirectoryDTO getLocalVolumeFiles(Long volumeId, String filePath) {
		List<DirectoryDTO.ChildrenDTO> fileList = new ArrayList<>();
		LocalVolumeEntity volume = (LocalVolumeEntity)volumeService.findById(volumeId);
		String httpUrl = volume.getDns() + filePath;
		List<NginxFilesDTO> files = webClientService.getObjectsFromUrl(httpUrl, NginxFilesDTO.class);
		int directoryCnt = 0;
		int fileCnt = 0;
		for (NginxFilesDTO file : files) {
			DirectoryDTO.ChildrenDTO children = DirectoryDTO.ChildrenDTO
				.builder()
				.name(file.getName())
				.type(file.getFileType())
				.size(CoreFileUtils.formatFileSize(file.getSize() == null ? 0 : Long.parseLong(file.getSize())))
				.path(FileType.D == file.getFileType() ? filePath + file.getName() + File.separator :
					filePath + file.getName())
				.build();
			if (file.getFileType() == FileType.D) {
				directoryCnt += 1;
			} else {
				fileCnt += 1;
			}
			fileList.add(children);
		}
		return DirectoryDTO.builder().children(fileList).directoryCnt(directoryCnt).fileCnt(fileCnt).build();
	}

	@Override
	public DownloadFileResDTO downloadLocalVolumeFile(Long volumeId, String filePath) {
		LocalVolumeEntity localVolumeEntity = (LocalVolumeEntity)volumeService.findById(volumeId);
		String httpUrl = localVolumeEntity.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		MediaType contentType = fileInfo.getContentType();
		byte[] fileContent = webClientService.downloadFile(httpUrl, contentType);
		ByteArrayResource resource = new ByteArrayResource(fileContent);
		String fileName = webClientService.retrieveFileName(httpUrl);

		return DownloadFileResDTO.builder()
			.byteArrayResource(resource)
			.fileName(fileName)
			.mediaType(contentType)
			.build();
	}

	@Override
	public FileInfoDTO getLocalVolumeFileInfo(Long volumeId, String filePath) {
		LocalVolumeEntity localVolumeEntity = (LocalVolumeEntity)volumeService.findById(volumeId);
		String httpUrl = localVolumeEntity.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String size = webClientService.formatFileSize(fileInfo.getContentLength());
		String lastModifiedTime = webClientService.formatLastModifiedTime(fileInfo.getLastModified());
		String contentPath = filePath;

		return FileInfoDTO.builder()
			.fileName(fileName)
			.size(size)
			.lastModifiedTime(lastModifiedTime)
			.contentPath(contentPath)
			.build();
	}

	@Override
	public DownloadFileResDTO getLocalVolumeFile(Long volumeId, String filePath) {
		LocalVolumeEntity localVolumeEntity = (LocalVolumeEntity)volumeService.findById(volumeId);
		String httpUrl = localVolumeEntity.getDns() + filePath;
		HttpHeaders fileInfo = webClientService.getFileInfo(httpUrl);
		MediaType contentType = fileInfo.getContentType();
		byte[] fileContent = webClientService.downloadFile(httpUrl, contentType);
		ByteArrayResource resource = new ByteArrayResource(fileContent);
		String fileName = webClientService.retrieveFileName(httpUrl);
		String type = fileInfo.getFirst(HttpHeaders.CONTENT_TYPE);
		if (type != null) {
			if (type.startsWith("image/") || type.startsWith("text/")) {
				return DownloadFileResDTO.builder()
					.byteArrayResource(resource)
					.fileName(fileName)
					.mediaType(contentType)
					.build();
			} else {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
			}
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
		}
	}

	@Override
	public FileInfoDTO getAstragoVolumeFileInfo(Long volumeId, String filePath) {
		volumeService.findById(volumeId);
		File file = new File(filePath);
		if (file.exists()) {
			String size = webClientService.formatFileSize(file.length());
			String lastModifiedTime = webClientService.formatLastModifiedTime(file.lastModified());
			String contentPath = filePath;
			return FileInfoDTO.builder()
				.fileName(file.getName())
				.size(size)
				.lastModifiedTime(lastModifiedTime)
				.contentPath(contentPath)
				.build();
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_FILE_NOT_FOUND);
		}
	}

	@Override
	public DownloadFileResDTO getAstragoVolumeFile(Long volumeId, String filePath) {
		volumeService.findById(volumeId);
		Path targetPath = Path.of(filePath);
		// 파일이 존재하는지 확인
		if (Files.exists(targetPath)) {
			String fileName = CoreFileUtils.getFileName(filePath);
			// 파일을 ByteArrayResource로 읽어와 ResponseEntity로 감싸서 반환
			byte[] fileContent;
			try {
				fileContent = Files.readAllBytes(targetPath);
			} catch (IOException e) {
				throw new RestApiException(DatasetErrorCode.DATASET_PREVIEW_FAIL);
			}
			String fileExtension = CoreFileUtils.getFileExtension(targetPath);
			if (fileExtension != null) {
				switch (fileExtension.toLowerCase()) {
					case "txt":
					case "xml":
					case "yaml":
						break;
					case "jpg":
					case "jpeg":
					case "png":
						break;
					default:
						throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
				}
			} else {
				throw new RestApiException(DatasetErrorCode.DATASET_NOT_SUPPORT_PREVIEW);
			}
			ByteArrayResource resource = new ByteArrayResource(fileContent);
			MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(fileName);

			return DownloadFileResDTO.builder()
				.byteArrayResource(resource)
				.fileName(fileName)
				.mediaType(mediaType)
				.build();
		} else {
			throw new RestApiException(DatasetErrorCode.DATASET_FILE_NOT_FOUND);
		}
	}

	@Override
	public WorkloadResDTO.PageUsingVolumeDTO getWorkloadsUsingVolume(PageInfo pageInfo, Long volumeId,
		UserDTO.UserInfo userInfoDTO) {
		//사용중인 워크로드 조회
		WorkloadResDTO.PageUsingVolumeDTO pageUsingVolumeDTO = workloadModuleFacadeService.workloadsUsingVolume(
			pageInfo.getPageNo(), pageInfo.getPageSize(), volumeId);
		//workload 권한 설정
		if (!CollectionUtils.isEmpty(pageUsingVolumeDTO.getUsingWorkloads())) {
			pageUsingVolumeDTO.getUsingWorkloads()
				.forEach(wl -> wl.updateIsAccessible(userInfoDTO.getId(), userInfoDTO.getMyWorkspaces()));
		}
		return pageUsingVolumeDTO;
	}
	// null 체크와 함께 isBlank를 수행하는 메서드
	public static boolean isBlankSafe(String str) {
		return str == null || str.isBlank();
	}
}
