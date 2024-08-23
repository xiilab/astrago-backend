package com.xiilab.modulek8s.deploy.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.SchedulingType;
import com.xiilab.modulek8s.workload.vo.JobCodeVO;
import com.xiilab.modulek8s.workload.vo.JobEnvVO;
import com.xiilab.modulek8s.workload.vo.JobImageVO;
import com.xiilab.modulek8s.workload.vo.JobPortVO;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
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
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpec;
import io.fabric8.kubernetes.api.model.batch.v1.JobSpecBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DeployVO extends K8SResourceReqVO {
	private String workspace;        //워크스페이스
	private String workspaceName;     //워크스페이스 이름
	private WorkloadType workloadType;        // 워크로드 타입
	private JobImageVO image;        //사용할 image
	private Integer gpuRequest;        // 워크로드 gpu 요청량
	private Float cpuRequest;        // 워크로드 cpu 요청량
	private Float memRequest;        // 워크로드 mem 요청량
	private List<JobCodeVO> codes;    // code 정의
	private String secretName;
	private String nodeName;
	private GPUType gpuType;
	private String gpuName;
	private Integer gpuOnePerMemory;
	private Integer resourcePresetId;
	private List<JobVolumeVO> volumes;        //volume 정의
	private List<JobEnvVO> envs;        //env 정의
	private List<JobPortVO> ports;        //port 정의
	private String workingDir;        // 명령어를 실행 할 path
	private String command;        // 워크로드 명령
	private String jobName;
	private String modelVersion;
	private int replica;
	private long deployModelId;
	private DeployType deployType;
	private String initContainerUrl;

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
		jobName = getUniqueJobName();
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
		annotationMap.put(AnnotationField.CODE_IDS.getField(), getJobCodeIds(this.codes));
		annotationMap.put(AnnotationField.IMAGE_ID.getField(), ValidUtils.isNullOrZero(getImage().id()) ?
			"" : String.valueOf(getImage().id()));
		annotationMap.put(AnnotationField.GPU_TYPE.getField(), this.gpuType.name());
		annotationMap.put(AnnotationField.NODE_NAME.getField(), this.nodeName);
		annotationMap.put(AnnotationField.GPU_NAME.getField(), this.gpuName);
		annotationMap.put(AnnotationField.GPU_ONE_PER_MEMORY.getField(),
			ValidUtils.isNullOrZero(this.gpuOnePerMemory) ? "" : String.valueOf(this.gpuOnePerMemory));
		annotationMap.put(AnnotationField.RESOURCE_PRESET_ID.getField(),
			ValidUtils.isNullOrZero(this.resourcePresetId) ? "" : String.valueOf(this.resourcePresetId));
		annotationMap.put(AnnotationField.DEPLOY_TYPE.getField(), this.deployType.name());
		annotationMap.put(AnnotationField.DEPLOY_MODEL_ID.getField(), String.valueOf(this.deployModelId));
		annotationMap.put(AnnotationField.DEPLOY_MODEL_VERSION.getField(), String.valueOf(this.modelVersion));
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
		map.put(LabelField.DEPLOY_MODEL_ID.getField(), String.valueOf(this.deployModelId));
		if(this.volumes != null && this.volumes.size() > 0){
			this.volumes.forEach(volume -> addVolumeMap(map, "vl-", volume.id()));
		}
		if(this.codes != null && this.codes.size() > 0){
			this.codes.forEach(code -> addVolumeMap(map, "cd-", code.id()));
		}
		return map;
	}

	// 스펙 정의
	public DeploymentSpec createSpec() {
		return new DeploymentSpecBuilder()
			.withReplicas(this.replica)
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
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		podSpecBuilder.withHostname("astrago");
		// 스케줄러 지정
		podSpecBuilder.withSchedulerName(SchedulingType.BIN_PACKING.getType());
		if (!ObjectUtils.isEmpty(this.secretName)) {
			podSpecBuilder.addNewImagePullSecret(this.secretName);
		}
		// 노드 지정
		if (!io.micrometer.common.util.StringUtils.isEmpty(this.nodeName)) {
			podSpecBuilder.withNodeSelector(Map.of("kubernetes.io/hostname", this.nodeName));
		}
		// GPU 지정
		// TODO MIG MIXED일 때 처리 필요함
		if (!io.micrometer.common.util.StringUtils.isEmpty(this.gpuName) && gpuType != GPUType.MPS) {
			podSpecBuilder.withNodeSelector(Map.of("nvidia.com/gpu.product", this.gpuName));
		}
		cloneGitRepo(podSpecBuilder, codes);
		addDefaultVolume(podSpecBuilder);
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
	}

	private void addContainerPort(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		if (ports != null && !ports.isEmpty()) {
			podSpecContainer.addAllToPorts(convertContainerPort());
		}
	}

	public List<ContainerPort> convertContainerPort() {
		return ports.stream()
			.map(port -> new ContainerPortBuilder()
				.withName(port.name())
				.withContainerPort(port.port())
				.build()
			).toList();
	}

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

		return result;
	}

	public List<String> convertCmd() {
		return List.of("sh", "-c", command);
	}

	public WorkloadType getWorkloadType() {
		return WorkloadType.DEPLOY;
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.DEPLOY;
	}

	private void addVolumeMap(Map<String, String> map, String prefix, Long id) {
		if (!ValidUtils.isNullOrZero(id)) {
			map.put(prefix + id, "true");
		}
	}
	private String getJobCodeIds(List<JobCodeVO> list) {
		if (CollectionUtils.isEmpty(list)) {
			return "";
		}

		return list.stream()
			.map(jobCodeVO -> String.valueOf(jobCodeVO.id()))
			.collect(Collectors.joining(","));
	}
	private Map<String, String> getPodAnnotationMap() {
		Map<String, String> map = new HashMap<>();
		if(this.volumes != null && this.volumes.size() > 0){
			this.volumes.forEach(volume -> addVolumeMap(map, "vl-", volume.id()));
		}
		if(this.codes != null && this.codes.size() > 0){
			this.codes.forEach(code -> {
				if (!ValidUtils.isNullOrZero(code.id())) {
					map.put("cd-" + code.id(), code.mountPath());
				}
			});
		}

		return map;
	}
	/**
	 * init 컨테이너에 소스코드 복사하고 emptyDir 볼륨 마운트
	 *
	 * @param podSpecBuilder
	 * @param codes 복사하려는 코드 목록
	 */
	public void cloneGitRepo(PodSpecBuilder podSpecBuilder, List<JobCodeVO> codes) {
		if (!CollectionUtils.isEmpty(codes)) {
			AtomicInteger index = new AtomicInteger(1);
			AtomicInteger volumeIndex = new AtomicInteger(1);
			// 소스 코드 복사
			List<Container> gitCloneContainers = codes.stream()
				.map(codeVO -> new ContainerBuilder().withName(getUniqueJobName() + "-git-clone-" + index)
					// .withImage("k8s.gcr.io/git-sync/git-sync:v3.2.2")
					.withImage(codeVO.initContainerImageUrl())
					// init컨테이너와 아래서 생성한 emptyDir 볼륨 연결
					.addNewVolumeMount()
					.withName("git-clone-" + index.getAndIncrement())
					.withMountPath("/git")
					// .withMountPath("/tmp/sourceCode")
					.endVolumeMount()
					.withEnv(getGithubEnvVarList(codeVO))
					.withNewResources()
					.addToRequests(Map.of(
						"cpu", new Quantity("500m"),
						"memory", new Quantity("500Mi")
					))
					.addToLimits(Map.of(
						"cpu", new Quantity("500m"),
						"memory", new Quantity("500Mi")
					))
					.endResources()
					.build())
				.toList();

			// emptyDir 볼륨 생성
			List<Volume> gitCloneVolumes = codes.stream()
				.map(codeReq -> new VolumeBuilder().withName("git-clone-" + volumeIndex.getAndIncrement())
					.withNewEmptyDir()
					.endEmptyDir()
					.build())
				.toList();

			podSpecBuilder.addAllToInitContainers(gitCloneContainers);
			podSpecBuilder.addAllToVolumes(gitCloneVolumes);
		}
	}

	public void addDefaultVolume(PodSpecBuilder podSpecBuilder) {
		List<Volume> addVolumes = new ArrayList<>();
		addVolumes.add(new VolumeBuilder()
			.withName("shmdir")
			.withNewEmptyDir()
			.withMedium("Memory")
			.endEmptyDir()
			.build());
		podSpecBuilder.addAllToVolumes(addVolumes);
	}

	/**
	 * 마운트할 볼륨 정의
	 *
	 * @param podSpecBuilder
	 * @param volumes 마운트 정의할 볼륨 목록
	 *
	 **/
	public void addVolumes(PodSpecBuilder podSpecBuilder, List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			List<Volume> addVolumes = volumes.stream()
				.map(volume -> new VolumeBuilder()
					.withName(volume.pvName())
					.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(volume.pvcName(), false))
					.build())
				.toList();
			podSpecBuilder.addAllToVolumes(addVolumes);
		}
	}
	public Map<String, Quantity> getWorkloadResourceMap() {
		Map<String, Quantity> result = new HashMap<>();
		// 소수점 한자리로 변환
		String strCpuRequest = String.valueOf(cpuRequest);
		String strMemRequest = String.format("%.1f", memRequest) + ResourcesUnit.MEM_UNIT.getUnit();

		if (ValidUtils.isNullOrZero(gpuRequest)) {
			result.put("cpu", new Quantity(strCpuRequest));
			result.put("memory", new Quantity(strMemRequest));
		} else {
			if (gpuType == GPUType.MPS) {
				result.put("nvidia.com/gpu.shared", new Quantity(String.valueOf(gpuRequest)));
			} else {
				result.put("nvidia.com/gpu", new Quantity(String.valueOf(gpuRequest)));
			}
			result.put("cpu", new Quantity(strCpuRequest));
			result.put("memory", new Quantity(strMemRequest));
		}

		return result;
	}
	private List<EnvVar> getGithubEnvVarList(JobCodeVO codeVO) {
		List<EnvVar> result = new ArrayList<>();
		result.add(
			new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_REPO.name()).withValue(codeVO.repositoryURL()).build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_BRANCH.name()).withValue(codeVO.branch()).build());
		result.add(
			new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_MOUNT_PATH.name()).withValue(codeVO.mountPath()).build());
		result.add(new EnvVarBuilder().withName(GitEnvType.REPOSITORY_TYPE.name())
			.withValue(codeVO.repositoryType().name())
			.build());
		result.add(new EnvVarBuilder().withName(GitEnvType.COMMAND.name()).withValue(codeVO.command()).build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_TIMEOUT.name()).withValue("600").build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_ROOT.name()).withValue("/git").build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_DEST.name()).withValue("code").build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_PERMISSIONS.name()).withValue("0777").build());
		result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_ONE_TIME.name()).withValue("true").build());

		// 공유 코드면 ID 환경변수로 저장
		if (!ValidUtils.isNullOrZero(codeVO.id())) {
			result.add(new EnvVarBuilder().withName(GitEnvType.SOURCE_CODE_ID.name())
				.withValue(String.valueOf(codeVO.id()))
				.build());
		}
		// GITHUB 크레덴셜 정보 환경변수로 저장
		if (codeVO.credentialVO() != null && org.springframework.util.StringUtils.hasText(codeVO.credentialVO().credentialLoginId())
			&& org.springframework.util.StringUtils.hasText(codeVO.credentialVO().credentialLoginPw())) {
			result.add(new EnvVarBuilder().withName(GitEnvType.CREDENTIAL_ID.name())
				.withValue(ValidUtils.isNullOrZero(codeVO.credentialVO().credentialId()) ? "" :
					String.valueOf(codeVO.credentialVO().credentialId()))
				.build());
			result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_USERNAME.name())
				.withValue(codeVO.credentialVO().credentialLoginId())
				.build());
			result.add(new EnvVarBuilder().withName(GitEnvType.GIT_SYNC_PASSWORD.name())
				.withValue(codeVO.credentialVO().credentialLoginPw())
				.build());
		}

		return result;
	}
}
