package com.xiilab.modulek8s.workload.vo;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.workload.enums.SchedulingType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.PodTemplateSpec;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetBuilder;
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpec;
import io.fabric8.kubernetes.api.model.apps.StatefulSetSpecBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DistributedJobVO extends WorkloadVO {
	private static final String ASTRAGO_HOROVOD_SSH_KEY = "astrago-horovod-ssh-key";
	private static final String ASTRAGO_HOROVOD_SCRIPT = "astrago-horovod-script";
	private List<JobEnvVO> envs;        //env 정의
	private List<JobPortVO> ports;        //port 정의
	private String workingDir;        // 명령어를 실행 할 path
	private String command;        // 워크로드 명령
	private Map<String, String> parameter;        // 사용자가 입력한 hyper parameter
	private String jobName;

	@PostConstruct
	void createUniqueName() {
		this.jobName = getUniqueResourceName();
	}

	@Override
	public JobSpec createSpec() {
		return new JobSpecBuilder()
			.withTtlSecondsAfterFinished(10)
			.withNewTemplate()
			.withNewMetadata()
			.withAnnotations(getPodAnnotationMap())
			.withLabels(Map.of(LabelField.APP.getField(), jobName)).endMetadata()
			.withSpec(createPodSpec())
			.endTemplate()
			.withBackoffLimit(0)
			.build();
	}

	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		podSpecBuilder.withHostname("astrago");
		// 스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());
		if (!ObjectUtils.isEmpty(this.secretName)) {
			podSpecBuilder.addNewImagePullSecret(this.secretName);
		}
		cloneGitRepo(podSpecBuilder, this.codes);
		addDefaultVolume(podSpecBuilder);
		addVolumes(podSpecBuilder, this.datasets);
		addVolumes(podSpecBuilder, this.models);

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withRestartPolicy("Never")
			.withTerminationGracePeriodSeconds(20L)
			.addNewContainer()
			.withName(this.jobName)
			.withImage(image.name());

		addContainerPort(podSpecContainer);
		addContainerEnv(podSpecContainer);
		addContainerCommand(podSpecContainer);
		addDefaultVolumeMountPath(podSpecContainer);
		addVolumeMount(podSpecContainer, datasets);
		addVolumeMount(podSpecContainer, models);
		addContainerSourceCode(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	@Override
	public List<ContainerPort> convertContainerPort() {
		return List.of();
	}

	@Override
	public List<EnvVar> convertEnv() {
		return List.of();
	}

	@Override
	public List<String> convertCmd() {
		return List.of();
	}

	@Override
	public WorkloadType getWorkloadType() {
		return WorkloadType.DISTRIBUTED;
	}

	@Override
	public HasMetadata createResource() {
		return new JobBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(jobName)
			.withNamespace(workspace)
			.withAnnotations(
				getAnnotationMap()
			).withLabels(
				getLabelMap()
			)
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKLOAD;
	}

	public StatefulSet createWorker() {
		return new StatefulSetBuilder()
			.withMetadata(createWorkderMeta())
			.withSpec(createWorkderSpec())
			.build();
	}

	private ObjectMeta createWorkderMeta() {
		return new ObjectMetaBuilder()
			.withName(jobName + "-worker")
			.withNamespace(workspace)
			.withAnnotations(getAnnotationMap())
			.withLabels(getLabelMap())
			.build();
	}

	private StatefulSetSpec createWorkderSpec() {
		return new StatefulSetSpecBuilder()
			.withReplicas(this.gpuRequest)
			.withPodManagementPolicy("Parallel")
			.withTemplate(createWorkerPodTemplateSpec())
			.build();
	}

	private PodTemplateSpec createWorkerPodTemplateSpec() {
		return new PodTemplateSpecBuilder()
			.withSpec(createWorkerPodSpec())
			.build();
	}

	private PodSpec createWorkerPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		podSpecBuilder.withHostname("astrago");
		//스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());

		if (!ObjectUtils.isEmpty(this.secretName)) {
			podSpecBuilder.addNewImagePullSecret(this.secretName);
		}

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withRestartPolicy("Never")
			.withTerminationGracePeriodSeconds(20L)
			.addNewContainer()
			.withName(this.jobName + "worker")
			.withImage(image.name());

		addContainerPort(podSpecContainer);
		addContainerEnv(podSpecContainer);
		addContainerCommand(podSpecContainer);
		addDefaultVolumeMountPath(podSpecContainer);
		addVolumeMount(podSpecContainer, datasets);
		addVolumeMount(podSpecContainer, models);
		addContainerSourceCode(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	private void addDefaultVolumeMountPath(
		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.addNewVolumeMount()
			.withName("shmdir")
			.withMountPath("/dev/shm")
			.endVolumeMount();
		// podSpecContainer.addNewVolumeMount()
		// 	.withName("tz-seoul")
		// 	.withMountPath("/etc/localtime")
		// 	.endVolumeMount();
	}

	private void addVolumeMount(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer,
		List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.forEach(volume -> podSpecContainer
				.addNewVolumeMount()
				.withName(volume.pvName())
				.withMountPath(volume.mountPath())
				.endVolumeMount());
		}
	}

	private void addContainerResource(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.withNewResources()
			.addToRequests(getWorkloadResourceMap())
			.addToLimits(getWorkloadResourceMap())
			.endResources();
	}

	private ResourceRequirements getLauncherResourceRequirements() {
		return new ResourceRequirementsBuilder()
			.addToRequests("cpu", new Quantity(String.valueOf(cpuRequest)))
			.addToRequests("cpu", new Quantity(String.valueOf(cpuRequest)))
			.build();
	}

	private ResourceRequirements getWorkerResourceRequirements() {
		return new ResourceRequirementsBuilder()
			.addToRequests(getWorkloadResourceMap())
			.addToRequests(getWorkloadResourceMap())
			.build();
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
					.withSubPath("code")
					.endVolumeMount());
		}
	}

	private void addContainerCommand(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (StringUtils.isNotBlank(command)) {
			podSpecContainer.addAllToCommand(convertCmd());
		}
		if (StringUtils.isNotBlank(workingDir)) {
			podSpecContainer.withWorkingDir(workingDir);
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

	private Map<String, String> getAnnotationMap() {
		String imageCredentialId = "";
		if (getImage() != null && getImage().credentialVO() != null && !ObjectUtils.isEmpty(
			getImage().credentialVO().credentialId())) {
			imageCredentialId = String.valueOf(getImage().credentialVO().credentialId());
		}

		Map<String, String> annotationMap = new HashMap<>();
		annotationMap.put(AnnotationField.NAME.getField(), getName());
		annotationMap.put(AnnotationField.DESCRIPTION.getField(), getDescription());
		annotationMap.put(AnnotationField.WORKSPACE_NAME.getField(), getWorkspaceName());
		annotationMap.put(AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString());
		annotationMap.put(AnnotationField.CREATOR_USER_NAME.getField(), getCreatorUserName());
		annotationMap.put(AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorFullName());
		annotationMap.put(AnnotationField.TYPE.getField(), getWorkloadType().name());
		annotationMap.put(AnnotationField.IMAGE_NAME.getField(), getImage().name());
		annotationMap.put(AnnotationField.IMAGE_TYPE.getField(), getImage().imageType().name());
		annotationMap.put(AnnotationField.IMAGE_CREDENTIAL_ID.getField(), imageCredentialId);
		annotationMap.put(AnnotationField.DATASET_IDS.getField(), getJobVolumeIds(this.datasets));
		annotationMap.put(AnnotationField.MODEL_IDS.getField(), getJobVolumeIds(this.models));
		annotationMap.put(AnnotationField.CODE_IDS.getField(), getJobCodeIds(this.codes));
		annotationMap.put(AnnotationField.IMAGE_ID.getField(), ValidUtils.isNullOrZero(getImage().id()) ?
			"" : String.valueOf(getImage().id()));
		annotationMap.put(AnnotationField.PARAMETER.getField(), JsonConvertUtil.convertMapToJson(this.parameter));

		return annotationMap;
	}

	private Map<String, String> getLabelMap() {
		Map<String, String> map = new HashMap<>();

		map.put(LabelField.CREATOR_ID.getField(), getCreatorId());
		map.put(LabelField.CONTROL_BY.getField(), "astra");
		map.put(LabelField.APP.getField(), jobName);
		map.put(LabelField.JOB_NAME.getField(), jobName);
		this.datasets.forEach(dataset -> addVolumeMap(map, "ds-", dataset.id()));
		this.models.forEach(model -> addVolumeMap(map, "md-", model.id()));
		this.codes.forEach(code -> addVolumeMap(map, "cd-", code.id()));

		return map;
	}

	private void addVolumeMap(Map<String, String> map, String prefix, Long id) {
		if (!ValidUtils.isNullOrZero(id)) {
			map.put(prefix + id, "true");
		}
	}
}
