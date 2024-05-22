package com.xiilab.modulek8s.workload.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.kubeflow.v2beta1.mpijobspec.MpiReplicaSpecs;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.Template;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.Spec;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Containers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.InitContainers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Volumes;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Ports;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Resources;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.VolumeMounts;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.volumes.EmptyDir;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.volumes.PersistentVolumeClaim;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.GitEnvType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class DistributedWorkloadVO extends K8SResourceReqVO {
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

	protected void addVolume(Spec spec, List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			List<Volumes> volumesList =
				CollectionUtils.isEmpty(spec.getVolumes()) ? new ArrayList<>() : spec.getVolumes();
			for (JobVolumeVO volume : volumes) {
				PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaim();
				persistentVolumeClaim.setClaimName(volume.pvcName());
				persistentVolumeClaim.setReadOnly(false);
				Volumes volumesInstance = new Volumes();
				volumesInstance.setName(volume.pvName());
				volumesInstance.setPersistentVolumeClaim(persistentVolumeClaim);
				volumesList.add(volumesInstance);
			}
			spec.setVolumes(volumesList);
		}
	}

	protected void addVolumeMounts(Containers container, List<JobVolumeVO> volumes) {
		if (!CollectionUtils.isEmpty(volumes)) {
			List<org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts> volumeMountsList =
				CollectionUtils.isEmpty(container.getVolumeMounts()) ? new ArrayList<>() : container.getVolumeMounts();
			for (JobVolumeVO volume : volumes) {
				org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts volumeMounts = new org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts();
				volumeMounts.setName(volume.pvName());
				volumeMounts.setMountPath(volume.mountPath());
				volumeMountsList.add(volumeMounts);
			}
			container.setVolumeMounts(volumeMountsList);
		}
	}

	protected void addCloneCodeVolumeMounts(Containers container, List<JobCodeVO> codes) {
		if (!CollectionUtils.isEmpty(codes)) {
			List<org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts> volumeMountsList =
				CollectionUtils.isEmpty(container.getVolumeMounts()) ? new ArrayList<>() : container.getVolumeMounts();
			AtomicInteger index = new AtomicInteger(1);
			for (JobCodeVO codeInfo : codes) {
				org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts volumeMounts = new org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.VolumeMounts();
				volumeMounts.setName("git-clone-" + index.getAndIncrement());
				volumeMounts.setMountPath(codeInfo.mountPath());
				volumeMounts.setSubPath("code");
				volumeMountsList.add(volumeMounts);
			}
			container.setVolumeMounts(volumeMountsList);
		}
	}

	protected void createGitCloneInitContainers(Spec spec, String jobName) {
		if (!CollectionUtils.isEmpty(codes)) {
			List<InitContainers> initContainerList = new ArrayList<>();
			List<Volumes> initContainerVolumeList = new ArrayList<>();
			AtomicInteger index = new AtomicInteger(1);
			AtomicInteger volumeIndex = new AtomicInteger(1);
			for (JobCodeVO code : codes) {
				VolumeMounts volumeMounts = new VolumeMounts();
				volumeMounts.setName(jobName + "-git-clone-" + index);
				volumeMounts.setMountPath("/git");
				InitContainers initContainers = new InitContainers();
				initContainers.setName(jobName + "-git-clone-" + index);
				initContainers.setImage(code.initContainerImageUrl());
				initContainers.setVolumeMounts(List.of(volumeMounts));
				initContainers.setEnv(getGithubEnvVarList(code));
				initContainers.setResources(createInitContainerResources());
				initContainerList.add(initContainers);

				Volumes volumes = new Volumes();
				volumes.setName("git-clone-" + volumeIndex.getAndIncrement());
				volumes.setEmptyDir(new EmptyDir());
				initContainerVolumeList.add(volumes);
			}
			spec.setInitContainers(initContainerList);
			spec.setVolumes(initContainerVolumeList);
		}
	}

	protected List<org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env> getGithubEnvVarList(
		JobCodeVO codeVO) {
		List<org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env> result = new ArrayList<>();
		result.add(createEnv(GitEnvType.GIT_SYNC_REPO.name(), codeVO.repositoryURL()));
		result.add(createEnv(GitEnvType.GIT_SYNC_BRANCH.name(), codeVO.branch()));
		result.add(createEnv(GitEnvType.GIT_SYNC_MOUNT_PATH.name(), codeVO.mountPath()));
		result.add(createEnv(GitEnvType.REPOSITORY_TYPE.name(), codeVO.repositoryType().name()));
		result.add(createEnv(GitEnvType.GIT_SYNC_TIMEOUT.name(), "600"));
		result.add(createEnv(GitEnvType.GIT_SYNC_ROOT.name(), "/git"));
		result.add(createEnv(GitEnvType.GIT_SYNC_DEST.name(), "code"));
		result.add(createEnv(GitEnvType.GIT_SYNC_PERMISSIONS.name(), "0777"));
		result.add(createEnv(GitEnvType.GIT_SYNC_ONE_TIME.name(), "true"));

		// 공유 코드면 ID 환경변수로 저장
		if (!ValidUtils.isNullOrZero(codeVO.id())) {
			result.add(createEnv(GitEnvType.SOURCE_CODE_ID.name(), String.valueOf(codeVO.id())));
		}
		// GITHUB 크레덴셜 정보 환경변수로 저장
		if (codeVO.credentialVO() != null && org.springframework.util.StringUtils.hasText(
			codeVO.credentialVO().credentialLoginId())
			&& org.springframework.util.StringUtils.hasText(codeVO.credentialVO().credentialLoginPw())) {
			result.add(
				createEnv(GitEnvType.CREDENTIAL_ID.name(),
					ValidUtils.isNullOrZero(codeVO.credentialVO().credentialId()) ? "" :
						String.valueOf(codeVO.credentialVO().credentialId())));
			result.add(
				createEnv(GitEnvType.GIT_SYNC_USERNAME.name(), codeVO.credentialVO().credentialLoginId()));
			result.add(
				createEnv(GitEnvType.GIT_SYNC_PASSWORD.name(), codeVO.credentialVO().credentialLoginPw()));
		}

		return result;
	}

	protected org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env createEnv(String name,
		String value) {
		org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env env = new org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Env();
		env.setName(name);
		env.setValue(value);
		return env;
	}

	protected org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Resources createInitContainerResources() {
		org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Resources resources = new org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.initcontainers.Resources();
		resources.setRequests(Map.of("cpu", new IntOrString(String.valueOf(0.5)), "memory",
			new IntOrString(convertGiToBytes(0.5f))));
		resources.setLimits(Map.of("cpu", new IntOrString(String.valueOf(0.5)), "memory",
			new IntOrString(convertGiToBytes(0.5f))));
		return resources;
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

	protected String convertGiToBytes(float memoryInGI) {
		if (memoryInGI == 0) {
			return "0";
		}
		return String.valueOf((memoryInGI * (1L << 30)));
	}

	public abstract KubernetesResource createSpec();

	public abstract List<Ports> convertContainerPort();

	public abstract List<Env> convertEnv();

	public abstract List<String> convertCmd();

	public abstract WorkloadType getWorkloadType();

	public abstract MpiReplicaSpecs createLaucherSpec();

	public abstract MpiReplicaSpecs createWorkerSpec();

	public abstract Template createLauncherTemplate();

	public abstract Spec createLauncherTemplateSpec();

	public abstract Template createWorkerTemplate();

	public abstract Spec createWorkerTemplateSpec();

	public abstract Containers createLauncherContainers();

	public abstract Containers createWorkerContainers();

	public abstract Resources createLauncherResources();

	public abstract Resources createWorkerResources();
}
