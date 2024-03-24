package com.xiilab.modulek8s.workload.dto.response;

import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ModuleInteractiveJobResDTO extends ModuleWorkloadResDTO {

	public ModuleInteractiveJobResDTO(Deployment deployment) {
		super(deployment);
		Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
		// 리소스 정보
		super.initializeResources(container.getResources().getLimits());
		// 데이터셋, 모델 마운트 패스 정보
		super.initializeVolumeMountPath(deployment.getSpec().getTemplate().getMetadata().getAnnotations());
		// 코드 정보
		super.codes = initializeCodesInfo(deployment.getSpec().getTemplate().getSpec().getInitContainers());
		super.image = container.getImage();
		super.envs = container.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2);
		super.status = getWorkloadStatus(deployment.getStatus());
		this.ide = deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField()) == null ? "CUSTOM" :
			deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField());
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.INTERACTIVE;
	}

	private WorkloadStatus getWorkloadStatus(DeploymentStatus deploymentStatus) {
		int replicas = deploymentStatus.getReplicas() == null ? 0 : deploymentStatus.getReplicas();
		int availableReplicas =
			deploymentStatus.getAvailableReplicas() == null ? 0 : deploymentStatus.getAvailableReplicas();
		int unavailableReplicas =
			deploymentStatus.getUnavailableReplicas() == null ? 0 : deploymentStatus.getUnavailableReplicas();
		int updateReplicas = deploymentStatus.getUpdatedReplicas() == null ? 0 : deploymentStatus.getUpdatedReplicas();
		if (unavailableReplicas > 0 && updateReplicas > 0) {
			return WorkloadStatus.ERROR;
		} else if (unavailableReplicas > 0) {
			return WorkloadStatus.PENDING;
		} else if (replicas == availableReplicas) {
			return WorkloadStatus.RUNNING;
		} else {
			return WorkloadStatus.PENDING;
		}
	}

	public void updatePort(String nodeIp, Service service) {
		if (Objects.nonNull(service) && Objects.nonNull(service.getSpec().getPorts())) {
			List<ServicePort> servicePorts = service.getSpec().getPorts();
			this.ports = servicePorts.stream().map(servicePort -> ModulePortResDTO.builder()
				.name(servicePort.getName())
				.originPort(servicePort.getPort())
				.url(String.format("%s:%s", nodeIp, servicePort.getNodePort()))
				.build()).toList();
		}
	}
}
