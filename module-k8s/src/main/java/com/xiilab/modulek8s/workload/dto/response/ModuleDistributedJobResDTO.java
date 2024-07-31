package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;

import org.kubeflow.v2beta1.MPIJob;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Containers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.InitContainers;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.DistributedResourceDTO;
import com.xiilab.modulek8s.common.dto.ResourceDTO;
import com.xiilab.modulek8s.common.enumeration.DistributedJobRole;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractDistributedWorkloadResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ModuleDistributedJobResDTO extends AbstractDistributedWorkloadResDTO {

	public ModuleDistributedJobResDTO(MPIJob mpiJob) {
		super(mpiJob);
		Containers launcherContainers = getLauncherContainers(mpiJob);
		// 데이터셋, 모델 마운트 패스 정보
		super.initializeVolumeMountPath(
			mpiJob.getSpec().getMpiReplicaSpecs().get("Launcher").getTemplate().getMetadata().getAnnotations());
		InitContainers mpiJobInitContainers = K8sInfoPicker.getMpiJobInitContainers(mpiJob);
		if (mpiJobInitContainers != null) {
			// 코드 정보
			super.codes = initializeCodesInfoFromInitContainers(List.of(mpiJobInitContainers));
		}
		super.image = launcherContainers.getImage();
		super.envs = launcherContainers.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		super.ports = launcherContainers.getPorts().stream()
			.map(port -> ModulePortResDTO.builder().name(port.getName()).originPort(port.getContainerPort()).build())
			.toList();
		super.workingDir = launcherContainers.getWorkingDir();
		super.command = CollectionUtils.isEmpty(launcherContainers.getCommand()) ? null :
			launcherContainers.getCommand().get(launcherContainers.getCommand().size() - 1);
		super.status = K8sInfoPicker.getDistributedWorkloadStatus(mpiJob.getStatus());
		this.launcherInfo = getLauncherContainerInfo(launcherContainers);
		this.workerInfo = getWorkerContainerInfo(mpiJob);
	}

	private List<ModuleCodeResDTO> initializeCodesInfoFromInitContainers(List<InitContainers> initContainers) {
		List<ModuleCodeResDTO> moduleCodeResDTOS = initContainers.stream()
			.map(initContainer -> new ModuleCodeResDTO(initContainer.getEnv())).toList();
		initializeCodeMountPath(moduleCodeResDTOS);
		return moduleCodeResDTOS;
	}

	private DistributedResourceDTO.LauncherInfo getLauncherContainerInfo(Containers containers) {
		ResourceDTO containersResourceReq = K8sInfoPicker.getContainersResourceReq(containers);
		return DistributedResourceDTO.LauncherInfo.builder()
			.cpuRequest(containersResourceReq.getCpuReq())
			.memRequest(containersResourceReq.getMemReq())
			.build();
	}

	private DistributedResourceDTO.WorkerInfo getWorkerContainerInfo(MPIJob mpiJob) {
		Containers workerContainers = getWorkerContainers(mpiJob);
		Integer mpiJobReplicasCount = K8sInfoPicker.getMpiJobReplicasCount(mpiJob);
		ResourceDTO containersResourceReq = K8sInfoPicker.getContainersResourceReq(workerContainers);
		return DistributedResourceDTO.WorkerInfo.builder()
			.cpuRequest(containersResourceReq.getCpuReq())
			.memRequest(containersResourceReq.getMemReq())
			.gpuRequest(containersResourceReq.getGpuReq())
			.workerCnt(mpiJobReplicasCount)
			.build();
	}

	private Containers getLauncherContainers(MPIJob mpiJob) {
		return mpiJob.getSpec()
			.getMpiReplicaSpecs()
			.get(DistributedJobRole.LAUNCHER.getName())
			.getTemplate()
			.getSpec()
			.getContainers()
			.get(0);
	}

	private Containers getWorkerContainers(MPIJob mpiJob) {
		return mpiJob.getSpec()
			.getMpiReplicaSpecs()
			.get(DistributedJobRole.WORKER.getName())
			.getTemplate()
			.getSpec()
			.getContainers()
			.get(0);
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.DISTRIBUTED;
	}
}
