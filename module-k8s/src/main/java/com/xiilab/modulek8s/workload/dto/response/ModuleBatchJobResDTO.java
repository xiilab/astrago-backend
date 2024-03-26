package com.xiilab.modulek8s.workload.dto.response;

import java.time.LocalDateTime;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
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
		super.command = CollectionUtils.isEmpty(container.getCommand()) ? null : container.getCommand().get(2);
		super.status = getWorkloadStatus(job.getStatus());
		// // 최초 종료 시간 예측
		// if (hasMetadata.getMetadata().getAnnotations().containsKey(AnnotationField.ESTIMATED_INITIAL_TIME.getField())) {
		// 	String estimatedInitialTimeString = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.ESTIMATED_INITIAL_TIME);
		// 	if (!ObjectUtils.isEmpty(estimatedInitialTimeString)) {
		// 		long estimatedInitialTimeSeconds = Long.valueOf(estimatedInitialTimeString);
		// 		super.estimatedInitialTime = createdAt.plusSeconds(estimatedInitialTimeSeconds);
		// 	}
		// }
		// // 실시간 종료 시간 예측
		// if (hasMetadata.getMetadata().getAnnotations().containsKey(AnnotationField.ESTIMATED_REMAINING_TIME.getField())) {
		// 	String estimatedRemainingTime = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.ESTIMATED_REMAINING_TIME);
		// 	if (!ObjectUtils.isEmpty(estimatedRemainingTime)) {
		// 		long estimatedRemainingTimeSeconds = Long.valueOf(estimatedRemainingTime);
		// 		super.estimatedInitialTime = LocalDateTime.now().plusSeconds(estimatedRemainingTimeSeconds);
		// 	}
		// }
	}

	@Override
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}

	private WorkloadStatus getWorkloadStatus(JobStatus jobStatus) {
		Integer active = jobStatus.getActive();
		Integer failed = jobStatus.getFailed();
		Integer ready = jobStatus.getReady();
		if (!NumberValidUtils.isNullOrZero(failed)) {
			return WorkloadStatus.ERROR;
		} else if (!NumberValidUtils.isNullOrZero(ready)) {
			return WorkloadStatus.RUNNING;
		} else if (ready == 0 || NumberValidUtils.isNullOrZero(active) && NumberValidUtils.isNullOrZero(failed) && NumberValidUtils.isNullOrZero(ready)) {
			return WorkloadStatus.PENDING;
		} else {
			return WorkloadStatus.END;
		}
	}
}
