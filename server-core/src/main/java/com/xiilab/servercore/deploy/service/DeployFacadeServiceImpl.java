package com.xiilab.servercore.deploy.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
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
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DeployErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.deploy.dto.request.ModifyDeployDTO;
import com.xiilab.modulek8s.deploy.dto.request.ModuleCreateDeployReqDTO;
import com.xiilab.modulek8s.deploy.repository.DeployK8sRepository;
import com.xiilab.modulek8s.facade.svc.SvcModuleFacadeService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.service.K8sVolumeService;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleCodeResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractSingleWorkloadResDTO;
import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.deploy.dto.DeploySearchCondition;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;
import com.xiilab.modulek8sdb.deploy.repository.DeployRepository;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.volume.entity.AstragoVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.LocalVolumeEntity;
import com.xiilab.modulek8sdb.volume.entity.Volume;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.PortRepository;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.service.CodeService;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.service.CredentialService;
import com.xiilab.servercore.deploy.dto.CreateDeployReqDTO;
import com.xiilab.servercore.deploy.dto.ResDeploys;
import com.xiilab.servercore.deploy.dto.ResReplica;
import com.xiilab.servercore.image.dto.ImageResDTO;
import com.xiilab.servercore.image.service.ImageService;
import com.xiilab.servercore.modelrepo.service.ModelRepoFacadeService;
import com.xiilab.servercore.node.service.NodeFacadeService;
import com.xiilab.servercore.node.service.NodeService;
import com.xiilab.servercore.storage.service.StorageService;
import com.xiilab.servercore.user.service.UserFacadeService;
import com.xiilab.servercore.volume.dto.VolumeResDTO;
import com.xiilab.servercore.volume.service.VolumeService;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeployFacadeServiceImpl {

	private final NodeFacadeService nodeFacadeService;
	private final ApplicationEventPublisher eventPublisher;
	private final WorkspaceService workspaceService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ModelRepoFacadeService modelRepoFacadeService;
	private final StorageService storageService;
	private final K8sVolumeService k8sVolumeService;
	private final DeployRepository deployRepository;
	private final DeployK8sRepository deployK8sRepository;
	private final CredentialService credentialService;
	private final VolumeService volumeService;
	private final ImageService imageService;
	private final SvcModuleFacadeService svcModuleFacadeService;
	private final NodeService nodeService;
	private final CodeService codeService;
	private final UserFacadeService userFacadeService;
	private final PortRepository portRepository;
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final NetworkRepository networkRepository;
	@Value("${astrago.private-registry-url}")
	private String privateRegistryUrl;

	@Transactional
	public void createDeploy(CreateDeployReqDTO createDeployReqDTO, MultipartFile tritonConfigFile, UserDTO.UserInfo userInfoDTO) {
		createDeployReqDTO.setUserInfo(userInfoDTO.getId(), userInfoDTO.getUserName(), userInfoDTO.getUserFullName());

		// MPS 노드 지정
		if (!Objects.isNull(createDeployReqDTO.getGpuType()) && createDeployReqDTO.getGpuType() == GPUType.MPS) {
			if (StringUtils.hasText(createDeployReqDTO.getNodeName())) {
				createDeployReqDTO.setNodeName(getMpsNodeName(createDeployReqDTO.getNodeName()));
			}
		}
		if (createDeployReqDTO.getDeployType() == DeployType.TRITON) {
			CreatePV createPV = null;
			CreatePVC createPVC = null;
			String deployJobResourceName = null;
			String deployDirectoryPath = null;
			try {
				String modelFileName = null;
				// 리소스 초과 알림
				checkAndSendWorkspaceResourceOverAlert(createDeployReqDTO, userInfoDTO);
				//모델 파일 -> triton model repository copy
				//model로 storage 조회
				ModelRepoEntity findModel = modelRepoFacadeService.getModelRepoEntityById(
					createDeployReqDTO.getModelId());
				String modelPath = findModel.getModelPath(); // /workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/models/model-4330c838-87a0-46e5-9bbb-0cb7c4e39662
				String modelConfigPath = findModel.getModelPath();
				List<String> modelConfigPaths = new ArrayList<>();
				boolean verionChk = false;
				for (ModelVersionEntity modelVersionEntity : findModel.getModelVersionList()) {
					if (modelVersionEntity.getVersion().equalsIgnoreCase(createDeployReqDTO.getModelVersion())) {
						modelFileName = modelVersionEntity.getModelFileName();
						modelPath +=
							"/" + modelVersionEntity.getVersion() + "/" + modelFileName; // /workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/models/model-4330c838-87a0-46e5-9bbb-0cb7c4e39662/v1/yolov8.onnx
						for (String modelConfigName : createDeployReqDTO.getModelConfigNames()) {
							modelConfigPaths.add(
								modelConfigPath + "/" + modelVersionEntity.getVersion() + "/" + modelConfigName);
						}
						verionChk = true;
					}
				}
				if (!verionChk) {
					throw new RestApiException(DeployErrorCode.NOT_FOUND_MODEL_VERSION);
				}
				String storageHostPath = findModel.getStorageEntity().getHostPath();
				String dbSaveModelPath = modelPath;
				modelPath = storageHostPath + "/" + modelPath;
				// /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a/workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/models/model-4330c838-87a0-46e5-9bbb-0cb7c4e39662/v1/yolov8.onnx
				// 모델 외 파일은 추후 작성
				// default storage 조회 후 해당 경로에 triton model 디렉토리 생성
				String deployUUID = "deploy-" + UUID.randomUUID().toString().substring(6);
				// String deployUUID = "deploy-" + "test";
				StorageEntity defaultStorage = storageService.getDefaultStorage();
				deployDirectoryPath = defaultStorage.getHostPath() + "/workspaces/" + createDeployReqDTO.getWorkspaceResourceName()
					+ "/deploy/" + deployUUID; // /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a/workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/deploy/uuid
				String tritonDirectoryRootPath =
					deployDirectoryPath + "/models/" + createDeployReqDTO.getModelId() + "/model_repository/";
				// /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a/workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/deploy/uuid/models/1/model_repository/
				String tritonModelRootPath = tritonDirectoryRootPath + findModel.getModelName(); // /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a/workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/deploy/uuid/models/1/model_repository/yolov8
				// String tritonModelRootPath = "/Users/leeyoungchun/kube-storage/test";
				String tritonModelDirectoryPath = tritonModelRootPath + "/1";
				FileUtils.createFolders(Path.of(tritonModelDirectoryPath));
				// 모델을 트리톤 디렉토리로 copy
				FileUtils.copyFile(Path.of(modelPath), Path.of(tritonModelDirectoryPath));
				FileUtils.renameFile(tritonModelDirectoryPath + "/" + modelFileName, tritonModelDirectoryPath + "/model.onnx");

				// 모델에 필요한 파일들을 트리톤 디렉토리로 copy
				for (String configPath : modelConfigPaths) {
					FileUtils.copyFile(Path.of(storageHostPath + "/" + configPath), Path.of(tritonModelRootPath));
				}
				//triton config.pbtxt 생성 후 tritonDirectoriesPath 경로에 저장
				Path configPath = Path.of(tritonModelRootPath);
				Path resolvePath = configPath.resolve("config.pbtxt");
				if (!(tritonConfigFile == null || tritonConfigFile.isEmpty())) {
					tritonConfigFile.transferTo(resolvePath);
				}
				if(createDeployReqDTO.getTritonConfigText() != null){
					Files.write(resolvePath, createDeployReqDTO.getTritonConfigText().getBytes());
				}

				//pv 생성
				String pvName = "triton-pv-" + deployUUID;
				String pvcName = "triton-pvc-" + deployUUID;
				String storagePath = defaultStorage.getStoragePath() + "/workspaces/" + createDeployReqDTO.getWorkspaceResourceName()
					+ "/deploy/" + deployUUID + "/models/" + createDeployReqDTO.getModelId() + "/model_repository/";
				createPV = CreatePV.builder()
					.pvName(pvName)
					.pvcName(pvcName)
					.ip(defaultStorage.getIp())
					.storagePath(storagePath)
					.storageType(defaultStorage.getStorageType())
					.requestVolume(defaultStorage.getRequestVolume())
					.namespace(createDeployReqDTO.getWorkspaceResourceName())
					.build();
				// k8sVolumeService.createPV(createPV);
				//pvc 생성
				createPVC = CreatePVC.builder()
					.pvcName(pvcName)
					.namespace(createDeployReqDTO.getWorkspaceResourceName())
					.requestVolume(defaultStorage.getRequestVolume())
					.build();
				// k8sVolumeService.createPVC(createPVC);
				ModuleVolumeReqDTO volume = ModuleVolumeReqDTO.builder()
					.mountPath("/models")
					.createPV(createPV)
					.createPVC(createPVC)
					.build();
				//ports 설정
				List<ModulePortReqDTO> ports = new ArrayList<>();
				ports.add(new ModulePortReqDTO("http", 8000));
				ports.add(new ModulePortReqDTO("grpc", 8001));
				ports.add(new ModulePortReqDTO("prometheus", 8002));
				createDeployReqDTO.setPorts(ports);

				//triton deployment 생성
				ImageEntity builtImage = imageService.findBuiltImageByName(
					"nvcr.io/nvidia/tritonserver:23.02-py3");
				ModuleImageReqDTO imageReqDTO = ModuleImageReqDTO.builder()
					.id(builtImage.getId())
					.name(builtImage.getImageName())
					.type(builtImage.getImageType())
					.repositoryAuthType(builtImage.getRepositoryAuthType())
					.build();
				CreateJobResDTO deployWorkload = workloadModuleFacadeService.createDeployWorkload(
					createDeployReqDTO.toTritonModuleDTO(List.of(volume), imageReqDTO, findModel.getStorageEntity().getStorageId(), dbSaveModelPath));
				deployJobResourceName = deployWorkload.getResourceName();
			}
			catch (IOException e) {
				log.error(e.toString());
				//생성된 directory 삭제
				if(deployDirectoryPath != null){
					FileUtils.deleteAllDirectory(deployDirectoryPath);
				}
				throw new RestApiException(DeployErrorCode.FAILED_CREATE_DEPLOY);
			}
			catch (RuntimeException e){
				log.error(e.toString());
				//생성된 deployment & svc 삭제
				if(deployJobResourceName != null){
					workloadModuleFacadeService.deleteDeployment(createDeployReqDTO.getWorkspaceResourceName(), deployJobResourceName);
				}
				if(createPVC != null){
					k8sVolumeService.deletePVC(createPVC.getPvcName(), createPVC.getNamespace());
				}
				if(createPV != null){
					k8sVolumeService.deletePV(createPV.getPvName());
				}
				//생성된 directory 삭제
				if(deployDirectoryPath != null){
					FileUtils.deleteAllDirectory(deployDirectoryPath);
				}
				throw new RestApiException(DeployErrorCode.FAILED_CREATE_DEPLOY);
			}
		}else{
			// 이미지 credential 세팅
			if (!ObjectUtils.isEmpty(createDeployReqDTO.getImage().getCredentialId())
				&& createDeployReqDTO.getImage().getCredentialId() > 0) {
				setImageCredentialReqDTO(createDeployReqDTO.getImage(), userInfoDTO);
			}
			// 볼륨 추가
			if (!CollectionUtils.isEmpty(createDeployReqDTO.getVolumes())) {
				setVolumes(createDeployReqDTO.getWorkspaceResourceName(), createDeployReqDTO.getVolumes());
			}

			try {
				NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
				// 커스텀 이미지일 때만 이미지 데이터 저장
				// workloadModuleFacadeService.createJobWorkload(moduleCreateWorkloadReqDTO.toModuleDTO(network.getInitContainerURL()));
				// 리소스 초과 알림
				log.info("폐쇄망 : " + network.getNetworkCloseYN());
				checkAndSendWorkspaceResourceOverAlert(createDeployReqDTO, userInfoDTO);
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
				if(createDeployReqDTO.getImage().getType() != ImageType.CUSTOM && networkCloseYN == NetworkCloseYN.Y){
					if(isBlankSafe(privateRegistryUrl)){
						imageName = createDeployReqDTO.getImage().getName();
					}else{
						imageName = privateRegistryUrl + "/" + createDeployReqDTO.getImage().getName();
					}
				}else{
					imageName = createDeployReqDTO.getImage().getName();
				}
				ModuleImageReqDTO image = createDeployReqDTO.getImage();
				image.modifyName(imageName);
				ModuleCreateDeployReqDTO userModuleDTO = createDeployReqDTO.toUserModuleDTO(initContainerUrl);
				userModuleDTO.modifyImage(image);
				CreateJobResDTO deployWorkload = workloadModuleFacadeService.createDeployWorkload(userModuleDTO);
			} catch (Exception e) {
				log.error(e.toString());
				//생성된 리소스들 삭제해야함
				//추 후
				throw new RestApiException(DeployErrorCode.FAILED_CREATE_DEPLOY);
			}

		}

	}

	// null 체크와 함께 isBlank를 수행하는 메서드
	public static boolean isBlankSafe(String str) {
		return str == null || str.isBlank();
	}
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
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resVolumeWithStorage.getIp(),
					filePath,
					resVolumeWithStorage.getStorageType());
			} else {
				setPvAndPVC(workspaceName, moduleVolumeReqDTO, resVolumeWithStorage.getIp(),
					resVolumeWithStorage.getStoragePath(), resVolumeWithStorage.getStorageType());
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
	private void setImageCredentialReqDTO(ModuleImageReqDTO moduleImageReqDTO, UserDTO.UserInfo userInfoDTO) {
		CredentialResDTO.CredentialInfo findCredential = credentialService.findCredentialById(
			moduleImageReqDTO.getCredentialId(), userInfoDTO);
		moduleImageReqDTO.setCredentialReqDTO(findCredential.toModuleCredentialReqDTO());
	}
	private void checkAndSendWorkspaceResourceOverAlert(CreateDeployReqDTO createDeployReqDTO,
		UserDTO.UserInfo userInfoDTO) {
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			createDeployReqDTO.getWorkspaceResourceName());
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
			createDeployReqDTO.getCpuRequest(),
			workspaceResourceStatus.getResourceStatus().getCpuLimit());
		// GPU
		boolean isGpuOverResource = isOverResource(workspaceResourceStatus.getResourceStatus().getGpuUsed(),
			createDeployReqDTO.getGpuRequest(), workspaceResourceStatus.getResourceStatus().getGpuLimit());

		boolean isMemOverResource = isOverResource(memUsed,
			createDeployReqDTO.getMemRequest(),
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
					PageNaviParam.builder()
						.workspaceResourceName(createDeployReqDTO.getWorkspaceResourceName())
						.build(),
					null));

		}
	}

	private boolean isOverResource(float workspaceResourceUsed, float createWorkloadResourceUsed,
		float workspaceResourceLimit) {
		float totalUsed = workspaceResourceUsed + createWorkloadResourceUsed;
		return totalUsed > workspaceResourceLimit;
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

	public ResDeploys getDeploys(String workspaceResourceName, DeploySearchCondition deploySearchCondition) {
		PageRequest pageRequest = PageRequest.of(deploySearchCondition.getPageNo(), deploySearchCondition.getPageSize());
		PageImpl<DeployEntity> deploys = deployRepository.getDeploys(workspaceResourceName, deploySearchCondition,
			pageRequest);
		List<DeployEntity> content = deploys.getContent();
		long totalCount = deploys.getTotalElements();
		return ResDeploys.entitiesToDtos(content, totalCount);
	}

	public List<ResReplica> getReplicasByDeployResourceName(String workspaceResourceName, String deployResourceName) {
		List<ResReplica> replicas = new ArrayList<>();
		List<Pod> pods = deployK8sRepository.getReplicasByDeployResourceName(
			workspaceResourceName, deployResourceName);
		for (int i = 0; i < pods.size(); i++) {
			String replicaName = "replica_" + (i + 1);
			String podResourceName = pods.get(i).getMetadata().getName();
			String nodeName = pods.get(i).getSpec().getNodeName();
			replicas.add(new ResReplica(replicaName, podResourceName, nodeName));
		}
		return replicas;
	}

	public void stopDeploy(String workspaceResourceName, String deployResourceName, UserDTO.UserInfo userInfoDTO) throws
		IOException {
		FindWorkloadResDTO activeSingleWorkloadDetail = null;
		ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleFacadeService.getInteractiveWorkload(
			workspaceResourceName, deployResourceName);
		activeSingleWorkloadDetail = getActiveWorkloadDetail(moduleInteractiveJobResDTO);
		stopInteractiveJobWorkload(workspaceResourceName, deployResourceName, userInfoDTO);

		if (!ObjectUtils.isEmpty(activeSingleWorkloadDetail)) {
			PageNaviParam pageNaviParam = PageNaviParam.builder()
				.workspaceResourceName(activeSingleWorkloadDetail.getWorkSpaceResourceName())
				.workloadResourceName(activeSingleWorkloadDetail.getWorkloadResourceName())
				.workloadType(activeSingleWorkloadDetail.getWorkloadType())
				.build();

			//서비스 종료 알림 발송
			String emailTitle = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMailTitle(), deployResourceName);
			String title = AlertMessage.WORKLOAD_END_CREATOR.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMessage(),
				activeSingleWorkloadDetail.getWorkloadName());

			String receiverMail = userFacadeService.getUserInfoById(activeSingleWorkloadDetail.getRegUserId())
				.getEmail();
			MailDTO mailDTO = MailServiceUtils.endWorkloadMail(activeSingleWorkloadDetail.getWorkloadName(),
				receiverMail);

			WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
				AlertName.USER_WORKLOAD_END, userInfoDTO.getId(), activeSingleWorkloadDetail.getRegUserId(), emailTitle,
				title, message, workspaceResourceName, pageNaviParam, mailDTO);
			eventPublisher.publishEvent(workspaceUserAlertEvent);
		}

	}

	private void stopInteractiveJobWorkload(String workSpaceName, String workloadName,
		UserDTO.UserInfo userInfoDTO) throws
		IOException {
		try {
			String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
				WorkloadType.DEPLOY);
			FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		} catch (KubernetesClientException | K8sException ignored) {

		}
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}
	private <T extends AbstractModuleWorkloadResDTO> FindWorkloadResDTO getActiveWorkloadDetail(
		T moduleJobResDTO) {
		String nodeName = workspaceService.getNodeName(moduleJobResDTO.getWorkspaceResourceName(),
			moduleJobResDTO.getResourceName());
		// 이미지 DTO 세팅
		FindWorkloadResDTO.Image image = generateImageResDTO(moduleJobResDTO);
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

		return FindWorkloadResDTO.SingleWorkloadDetail.from((AbstractSingleWorkloadResDTO)moduleJobResDTO, image,
			volumes, codes, ports, envs,
			nodeName);

	}
	private Optional<ResponseDTO.NodeDTO> getConnectedNode() {
		ResponseDTO.PageNodeDTO nodeList = nodeService.getNodeList(1, 1);
		return nodeList.getNodes().stream().findFirst();
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
	private CredentialResDTO.CredentialInfo getCredentialInfoDTO(Long credentialId) {
		if (!ValidUtils.isNullOrZero(credentialId)) {
			return credentialService.findCredentialById(credentialId, null);
		} else {
			return null;
		}
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

	@Transactional
	public void deleteDeploy(String deployResourceName, UserDTO.UserInfo userInfoDTO) {
		Optional<DeployEntity> deployEntity = deployRepository.findByResourceName(deployResourceName);
		if(deployEntity.isPresent()){
			DeployEntity deploy = deployEntity.get();
			WorkloadEntity jobEntity = workloadHistoryRepo.findById(deploy.getId()).get();
			// owner 권한인 워크스페이스 목록 가져옴
			List<String> loginUserOwnerWorkspaceList = userInfoDTO.getWorkspaces()
				.stream()
				.filter(workspace -> workspace.contains("/owner"))
				.map(workspace -> workspace.split("/owner")[0])
				.toList();
			String workloadName = deploy.getName();
			WorkspaceUserAlertEvent workspaceUserAlertEvent = null;
			// 워크로드 생성자가 삭제
			if (deploy.getCreatorId().equals(userInfoDTO.getId())) {
				String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMailTitle(), workloadName);
				String title = AlertMessage.WORKLOAD_DELETE_CREATOR.getTitle();
				String message = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMessage(), workloadName);
				workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
					userInfoDTO.getId(), deploy.getCreatorId(), emailTitle, title, message,
					deploy.getWorkspaceResourceName(), null, null);

			} else if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN || loginUserOwnerWorkspaceList.contains(
				deploy.getWorkspaceResourceName())) {    // 관리자 또는 워크스페이스 생성자가 삭제
				String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMailTitle(), workloadName);
				String title = AlertMessage.WORKLOAD_DELETE_ADMIN.getTitle();
				String message = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMessage(),
					userInfoDTO.getUserFullName(), userInfoDTO.getEmail(), workloadName);
				String receiverMail = userFacadeService.getUserInfoById(deploy.getCreatorId()).getEmail();
				MailDTO mailDTO = MailServiceUtils.deleteWorkloadMail(deploy.getName(), receiverMail);
				workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
					userInfoDTO.getId(), deploy.getCreatorId(), emailTitle, title, message,
					deploy.getWorkspaceResourceName(), null, mailDTO);
			} else {
				throw new RestApiException(WorkloadErrorCode.FAILED_DELETE_DEPLOY);
			}
			workloadHistoryRepo.deleteByWorkloadId(jobEntity.getId());
			imageService.deleteImageWorkloadMapping(deploy.getId());
			codeService.deleteCodeWorkloadMapping(deploy.getId());
			volumeService.deleteVolumeWorkloadMappingByDeployId(deploy.getId());
			eventPublisher.publishEvent(workspaceUserAlertEvent);
		}
	}

	public byte[] getDeployEndLog(String deployResourceName, UserDTO.UserInfo userInfoDTO) {
		//저장된 로그 path 구하기
		String logPath = FileUtils.getUserLogPath(userInfoDTO.getId(), deployResourceName);
		try {
			return Files.readAllBytes(Path.of(logPath));
		} catch (IOException e) {
			throw new RestApiException(WorkloadErrorCode.NOT_FOUND_JOB_LOG);
		}
	}

	public ResDeploys.DeployInfo getDeployInfo(String workspaceResourceName, String deployResourceName, UserDTO.UserInfo userInfoDTO) {
		WorkloadEntity workloadEntity = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workspaceResourceName, deployResourceName)
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_DEPLOY_INFO));
		// 삭제된 워크로드는 다른 에러메시지 처리
		if (workloadEntity.getDeleteYN() == DeleteYN.Y) {
			throw new RestApiException(WorkloadErrorCode.DELETED_DEPLOY_INFO);
		}
		Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);
		workloadEntity.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);
		return ResDeploys.DeployInfo.from((DeployEntity) workloadEntity);
	}

	@Transactional
	public void modifyDeploy(ModifyDeployDTO modifyDeployDTO, UserDTO.UserInfo userInfoDTO) {
		Optional<DeployEntity> deployEntity = deployRepository.findByResourceName(modifyDeployDTO.getDeployResourceName());
		if(deployEntity.isPresent()) {
			DeployEntity deploy = deployEntity.get();
			// owner 권한인 워크스페이스 목록 가져옴
			List<String> loginUserOwnerWorkspaceList = userInfoDTO.getWorkspaces()
				.stream()
				.filter(workspace -> workspace.contains("/owner"))
				.map(workspace -> workspace.split("/owner")[0])
				.toList();
			if (deploy.getCreatorId().equals(userInfoDTO.getId())) {
				deploy.modifyNameAndDescription(modifyDeployDTO.getName(), modifyDeployDTO.getDescription());

			} else if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN || loginUserOwnerWorkspaceList.contains(
				deploy.getWorkspaceResourceName())) {    // 관리자 또는 워크스페이스 생성자가 수정
				deploy.modifyNameAndDescription(modifyDeployDTO.getName(), modifyDeployDTO.getDescription());
			} else {
				throw new RestApiException(WorkloadErrorCode.FAILED_MODIFY_DEPLOY);
			}
		}else{
			throw new RestApiException(WorkloadErrorCode.FAILED_LOAD_DEPLOY_INFO);
		}
	}
}
