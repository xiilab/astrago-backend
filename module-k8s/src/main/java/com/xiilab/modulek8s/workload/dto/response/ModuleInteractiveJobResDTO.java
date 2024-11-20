package com.xiilab.modulek8s.workload.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractSingleWorkloadResDTO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@SuperBuilder
@Slf4j
public class ModuleInteractiveJobResDTO extends AbstractSingleWorkloadResDTO {
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
		super.ports = container.getPorts().stream()
			.map(ModulePortResDTO::new)
			.toList();
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2);
		super.status = K8sInfoPicker.getInteractiveWorkloadStatus(deployment.getStatus());
		this.ide = deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField()) == null ? "CUSTOM" :
			deployment.getMetadata().getAnnotations().get(AnnotationField.IDE.getField());
		

		// deployment 에서 EXPIRATION_TIME Annotations 을 읽어와서 디비에 적재한다.
		try {
			super.expirationTime = 
				deployment.getMetadata().getAnnotations().get(AnnotationField.EXPIRATION_TIME.getField()) == null || 
				deployment.getMetadata().getAnnotations().get(AnnotationField.EXPIRATION_TIME.getField()) == "" 
				? null: LocalDateTime.parse(deployment.getMetadata().getAnnotations().get(AnnotationField.EXPIRATION_TIME.getField()) , DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
			log.info("생성된 잡의 종료예정시간은 {} 입니다" , super.expirationTime);
		} catch (Exception e) {
			log.error("날짜 파싱간 에러 발생" , e);
			super.expirationTime = null;
		}

	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.INTERACTIVE;
	}
}
