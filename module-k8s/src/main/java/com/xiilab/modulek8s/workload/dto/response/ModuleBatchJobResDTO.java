package com.xiilab.modulek8s.workload.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ModuleBatchJobResDTO extends ModuleWorkloadResDTO {
	public ModuleBatchJobResDTO(Job job) {
		super(job);
		Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
		// 리소스 정보
		super.initializeResources(container.getResources().getLimits());
		// 데이터셋, 모델 마운트 패스 정보
		super.initializeVolumeMountPath(job.getSpec().getTemplate().getMetadata().getAnnotations());
		// 코드 정보
		super.codes = initializeCodesInfo(job.getSpec().getTemplate().getSpec().getInitContainers());
		super.image = container.getImage();
		super.envs = container.getEnv().stream()
			.map(env -> new ModuleEnvResDTO(env.getName(), env.getValue()))
			.toList();
		super.ports = container.getPorts().stream()
			.map(port -> ModulePortResDTO.builder().name(port.getName()).originPort(port.getContainerPort()).build())
			.toList();
		this.workingDir = container.getWorkingDir();
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2);
		super.status = K8sInfoPicker.getBatchWorkloadStatus(job.getStatus());
		// 파드 시작 시간
		// .subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
		// 최초 종료 시간 예측
		Optional.ofNullable(job.getMetadata()
				.getAnnotations()
				.getOrDefault(AnnotationField.ESTIMATED_INITIAL_TIME.getField(), null))
			.ifPresent(estimatedInitialTimeString -> super.estimatedInitialTime = createdAt.plusSeconds(
				Long.parseLong(estimatedInitialTimeString)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
		// 실시간 종료 시간 예측
		Optional.ofNullable(job.getMetadata()
				.getAnnotations()
				.getOrDefault(AnnotationField.ESTIMATED_REMAINING_TIME.getField(), null))
			.ifPresent(estimatedRemainingTime -> super.estimatedRemainingTime = LocalDateTime.now().plusSeconds(
					Long.parseLong(estimatedRemainingTime)).format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}
}
