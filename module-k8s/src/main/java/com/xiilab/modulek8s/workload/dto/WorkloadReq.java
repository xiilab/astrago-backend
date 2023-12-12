package com.xiilab.modulek8s.workload.dto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.util.CollectionUtils;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class WorkloadReq {
	String name;		// 워크로드 이름
	String description;		// 워크로드 설명
	String creatorId;		//생성자 ID
	String workspace;		//워크스페이스
	WorkloadType type;		// 워크로드 타입
	String image;		//사용할 image
	int gpuRequest;		// 워크로드 gpu 요청량
	int cpuRequest;		// 워크로드 cpu 요청량
	int memRequest;		// 워크로드 mem 요청량
	SchedulingType schedulingType;		// 스케줄링 방식
	Map<String, String> env;		//env 정의
	Map<String, Integer> port;		//port 정의
	String command;		// 워크로드 명령

	public void cloneGitRepo(PodSpecBuilder podSpecBuilder, List<CodeDTO> codeReqs) {
		if (!CollectionUtils.isEmpty(codeReqs)) {
			AtomicInteger index = new AtomicInteger(1);
			AtomicInteger volumeIndex = new AtomicInteger(1);
			List<Container> gitCloneContainers = codeReqs.stream().map(codeReq -> new ContainerBuilder()
				.withName(name + "-git-clone-" + index)
				.withImage("alpine/git")
				.addAllToArgs(List.of(
					"clone",
					"-b",
					codeReq.branch(),
					codeReq.repositoryURL(),
					codeReq.mountPath()
				))
				.addNewVolumeMount()
				.withName(name + "-git-clone-" + index.getAndIncrement())
				.withMountPath(codeReq.mountPath())
				.endVolumeMount()
				.build()).toList();

			List<Volume> gitCloneVolumes = codeReqs.stream().map(codeReq -> new VolumeBuilder()
				.withName(name + "-git-clone-" + volumeIndex.getAndIncrement())
				.withNewEmptyDir()
				.endEmptyDir()
				.build()).toList();

			podSpecBuilder.addAllToInitContainers(gitCloneContainers);
			podSpecBuilder.addAllToVolumes(gitCloneVolumes);
		}
	}

	public abstract HasMetadata createResource();
	public abstract ObjectMeta createMeta();
	public abstract KubernetesResource createSpec();
	public abstract PodSpec createPodSpec();
	public abstract List<ContainerPort> convertContainerPort();
	public abstract List<EnvVar> convertEnv();
	public abstract List<String> convertCmd();
	public abstract WorkloadType getType();
}
