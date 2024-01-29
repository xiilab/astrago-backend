package com.xiilab.modulek8s.workload.vo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.workload.enums.ResourcesUnit;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
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
	List<JobVolumeVO> volumes;    // volume 정의

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
				.map(codeReq -> new ContainerBuilder()
					.withName(getUniqueResourceName() + "-git-clone-" + index)
					.withImage("alpine/git")
					.addAllToArgs(List.of(
						"clone",
						"-b",
						codeReq.branch(),
						codeReq.repositoryURL(),
						codeReq.mountPath()
					))
					// init컨테이너와 아래서 생성한 emptyDir 볼륨 연결
					.addNewVolumeMount()
					.withName("git-clone-" + index.getAndIncrement())
					.withMountPath(codeReq.mountPath())
					.endVolumeMount()
					.build()).toList();

			// emptyDir 볼륨 생성
			List<Volume> gitCloneVolumes = codes.stream().map(codeReq -> new VolumeBuilder()
				.withName("git-clone-" + volumeIndex.getAndIncrement())
				.withNewEmptyDir()
				.endEmptyDir()
				.build()).toList();

			podSpecBuilder.addAllToInitContainers(gitCloneContainers);
			podSpecBuilder.addAllToVolumes(gitCloneVolumes);
		}
	}

	/**
	 * 마운트할 볼륨 정의
	 *
	 * @param podSpecBuilder
	 * @param volumes 마운트 정의할 볼륨 목록
	 */
	public void addVolumes(PodSpecBuilder podSpecBuilder, List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			List<Volume> addVolumes = volumes.stream().map(volume -> new VolumeBuilder()
				.withName(volume.name())
				.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(volume.mountPath(), false))
				.build()).toList();

			podSpecBuilder.addAllToVolumes(addVolumes);
		}

	}

	// 요청된 워크로드 리소스 MAP로 반환
	public Map<String, Quantity> getWorkloadResourceMap() {
		// 소수점 한자리로 변환
		String strCpuRequest = String.format("%.1f", cpuRequest) + ResourcesUnit.CPU_UNIT.getUnit();
		String strMemRequest = String.format("%.1f", memRequest) + ResourcesUnit.MEM_UNIT.getUnit();

		// gpu 요청여부에 따라 다른 결과 반환
		return gpuRequest == 0 ?
			Map.of(
				"cpu", new Quantity(strCpuRequest),
				"memory", new Quantity(strMemRequest)
			)
			:
			Map.of(
				"nvidia.com/gpu", new Quantity(String.valueOf(gpuRequest)),
				"cpu", new Quantity(strCpuRequest),
				"memory", new Quantity(strMemRequest)
			);
	}

	public abstract KubernetesResource createSpec();

	public abstract PodSpec createPodSpec();

	public abstract List<ContainerPort> convertContainerPort();

	public abstract List<EnvVar> convertEnv();

	public abstract List<String> convertCmd();

	public abstract WorkloadType getWorkloadType();
}
