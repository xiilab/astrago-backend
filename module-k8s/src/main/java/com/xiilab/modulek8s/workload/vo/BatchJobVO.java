package com.xiilab.modulek8s.workload.vo;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
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
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectFieldSelectorBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.Quantity;
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
	private List<JobEnvVO> envs; // env 정의
	private List<JobPortVO> ports; // port 정의
	private String workingDir; // 명령어를 실행 할 path
	private String command; // 워크로드 명령
	private Map<String, String> parameter; // 사용자가 입력한 hyper parameter
	private String jobName;
	private String userUUID; // je.kim 한자연 UUID

	@Override
	public KubernetesResource createSpec() {
		throw new UnsupportedOperationException("Unimplemented method 'createSpec'");
	}

	@Override
	public PodSpec createPodSpec() {
		throw new UnsupportedOperationException("Unimplemented method 'createPodSpec'");
	}

	@Override
	public HasMetadata createResource() {
		throw new UnsupportedOperationException("Unimplemented method 'createResource'");
	}

	/**
	 * createMyDiskPv
	 * 
	 * @return
	 */
	public PersistentVolume createMyDiskPv() {
		String pvName = jobName + "-mydisk-pv";
		return new PersistentVolumeBuilder()
				.withNewMetadata()
				.withName(pvName)
				.addToLabels("type", StorageType.NFS.name())
				.endMetadata()
				.withNewSpec()
				.withCapacity(Map.of("storage", new Quantity("100Gi")))
				.withAccessModes("ReadWriteMany")
				.withNewNfs()
				.withServer("10.10.0.120") // 마켓플레이스 주소
				.withPath("/kadap-portal") // 저장소 위치
				.withReadOnly(false)
				.endNfs()
				.endSpec()
				.build();
	}

	/**
	 * createMyDiskPvc
	 * 
	 * @return
	 */
	public PersistentVolumeClaim createMyDiskPvc() {
		String pvcName = jobName + "-mydisk-pvc";
		String pvName = jobName + "-mydisk-pv";
		return new PersistentVolumeClaimBuilder()
				.withNewMetadata()
				.withName(pvcName)
				.withNamespace(workspace)
				.endMetadata()
				.withNewSpec()
				.withAccessModes(List.of("ReadWriteMany"))
				.withNewResources()
				.addToRequests("storage", new Quantity("100Gi"))
				.endResources()
				.withVolumeName(pvName)
				.endSpec()
				.build();
	}

	@Override
	public Job createResource(String userUUID) {
		return new JobBuilder()
				.withMetadata(createMeta())
				.withSpec(createSpec(userUUID))
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
						getAnnotationMap())
				.withLabels(
						getLabelMap())
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
		annotationMap.put(AnnotationField.DATASET_IDS.getField(), getJobVolumeIds(this.datasets));
		annotationMap.put(AnnotationField.MODEL_IDS.getField(), getJobVolumeIds(this.models));
		annotationMap.put(AnnotationField.CODE_IDS.getField(), getJobCodeIds(this.codes));
		annotationMap.put(AnnotationField.IMAGE_ID.getField(),
				ValidUtils.isNullOrZero(getImage().id()) ? "" : String.valueOf(getImage().id()));
		annotationMap.put(AnnotationField.PARAMETER.getField(), JsonConvertUtil.convertMapToJson(this.parameter));
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
		this.datasets.forEach(dataset -> addVolumeMap(map, "ds-", dataset.id()));
		this.models.forEach(model -> addVolumeMap(map, "md-", model.id()));
		this.codes.forEach(code -> addVolumeMap(map, "cd-", code.id()));

		return map;
	}

	// 스펙 정의
	@Override
	public JobSpec createSpec(String userUUID) {
		return new JobSpecBuilder()
				// .withNewSelector().withMatchLabels(Map.of(LabelField.APP.getField(),
				// jobName)).endSelector()
				.withTtlSecondsAfterFinished(20)
				.withNewTemplate()
				.withNewMetadata()
				.withAnnotations(getPodAnnotationMap())
				.withLabels(Map.of(LabelField.APP.getField(), jobName)).endMetadata()
				.withSpec(createPodSpec(userUUID))
				.endTemplate()
				.withBackoffLimit(6)
				.build();
	}

	// 파드 및 잡 상세 스펙 정의
	@Override
	public PodSpec createPodSpec(String userUUID) {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		podSpecBuilder.withHostname("astrago");
		// 노드 지정
		if (!StringUtils.isEmpty(this.nodeName)) {
			podSpecBuilder.withNodeSelector(Map.of("kubernetes.io/hostname", this.nodeName));
		}
		// GPU 지정
		// TODO MIG mixed일 때 처리 필요함
		if (!StringUtils.isEmpty(this.gpuName)) {
			podSpecBuilder.withNodeSelector(Map.of("nvidia.com/gpu.product", this.gpuName));
		}
		// 스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());
		if (!ObjectUtils.isEmpty(this.secretName)) {
			podSpecBuilder.addNewImagePullSecret(this.secretName);
		}
		if (!(this.image.imageType() == ImageType.HUB)) {
			cloneGitRepo(podSpecBuilder, this.codes);
		}
		addDefaultVolume(podSpecBuilder);
		addVolumes(podSpecBuilder, this.datasets);
		addVolumes(podSpecBuilder, this.models);

		String pvcName = jobName + "-mydisk-pvc";

		// 한자연 전용
		List<JobVolumeVO> katechMydisk = new CopyOnWriteArrayList<>() {
			{
				// name , pvcName , subpath
				add(
						new JobVolumeVO(
								"mydisk-pvc",
								pvcName,
								Paths.get("/root", "/kadap", "/MyDisk").toString(),
								"USER/" + userUUID));
			}
		};
		addVolumes(podSpecBuilder, katechMydisk);

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
				.withRestartPolicy("Never")
				.withTerminationGracePeriodSeconds(20L)
				.addNewContainer()
				.withName(getUniqueResourceName())
				.withImage(image.name());

		addContainerPort(podSpecContainer);
		addContainerEnv(podSpecContainer);
		addContainerCommand(podSpecContainer);
		if (this.gpuType != GPUType.MPS) {
			addDefaultVolumeMountPath(podSpecContainer);
		}

		addVolumeMount(podSpecContainer, datasets);
		addVolumeMount(podSpecContainer, models);
		// 한자연 마이 디스크
		addVolumeMount(podSpecContainer, katechMydisk);

		if (!(this.image.imageType() == ImageType.HUB)) {
			addContainerSourceCode(podSpecContainer);
		}
		addContainerResource(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	private void addDefaultVolumeMountPath(
			PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer.addNewVolumeMount()
				.withName("shmdir")
				.withMountPath("/dev/shm")
				.endVolumeMount();
	}

	private void addVolumeMount(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer,
			List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.forEach(volume -> {
				if (volume.subPath() == null) {
					podSpecContainer
							.addNewVolumeMount()
							.withName(volume.pvName())
							.withMountPath(volume.mountPath())
							.endVolumeMount();
				} else {
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
			codes.forEach(codeReq -> podSpecContainer
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

	@Override
	public List<ContainerPort> convertContainerPort() {
		return ports.stream()
				.map(port -> new ContainerPortBuilder()
						.withName(port.name())
						.withContainerPort(port.port())
						.build())
				.toList();
	}

	@Override
	public List<EnvVar> convertEnv() {
		List<EnvVar> envVars = envs.stream()
				.map(env -> new EnvVarBuilder()
						.withName(env.name())
						.withValue(env.value())
						.build())
				.toList();
		List<EnvVar> result = new ArrayList<>(envVars);
		// GPU 미사용시, GPU 접근 막는 환경변수
		if (ValidUtils.isNullOrZero(this.gpuRequest)) {
			result.add(new EnvVarBuilder()
					.withName("NVIDIA_VISIBLE_DEVICES")
					.withValue("none")
					.build());
		}
		if (super.image.imageType() == ImageType.HUB) {
			result.add(new EnvVarBuilder()
					.withName("POD_NAME")
					.withValueFrom(new EnvVarSourceBuilder()
							.withFieldRef(new ObjectFieldSelectorBuilder()
									.withFieldPath("metadata.name")
									.build())
							.build())
					.build());

			result.add(new EnvVarBuilder()
					.withName("POD_NAMESPACE")
					.withValueFrom(new EnvVarSourceBuilder()
							.withFieldRef(new ObjectFieldSelectorBuilder()
									.withFieldPath("metadata.namespace")
									.build())
							.build())
					.build());
		}

		return result;
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

	private void addVolumeMap(Map<String, String> map, String prefix, Long id) {
		if (!ValidUtils.isNullOrZero(id)) {
			map.put(prefix + id, "true");
		}
	}
}
