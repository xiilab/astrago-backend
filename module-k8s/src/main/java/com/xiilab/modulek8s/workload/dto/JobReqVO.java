package com.xiilab.modulek8s.workload.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class JobReqVO extends WorkloadReqVO {
	private List<CodeDTO> codeReqs;

	@Override
	public Job createResource() {
		return new JobBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	@Override
	public ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getResourceName())
			.withNamespace(workspace)
			.withAnnotations(
				Map.of(
					AnnotationField.NAME.getField(), getName(),
					AnnotationField.DESCRIPTION.getField(), getDescription(),
					AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString(),
					AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorName()
				))
			.withLabels(
				Map.of(
					LabelField.CREATOR.getField(), getCreator(),
					LabelField.IMAGE.getField(), image
				))
			.build();
	}

	@Override
	public JobSpec createSpec() {
		return new JobSpecBuilder()
			.withTtlSecondsAfterFinished(100)
			.withNewTemplate()
			.withSpec(createPodSpec())
			.endTemplate()
			.build();
	}

	// 컨테이너 정보
	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		cloneGitRepo(podSpecBuilder, codeReqs);
		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withRestartPolicy("Never")
			.addNewContainer()
			.withName(getResourceName())
			.withImage(image);

		if (port != null && !port.isEmpty()) {
			podSpecContainer.addAllToPorts(convertContainerPort());
		}

		if (env != null && !env.isEmpty()) {
			podSpecContainer.addAllToEnv(convertEnv());
		}

		if (StringUtils.isNotBlank(command)) {
			podSpecContainer.addAllToCommand(convertCmd());
		}

		AtomicInteger index = new AtomicInteger(1);

		if (codeReqs != null && codeReqs.size() > 0) {
			codeReqs.forEach(codeReq ->
				podSpecContainer.addNewVolumeMount()
					.withName(getResourceName() + "-git-clone-" + index.getAndIncrement())
					.withMountPath(codeReq.mountPath())
					.endVolumeMount());
		}
		// podSpecContainer.withVolumeMounts();
		VolumeMount mount = new VolumeMountBuilder().withName("test-name").withMountPath("/con").build();

		return podSpecContainer.endContainer().build();
	}

	@Override
	public List<ContainerPort> convertContainerPort() {
		return port.entrySet().stream().map(
				e -> new ContainerPortBuilder()
					.withName(e.getKey())
					.withContainerPort(e.getValue())
					.build())
			.toList();
	}

	@Override
	public List<EnvVar> convertEnv() {
		return env.entrySet().stream().map(
				e -> new EnvVarBuilder()
					.withName(e.getKey())
					.withValue(e.getValue())
					.build())
			.toList();
	}

	@Override
	public List<String> convertCmd() {
		return List.of("sh", "-c", command);
	}

	@Override
	public WorkloadType getWorkloadType() {
		return WorkloadType.BATCH;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKLOAD;
	}
}