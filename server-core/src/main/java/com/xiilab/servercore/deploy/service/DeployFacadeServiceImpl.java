package com.xiilab.servercore.deploy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.DeployErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.service.K8sVolumeService;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.deploy.dto.CreateDeployReqDTO;
import com.xiilab.servercore.modelrepo.service.ModelRepoFacadeService;
import com.xiilab.servercore.node.service.NodeFacadeService;
import com.xiilab.servercore.storage.service.StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class DeployFacadeServiceImpl {

	private final NodeFacadeService nodeFacadeService;
	private final NetworkRepository networkRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final WorkspaceService workspaceService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ModelRepoFacadeService modelRepoFacadeService;
	private final StorageService storageService;
	private final K8sVolumeService k8sVolumeService;

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
				modelPath = storageHostPath + "/" + modelPath;  // /root/kube-storage/Astrago_real_storage-b3-5aba-475a-9969-78e5c7b1d73a/workspaces/ws-e95611a8-25e2-4219-97c6-60b7d4363f1d/models/model-4330c838-87a0-46e5-9bbb-0cb7c4e39662/v1/yolov8.onnx
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
				k8sVolumeService.createPV(createPV);
				//pvc 생성
				createPVC = CreatePVC.builder()
					.pvcName(pvcName)
					.namespace(createDeployReqDTO.getWorkspaceResourceName())
					.requestVolume(defaultStorage.getRequestVolume())
					.build();
				k8sVolumeService.createPVC(createPVC);
				ModuleVolumeReqDTO volume = ModuleVolumeReqDTO.builder()
					.mountPath("/models")
					.createPV(createPV)
					.createPVC(createPVC)
					.build();
				//ports 설정
				List<ModulePortReqDTO> ports = new ArrayList<>();
				ports.add(new ModulePortReqDTO("HTTP", 8000));
				ports.add(new ModulePortReqDTO("GRPC", 8001));
				ports.add(new ModulePortReqDTO("PROMETHEUS", 8002));
				createDeployReqDTO.setPorts(ports);

				//triton deployment 생성
				CreateJobResDTO deployWorkload = workloadModuleFacadeService.createDeployWorkload(
					createDeployReqDTO.toTritonModuleDTO(List.of(volume)));
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
		}

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
}
