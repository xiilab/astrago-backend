package com.xiilab.modulek8s.workload.dto;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@SuperBuilder
public class JobReqDTO extends WorkloadReq {
	private List<CodeDTO> codeReqs;

	@Override
	public Job createResource() {
		return new JobBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	public Deployment updateResource(Deployment deployment) {
		return new DeploymentBuilder(deployment)
				.editMetadata()
				.withAnnotations(Map.of("description", "description"))
				.endMetadata()
				.build();
	}

	@Override
	public ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(name)
			.withNamespace(workspace)
			.withAnnotations(Map.of(
				"description", description,
				"creatorId", creatorId
			))
			.build();
	}

	@Override
	public JobSpec createSpec() {
		return new JobSpecBuilder()
			.withTtlSecondsAfterFinished(1000)
			.withNewTemplate()
			.withSpec(createPodSpec())
			.endTemplate()
			.build();
	}

	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		cloneGitRepo(podSpecBuilder, codeReqs);
		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withRestartPolicy("Never")
			.addNewContainer()
			.withName(name)
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
		codeReqs.forEach(codeReq ->
			podSpecContainer.addNewVolumeMount()
				.withName(name + "-git-clone-" + index.getAndIncrement())
				.withMountPath(codeReq.mountPath())
				.endVolumeMount());

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
	public WorkloadType getType() {
		return WorkloadType.BATCH;
	}
}
