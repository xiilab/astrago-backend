package com.xiilab.modulek8s.workload.dto.response;

import io.fabric8.kubernetes.api.model.EnvVar;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ModuleEnvResDTO {
	private String name;
	private String value;

	public ModuleEnvResDTO(Object object) {
		if (object instanceof EnvVar env) {
			this.name = env.getName();
			this.value = env.getValue();
		} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env env) {
			this.name = env.getName();
			this.value = env.getValue();
		} else if (object instanceof org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env env) {
			this.name = env.getName();
			this.value = env.getValue();
		}
	}
}
