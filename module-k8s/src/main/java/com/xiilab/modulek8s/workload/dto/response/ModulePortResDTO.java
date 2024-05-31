package com.xiilab.modulek8s.workload.dto.response;

import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Ports;

import io.fabric8.kubernetes.api.model.ContainerPort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ModulePortResDTO {
	private String name;
	private Integer originPort;
	private String url;

	public ModulePortResDTO(Object object) {
		if (object instanceof Ports portInstance) {
			this.name = portInstance.getName();
			this.originPort = portInstance.getContainerPort();
		} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Ports portInstance) {
			this.name = portInstance.getName();
			this.originPort = portInstance.getContainerPort();
		} else if (object instanceof Ports portInstance) {
			this.name = portInstance.getName();
			this.originPort = portInstance.getContainerPort();
		} else if (object instanceof ContainerPort portInstance) {
			this.name = portInstance.getName();
			this.originPort = portInstance.getContainerPort();
		}
	}
}
