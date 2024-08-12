package com.xiilab.modulek8s.workload.dto.response;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractSingleWorkloadResDTO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ModuleDeployResDTO extends AbstractSingleWorkloadResDTO {
	private Integer replica;
	private DeployType deployType;
	private Long deployModelId;
	public ModuleDeployResDTO(Deployment deployment) {
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
		super.ports = container.getPorts().stream()
			.map(ModulePortResDTO::new)
			.toList();
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2);
		super.status = K8sInfoPicker.getInteractiveWorkloadStatus(deployment.getStatus());
		this.ide = deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField()) == null ? "CUSTOM" :
			deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField());
		this.replica = deployment.getSpec().getReplicas();
		this.deployType = DeployType.valueOf(
			deployment.getMetadata().getAnnotations().get(AnnotationField.DEPLOY_TYPE.getField()));
		this.deployModelId = Long.valueOf(
			deployment.getMetadata().getAnnotations().get(AnnotationField.DEPLOY_MODEL_ID.getField()));
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.DEPLOY;
	}
}
