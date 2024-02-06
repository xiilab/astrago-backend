package com.xiilab.modulek8s.workload.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BatchJobVO extends WorkloadVO {
	private List<JobEnvVO> envs;        //env 정의
	private List<JobPortVO> ports;        //port 정의
	private String command;        // 워크로드 명령
	private String jobName;

	@Override
	public Job createResource() {
		return new JobBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	// 메타데이터 정의
	@Override
	public ObjectMeta createMeta() {
		jobName = getUniqueResourceName();
		return new ObjectMetaBuilder()
			.withName(jobName)
			.withNamespace(workspace)
			.withAnnotations(
				Map.of(
					AnnotationField.NAME.getField(), getName(),
					AnnotationField.DESCRIPTION.getField(), getDescription(),
					AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString(),
					AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorName(),
					AnnotationField.TYPE.getField(), getWorkloadType().getType(),
					AnnotationField.IMAGE_NAME.getField(), getImage().name(),
					AnnotationField.IMAGE_TAG.getField(), getImage().tag(),
					AnnotationField.IMAGE_TYPE.getField(), getImage().imageType().getType()
				))
			.withLabels(
				getLabelMap()
			)
			.build();
	}

	private Map<String, String> getLabelMap() {
		Map<String, String> map = new HashMap<>();

		map.put(LabelField.CREATOR.getField(), getCreator());
		map.put(LabelField.CONTROL_BY.getField(), "astra");
		map.put(LabelField.APP.getField(), jobName);
		map.put(LabelField.JOB_NAME.getField(), jobName);
		this.volumes.forEach(volume -> map.put(volume.name(), "true"));

		return map;
	}

	// 스펙 정의
	@Override
	public JobSpec createSpec() {
		return new JobSpecBuilder()
			// .withNewSelector().withMatchLabels(Map.of(LabelField.APP.getField(), jobName)).endSelector()
			.withTtlSecondsAfterFinished(1000)
			.withNewTemplate()
			.withNewMetadata()
			.withLabels(Map.of(LabelField.APP.getField(), jobName)).endMetadata()
			.withSpec(createPodSpec())
			.endTemplate()
			.build();
	}

	// 파드 및 잡 상세 스펙 정의
	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		// 스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());
		if (this.secretName != null && this.secretName.length() > 0) {
			podSpecBuilder.addNewImagePullSecret(secretName);
		}
		cloneGitRepo(podSpecBuilder, codes);
		addVolumes(podSpecBuilder, volumes);

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withRestartPolicy("Never")
			.addNewContainer()
			.withName(getUniqueResourceName())
			.withImage(image.name() + ":" + image.tag());

		addContainerPort(podSpecContainer);
		addContainerEnv(podSpecContainer);
		addContainerCommand(podSpecContainer);
		addVolumeMount(podSpecContainer);
		addContainerSourceCode(podSpecContainer);
		addContainerResource(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	private void addVolumeMount(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.forEach(volume -> podSpecContainer
				.addNewVolumeMount()
				.withName(volume.name())
				.withMountPath(volume.mountPath())
				.endVolumeMount());
		}
	}

	private void addContainerResource(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.withNewResources()
			.addToLimits(getWorkloadResourceMap())
			.endResources();
	}

	/**
	 * init컨테이너와 연결된 볼륨을 컨테이너와 연결
	 *
	 * @param podSpecContainer
	 */
	private void addContainerSourceCode(
		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (!CollectionUtils.isEmpty(codes)) {
			AtomicInteger index = new AtomicInteger(1);
			codes.forEach(codeReq ->
				podSpecContainer
					.addNewVolumeMount()
					.withName("git-clone-" + index.getAndIncrement())
					.withMountPath(codeReq.mountPath())
					.endVolumeMount());
		}
	}

	private void addContainerCommand(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (StringUtils.isNotBlank(command)) {
			podSpecContainer.addAllToCommand(convertCmd());
		}
	}

	private void addContainerEnv(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (envs != null && !envs.isEmpty()) {
			podSpecContainer.addAllToEnv(convertEnv());
		}
	}

	private void addContainerPort(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (ports != null && !ports.isEmpty()) {
			podSpecContainer.addAllToPorts(convertContainerPort());
		}
	}

	@Override
	public List<ContainerPort> convertContainerPort() {
		return ports.stream()
			.map(port -> new ContainerPortBuilder()
				.withName(port.name())
				.withContainerPort(port.port())
				.build()
			).toList();
	}

	@Override
	public List<EnvVar> convertEnv() {
		return envs.stream()
			.map(env -> new EnvVarBuilder()
				.withName(env.varName())
				.withValue(env.value())
				.build()
			).toList();
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
