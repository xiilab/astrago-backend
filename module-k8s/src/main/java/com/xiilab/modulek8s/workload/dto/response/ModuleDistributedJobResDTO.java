package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;

import org.kubeflow.v2beta1.MPIJob;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Containers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.InitContainers;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.ResourceDTO;
import com.xiilab.modulek8s.common.enumeration.DistributedJobRole;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ModuleDistributedJobResDTO extends ModuleWorkloadResDTO {
	public ModuleDistributedJobResDTO(MPIJob mpiJob) {
		super(mpiJob);
		Containers container = mpiJob.getSpec()
			.getMpiReplicaSpecs()
			.get(DistributedJobRole.LAUNCHER.getName())
			.getTemplate()
			.getSpec()
			.getContainers()
			.get(0);
		// 리소스 정보
		Integer mpiJobReplicasCount = K8sInfoPicker.getMpiJobReplicasCount(mpiJob);
		ResourceDTO containersResourceReq = K8sInfoPicker.getContainersResourceReq(container, mpiJobReplicasCount);
		this.cpuRequest = String.valueOf(containersResourceReq.getCpuReq());
		this.memRequest = String.valueOf(containersResourceReq.getMemReq());
		this.gpuRequest = String.valueOf(containersResourceReq.getGpuReq());
		// 데이터셋, 모델 마운트 패스 정보
		super.initializeVolumeMountPath(mpiJob.getMetadata().getAnnotations());
		InitContainers mpiJobInitContainers = K8sInfoPicker.getMpiJobInitContainers(mpiJob);
		if (mpiJobInitContainers != null) {
			// 코드 정보
			super.codes = initializeCodesInfoFromInitContainers(List.of(mpiJobInitContainers));
		}
		super.image = container.getImage();
		super.envs = container.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		super.ports = container.getPorts().stream()
			.map(port -> ModulePortResDTO.builder().name(port.getName()).originPort(port.getContainerPort()).build())
			.toList();
		this.workingDir = container.getWorkingDir();
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null :
			container.getCommand().get(container.getCommand().size() - 1);
		super.status = K8sInfoPicker.getDistributedWorkloadStatus(mpiJob.getStatus());
	}

	private List<ModuleCodeResDTO> initializeCodesInfoFromInitContainers(List<InitContainers> initContainers) {
		return initContainers.stream()
			.map(initContainer -> new ModuleCodeResDTO(initContainer.getEnv())).toList();
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.DISTRIBUTED;
	}
}
