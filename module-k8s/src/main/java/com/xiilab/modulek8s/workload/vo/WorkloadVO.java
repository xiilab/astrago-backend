package com.xiilab.modulek8s.workload.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulecommon.enums.WorkloadType;

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
	WorkloadType workloadType;        // 워크로드 타입
	JobImageVO image;        //사용할 image
	int gpuRequest;        // 워크로드 gpu 요청량
	float cpuRequest;        // 워크로드 cpu 요청량
	float memRequest;        // 워크로드 mem 요청량
	//SchedulingType schedulingType;        // 스케줄링 방식
	List<JobCodeVO> codes;    // code 정의
	List<JobVolumeVO> datasets;
	List<JobVolumeVO> models;
	// List<JobVolumeVO> volumes;    // volume 정의
	String secretName;

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
					.withImage("k8s.gcr.io/git-sync/git-sync:v3.2.2")
					// init컨테이너와 아래서 생성한 emptyDir 볼륨 연결
					.addNewVolumeMount()
					.withName("git-clone-" + index.getAndIncrement())
					.withMountPath(codeVO.mountPath())
					.endVolumeMount()
					.withEnv(getGithubEnvVarList(codeVO))
					.withNewResources()
					.addToRequests(Map.of(
						"cpu", new Quantity("500m"),
						"memory", new Quantity("1000Mi")
					))
					.addToLimits(Map.of(
						"cpu", new Quantity("500m"),
						"memory", new Quantity("1000Mi")
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
		// 소수점 한자리로 변환
		String strCpuRequest = String.format("%.1f", cpuRequest) + ResourcesUnit.CPU_UNIT.getUnit();
		String strMemRequest = String.format("%.1f", memRequest) + ResourcesUnit.MEM_UNIT.getUnit();

		// gpu 요청여부에 따라 다른 결과 반환
		return gpuRequest == 0 ? Map.of("cpu", new Quantity(strCpuRequest), "memory", new Quantity(strMemRequest)) :
			Map.of("nvidia.com/gpu", new Quantity(String.valueOf(gpuRequest)), "cpu", new Quantity(strCpuRequest),
				"memory", new Quantity(strMemRequest));
	}

	private List<EnvVar> getGithubEnvVarList(JobCodeVO codeVO) {
		List<EnvVar> result = new ArrayList<>();
		result.add(new EnvVarBuilder().withName("GIT_SYNC_REPO").withValue(codeVO.repositoryURL()).build());
		result.add(new EnvVarBuilder().withName("GIT_SYNC_BRANCH").withValue(codeVO.branch()).build());
		result.add(new EnvVarBuilder().withName("GIT_SYNC_ROOT").withValue(codeVO.mountPath()).build());
		result.add(new EnvVarBuilder().withName("GIT_SYNC_PERMISSIONS").withValue("0777").build());
		result.add(new EnvVarBuilder().withName("GIT_SYNC_ONE_TIME").withValue("true").build());
		result.add(new EnvVarBuilder().withName("GIT_SYNC_TIMEOUT").withValue("600").build());
		if (codeVO.credentialVO() != null && StringUtils.hasText(codeVO.credentialVO().credentialName())
			&& StringUtils.hasText(codeVO.credentialVO().credentialLoginPw())) {
			result.add(new EnvVarBuilder().withName("GIT_SYNC_USERNAME")
				.withValue(codeVO.credentialVO().credentialName())
				.build());
			result.add(new EnvVarBuilder().withName("GIT_SYNC_PASSWORD")
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

	public abstract KubernetesResource createSpec();

	public abstract PodSpec createPodSpec();

	public abstract List<ContainerPort> convertContainerPort();

	public abstract List<EnvVar> convertEnv();

	public abstract List<String> convertCmd();

	public abstract WorkloadType getWorkloadType();
}
