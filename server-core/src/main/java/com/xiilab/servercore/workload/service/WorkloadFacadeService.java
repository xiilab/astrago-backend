package com.xiilab.servercore.workload.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.K8sContainerReason;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.facade.svc.SvcModuleFacadeService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleCodeResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadEventDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.alert.systemalert.service.AlertService;
import com.xiilab.servercore.alert.systemalert.service.WorkspaceAlertSetService;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.service.CodeService;
import com.xiilab.servercore.common.dto.FileUploadResultDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.service.CredentialService;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.image.dto.ImageResDTO;
import com.xiilab.servercore.image.service.ImageService;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.servercore.model.service.ModelService;
import com.xiilab.servercore.node.service.NodeService;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadEventReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.enumeration.WorkloadEventAgeSortCondition;
import com.xiilab.servercore.workload.enumeration.WorkloadEventTypeSortCondition;
import com.xiilab.servercore.workload.enumeration.WorkloadSortCondition;

import io.fabric8.kubernetes.api.model.events.v1.Event;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WorkloadFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final SvcModuleFacadeService svcModuleFacadeService;
	// private final NodeFacadeService nodeFacadeService;
	private final NodeService nodeService;
	private final PinService pinService;
	private final DatasetService datasetService;
	private final ModelService modelService;
	private final WorkloadHistoryService workloadHistoryService;
	private final CredentialService credentialService;
	private final CodeService codeService;
	private final ImageService imageService;
	private final WorkspaceAlertSetService workspaceAlertSetService;
	private final AlertService alertService;
	private final WorkspaceService workspaceService;
	private final ApplicationEventPublisher eventPublisher;
	private final NetworkRepository networkRepository;
	private final MailService mailService;

	@Transactional
	public void createWorkload(CreateWorkloadJobReqDTO createWorkloadReqDTO, UserInfoDTO userInfoDTO) {
		createWorkloadReqDTO.setUserInfo(userInfoDTO.getId(), userInfoDTO.getUserName(), userInfoDTO.getUserFullName());

		// 이미지 credential 세팅
		if (!ObjectUtils.isEmpty(createWorkloadReqDTO.getImage().getCredentialId())
			&& createWorkloadReqDTO.getImage().getCredentialId() > 0) {
			setImageCredentialReqDTO(createWorkloadReqDTO.getImage(), userInfoDTO);
		}

		// 코드 credential 세팅
		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getCodes())) {
			setCodeCredentialReqDTO(createWorkloadReqDTO.getCodes());
		}

		// 데이터셋 볼륨 추가
		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getDatasets())) {
			setDatasetVolume(createWorkloadReqDTO.getWorkspace(), createWorkloadReqDTO.getDatasets());
		}

		// 모델 볼륨 추가
		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getModels())) {
			setModelVolume(createWorkloadReqDTO.getWorkspace(), createWorkloadReqDTO.getModels());
		}

		//Image IDE 정보 주입
		if (createWorkloadReqDTO.getImage().getType() == ImageType.BUILT) {
			ImageResDTO.FindImage imageInfo = imageService.findImageById(createWorkloadReqDTO.getImage().getId());
			createWorkloadReqDTO.setIde(imageInfo.getIde());
		} else {
			createWorkloadReqDTO.setIde(FrameWorkType.CUSTOM);
		}

		try {
			NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
			// 커스텀 이미지일 때만 이미지 데이터 저장
			// workloadModuleFacadeService.createJobWorkload(moduleCreateWorkloadReqDTO.toModuleDTO(network.getInitContainerURL()));
			// 리소스 초과 알림
			log.info("폐쇄망 : " + network.getNetworkCloseYN());
			checkAndSendWorkspaceResourceOverAlert(createWorkloadReqDTO, userInfoDTO);
			workloadModuleFacadeService.createJobWorkload(
				createWorkloadReqDTO.toModuleDTO(network.getInitContainerURL()));
			// 워크로드
		} catch (RestApiException e) {
			e.printStackTrace();
			throw e;
		}

	}

	private void checkAndSendWorkspaceResourceOverAlert(CreateWorkloadJobReqDTO moduleCreateWorkloadReqDTO,
		UserInfoDTO userInfoDTO) {
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			moduleCreateWorkloadReqDTO.getWorkspace());
		// CPU
		float cpuUsed = Float.parseFloat(workspaceResourceStatus.getResourceStatus().getCpuUsed());
		if (cpuUsed != 0.0f) {
			cpuUsed = cpuUsed / 1000.0f;
		}

		// MEM
		float memUsed = Float.parseFloat(workspaceResourceStatus.getResourceStatus().getMemUsed());
		if (memUsed != 0.0f) {
			memUsed = memUsed / 1000.0f;
		}

		boolean isCpuOverResource = isOverResource(String.valueOf(cpuUsed), moduleCreateWorkloadReqDTO.getCpuRequest(),
			workspaceResourceStatus.getResourceStatus().getCpuLimit());
		// GPU
		boolean isGpuOverResource = isOverResource(workspaceResourceStatus.getResourceStatus().getGpuUsed(),
			moduleCreateWorkloadReqDTO.getGpuRequest(), workspaceResourceStatus.getResourceStatus().getGpuLimit());

		boolean isMemOverResource = isOverResource(String.valueOf(memUsed), moduleCreateWorkloadReqDTO.getMemRequest(),
			workspaceResourceStatus.getResourceStatus().getMemLimit());

		if (isCpuOverResource || isGpuOverResource || isMemOverResource) {
			AlertMessage workspaceResourceOverAdmin = AlertMessage.WORKSPACE_RESOURCE_OVER_ADMIN;
			String mailTitle = String.format(workspaceResourceOverAdmin.getMailTitle(),
				workspaceResourceStatus.getName());
			String title = workspaceResourceOverAdmin.getTitle();
			String message = String.format(workspaceResourceOverAdmin.getMessage(),
				workspaceResourceStatus.getCreatorFullName(), workspaceResourceStatus.getCreatorUserName(),
				workspaceResourceStatus.getName());

			eventPublisher.publishEvent(
				new AdminAlertEvent(AlertName.ADMIN_WORKSPACE_RESOURCE_OVER, userInfoDTO.getId(), mailTitle, title,
					message,
					PageNaviParam.builder().workspaceResourceName(moduleCreateWorkloadReqDTO.getWorkspace()).build()));

			MailAttribute mail = MailAttribute.WORKSPACE_RESOURCE_OVER;
			// Mail Contents 작성
			List<MailDTO.Content> contents = List.of(
				MailDTO.Content.builder().col1("GPU : ").col2(workspaceResourceStatus.getResourceStatus().getGpuUsed()).build(),
				MailDTO.Content.builder().col1("CPU : ").col2(String.valueOf(cpuUsed)).build(),
				MailDTO.Content.builder().col1("MEM : ").col2(String.valueOf(memUsed)).build()
			);
			// Mail 전송
			mailService.sendMail(MailDTO.builder()
				.subject(mail.getSubject())
				.title(String.format(mail.getTitle(), userInfoDTO.getUserFullName() , userInfoDTO.getEmail(), moduleCreateWorkloadReqDTO.getWorkspace()))
				.subTitle(mail.getSubTitle())
				.contentTitle(mail.getContentTitle())
				.contents(contents)
				.footer(mail.getFooter())
				.build());
		}
	}

	private void setCodeCredentialReqDTO(List<ModuleCodeReqDTO> codes) {
		// 코드 목록에 있는 크레덴셜 ID만 추출
		List<Long> credentialIds = codes.stream()
			.map(ModuleCodeReqDTO::getCredentialId)
			.filter(credentialId -> !NumberValidUtils.isNullOrZero(credentialId))
			.toList();
		if (CollectionUtils.isEmpty(credentialIds)) {
			return;
		}

		// 크레덴셜 목록 조회
		CredentialResDTO.CredentialInfos credentialInfos = credentialService.findCredentialByIdIn(credentialIds,
			PageRequest.of(1, Integer.MAX_VALUE));
		Map<Long, CredentialResDTO.CredentialInfo> credentialInfoMap = convertListToMap(
			!CollectionUtils.isEmpty(credentialInfos.getCredentials()) ? credentialInfos.getCredentials() :
				new ArrayList<>());

		codes.forEach(moduleCodeReqDTO -> {
			Long credentialId = moduleCodeReqDTO.getCredentialId();
			if (!ObjectUtils.isEmpty(credentialId) && credentialInfoMap.containsKey(credentialId)) {
				CredentialResDTO.CredentialInfo credentialInfo = credentialInfoMap.get(
					moduleCodeReqDTO.getCredentialId());
				moduleCodeReqDTO.setCredentialReqDTO(credentialInfo.toModuleCredentialReqDTO());
			}
		});
	}

	private Map<Long, CredentialResDTO.CredentialInfo> convertListToMap(
		List<CredentialResDTO.CredentialInfo> datasets) {
		return datasets.stream().collect(Collectors.toMap(CredentialResDTO::getId, credentialInfo -> credentialInfo));
	}

	private void setImageCredentialReqDTO(ModuleImageReqDTO moduleImageReqDTO, UserInfoDTO userInfoDTO) {
		CredentialResDTO.CredentialInfo findCredential = credentialService.findCredentialById(
			moduleImageReqDTO.getCredentialId(), userInfoDTO);
		moduleImageReqDTO.setCredentialReqDTO(findCredential.toModuleCredentialReqDTO());
	}

	public FindWorkloadResDTO.WorkloadDetail getWorkloadInfoByResourceName(WorkloadType workloadType,
		String workspaceName, String workloadResourceName) {
		// 실행중일 떄
		try {
			// String nodeName = workspaceService.getNodeName(workspaceName, workloadResourceName);
			if (workloadType == WorkloadType.BATCH) {
				ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
					workloadResourceName);
				return getActiveWorkloadDetail(moduleBatchJobResDTO);
			} else if (workloadType == WorkloadType.INTERACTIVE) {
				ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
					workspaceName, workloadResourceName);
				return getActiveWorkloadDetail(moduleInteractiveJobResDTO);
			}
		} catch (Exception e) {
			try {
				return workloadHistoryService.getWorkloadInfoByResourceName(workspaceName, workloadResourceName);
			} catch (Exception e2) {
				throw e2;
			}
		}

		return null;
	}

	private <T extends ModuleWorkloadResDTO> FindWorkloadResDTO.WorkloadDetail getActiveWorkloadDetail(
		T moduleJobResDTO) {
		String nodeName = workspaceService.getNodeName(moduleJobResDTO.getWorkspaceResourceName(),
			moduleJobResDTO.getResourceName());
		// 이미지 DTO 세팅
		FindWorkloadResDTO.Image image = generateImageResDTO(moduleJobResDTO);
		// 모델 세팅
		List<FindWorkloadResDTO.Volume> models = generateModelResDTO(moduleJobResDTO.getModelIds(),
			moduleJobResDTO.getModelMountPathMap());
		// 데이터셋 세팅
		List<FindWorkloadResDTO.Volume> datasets = generateDatasetResDTO(moduleJobResDTO.getDatasetIds(),
			moduleJobResDTO.getDatasetMountPathMap());
		// 코드 세팅
		List<FindWorkloadResDTO.Code> codes = generateCodeResDTO(moduleJobResDTO);
		// PORT 세팅
		List<FindWorkloadResDTO.Port> ports = generatePortResDTO(moduleJobResDTO);
		// ENV 세팅
		List<FindWorkloadResDTO.Env> envs = moduleJobResDTO.getEnvs()
			.stream()
			.map(env -> new FindWorkloadResDTO.Env(env.variable(), env.value()))
			.toList();

		return FindWorkloadResDTO.WorkloadDetail.from(moduleJobResDTO, image, models, datasets, codes, ports, envs,
			nodeName);
	}

	public void stopWorkload(String workspaceName, String workloadName, WorkloadType workloadType,
		UserInfoDTO userInfoDTO) throws IOException {

		FindWorkloadResDTO.WorkloadDetail activeWorkloadDetail = null;
		if (workloadType == WorkloadType.BATCH) {
			ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
				workloadName);
			activeWorkloadDetail = getActiveWorkloadDetail(moduleBatchJobResDTO);
			stopBatchHobWorkload(workspaceName, workloadName, userInfoDTO);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
				workspaceName, workloadName);
			activeWorkloadDetail = getActiveWorkloadDetail(moduleInteractiveJobResDTO);
			stopInteractiveJobWorkload(workspaceName, workloadName, userInfoDTO);
		}

		if (!ObjectUtils.isEmpty(activeWorkloadDetail)) {
			PageNaviParam pageNaviParam = PageNaviParam.builder()
				.workspaceResourceName(activeWorkloadDetail.getWorkSpaceResourceName())
				.workloadResourceName(activeWorkloadDetail.getWorkloadResourceName())
				.workloadType(activeWorkloadDetail.getWorkloadType())
				.build();

			//워크로드 종료 알림 발송
			String emailTitle = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_END_CREATOR.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMessage(), workloadName);
			WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
				AlertName.USER_WORKLOAD_END, userInfoDTO.getId(), activeWorkloadDetail.getRegUserId(), emailTitle,
				title, message, workspaceName, pageNaviParam);
			eventPublisher.publishEvent(workspaceUserAlertEvent);
		}
	}

	public void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO) {
		ModuleWorkloadResDTO workloadHistory = workloadHistoryService.getWorkloadHistoryById(id);
		List<JobEntity> workloads = workloadHistoryService.getWorkloadByResourceName(
			workloadHistory.getWorkspaceResourceName());
		workloadHistoryService.deleteWorkloadHistory(id, userInfoDTO);
		//해당 워크로드를 등록한 모든 Pin 삭제
		pinService.deletePin(workloadHistory.getResourceName(), PinType.WORKLOAD);
		//워크로드 삭제 시 매핑 데이터 deleteYN 업데이트
		for (JobEntity workload : workloads) {
			datasetService.deleteDatasetWorkloadMapping(workload.getId());
			modelService.deleteModelWorkloadMapping(workload.getId());
			codeService.deleteCodeWorkloadMapping(workload.getId());
			imageService.deleteImageWorkloadMapping(workload.getId());
		}
	}

	public PageDTO<ModuleWorkloadResDTO> getOverViewWorkloadList(WorkloadType workloadType, String workspaceName,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, int pageNum,
		UserInfoDTO userInfoDTO) {
		//통합용 리스트 선언
		List<ModuleWorkloadResDTO> workloadResDTOList = new ArrayList<>();
		if (workloadType == WorkloadType.BATCH) {
			//k8s cluster에 생성되어있는 batchJob list
			List<ModuleBatchJobResDTO> batchJobListFromCluster = workloadModuleService.getBatchWorkloadListByCondition(
				workspaceName, userInfoDTO.getId());
			//종료된 batchJob list
			List<ModuleBatchJobResDTO> batchWorkloadHistoryList = workloadHistoryService.getBatchWorkloadHistoryList(
				workspaceName, null, userInfoDTO.getId());
			workloadResDTOList.addAll(batchJobListFromCluster);
			workloadResDTOList.addAll(batchWorkloadHistoryList);
		} else {
			//k8s cluster에서 생성되어있는 interactive job list 조회
			List<ModuleInteractiveJobResDTO> interactiveJobFromCluster = workloadModuleService.getInteractiveWorkloadListByCondition(
				workspaceName, userInfoDTO.getId());
			//종료된 interactive job list 조회
			List<ModuleInteractiveJobResDTO> interactiveWorkloadHistoryList = workloadHistoryService.getInteractiveWorkloadHistoryList(
				workspaceName, null, userInfoDTO.getId());
			workloadResDTOList.addAll(interactiveJobFromCluster);
			workloadResDTOList.addAll(interactiveWorkloadHistoryList);
		}
		//핀 워크로드 목록 필터링
		List<ModuleWorkloadResDTO> pinWorkloadList = filterAndMarkPinnedWorkloads(workloadResDTOList,
			userInfoDTO.getId());
		//일반 워크로드 목록 필터링
		List<ModuleWorkloadResDTO> normalWorkloadList = filterNormalWorkloads(workloadResDTOList, searchName,
			workloadStatus, workloadSortCondition, userInfoDTO.getId());
		PageDTO<ModuleWorkloadResDTO> moduleWorkloadResDTOPageDTO = new PageDTO<>(pinWorkloadList, normalWorkloadList,
			pageNum, 10);
		moduleWorkloadResDTOPageDTO.getContent()
			.forEach(moduleWorkloadResDTO -> moduleWorkloadResDTO.updateCanBeDeleted(userInfoDTO.getId(),
				userInfoDTO.getWorkspaceList(true)));
		return moduleWorkloadResDTOPageDTO;
	}

	public DirectoryDTO getFileListInWorkloadContainer(String workloadName, String workspaceName,
		WorkloadType workloadType, String path) throws IOException {
		return workloadModuleService.getDirectoryDTOListInWorkloadContainer(workloadName, workspaceName, workloadType,
			path);
	}

	public FileInfoDTO getFileInfoInWorkloadContainer(String workloadName, String workspaceName,
		WorkloadType workloadType, String path) throws IOException {
		return workloadModuleService.getFileInfoDtoInWorkloadContainer(workloadName, workspaceName, workloadType, path);
	}

	public Resource downloadFileFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) throws IOException {
		String[] split = path.split("/");
		String fileName = split[split.length - 1];
		if (!fileName.contains(".")) {
			throw new RestApiException(WorkloadErrorCode.WORKLOAD_FOLDER_DOWN_ERR);
		} else {
			return workloadModuleService.downloadFileFromWorkload(workloadName, workspaceName, workloadType, path);
		}
	}

	public void deleteFileFromWorkload(String workloadName, String workspaceName, WorkloadType workloadType,
		List<String> paths) {
		for (String path : paths) {
			workloadModuleService.deleteFileFromWorkload(workloadName, workspaceName, workloadType, path);
		}
	}

	public FileUploadResultDTO workloadFileUpload(String workloadName, String workspaceName, WorkloadType workloadType,
		String path, List<MultipartFile> files) {
		int successCnt = 0;
		int failCnt = 0;
		List<File> fileList = files.stream().map(file -> {
			try {
				return CoreFileUtils.convertInputStreamToFile(file);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).toList();
		for (File file : fileList) {
			Boolean result = workloadModuleService.uploadFileToWorkload(workloadName, workspaceName, workloadType, path,
				file);
			if (result) {
				successCnt += 1;
			} else {
				failCnt += 1;
			}
		}
		for (File file : fileList) {
			boolean delete = file.delete();
			log.info("{} 파일 삭제 결과 : {}", file.getName(), delete);
		}
		return new FileUploadResultDTO(successCnt, failCnt);
	}

	public FileInfoDTO getWorkloadFileInfo(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) throws IOException {
		return getFileInfoInWorkloadContainer(workloadName, workspaceName, workloadType, path);
	}

	public byte[] getWorkloadFilePreview(String workloadName, String workspaceName, WorkloadType workloadType,
		String path) throws IOException {
		return downloadFileFromWorkload(workloadName, workspaceName, workloadType, path).getContentAsByteArray();
	}

	public byte[] getWorkloadLogFile(String workloadName, UserInfoDTO userInfoDTO) {
		//저장된 로그 path 구하기
		String logPath = FileUtils.getUserLogPath(userInfoDTO.getId(), workloadName);
		try {
			return Files.readAllBytes(Path.of(logPath));
		} catch (IOException e) {
			throw new RestApiException(WorkloadErrorCode.NOT_FOUND_JOB_LOG);
		}
	}

	public void editWorkload(WorkloadType workloadType, WorkloadUpdateDTO workloadUpdateDTO) {
		try {
			if (workloadType == WorkloadType.BATCH) {
				workloadModuleFacadeService.editBatchJob(workloadUpdateDTO.getWorkspaceResourceName(),
					workloadUpdateDTO.getWorkloadResourceName(), workloadUpdateDTO.getName(),
					workloadUpdateDTO.getDescription());
			} else if (workloadType == WorkloadType.INTERACTIVE) {
				workloadModuleFacadeService.editInteractiveJob(workloadUpdateDTO.getWorkspaceResourceName(),
					workloadUpdateDTO.getWorkloadResourceName(), workloadUpdateDTO.getName(),
					workloadUpdateDTO.getDescription());
			}
		} catch (RestApiException e) {
			try {
				workloadHistoryService.editWorkloadHistory(workloadUpdateDTO);
			} catch (RestApiException e2) {
				throw e2;
			}
		}
	}

	public PageDTO<WorkloadEventDTO> getWorkloadEvent(WorkloadType workloadType,
		WorkloadEventReqDTO workloadEventReqDTO) {
		List<Event> workloadEventList = workloadModuleService.getWorkloadEventList(workloadEventReqDTO.getWorkload(),
			workloadEventReqDTO.getWorkspace(), workloadType);

		Stream<Event> eventStream = workloadEventList.stream();

		if (Objects.nonNull(workloadEventReqDTO.getSearchCondition())) {
			eventStream = workloadEventList.stream()
				.filter(workloadEvent -> workloadEvent.getNote().contains(workloadEventReqDTO.getSearchCondition()));
		}

		if (Objects.nonNull(workloadEventReqDTO.getK8SReasonType())) {
			eventStream = workloadEventList.stream()
				.filter(
					workloadEvent -> workloadEvent.getReason().equals(workloadEventReqDTO.getK8SReasonType().name()));
		}

		Comparator<Event> comparator = null;

		//Type 정렬 조건 적용
		if (workloadEventReqDTO.getTypeSortCondition() != null) {
			comparator = Comparator.comparing(Event::getType);
			if (workloadEventReqDTO.getTypeSortCondition() == WorkloadEventTypeSortCondition.TYPE_DESC) {
				comparator = comparator.reversed();
			}
		}

		//Age 정렬 조건 적용
		if (workloadEventReqDTO.getAgeSortCondition() != null) {
			// Event 클래스에 getAge 메소드가 있다고 가정합니다.
			Comparator<Event> ageComparator = Comparator.comparing(event -> event.getMetadata().getCreationTimestamp());
			if (workloadEventReqDTO.getAgeSortCondition() == WorkloadEventAgeSortCondition.AGE_DESC) {
				ageComparator = ageComparator.reversed();
			}
			// 이미 comparator가 설정되어 있으면 thenComparing으로 추가, 아니면 첫 조건으로 할당
			comparator = (comparator == null) ? ageComparator : comparator.thenComparing(ageComparator);
		}

		//정렬 조건이 존재할 경우 적용
		if (comparator != null) {
			eventStream = eventStream.sorted(comparator);
		}

		List<WorkloadEventDTO> result = eventStream.map(event -> WorkloadEventDTO.builder()
			.type(event.getType())
			.reason(K8sContainerReason.valueOf(event.getReason()))
			.from(event.getReportingController())
			.age(new AgeDTO(DateUtils.convertK8sUtcTimeString(event.getMetadata().getCreationTimestamp())))
			.message(event.getNote())
			.build()).toList();

		return new PageDTO<>(result, workloadEventReqDTO.getPageNum(), workloadEventReqDTO.getPageSize());
	}

	private List<ModuleWorkloadResDTO> filterNormalWorkloads(List<ModuleWorkloadResDTO> workloadList, String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, String userId) {
		// 사용자가 추가한 PIN 목록을 가져옵니다.
		Set<String> userWorkloadPinList = getUserWorkloadPinList(userId);
		// PIN이 없는 워크로드를 필터링합니다.
		List<ModuleWorkloadResDTO> normalWorkloadList = filterPinnedWorkloads(workloadList, userWorkloadPinList, false);
		//필터링 및 정렬 적용
		return applyWorkloadListCondition(normalWorkloadList, searchName, workloadStatus, workloadSortCondition);
	}

	/**
	 * 워크로드 목록을 사용자가 추가한 PIN에 따라 필터링하고, PINYN을 업데이트한 후 반환합니다.
	 *
	 * @param workloadList 워크로드 목록
	 * @param userId       사용자 ID
	 * @return PIN에 따라 필터링된 작업량 목록
	 */
	private List<ModuleWorkloadResDTO> filterAndMarkPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList,
		String userId) {
		// 사용자가 추가한 PIN 목록을 가져옵니다.
		Set<String> userWorkloadPinList = getUserWorkloadPinList(userId);
		// PIN에 따라 작업량을 필터링합니다.
		List<ModuleWorkloadResDTO> pinWorkloadList = filterPinnedWorkloads(workloadList, userWorkloadPinList, true);
		// 필터링된 작업량의 PINYN을 업데이트합니다.
		markPinnedWorkloads(pinWorkloadList);
		return pinWorkloadList;
	}

	/**
	 * 사용자가 추가한 PIN 목록을 가져옵니다.
	 *
	 * @param userId 사용자 ID
	 * @return 사용자가 추가한 PIN 목록
	 */
	private Set<String> getUserWorkloadPinList(String userId) {
		return pinService.getUserWorkloadPinList(userId);
	}

	/**
	 * 작업량 목록을 사용자가 추가한 PIN에 따라 필터링하여 반환합니다.
	 *
	 * @param workloadList        작업량 목록
	 * @param userWorkloadPinList 사용자가 추가한 PIN 목록
	 * @param pinFilterCondition  pin여부에 따라 필터링 할지에 대한 flag 값
	 * @return PIN에 따라 필터링된 작업량 목록
	 */
	private List<ModuleWorkloadResDTO> filterPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList,
		Set<String> userWorkloadPinList, boolean pinFilterCondition) {
		return workloadList.stream()
			.filter(workload -> pinFilterCondition == userWorkloadPinList.contains(workload.getResourceName()))
			.toList();
	}

	/**
	 * 작업량 목록의 PINYN을 업데이트합니다.
	 *
	 * @param workloadList 작업량 목록
	 */
	private void markPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList) {
		workloadList.forEach(workload -> workload.updatePinYN(true));
	}

	private List<ModuleWorkloadResDTO> applyWorkloadListCondition(List<ModuleWorkloadResDTO> workloadList,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition) {

		Stream<ModuleWorkloadResDTO> workloadStream = workloadList.stream()
			.filter(batch -> searchName == null || (batch.getName() != null && batch.getName().contains(searchName)))
			.filter(batch -> workloadStatus == null || batch.getStatus() == workloadStatus);

		if (sortCondition != null) {
			return switch (sortCondition) {
				case AGE_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt)).toList();
				case AGE_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt).reversed()).toList();
				case REMAIN_TIME_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getRemainTime)).toList();
				case REMAIN_TIME_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getRemainTime).reversed())
						.toList();
			};
		} else {
			return workloadStream.toList();
		}
	}

	private void stopBatchHobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
				WorkloadType.BATCH);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteBatchHobWorkload(workSpaceName, workloadName);

		// WorkspaceAlertSetDTO.ResponseDTO workspaceAlertSet = workspaceAlertSetService.getWorkspaceAlertSet(
		// 	workloadName);
		// 해당 워크스페이스 알림 설정이 True인 경우
		// if (workspaceAlertSet.isWorkloadEndAlert()) {
		// 	systemAlertService.sendAlert(SystemAlertDTO.builder()
		// 		.recipientId(userInfoDTO.getId())
		// 		.sendUserId("SYSTEM")
		// 		.alertType(SystemAlertType.WORKLOAD)
		// 		.message(String.format(SystemAlertMessage.WORKSPACE_END.getMessage(), workloadName))
		// 		.build());
		// }
	}

	private void stopInteractiveJobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
				WorkloadType.INTERACTIVE);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}

	private void setDatasetVolume(String workspaceName, List<ModuleVolumeReqDTO> list) {
		for (ModuleVolumeReqDTO moduleVolumeReqDTO : list) {
			Dataset findDataset = datasetService.findById(moduleVolumeReqDTO.getId());
			DatasetDTO.ResDatasetWithStorage resDatasetWithStorage = DatasetDTO.ResDatasetWithStorage.toDto(
				findDataset);
			if (resDatasetWithStorage.getDivision() == RepositoryDivision.ASTRAGO) {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resDatasetWithStorage.getIp(),
					resDatasetWithStorage.getStoragePath() + resDatasetWithStorage.getSaveDirectoryName(),
					resDatasetWithStorage.getStorageType());
			} else {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resDatasetWithStorage.getIp(),
					resDatasetWithStorage.getStoragePath(), resDatasetWithStorage.getStorageType());
			}
		}
	}

	private void setModelVolume(String workspaceName, List<ModuleVolumeReqDTO> list) {
		for (ModuleVolumeReqDTO moduleVolumeReqDTO : list) {
			Model findModel = modelService.findById(moduleVolumeReqDTO.getId());
			ModelDTO.ResModelWithStorage resModelWithStorage = ModelDTO.ResModelWithStorage.toDto(findModel);
			if (resModelWithStorage.getDivision() == RepositoryDivision.ASTRAGO) {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resModelWithStorage.getIp(),
					resModelWithStorage.getStoragePath() + resModelWithStorage.getSaveDirectoryName(),
					resModelWithStorage.getStorageType());
			} else {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resModelWithStorage.getIp(),
					resModelWithStorage.getStoragePath(), resModelWithStorage.getStorageType());
			}
		}
	}

	private static void setPvAndPVC(String workspaceName, ModuleVolumeReqDTO moduleVolumeReqDTO, String ip,
		String storagePath, StorageType storageType) {
		String pvcName = "astrago-storage-pvc-" + UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-storage-pv-" + UUID.randomUUID().toString().substring(6);
		int requestVolume = 50;

		// PV 생성
		CreatePV createPV = CreatePV.builder()
			.pvcName(pvcName)
			.pvName(pvName)
			.ip(ip)
			.storagePath(storagePath)
			.namespace(workspaceName)
			.storageType(storageType)
			.requestVolume(requestVolume)
			.build();
		moduleVolumeReqDTO.setCreatePV(createPV);
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(workspaceName)
			.requestVolume(requestVolume)
			.build();
		moduleVolumeReqDTO.setCreatePVC(createPVC);
	}

	public boolean workloadMkdir(String workloadName, String workspaceName, WorkloadType workloadType, String path) {
		return workloadModuleService.mkdirToWorkload(workloadName, workspaceName, workloadType, path);
	}

	private <T extends ModuleWorkloadResDTO> FindWorkloadResDTO.Image generateImageResDTO(T moduleJobResDTO) {
		if (StringUtils.hasText(moduleJobResDTO.getImageType())) {
			if (!StringUtils.hasText(moduleJobResDTO.getImageId()) && moduleJobResDTO.getImageType()
				.equals(ImageType.CUSTOM.name())) {
				return createCustomTypeImageDTO(moduleJobResDTO);
			} else {
				return createOtherTypeImageDTO(moduleJobResDTO);
			}
		} else {
			throw new RestApiException(WorkloadErrorCode.FAILED_LOAD_IMAGE_INFO);
		}
	}

	private CredentialResDTO.CredentialInfo getCredentialInfoDTO(Long credentialId) {
		if (!NumberValidUtils.isNullOrZero(credentialId)) {
			return credentialService.findCredentialById(credentialId, null);
		} else {
			return null;
		}
	}

	private <T extends ModuleWorkloadResDTO> FindWorkloadResDTO.Image createCustomTypeImageDTO(T moduleJobResDTO) {
		CredentialResDTO.CredentialInfo findCredential = getCredentialInfoDTO(moduleJobResDTO.getImageCredentialId());

		return FindWorkloadResDTO.Image.customTypeImageResDTO()
			.regUserId(moduleJobResDTO.getCreatorId())
			.regUserName(moduleJobResDTO.getCreatorUserName())
			.regUserRealName(moduleJobResDTO.getCreatorFullName())
			.regDate(moduleJobResDTO.getCreatedAt())
			.title(moduleJobResDTO.getImage())
			.name(moduleJobResDTO.getImage())
			.type(ImageType.valueOf(moduleJobResDTO.getImageType()))
			.repositoryAuthType(RepositoryAuthType.PUBLIC)
			.credentialId(findCredential != null ? findCredential.getId() : null)
			.credentialName(findCredential != null ? findCredential.getName() : null)
			.build();
	}

	private <T extends ModuleWorkloadResDTO> FindWorkloadResDTO.Image createOtherTypeImageDTO(T moduleJobResDTO) {
		ImageResDTO.FindImage findImage = imageService.findImageById(Long.parseLong(moduleJobResDTO.getImageId()));
		return FindWorkloadResDTO.Image.otherTypeImageResDTO()
			.regUserId(findImage.getRegUserId())
			.regUserName(findImage.getRegUserName())
			.regUserRealName(findImage.getRegUserRealName())
			.regDate(findImage.getRegDate())
			.title(StringUtils.hasText(findImage.getTitle()) ? findImage.getTitle() : findImage.getImageName())
			.id(findImage.getId())
			.name(findImage.getImageName())
			.type(findImage.getImageType())
			.repositoryAuthType(findImage.getRepositoryAuthType())
			.build();
	}

	private List<FindWorkloadResDTO.Volume> generateModelResDTO(String ids, Map<Long, String> mountMap) {
		List<FindWorkloadResDTO.Volume> models = new ArrayList<>();
		if (StringUtils.hasText(ids)) {
			String[] splitIds = ids.split(",");
			for (String s : splitIds) {
				long modelId = Long.parseLong(s);
				Model findModel = modelService.findById(modelId);
				FindWorkloadResDTO.Volume modelVol = FindWorkloadResDTO.Volume.volumeResDTO()
					.id(findModel.getModelId())
					.name(findModel.getModelName())
					.mountPath(mountMap.get(findModel.getModelId()))
					.size(findModel.getModelSize())
					.division(findModel.getDivision())
					.storageType(findModel.isAstragoModel() ?
						((AstragoModelEntity)findModel).getStorageEntity().getStorageType() :
						((LocalModelEntity)findModel).getStorageType())
					.regUserId(findModel.getRegUser().getRegUserId())
					.regUserName(findModel.getRegUser().getRegUserName())
					.regUserRealName(findModel.getRegUser().getRegUserRealName())
					.regDate(findModel.getRegDate())
					.modDate(findModel.getModDate())
					.build();
				models.add(modelVol);
			}
		}

		return models;
	}

	private List<FindWorkloadResDTO.Volume> generateDatasetResDTO(String ids, Map<Long, String> mountMap) {
		List<FindWorkloadResDTO.Volume> datasets = new ArrayList<>();
		if (StringUtils.hasText(ids)) {
			String[] splitIds = ids.split(",");
			for (String s : splitIds) {
				long datasetId = Long.parseLong(s);
				Dataset findDataset = datasetService.findById(datasetId);
				FindWorkloadResDTO.Volume datasetVol = FindWorkloadResDTO.Volume.volumeResDTO()
					.regUserId(findDataset.getRegUser().getRegUserId())
					.regUserName(findDataset.getRegUser().getRegUserName())
					.regUserRealName(findDataset.getRegUser().getRegUserRealName())
					.regDate(findDataset.getRegDate())
					.modDate(findDataset.getModDate())
					.id(findDataset.getDatasetId())
					.name(findDataset.getDatasetName())
					.mountPath(mountMap.get(findDataset.getDatasetId()))
					.size(findDataset.getDatasetSize())
					.division(findDataset.getDivision())
					.storageType(findDataset.isAstragoDataset() ?
						((AstragoDatasetEntity)findDataset).getStorageEntity().getStorageType() :
						((LocalDatasetEntity)findDataset).getStorageType())
					.build();
				datasets.add(datasetVol);
			}
		}
		return datasets;
	}

	private <T extends ModuleWorkloadResDTO> List<FindWorkloadResDTO.Code> generateCodeResDTO(T moduleJobResDTO) {
		List<FindWorkloadResDTO.Code> codes = new ArrayList<>();
		for (ModuleCodeResDTO code : moduleJobResDTO.getCodes()) {
			FindWorkloadResDTO.Code addCode = null;
			CredentialResDTO.CredentialInfo findCredential = getCredentialInfoDTO(code.getCredentialId());
			// 커스텀 코드일 경우
			if (NumberValidUtils.isNullOrZero(code.getSourceCodeId())
				&& code.getRepositoryType() == RepositoryType.USER) {
				addCode = FindWorkloadResDTO.Code.codeResDTO()
					.id(null)
					.regUserId(moduleJobResDTO.getCreatorId())
					.regUserName(moduleJobResDTO.getCreatorUserName())
					.regUserRealName(moduleJobResDTO.getCreatorFullName())
					.regDate(moduleJobResDTO.getCreatedAt())
					.title(code.getRepositoryUrl())
					.repositoryURL(code.getRepositoryUrl())
					.branch(code.getBranch())
					.mountPath(code.getMountPath())
					.codeType(code.getCodeType())
					.repositoryAuthType(code.getRepositoryAuthType())
					.credentialId(findCredential != null ? findCredential.getId() : null)
					.credentialName(findCredential != null ? findCredential.getName() : null)
					.repositoryType(code.getRepositoryType())
					.build();
			} else {    // 공유 코드일 경우
				CodeResDTO findCode = codeService.getCodeById(code.getSourceCodeId());
				addCode = FindWorkloadResDTO.Code.codeResDTO()
					.id(findCode.getId())
					.regUserId(findCode.getRegUser().getRegUserId())
					.regUserName(findCode.getRegUser().getRegUserName())
					.regUserRealName(findCode.getRegUser().getRegUserRealName())
					.regDate(findCode.getRegDate())
					.title(code.getRepositoryUrl())
					.repositoryURL(code.getRepositoryUrl())
					.branch(code.getBranch())
					.mountPath(code.getMountPath())
					.codeType(code.getCodeType())
					.repositoryAuthType(code.getRepositoryAuthType())
					.credentialId(findCredential != null ? findCredential.getId() : null)
					.credentialName(findCredential != null ? findCredential.getName() : null)
					.repositoryType(code.getRepositoryType())
					.build();
			}

			codes.add(addCode);
		}

		return codes;
	}

	private boolean isOverResource(String workspaceResourceUsed, float createWorkloadResourceUsed,
		String workspaceResourceLimit) {
		float totalUsed = Float.parseFloat(workspaceResourceUsed) + createWorkloadResourceUsed;
		float resourceLimit = Float.parseFloat(workspaceResourceLimit);
		return totalUsed > resourceLimit;
	}

	private <T extends ModuleWorkloadResDTO> List<FindWorkloadResDTO.Port> generatePortResDTO(T moduleJobResDTO) {
		ResponseDTO.PageNodeDTO nodeList = nodeService.getNodeList(1, 1);
		Optional<ResponseDTO.NodeDTO> node = nodeList.getNodes().stream().findFirst();
		List<FindWorkloadResDTO.Port> ports = new ArrayList<>();
		if (node.isPresent()) {
			// 서비스 포트 찾기
			SvcResDTO.FindSvcs findSvcs = svcModuleFacadeService.getServicesByResourceName(
				moduleJobResDTO.getWorkspaceResourceName(), moduleJobResDTO.getResourceName());

			for (SvcResDTO.FindSvcDetail findSvcDetail : findSvcs.getServices()) {
				Map<Integer, Integer> portMap = findSvcDetail.getPorts()
					.stream()
					.collect(Collectors.toMap(SvcResDTO.Port::getPort, SvcResDTO.Port::getNodePort));
				ports = moduleJobResDTO.getPorts()
					.stream()
					.map(port -> new FindWorkloadResDTO.Port(port.name(), port.originPort(),
						node.get().getIp() + ":" + portMap.get(port.originPort())))
					.toList();
			}
		} else {
			ports = moduleJobResDTO.getPorts()
				.stream()
				.map(port -> new FindWorkloadResDTO.Port(port.name(), port.originPort(), null))
				.toList();
		}

		return ports;
	}
}
