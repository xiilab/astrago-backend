package com.xiilab.modulek8s.workload.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class WorkloadVO extends K8SResourceReqVO {
	String workspace;        //워크스페이스
	String workspaceName;     //워크스페이스 이름
	WorkloadType workloadType;        // 워크로드 타입
	JobImageVO image;        //사용할 image
	Integer gpuRequest;        // 워크로드 gpu 요청량
	Float cpuRequest;        // 워크로드 cpu 요청량
	Float memRequest;        // 워크로드 mem 요청량
	//SchedulingType schedulingType;        // 스케줄링 방식
	List<JobCodeVO> codes;    // code 정의
	List<JobVolumeVO> datasets;
	List<JobVolumeVO> models;
	String secretName;
	String nodeName;
	GPUType gpuType;
	String gpuName;
	Integer gpuOnePerMemory;
	Integer resourcePresetId;
	String subPath;

	LocalDateTime expirationTime;	//je.kim 한자연 커스텀 : 종료예정시간 추가


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
				.map(codeVO -> new ContainerBuilder().withName(getUniqueResourceName() + "-git-clone-" + index)
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

	// 요청된 워크로드 리소스 MAP로 반환
	public Map<String, Quantity> getWorkloadResourceMap() {
		Map<String, Quantity> result = new HashMap<>();
		// 소수점 한자리로 변환
		String strCpuRequest = String.valueOf(cpuRequest);
		String strMemRequest = String.valueOf(memRequest);
		result.put("cpu", new Quantity(strCpuRequest));
		result.put("memory", new Quantity(strMemRequest, "Gi"));

		if (gpuRequest != null && gpuRequest > 0) {
			if (gpuType == GPUType.MPS) {
				result.put("nvidia.com/gpu.shared", new Quantity(String.valueOf(gpuRequest)));
			} else {
				result.put("nvidia.com/gpu", new Quantity(String.valueOf(gpuRequest)));
			}
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
		if (codeVO.credentialVO() != null && StringUtils.hasText(codeVO.credentialVO().credentialLoginId())
			&& StringUtils.hasText(codeVO.credentialVO().credentialLoginPw())) {
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

	protected String getJobVolumeIds(List<JobVolumeVO> list) {
		if (CollectionUtils.isEmpty(list)) {
			return "";
		}

		return list.stream()
			.map(jobVolumeVO -> String.valueOf(jobVolumeVO.id()))
			.collect(Collectors.joining(","));
	}

	protected String getJobCodeIds(List<JobCodeVO> list) {
		if (CollectionUtils.isEmpty(list)) {
			return "";
		}

		return list.stream()
			.map(jobCodeVO -> String.valueOf(jobCodeVO.id()))
			.collect(Collectors.joining(","));
	}

	protected Map<String, String> getPodAnnotationMap() {
		Map<String, String> map = new HashMap<>();
		this.datasets.forEach(dataset -> map.put("ds-" + dataset.id(), dataset.mountPath()));
		this.models.forEach(model -> map.put("md-" + model.id(), model.mountPath()));
		this.codes.forEach(code -> {
			if (!ValidUtils.isNullOrZero(code.id())) {
				map.put("cd-" + code.id(), code.mountPath());
			}
		});

		return map;
	}

	public abstract KubernetesResource createSpec();

	public abstract PodSpec createPodSpec();

	public abstract List<ContainerPort> convertContainerPort();

	public abstract List<EnvVar> convertEnv();

	public abstract List<String> convertCmd();

	public abstract WorkloadType getWorkloadType();
}
