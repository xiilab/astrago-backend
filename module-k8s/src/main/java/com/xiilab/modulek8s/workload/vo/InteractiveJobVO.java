package com.xiilab.modulek8s.workload.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.workload.enums.SchedulingType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.ContainerPortBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.EnvVarSourceBuilder;
import io.fabric8.kubernetes.api.model.ObjectFieldSelectorBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class InteractiveJobVO extends WorkloadVO {
	private List<JobEnvVO> envs;        //env 정의
	private List<JobPortVO> ports;        //port 정의
	private String command;        // 워크로드 명령
	private String ide;

	@Override
	public Deployment createResource() {
		return new DeploymentBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	// 메타데이터 정의
	@Override
	public ObjectMeta createMeta() {
		jobName = !StringUtils.isEmpty(this.jobName) ? this.jobName : getUniqueJobName();
		return new ObjectMetaBuilder()
			.withName(jobName)
			.withNamespace(workspace)
			.withAnnotations(
				getAnnotationMap()
			)
			.withLabels(
				getLabelMap()
			)
			.build();
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
		// TODO 삭제 예정
		// annotationMap.put(AnnotationField.DATASET_IDS.getField(), getJobVolumeIds(this.datasets));
		// annotationMap.put(AnnotationField.MODEL_IDS.getField(), getJobVolumeIds(this.models));
		annotationMap.put(AnnotationField.VOLUME_IDS.getField(), getIds(this.volumes, JobVolumeVO::id));
		annotationMap.put(AnnotationField.CODE_IDS.getField(), getIds(this.codes, JobCodeVO::id));
		annotationMap.put(AnnotationField.LABEL_IDS.getField(), getIds(this.labelIds, Function.identity()));
		annotationMap.put(AnnotationField.IMAGE_ID.getField(), ValidUtils.isNullOrZero(getImage().id()) ?
			"" : String.valueOf(getImage().id()));
		annotationMap.put(AnnotationField.IDE.getField(), getIde());
		annotationMap.put(AnnotationField.GPU_TYPE.getField(), this.gpuType.name());
		annotationMap.put(AnnotationField.NODE_NAME.getField(), this.nodeName);
		annotationMap.put(AnnotationField.GPU_NAME.getField(), this.gpuName);
		annotationMap.put(AnnotationField.GPU_ONE_PER_MEMORY.getField(),
			ValidUtils.isNullOrZero(this.gpuOnePerMemory) ? "" : String.valueOf(this.gpuOnePerMemory));
		annotationMap.put(AnnotationField.RESOURCE_PRESET_ID.getField(),
			ValidUtils.isNullOrZero(this.resourcePresetId) ? "" : String.valueOf(this.resourcePresetId));

		return annotationMap;
	}

	private Map<String, String> getLabelMap() {
		Map<String, String> map = new HashMap<>();

		map.put(LabelField.CREATOR_ID.getField(), getCreatorId());
		map.put(LabelField.CONTROL_BY.getField(), "astra");
		map.put(LabelField.APP.getField(), jobName);
		map.put(LabelField.JOB_NAME.getField(), jobName);
		map.put(LabelField.GPU_NAME.getField(), gpuName);
		map.put(LabelField.GPU_TYPE.getField(), gpuType.name());
		// TODO 삭제 예정
		// this.datasets.forEach(dataset -> addVolumeMap(map, "ds-", dataset.id()));
		// this.models.forEach(model -> addVolumeMap(map, "md-", model.id()));
		this.volumes.forEach(volume -> addVolumeMap(map, "vl-", volume.id()));
		this.codes.forEach(code -> addVolumeMap(map, "cd-", code.id()));

		return map;
	}

	// 스펙 정의
	@Override
	public DeploymentSpec createSpec() {
		return new DeploymentSpecBuilder()
			.withReplicas(1)
			.withNewSelector().withMatchLabels(Map.of(LabelField.APP.getField(), jobName)).endSelector()
			.withTemplate(new PodTemplateSpecBuilder()
				.withNewMetadata()
				.withAnnotations(getPodAnnotationMap())
				.withLabels(Collections.singletonMap(LabelField.APP.getField(), jobName))
				.endMetadata()
				.withSpec(createPodSpec())
				.build()
			)
			.build();
	}

	// 파드 및 잡 상세 스펙 정의
	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		podSpecBuilder.withHostname("astrago");
		// 스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());
		if (!ObjectUtils.isEmpty(this.secretName)) {
			podSpecBuilder.addNewImagePullSecret(this.secretName);
		}
		// 노드 지정
		if (!StringUtils.isEmpty(this.nodeName)) {
			podSpecBuilder.withNodeSelector(Map.of("kubernetes.io/hostname", this.nodeName));
		}
		// GPU 지정
		// TODO MIG MIXED일 때 처리 필요함
		if (!StringUtils.isEmpty(this.gpuName)) {
			podSpecBuilder.withNodeSelector(Map.of("nvidia.com/gpu.product", this.gpuName));
		}
		cloneGitRepo(podSpecBuilder, codes);
		addDefaultVolume(podSpecBuilder);
		// TODO 삭제예정
		// addVolumes(podSpecBuilder, datasets);
		// addVolumes(podSpecBuilder, models);
		addVolumes(podSpecBuilder, volumes);

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.withTerminationGracePeriodSeconds(20L)
			.addNewContainer()
			.withName(getUniqueJobName())
			.withImage(image.name());

		addContainerPort(podSpecContainer);
		addContainerEnv(podSpecContainer);
		addContainerCommand(podSpecContainer);
		if (this.gpuType != GPUType.MPS) {
			addDefaultShmVolumeMountPath(podSpecContainer);
		}
		// TODO 삭제 예정
		// addVolumeMount(podSpecContainer, datasets);
		// addVolumeMount(podSpecContainer, models);
		addVolumeMount(podSpecContainer, volumes);
		addContainerSourceCode(podSpecContainer);
		addContainerResource(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	private void addDefaultShmVolumeMountPath(
		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.addNewVolumeMount()
			.withName("shmdir")
			.withMountPath("/dev/shm")
			.endVolumeMount();
	}

	private void addVolumeMount(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer,
		List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.forEach(volume ->{
				if(volume.subPath() == null){
					podSpecContainer
						.addNewVolumeMount()
						.withName(volume.pvName())
						.withMountPath(volume.mountPath())
						.endVolumeMount();
				}else{
					podSpecContainer
						.addNewVolumeMount()
						.withName(volume.pvName())
						.withMountPath(volume.mountPath())
						.withSubPath(volume.subPath())
						.endVolumeMount();
				}
			});
		}
	}

	private void addContainerResource(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.withNewResources()
			.addToRequests(getWorkloadResourceMap())
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
					.withSubPath("code")
					.endVolumeMount());
		}
	}

	private void addContainerCommand(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (StringUtils.isNotBlank(command)) {
			podSpecContainer.addAllToCommand(convertCmd());
		} else {
			podSpecContainer.withTty(true);
		}
	}

	private void addContainerEnv(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (envs != null && !envs.isEmpty()) {
			podSpecContainer.addAllToEnv(convertEnv());
		}
		podSpecContainer.addAllToEnv(getMetadataEnv());
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
		List<EnvVar> envVars = envs.stream()
			.map(env -> new EnvVarBuilder()
				.withName(env.name())
				.withValue(env.value())
				.build()
			).toList();

		List<EnvVar> result = new ArrayList<>(envVars);
		// GPU 미사용시, GPU 접근 막는 환경변수
		if (ValidUtils.isNullOrZero(this.gpuRequest)) {
			result.add(new EnvVarBuilder()
				.withName("NVIDIA_VISIBLE_DEVICES")
				.withValue("none")
				.build()
			);
		}
		if (super.image.imageType() == ImageType.HUB) {
			result.add(new EnvVarBuilder()
				.withName("POD_NAME")
				.withValueFrom(new EnvVarSourceBuilder()
					.withFieldRef(new ObjectFieldSelectorBuilder()
						.withFieldPath("metadata.name")
						.build()
					)
					.build()
				)
				.build()
			);

			result.add(new EnvVarBuilder()
				.withName("POD_NAMESPACE")
				.withValueFrom(new EnvVarSourceBuilder()
					.withFieldRef(new ObjectFieldSelectorBuilder()
						.withFieldPath("metadata.namespace")
						.build()
					)
					.build()
				)
				.build()
			);
		}

		return result;
	}

	@Override
	public List<String> convertCmd() {
		return List.of("sh", "-c", command);
	}

	@Override
	public WorkloadType getWorkloadType() {
		return WorkloadType.INTERACTIVE;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKLOAD;
	}

	private void addVolumeMap(Map<String, String> map, String prefix, Long id) {
		if (!ValidUtils.isNullOrZero(id)) {
			map.put(prefix + id, "true");
		}
	}
}
