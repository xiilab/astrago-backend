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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.common.utils.DateUtils;
import com.xiilab.modulek8s.facade.svc.SvcModuleFacadeService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleCodeResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDistributedJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModulePortResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadEventDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractDistributedWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractSingleWorkloadResDTO;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.PortRepository;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.service.CodeService;
import com.xiilab.servercore.common.dto.FileUploadResultDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.service.CredentialService;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.image.dto.ImageResDTO;
import com.xiilab.servercore.image.service.ImageService;
import com.xiilab.servercore.model.service.ModelService;
import com.xiilab.servercore.node.service.NodeFacadeService;
import com.xiilab.servercore.node.service.NodeService;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.storage.service.StorageService;
import com.xiilab.servercore.user.service.UserFacadeService;
import com.xiilab.servercore.volume.dto.VolumeResDTO;
import com.xiilab.servercore.volume.service.VolumeFacadeService;
import com.xiilab.servercore.volume.service.VolumeService;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadEventReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.ExperimentDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.OverViewWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadSummaryDTO;
import com.xiilab.servercore.workload.enumeration.WorkloadEventAgeSortCondition;
import com.xiilab.servercore.workload.enumeration.WorkloadEventTypeSortCondition;

import io.fabric8.kubernetes.api.model.ServicePort;
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
	private final VolumeFacadeService volumeFacadeService;
	private final NodeService nodeService;
	private final PinService pinService;
	private final DatasetService datasetService;
	private final ModelService modelService;
	private final VolumeService volumeService;
	private final WorkloadHistoryService workloadHistoryService;
	private final CredentialService credentialService;
	private final CodeService codeService;
	private final ImageService imageService;
	private final WorkspaceService workspaceService;
	private final ApplicationEventPublisher eventPublisher;
	private final NetworkRepository networkRepository;
	private final UserFacadeService userFacadeService;
	private final NodeFacadeService nodeFacadeService;
	private final PortRepository portRepository;
	private final StorageService storageService;
	@Value("${astrago.private-registry-url}")
	private String privateRegistryUrl;

	@Transactional
	public void createWorkload(CreateWorkloadJobReqDTO createWorkloadReqDTO, UserDTO.UserInfo userInfoDTO) {
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
		// TODO 삭제 예정
/*		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getDatasets())) {
			setDatasetVolume(createWorkloadReqDTO.getWorkspace(), createWorkloadReqDTO.getDatasets());
		}

		// 모델 볼륨 추가
		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getModels())) {
			setModelVolume(createWorkloadReqDTO.getWorkspace(), createWorkloadReqDTO.getModels());
		}
		//*/

		// outputMountPath(사용자가 입력, 컨테이너 마운트 경로) <=> hostPath 볼륨 생성
		// hostPath 형식: {defaultStorageHostPath} + "/workspace/{wsResourceName}/workloads/{wlResourceName}/models"
		if (StringUtils.hasText(createWorkloadReqDTO.getOutputMountPath())) {
			// 볼륨 DB에 저장
			String volumeName = String.format("%s의 Output volume", createWorkloadReqDTO.getName());
			Long volumeId = volumeFacadeService.insertAstragoOutputVolume(
				volumeName,
				createWorkloadReqDTO.getWorkspace(),
				createWorkloadReqDTO.getJobName(),
				createWorkloadReqDTO.getOutputMountPath());
			// reqDTO에 추가
			createWorkloadReqDTO.getVolumes()
				.add(new ModuleVolumeReqDTO(volumeId, createWorkloadReqDTO.getOutputMountPath()));
		}

		// 볼륨 추가
		if (!CollectionUtils.isEmpty(createWorkloadReqDTO.getVolumes())) {
			setVolumes(createWorkloadReqDTO.getWorkspace(), createWorkloadReqDTO.getVolumes());
		}

		//Image IDE 정보 주입
		if (createWorkloadReqDTO.getImage().getType() == ImageType.BUILT) {
			ImageResDTO.FindImage imageInfo = imageService.findImageById(createWorkloadReqDTO.getImage().getId());
			createWorkloadReqDTO.setIde(imageInfo.getIde());
		} else {
			createWorkloadReqDTO.setIde(FrameWorkType.CUSTOM);
		}

		// MPS 노드 지정
		if (!Objects.isNull(createWorkloadReqDTO.getGpuType()) && createWorkloadReqDTO.getGpuType() == GPUType.MPS) {
			if (StringUtils.hasText(createWorkloadReqDTO.getNodeName())) {
				createWorkloadReqDTO.setNodeName(getMpsNodeName(createWorkloadReqDTO.getNodeName()));
			}
		}

		try {
			NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
			// 커스텀 이미지일 때만 이미지 데이터 저장
			// workloadModuleFacadeService.createJobWorkload(moduleCreateWorkloadReqDTO.toModuleDTO(network.getInitContainerURL()));
			// 리소스 초과 알림
			log.info("폐쇄망 : " + network.getNetworkCloseYN());
			checkAndSendWorkspaceResourceOverAlert(createWorkloadReqDTO, userInfoDTO);
			NetworkCloseYN networkCloseYN = network.getNetworkCloseYN();

			String initContainerUrl = "";
			String imageName = "";
			if(networkCloseYN == NetworkCloseYN.Y){
				if(isBlankSafe(privateRegistryUrl)){
					initContainerUrl = network.getInitContainerImageUrl();
				}else{
					initContainerUrl = privateRegistryUrl + "/" + network.getInitContainerImageUrl();
				}
			}else{
				initContainerUrl = network.getInitContainerImageUrl();
			}
			if(createWorkloadReqDTO.getImage().getType() != ImageType.CUSTOM && networkCloseYN == NetworkCloseYN.Y){
				if(isBlankSafe(privateRegistryUrl)){
					imageName = createWorkloadReqDTO.getImage().getName();
				}else{
					imageName = privateRegistryUrl + "/" + createWorkloadReqDTO.getImage().getName();
				}
			}else{
				imageName = createWorkloadReqDTO.getImage().getName();
			}
			ModuleImageReqDTO image = createWorkloadReqDTO.getImage();
			image.modifyName(imageName);
			CreateWorkloadReqDTO moduleDTO = createWorkloadReqDTO.toModuleDTO(initContainerUrl);
			moduleDTO.modifyImage(image);
			workloadModuleFacadeService.createJobWorkload(moduleDTO);
			// 워크로드
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}
	// null 체크와 함께 isBlank를 수행하는 메서드
	public static boolean isBlankSafe(String str) {
		return str == null || str.isBlank();
	}
	private String getMpsNodeName(String nodeNames) {
		String[] splitNodeName = nodeNames.replaceAll(" ", "").split(",");
		if (splitNodeName.length == 1) {
			return splitNodeName[0];
		} else {
			// 할당 가능한 GPU가 가장 많은 노드에 워크로드 생성
			String createWorkloadNodeName = null;
			int maximumAllocatableGPU = Integer.MIN_VALUE;
			for (String nodeName : splitNodeName) {
				ResponseDTO.NodeResourceInfo nodeResourceInfo = nodeFacadeService.getNodeResourceByResourceName(
					nodeName);
				int allocatableGPU = Integer.parseInt(nodeResourceInfo.getAllocatable().getAllocatableGpu());
				if (allocatableGPU > maximumAllocatableGPU) {
					createWorkloadNodeName = nodeName;
					maximumAllocatableGPU = allocatableGPU;
				}
			}

			return createWorkloadNodeName;
		}
	}

	private void checkAndSendWorkspaceResourceOverAlert(CreateWorkloadJobReqDTO moduleCreateWorkloadReqDTO,
		UserDTO.UserInfo userInfoDTO) {
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			moduleCreateWorkloadReqDTO.getWorkspace());
		// CPU
		float cpuUsed = workspaceResourceStatus.getResourceStatus().getCpuUsed();
		if (cpuUsed != 0.0f) {
			cpuUsed = cpuUsed / 1000.0f;
		}

		// MEM
		float memUsed = workspaceResourceStatus.getResourceStatus().getMemUsed();
		if (memUsed != 0.0f) {
			memUsed = memUsed / 1000.0f;
		}

		boolean isCpuOverResource = isOverResource(cpuUsed,
			moduleCreateWorkloadReqDTO.getTotalCpuRequest(),
			workspaceResourceStatus.getResourceStatus().getCpuLimit());
		// GPU
		boolean isGpuOverResource = isOverResource(workspaceResourceStatus.getResourceStatus().getGpuUsed(),
			moduleCreateWorkloadReqDTO.getTotalGpuRequest(), workspaceResourceStatus.getResourceStatus().getGpuLimit());

		boolean isMemOverResource = isOverResource(memUsed,
			moduleCreateWorkloadReqDTO.getTotalMemoryRequest(),
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
					PageNaviParam.builder().workspaceResourceName(moduleCreateWorkloadReqDTO.getWorkspace()).build(),
					null));

		}
	}

	private void setCodeCredentialReqDTO(List<ModuleCodeReqDTO> codes) {
		// 코드 목록에 있는 크레덴셜 ID만 추출
		List<Long> credentialIds = codes.stream()
			.map(ModuleCodeReqDTO::getCredentialId)
			.filter(credentialId -> !ValidUtils.isNullOrZero(credentialId))
			.toList();
		if (CollectionUtils.isEmpty(credentialIds)) {
			return;
		}

		// 크레덴셜 목록 조회
		CredentialResDTO.CredentialInfos credentialInfos = credentialService.findCredentialByIdIn(credentialIds,
			PageRequest.of(1, Integer.MAX_VALUE));
		Map<Long, CredentialResDTO.CredentialInfo> credentialInfoMap = convertCredentialListToMap(
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

	private Map<Long, CredentialResDTO.CredentialInfo> convertCredentialListToMap(
		List<CredentialResDTO.CredentialInfo> credentials) {
		return credentials.stream()
			.collect(Collectors.toMap(CredentialResDTO::getId, credentialInfo -> credentialInfo));
	}

	private void setImageCredentialReqDTO(ModuleImageReqDTO moduleImageReqDTO, UserDTO.UserInfo userInfoDTO) {
		CredentialResDTO.CredentialInfo findCredential = credentialService.findCredentialById(
			moduleImageReqDTO.getCredentialId(), userInfoDTO);
		moduleImageReqDTO.setCredentialReqDTO(findCredential.toModuleCredentialReqDTO());
	}

	public List<FindWorkloadResDTO.Port> getWorkloadPortInfo(WorkloadType workloadType, String workspaceName,
		String workloadResourceName) {
		AbstractModuleWorkloadResDTO moduleWorkloadResDTO = null;
		if (workloadType == WorkloadType.BATCH) {
			moduleWorkloadResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
				workloadResourceName);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			moduleWorkloadResDTO = workloadModuleFacadeService.getInteractiveWorkload(
				workspaceName, workloadResourceName);
		} else if (workloadType == WorkloadType.DISTRIBUTED) {
			moduleWorkloadResDTO = workloadModuleFacadeService.getDistributedWorkload(
				workspaceName, workloadResourceName);
		}

		return generatePortResDTO(moduleWorkloadResDTO);
	}

	@Transactional
	public FindWorkloadResDTO getWorkloadInfoByResourceName(WorkloadType workloadType,
		String workspaceName, String workloadResourceName, UserDTO.UserInfo userInfoDTO) {

		FindWorkloadResDTO workloadInfo = workloadHistoryService.getWorkloadInfoByResourceName(
			workspaceName, workloadResourceName,
			userInfoDTO);
		WorkloadEntity workloadEntity = workloadHistoryService.findById(workloadInfo.getId());
		ResponseDTO.NodeDTO connectedNode = getConnectedNode().get();
		String ip = connectedNode.getIp();

		if(workloadInfo.getPorts() == null || workloadInfo.getPorts().size() == 0){
			List<List<ServicePort>> servicePorts = svcModuleFacadeService.getPortsByWorkloadResourceName(
				workspaceName, workloadResourceName);
			List<FindWorkloadResDTO.Port> ports = new ArrayList<>();
			for (List<ServicePort> port : servicePorts) {
				for (ServicePort servicePort : port) {
					PortEntity portEntity = PortEntity.builder()
						.name(servicePort.getName())
						.portNum(servicePort.getPort())
						.targetPortNum(servicePort.getNodePort())
						.build();
					portEntity.setWorkload(workloadEntity);
					portRepository.save(portEntity);
					ports.add(new FindWorkloadResDTO.Port(servicePort.getName(), servicePort.getPort(), servicePort.getNodePort(), ip + ":" + servicePort.getNodePort()));
				}
			}
			workloadInfo.setPorts(ports);
		}else{
			List<FindWorkloadResDTO.Port> ports = workloadInfo.getPorts().stream().map(port ->
				new FindWorkloadResDTO.Port(port.getName(), port.getPort(), port.getTargetPort(),ip + ":" + port.getTargetPort())).toList();
			workloadInfo.setPorts(ports);
		}

		try {
			if (workloadInfo.getImage().getType() == ImageType.HUB) {
				ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
					workloadResourceName);
				workloadInfo.updateHubPredictTime(moduleBatchJobResDTO.getEstimatedInitialTime(),
					moduleBatchJobResDTO.getEstimatedRemainingTime());
			}
			return workloadInfo;
		} catch (Exception e) {
			return workloadInfo;
		}

		// HUB일 떄,
		// 실행중일 떄
		// try {
		// 	UserDTO.UserInfo userInfo = userFacadeService.getUserById(userInfoDTO.getId());
		// 	Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);
		// 	// String nodeName = workspaceService.getNodeName(workspaceName, workloadResourceName);
		// 	if (workloadType == WorkloadType.BATCH) {
		// 		ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
		// 			workloadResourceName);
		// 		// 삭제권한 업데이트
		// 		moduleBatchJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), userInfo.getMyWorkspaces());
		//
		// 		return getActiveWorkloadDetail(moduleBatchJobResDTO);
		// 	} else if (workloadType == WorkloadType.INTERACTIVE) {
		// 		ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
		// 			workspaceName, workloadResourceName);
		// 		//삭제권한 업데이트
		// 		moduleInteractiveJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);
		//
		// 		return getActiveWorkloadDetail(moduleInteractiveJobResDTO);
		// 	} else if (workloadType == WorkloadType.DISTRIBUTED) {
		// 		ModuleDistributedJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getDistributedWorkload(
		// 			workspaceName, workloadResourceName);
		// 		moduleInteractiveJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);
		//
		// 		return getActiveWorkloadDetail(moduleInteractiveJobResDTO);
		// 	}
		// } catch (Exception e) {
		// 	return workloadHistoryService.getWorkloadInfoByResourceName(workspaceName, workloadResourceName,
		// 		userInfoDTO);
		// }
		//
		// return null;
	}

	private <T extends AbstractModuleWorkloadResDTO> FindWorkloadResDTO getActiveWorkloadDetail(
		T moduleJobResDTO) {
		String nodeName = workspaceService.getNodeName(moduleJobResDTO.getWorkspaceResourceName(),
			moduleJobResDTO.getResourceName());
		// 이미지 DTO 세팅
		FindWorkloadResDTO.Image image = generateImageResDTO(moduleJobResDTO);
		// TODO 삭제 예쩡
		// 모델 세팅
		// List<FindWorkloadResDTO.Volume> models = generateModelResDTO(moduleJobResDTO.getModelIds(),
		// 	moduleJobResDTO.getModelMountPathMap());
		// // 데이터셋 세팅
		// List<FindWorkloadResDTO.Volume> datasets = generateDatasetResDTO(moduleJobResDTO.getDatasetIds(),
		// 	moduleJobResDTO.getDatasetMountPathMap());
		List<FindWorkloadResDTO.Volume> volumes = generateVolumeResDTO(moduleJobResDTO.getVolumeIds(),
			moduleJobResDTO.getVolumeMountPathMap());
		// 코드 세팅
		List<FindWorkloadResDTO.Code> codes = generateCodeResDTO(moduleJobResDTO);
		// PORT 세팅
		List<FindWorkloadResDTO.Port> ports = generatePortResDTO(moduleJobResDTO);
		// ENV 세팅
		List<FindWorkloadResDTO.Env> envs = moduleJobResDTO.getEnvs()
			.stream()
			.map(env -> new FindWorkloadResDTO.Env(env.getName(), env.getValue()))
			.toList();

		if (moduleJobResDTO.getType() == WorkloadType.DISTRIBUTED) {
			// TODO 삭제 예정
			// return FindWorkloadResDTO.DistributedWorkloadDetail.from((AbstractDistributedWorkloadResDTO)moduleJobResDTO,
			// 	image, models, datasets, codes, ports, envs,
			// 	nodeName);
			return FindWorkloadResDTO.DistributedWorkloadDetail.from((AbstractDistributedWorkloadResDTO)moduleJobResDTO,
				image, volumes, codes, ports, envs,
				nodeName);
		} else if (moduleJobResDTO.getType() == WorkloadType.BATCH) {
			// TODO 삭제 예정
			// return FindWorkloadResDTO.SingleWorkloadDetail.from((AbstractSingleWorkloadResDTO)moduleJobResDTO, image,
			// 	models, datasets, codes, ports, envs,
			// 	nodeName);
			return FindWorkloadResDTO.SingleWorkloadDetail.from((AbstractSingleWorkloadResDTO)moduleJobResDTO, image,
				volumes, codes, ports, envs,
				nodeName);
		} else if (moduleJobResDTO.getType() == WorkloadType.INTERACTIVE) {
			// return FindWorkloadResDTO.SingleWorkloadDetail.from((AbstractSingleWorkloadResDTO)moduleJobResDTO, image,
			// 	models, datasets, codes, ports, envs,
			// 	nodeName);
			return FindWorkloadResDTO.SingleWorkloadDetail.from((AbstractSingleWorkloadResDTO)moduleJobResDTO, image,
				volumes, codes, ports, envs,
				nodeName);
		} else {
			return null;
		}
	}

	public void stopWorkload(String workspaceName, String workloadName, WorkloadType workloadType,
		UserDTO.UserInfo userInfoDTO) throws IOException {

		FindWorkloadResDTO activeSingleWorkloadDetail = null;
		if (workloadType == WorkloadType.BATCH) {
			ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
				workloadName);
			activeSingleWorkloadDetail = getActiveWorkloadDetail(moduleBatchJobResDTO);
			stopBatchJobWorkload(workspaceName, workloadName, userInfoDTO);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
				workspaceName, workloadName);
			activeSingleWorkloadDetail = getActiveWorkloadDetail(moduleInteractiveJobResDTO);
			stopInteractiveJobWorkload(workspaceName, workloadName, userInfoDTO);
		} else if (workloadType == WorkloadType.DISTRIBUTED) {
			ModuleDistributedJobResDTO distributedWorkload = workloadModuleFacadeService.getDistributedWorkload(
				workspaceName, workloadName);
			activeSingleWorkloadDetail = getActiveWorkloadDetail(distributedWorkload);
			stopDistributedWorkload(workspaceName, workloadName, userInfoDTO);
		}

		if (!ObjectUtils.isEmpty(activeSingleWorkloadDetail)) {
			PageNaviParam pageNaviParam = PageNaviParam.builder()
				.workspaceResourceName(activeSingleWorkloadDetail.getWorkSpaceResourceName())
				.workloadResourceName(activeSingleWorkloadDetail.getWorkloadResourceName())
				.workloadType(activeSingleWorkloadDetail.getWorkloadType())
				.build();

			//워크로드 종료 알림 발송
			String emailTitle = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_END_CREATOR.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMessage(),
				activeSingleWorkloadDetail.getWorkloadName());

			String receiverMail = userFacadeService.getUserInfoById(activeSingleWorkloadDetail.getRegUserId())
				.getEmail();
			MailDTO mailDTO = MailServiceUtils.endWorkloadMail(activeSingleWorkloadDetail.getWorkloadName(),
				receiverMail);

			WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
				AlertName.USER_WORKLOAD_END, userInfoDTO.getId(), activeSingleWorkloadDetail.getRegUserId(), emailTitle,
				title, message, workspaceName, pageNaviParam, mailDTO);
			eventPublisher.publishEvent(workspaceUserAlertEvent);
		}
	}

	private void stopDistributedWorkload(String workspaceName, String workloadName, UserDTO.UserInfo userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workspaceName, workloadName,
				WorkloadType.DISTRIBUTED);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteDistributedWorkload(workspaceName, workloadName);
	}

	public void deleteWorkloadHistory(long id, UserDTO.UserInfo userInfoDTO) {
		WorkloadSummaryDTO workloadHistory = workloadHistoryService.getWorkloadHistoryById(id);
		List<WorkloadEntity> workloads = workloadHistoryService.getWorkloadByResourceName(
			workloadHistory.getWorkspaceResourceName());
		workloadHistoryService.deleteWorkloadHistory(id, userInfoDTO);
		//해당 워크로드를 등록한 모든 Pin 삭제
		pinService.deletePin(workloadHistory.getResourceName(), PinType.WORKLOAD);
		//워크로드 삭제 시 매핑 데이터 deleteYN 업데이트
		for (WorkloadEntity workload : workloads) {
			datasetService.deleteDatasetWorkloadMapping(workload.getId());
			modelService.deleteModelWorkloadMapping(workload.getId());
			codeService.deleteCodeWorkloadMapping(workload.getId());
			imageService.deleteImageWorkloadMapping(workload.getId());
		}
	}

	public PageDTO<WorkloadSummaryDTO> getOverViewWorkloadList(WorkloadType workloadType, String workspaceName,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, int pageNum,
		Boolean isCreatedByMe, UserDTO.UserInfo userInfoDTO) {
		//pin 워크로드 조회 - 검색 x, sort만 적용
		List<String> pinResourceNameList = pinService.getWorkloadPinListByUserId(userInfoDTO.getId(), workspaceName);

		List<WorkloadSummaryDTO> pinList = workloadHistoryService.getWorkloadHistoryInResourceNames(
			pinResourceNameList, workloadType, workloadSortCondition);
		markPinnedWorkloads(pinList);
		List<WorkloadSummaryDTO> workloadResDTOList = new ArrayList<>(pinList);
		//(pageSize - pin)개수만큼 normal 워크로드 조회 - 검색, sort 적용 - not in pin 워크로드 resourceName
		int pageSize = 8;
		int normalPageSize = pageSize - pinList.size();
		PageRequest pageRequest = PageRequest.of(pageNum - 1, normalPageSize);
		OverViewWorkloadResDTO<WorkloadSummaryDTO> overViewWorkloadResDTO = workloadHistoryService.getOverViewWorkloadList(
			workspaceName, workloadType, searchName, isCreatedByMe, userInfoDTO.getId(), pinResourceNameList,
			workloadStatus, workloadSortCondition, pageRequest);

		//workload 삭제 권한 체크
		Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);

		//page 계산
		int totalSize = (int)(pinList.size() + overViewWorkloadResDTO.getTotalSize());
		int totalPageNum = (int)Math.ceil(totalSize / (double)pageSize);
		workloadResDTOList.addAll(overViewWorkloadResDTO.getContent());
		workloadResDTOList.forEach(wl -> wl.updateCanBeDeleted(userInfoDTO.getId(), workspaceList));
		return new PageDTO<>(totalSize, totalPageNum, workloadResDTOList);
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

	public byte[] getWorkloadLogFile(String workloadName, UserDTO.UserInfo userInfoDTO) {
		//저장된 로그 path 구하기
		String logPath = FileUtils.getUserLogPath(userInfoDTO.getId(), workloadName);
		try {
			return Files.readAllBytes(Path.of(logPath));
		} catch (IOException e) {
			throw new RestApiException(WorkloadErrorCode.NOT_FOUND_JOB_LOG);
		}
	}

	public void editWorkload(WorkloadType workloadType, WorkloadUpdateDTO workloadUpdateDTO) {
		/*	try {
			if (workloadType == WorkloadType.BATCH) {
				workloadModuleFacadeService.editBatchJob(workloadUpdateDTO.getWorkspaceResourceName(),
					workloadUpdateDTO.getWorkloadResourceName(), workloadUpdateDTO.getName(),
					workloadUpdateDTO.getDescription());
			} else if (workloadType == WorkloadType.INTERACTIVE) {
				workloadModuleFacadeService.editInteractiveJob(workloadUpdateDTO.getWorkspaceResourceName(),
					workloadUpdateDTO.getWorkloadResourceName(), workloadUpdateDTO.getName(),
					workloadUpdateDTO.getDescription());
			}
		} finally {
			workloadHistoryService.editWorkloadHistory(workloadUpdateDTO);
		}*/
		workloadHistoryService.editWorkloadHistory(workloadUpdateDTO);
	}

	public List<WorkloadEventDTO.Recently> getWorkloadRecentlyEventReason(List<String> workloadNames,
		String workspace) {
		Map<String, Event> workloadRecentlyEvent = workloadModuleService.getWorkloadRecentlyEvent(workloadNames,
			workspace);
		return workloadRecentlyEvent.entrySet().stream()
			.map(key -> {
				if (key.getValue() == null) {
					return WorkloadEventDTO.Recently.builder()
						.workload(key.getKey())
						.build();
				}
				return WorkloadEventDTO.Recently.builder()
					.workload(key.getKey())
					.type(key.getValue().getType())
					.reason(key.getValue().getReason())
					.build();
			})
			.toList();
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
			Comparator<Event> ageComparator = Comparator
				.comparing((Event event) -> event.getMetadata().getCreationTimestamp())
				.thenComparingLong(event -> Long.parseLong(event.getMetadata().getResourceVersion()));
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
				.reason(event.getReason())
				.from(event.getReportingController())
				.age(new AgeDTO(DateUtils.convertK8sUtcTimeString(event.getMetadata().getCreationTimestamp())))
				.message(event.getNote())
				.build())
			.toList();

		return new PageDTO<>(result, workloadEventReqDTO.getPageNum(), workloadEventReqDTO.getPageSize());
	}

	private List<AbstractModuleWorkloadResDTO> filterNormalWorkloads(List<AbstractModuleWorkloadResDTO> workloadList,
		String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, String userId) {
		// 사용자가 추가한 PIN 목록을 가져옵니다.
		Set<String> userWorkloadPinList = getUserWorkloadPinList(userId); //workload resourceNames
		// PIN이 없는 워크로드를 필터링합니다.
		//전체 워크로드 리스트에서 pin을 제외한 항목만 추출
		List<AbstractModuleWorkloadResDTO> normalWorkloadList = filterPinnedWorkloads(workloadList, userWorkloadPinList,
			false);

		//필터링 및 정렬 적용
		return applyWorkloadListCondition(normalWorkloadList, searchName, workloadStatus, workloadSortCondition);
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
	private List<AbstractModuleWorkloadResDTO> filterPinnedWorkloads(List<AbstractModuleWorkloadResDTO> workloadList,
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
	private void markPinnedWorkloads(List<WorkloadSummaryDTO> workloadList) {
		workloadList.forEach(workload -> workload.updatePinYN(true));
	}

	private List<AbstractModuleWorkloadResDTO> applyWorkloadListCondition(
		List<AbstractModuleWorkloadResDTO> workloadList,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition) {

		Stream<AbstractModuleWorkloadResDTO> workloadStream = workloadList.stream()
			.filter(batch -> searchName == null || (batch.getName() != null && batch.getName().contains(searchName)))
			.filter(batch -> workloadStatus == null || batch.getStatus() == workloadStatus);

		if (sortCondition != null) {
			return switch (sortCondition) {
				case AGE_ASC ->
					workloadStream.sorted(Comparator.comparing(AbstractModuleWorkloadResDTO::getCreatedAt)).toList();
				case AGE_DESC ->
					workloadStream.sorted(Comparator.comparing(AbstractModuleWorkloadResDTO::getCreatedAt).reversed())
						.toList();
				case REMAIN_TIME_ASC ->
					workloadStream.sorted(Comparator.comparing(AbstractModuleWorkloadResDTO::getRemainTime)).toList();
				case REMAIN_TIME_DESC ->
					workloadStream.sorted(Comparator.comparing(AbstractModuleWorkloadResDTO::getRemainTime).reversed())
						.toList();
			};
		} else {
			return workloadStream.toList();
		}
	}

	private void stopBatchJobWorkload(String workSpaceName, String workloadName, UserDTO.UserInfo userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
				WorkloadType.BATCH);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteBatchJobWorkload(workSpaceName, workloadName);

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

	private void stopInteractiveJobWorkload(String workSpaceName, String workloadName,
		UserDTO.UserInfo userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
				WorkloadType.INTERACTIVE);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}

	/*private void setDatasetVolume(String workspaceName, List<ModuleVolumeReqDTO> list) {
		for (ModuleVolumeReqDTO moduleVolumeReqDTO : list) {
			Dataset findDataset = datasetService.findById(moduleVolumeReqDTO.getId());
			DatasetDTO.ResDatasetWithStorage resDatasetWithStorage = DatasetDTO.ResDatasetWithStorage.toDto(
				findDataset);
			if (resDatasetWithStorage.getDivision() == RepositoryDivision.ASTRAGO) {
				String storagePath = resDatasetWithStorage.getStoragePath();
				String saveDirectoryName = resDatasetWithStorage.getSaveDirectoryName();

				// storagePath의 끝이 '/'로 끝나는지 여부 확인
				if (!storagePath.endsWith(File.separator)) {
					storagePath += File.separator;
				}

				String filePath = storagePath + saveDirectoryName;

				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resDatasetWithStorage.getIp(),
					filePath,
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
				String storagePath = resModelWithStorage.getStoragePath();
				String saveDirectoryName = resModelWithStorage.getSaveDirectoryName();

				// storagePath의 끝이 '/'로 끝나는지 여부 확인
				if (!storagePath.endsWith(File.separator)) {
					storagePath += File.separator;
				}

				String filePath = storagePath + saveDirectoryName;
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resModelWithStorage.getIp(),
					filePath,
					resModelWithStorage.getStorageType());
			} else {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resModelWithStorage.getIp(),
					resModelWithStorage.getStoragePath(), resModelWithStorage.getStorageType());
			}
		}
	}*/

	private void setVolumes(String workspaceName, List<ModuleVolumeReqDTO> list) {
		for (ModuleVolumeReqDTO moduleVolumeReqDTO : list) {
			Volume findVolume = volumeService.findById(moduleVolumeReqDTO.getId());
			VolumeResDTO.ResVolumeWithStorage resVolumeWithStorage = VolumeResDTO.ResVolumeWithStorage.toDto(
				findVolume);
			if (resVolumeWithStorage.getDivision() == RepositoryDivision.ASTRAGO) {
				String storagePath = resVolumeWithStorage.getStoragePath();
				String saveDirectoryName = resVolumeWithStorage.getSaveDirectoryName();

				// storagePath의 끝이 '/'로 끝나는지 여부 확인
				if (!storagePath.endsWith(File.separator)) {
					storagePath += File.separator;
				}
				String filePath = storagePath + saveDirectoryName;
				StorageEntity storageEntity = storageService.getStorageClassByVolumeId(moduleVolumeReqDTO.getId());

				if(storageEntity.getStorageType().equals(StorageType.DELL_UNITY)){
					moduleVolumeReqDTO.setSubPath(saveDirectoryName);
				}

				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resVolumeWithStorage.getIp(),
					filePath,
					resVolumeWithStorage.getStorageType(), storageEntity.getVolumeName(), storageEntity.getArrayId(), storageEntity.getDellVolumeId());
			} else {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resVolumeWithStorage.getIp(),
					resVolumeWithStorage.getStoragePath(), resVolumeWithStorage.getStorageType(), "", "", "");
			}
		}
	}

	private static void setPvAndPVC(String workspaceName, ModuleVolumeReqDTO moduleVolumeReqDTO, String ip,
		String storagePath, StorageType storageType, String volumeName, String arrayId, String dellVolumeId) {
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
			.arrayId(arrayId)
			.dellVolumeId(dellVolumeId)
			.build();
		moduleVolumeReqDTO.setCreatePV(createPV);
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(workspaceName)
			.requestVolume(requestVolume)
			.volumeName(volumeName)
			.build();
		moduleVolumeReqDTO.setCreatePVC(createPVC);
	}

	public boolean workloadMkdir(String workloadName, String workspaceName, WorkloadType workloadType, String path) {
		return workloadModuleService.mkdirToWorkload(workloadName, workspaceName, workloadType, path);
	}

	private <T extends AbstractModuleWorkloadResDTO> FindWorkloadResDTO.Image generateImageResDTO(T moduleJobResDTO) {
		if (StringUtils.hasText(String.valueOf(moduleJobResDTO.getImageType()))) {
			if (moduleJobResDTO.getImageId() == null && moduleJobResDTO.getImageType().equals(ImageType.CUSTOM)) {
				return createCustomTypeImageDTO(moduleJobResDTO);
			} else {
				return createOtherTypeImageDTO(moduleJobResDTO);
			}
		} else {
			throw new RestApiException(WorkloadErrorCode.FAILED_LOAD_IMAGE_INFO);
		}
	}

	private CredentialResDTO.CredentialInfo getCredentialInfoDTO(Long credentialId) {
		if (!ValidUtils.isNullOrZero(credentialId)) {
			return credentialService.findCredentialById(credentialId, null);
		} else {
			return null;
		}
	}

	private <T extends AbstractModuleWorkloadResDTO> FindWorkloadResDTO.Image createCustomTypeImageDTO(
		T moduleJobResDTO) {
		CredentialResDTO.CredentialInfo findCredential = getCredentialInfoDTO(moduleJobResDTO.getImageCredentialId());

		return FindWorkloadResDTO.Image.customTypeImageResDTO()
			.regUserId(moduleJobResDTO.getCreatorId())
			.regUserName(moduleJobResDTO.getCreatorUserName())
			.regUserRealName(moduleJobResDTO.getCreatorFullName())
			.regDate(moduleJobResDTO.getCreatedAt())
			.title(moduleJobResDTO.getImage())
			.name(moduleJobResDTO.getImage())
			.type(moduleJobResDTO.getImageType())
			.repositoryAuthType(RepositoryAuthType.PUBLIC)
			.credentialId(findCredential != null ? findCredential.getId() : null)
			.credentialName(findCredential != null ? findCredential.getName() : null)
			.build();
	}

	private <T extends AbstractModuleWorkloadResDTO> FindWorkloadResDTO.Image createOtherTypeImageDTO(
		T moduleJobResDTO) {
		ImageResDTO.FindImage findImage = imageService.findImageById(moduleJobResDTO.getImageId());
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
					.deleteYN(findModel.getDeleteYn())
					.build();
				models.add(modelVol);
			}
		}

		return models;
	}

	private List<FindWorkloadResDTO.Volume> generateVolumeResDTO(String ids, Map<Long, String> mountMap) {
		List<FindWorkloadResDTO.Volume> datasets = new ArrayList<>();
		if (StringUtils.hasText(ids)) {
			String[] splitIds = ids.split(",");
			for (String s : splitIds) {
				long volumeId = Long.parseLong(s);
				Volume findVolume = volumeService.findById(volumeId);
				FindWorkloadResDTO.Volume datasetVol = FindWorkloadResDTO.Volume.volumeResDTO()
					.regUserId(findVolume.getRegUser().getRegUserId())
					.regUserName(findVolume.getRegUser().getRegUserName())
					.regUserRealName(findVolume.getRegUser().getRegUserRealName())
					.regDate(findVolume.getRegDate())
					.modDate(findVolume.getModDate())
					.id(findVolume.getVolumeId())
					.name(findVolume.getVolumeName())
					.mountPath(mountMap.get(findVolume.getVolumeId()))
					.size(findVolume.getVolumeSize())
					.division(findVolume.getDivision())
					.storageType(findVolume.isAstragoVolume() ?
						((AstragoVolumeEntity)findVolume).getStorageEntity().getStorageType() :
						((LocalVolumeEntity)findVolume).getStorageType())
					.deleteYN(findVolume.getDeleteYn())
					.build();
				datasets.add(datasetVol);
			}
		}
		return datasets;
	}

	private <T extends AbstractModuleWorkloadResDTO> List<FindWorkloadResDTO.Code> generateCodeResDTO(
		T moduleJobResDTO) {
		List<FindWorkloadResDTO.Code> codes = new ArrayList<>();
		if (!CollectionUtils.isEmpty(moduleJobResDTO.getCodes())) {
			for (ModuleCodeResDTO code : moduleJobResDTO.getCodes()) {
				FindWorkloadResDTO.Code addCode = null;
				CredentialResDTO.CredentialInfo findCredential = getCredentialInfoDTO(code.getCredentialId());
				// 커스텀 코드일 경우
				if (ValidUtils.isNullOrZero(code.getSourceCodeId())
					&& code.getRepositoryType() == RepositoryType.CUSTOM) {
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
				} else if (code.getRepositoryType() == RepositoryType.USER
					|| code.getRepositoryType() == RepositoryType.WORKSPACE) {    // 공유 코드일 경우
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
		}
		return codes;
	}

	private boolean isOverResource(float workspaceResourceUsed, float createWorkloadResourceUsed,
		float workspaceResourceLimit) {
		float totalUsed = workspaceResourceUsed + createWorkloadResourceUsed;
		return totalUsed > workspaceResourceLimit;
	}

	private <T extends AbstractModuleWorkloadResDTO> List<FindWorkloadResDTO.Port> generatePortResDTO(
		T moduleJobResDTO) {
		Optional<ResponseDTO.NodeDTO> node = getConnectedNode();
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
					.map(port -> new FindWorkloadResDTO.Port(port.getName(), port.getOriginPort(),
						node.get().getIp() + ":" + portMap.get(port.getOriginPort())))
					.toList();
			}
		} else {
			ports = moduleJobResDTO.getPorts()
				.stream()
				.map(port -> new FindWorkloadResDTO.Port(port.getName(), port.getOriginPort(), null))
				.toList();
		}

		return ports;
	}

	private <T extends AbstractModuleWorkloadResDTO> void updatePortResDTO(Optional<ResponseDTO.NodeDTO> node,
		T moduleJobResDTO) {
		if (node.isPresent()) {
			// 서비스 포트 찾기
			SvcResDTO.FindSvcs findSvcs = svcModuleFacadeService.getServicesByResourceName(
				moduleJobResDTO.getWorkspaceResourceName(), moduleJobResDTO.getResourceName());

			for (SvcResDTO.FindSvcDetail findSvcDetail : findSvcs.getServices()) {
				List<ModulePortResDTO> modulePortResDTOS = findSvcDetail.getPorts()
					.stream()
					.map(port -> ModulePortResDTO.builder()
						.name(port.getName())
						.originPort(port.getPort())
						.url(node.get().getIp() + ":" + port.getNodePort())
						.build())
					.toList();
				moduleJobResDTO.updatePort(modulePortResDTOS);
			}
		}
	}

	private Optional<ResponseDTO.NodeDTO> getConnectedNode() {
		ResponseDTO.PageNodeDTO nodeList = nodeService.getNodeList(1, 1);
		return nodeList.getNodes().stream().findFirst();
	}

	public PageDTO<WorkloadSummaryDTO> getAdminOverViewWorkloadList(WorkloadType workloadType, String workspaceName,
		String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, int pageNum, Boolean isCreatedByMe,
		UserDTO.UserInfo userInfoDTO) {
		//통합용 리스트 선언
		List<WorkloadSummaryDTO> workloadResDTOList = new ArrayList<>();

		PageRequest pageRequest = PageRequest.of(pageNum - 1, 8);
		OverViewWorkloadResDTO<WorkloadSummaryDTO> overViewWorkloadResDTO = workloadHistoryService.getAdminWorkloadList(
			workspaceName, workloadType, searchName, isCreatedByMe, workloadStatus, workloadSortCondition, pageRequest);
		//page 계산
		int totalSize = (int)overViewWorkloadResDTO.getTotalSize();
		int totalPageNum = (int)Math.ceil(totalSize / (double)10);
		workloadResDTOList.addAll(overViewWorkloadResDTO.getContent());
		workloadResDTOList.forEach(wl -> wl.updateCanBeDeleted(true));
		return new PageDTO<>(totalSize, totalPageNum, workloadResDTOList);
	}

	public FindWorkloadResDTO getAdminWorkloadInfoByResourceName(WorkloadType workloadType,
		String workspaceName,
		String workloadResourceName, UserDTO.UserInfo userInfoDTO) {

		FindWorkloadResDTO workloadInfo = workloadHistoryService.getAdminWorkloadInfoByResourceName(
			workspaceName, workloadResourceName,
			userInfoDTO);
		ResponseDTO.NodeDTO connectedNode = getConnectedNode().get();
		String ip = connectedNode.getIp();
		List<FindWorkloadResDTO.Port> ports = workloadInfo.getPorts().stream().map(port ->
			new FindWorkloadResDTO.Port(port.getName(), port.getPort(), port.getTargetPort(),ip + ":" + port.getTargetPort())).toList();
		workloadInfo.setPorts(ports);
		try {
			if (workloadInfo.getImage().getType() == ImageType.HUB) {
				ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
					workloadResourceName);
				workloadInfo.updateHubPredictTime(moduleBatchJobResDTO.getEstimatedInitialTime(),
					moduleBatchJobResDTO.getEstimatedRemainingTime());
			}

			return workloadInfo;
		} catch (Exception e) {
			return workloadInfo;
		}

		// 실행중일 떄
		// try {
		// 	UserDTO.UserInfo userInfo = userFacadeService.getUserById(userInfoDTO.getId());
		// 	Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);
		// 	// String nodeName = workspaceService.getNodeName(workspaceName, workloadResourceName);
		// 	if (workloadType == WorkloadType.BATCH) {
		// 		ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleFacadeService.getBatchWorkload(workspaceName,
		// 			workloadResourceName);
		// 		// 삭제권한 업데이트
		// 		moduleBatchJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), userInfo.getMyWorkspaces());
		//
		// 		return getActiveWorkloadDetail(moduleBatchJobResDTO);
		// 	} else if (workloadType == WorkloadType.INTERACTIVE) {
		// 		ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
		// 			workspaceName, workloadResourceName);
		// 		//삭제권한 업데이트
		// 		moduleInteractiveJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);
		// 		return getActiveWorkloadDetail(moduleInteractiveJobResDTO);
		// 	} else if (workloadType == WorkloadType.DISTRIBUTED) {
		// 		ModuleDistributedJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getDistributedWorkload(
		// 			workspaceName, workloadResourceName);
		// 		moduleInteractiveJobResDTO.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);
		// 		return getActiveWorkloadDetail(moduleInteractiveJobResDTO);
		// 	}
		// } catch (Exception e) {
		// 	try {
		// 		return workloadHistoryService.getWorkloadInfoByResourceName(workspaceName, workloadResourceName,
		// 			userInfoDTO);
		// 	} catch (Exception e2) {
		// 		throw e2;
		// 	}
		// }
		// return null;
	}

	public Page<ExperimentDTO> getExperiments(String searchCondition, String workspace, String userId,
		WorkloadStatus status,
		Pageable pageable) {
		return workloadHistoryService.getExperiments(searchCondition, workspace, userId, status, pageable);
	}

	public void updateExperimentViewYN(List<String> experimentUUIDs, boolean isViewYN) {
		workloadHistoryService.updateExperimentViewYN(experimentUUIDs, isViewYN);
	}
}
