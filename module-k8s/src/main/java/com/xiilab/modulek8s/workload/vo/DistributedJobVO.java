package com.xiilab.modulek8s.workload.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kubeflow.v2beta1.MPIJob;
import org.kubeflow.v2beta1.MPIJobSpec;
import org.kubeflow.v2beta1.mpijobspec.MpiReplicaSpecs;
import org.kubeflow.v2beta1.mpijobspec.RunPolicy;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.Template;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.Metadata;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.Spec;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Containers;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Env;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Ports;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.containers.Resources;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.DistributedJobRole;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DistributedJobVO extends DistributedWorkloadVO {
	private List<JobEnvVO> envs;        //env 정의
	private List<JobPortVO> ports;        //port 정의
	private String workingDir;        // 명령어를 실행 할 path
	private String command;        // 워크로드 명령
	private Map<String, String> parameter;        // 사용자가 입력한 hyper parameter
	private String jobName;

	@Override
	public MPIJobSpec createSpec() {
		MPIJobSpec mpiJobSpec = new MPIJobSpec();
		mpiJobSpec.setSlotsPerWorker(1);
		mpiJobSpec.setMpiReplicaSpecs(Map.of(
			DistributedJobRole.LAUNCHER.getName(), createLaucherSpec(),
			DistributedJobRole.WORKER.getName(), createWorkerSpec()));
		RunPolicy runPolicy = new RunPolicy();
		mpiJobSpec.setRunPolicy(runPolicy);
		return mpiJobSpec;
	}

	@Override
	public List<Ports> convertContainerPort() {
		List<Ports> portsList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(ports)) {
			for (JobPortVO port : ports) {
				Ports portInstance = new Ports();
				portInstance.setName(port.name());
				portInstance.setContainerPort(port.port());
				portsList.add(portInstance);
			}
		}
		return portsList;
	}

	@Override
	public List<Env> convertEnv() {
		List<Env> envList = new ArrayList<>();
		if (!CollectionUtils.isEmpty(envs)) {
			for (JobEnvVO envInfo : envs) {
				Env env = new Env();
				env.setName(envInfo.name());
				env.setValue(envInfo.value());
				envList.add(env);
			}
		}
		return envList;
	}

	@Override
	public List<String> convertCmd() {
		List<String> commandList = new ArrayList<>();
		commandList.add("mpirun");
		commandList.add("--allow-run-as-root");
		commandList.add("-np");
		commandList.add(String.valueOf(workerCnt));
		commandList.add("-bind-to");
		commandList.add("none");
		commandList.add("-map-by");
		commandList.add("slot");
		commandList.add("-x");
		commandList.add("NCCL_DEBUG=INFO");
		commandList.add("-x");
		commandList.add("LD_LIBRARY_PATH");
		commandList.add("-x");
		commandList.add("PATH");
		commandList.add("-mca");
		commandList.add("pml");
		commandList.add("ob1");
		commandList.add("-mca");
		commandList.add("btl");
		commandList.add("^openib");
		commandList.add("sh");
		commandList.add("-c");
		commandList.add(command);
		return commandList;
	}

	@Override
	public WorkloadType getWorkloadType() {
		return WorkloadType.DISTRIBUTED;
	}

	@Override
	public MpiReplicaSpecs createLaucherSpec() {
		MpiReplicaSpecs launcherSpec = new MpiReplicaSpecs();
		//launcher는 replicas는 1개로 고정한다.
		launcherSpec.setReplicas(1);
		launcherSpec.setTemplate(createLauncherTemplate());
		return launcherSpec;
	}

	@Override
	public MpiReplicaSpecs createWorkerSpec() {
		MpiReplicaSpecs workerReplicaSpec = new MpiReplicaSpecs();
		//요청한 gpu개수 만큼 replicas를 분할한다.(ex. 5개 요청 -> 5개의 한개의 gpu를 할당 받은 worker가 생성된다.)
		workerReplicaSpec.setReplicas(workerCnt);
		workerReplicaSpec.setTemplate(createWorkerTemplate());
		return workerReplicaSpec;
	}

	@Override
	public Template createLauncherTemplate() {
		Template template = new Template();
		template.setMetadata(getMpiReplicasMetadata());
		template.setSpec(createLauncherTemplateSpec());
		return template;
	}

	@Override
	public Spec createLauncherTemplateSpec() {
		Spec spec = new Spec();
		createGitCloneInitContainers(spec, jobName);
		spec.setContainers(List.of(createLauncherContainers()));
		// 노드 지정
		if (!StringUtils.isEmpty(this.nodeName)) {
			spec.setNodeSelector(Map.of("kubernetes.io/hostname", this.nodeName));
		}
		// GPU 지정
		// TODO MIG mixed일 때 처리 필요함
		if (!StringUtils.isEmpty(this.gpuName)) {
			spec.setNodeSelector(Map.of("nvidia.com/gpu.product", this.gpuName));
		}
		addVolume(spec, datasets);
		addVolume(spec, models);
		return spec;
	}

	@Override
	public Template createWorkerTemplate() {
		Template template = new Template();
		template.setMetadata(getMpiReplicasMetadata());
		template.setSpec(createWorkerTemplateSpec());
		return template;
	}

	@Override
	public Spec createWorkerTemplateSpec() {
		Spec spec = new Spec();
		createGitCloneInitContainers(spec, jobName);
		spec.setContainers(List.of(createWorkerContainers()));
		// 노드 지정
		if (!StringUtils.isEmpty(this.nodeName)) {
			spec.setNodeSelector(Map.of("kubernetes.io/hostname", this.nodeName));
		}
		// GPU 지정
		// TODO MIG mixed일 때 처리 필요함
		if (!StringUtils.isEmpty(this.gpuName)) {
			spec.setNodeSelector(Map.of("nvidia.com/gpu.product", this.gpuName));
		}
		addVolume(spec, datasets);
		addVolume(spec, models);
		return spec;
	}

	@Override
	public Containers createLauncherContainers() {
		Containers launcherContainer = new Containers();
		launcherContainer.setName(jobName);
		launcherContainer.setImage(image.name());
		launcherContainer.setResources(createLauncherResources());
		launcherContainer.setEnv(convertEnv());
		launcherContainer.setCommand(convertCmd());
		launcherContainer.setWorkingDir(workingDir);
		launcherContainer.setImagePullPolicy("IfNotPresent");
		launcherContainer.setPorts(convertContainerPort());
		addVolumeMounts(launcherContainer, datasets);
		addVolumeMounts(launcherContainer, models);
		addCloneCodeVolumeMounts(launcherContainer, codes);
		if (StringUtils.isNotBlank(workingDir)) {
			launcherContainer.setWorkingDir(workingDir);
		}
		return launcherContainer;
	}

	@Override
	public Containers createWorkerContainers() {
		Containers workerContainer = new Containers();
		workerContainer.setName(jobName);
		workerContainer.setImage(image.name());
		workerContainer.setResources(createWorkerResources());
		workerContainer.setEnv(convertEnv());
		workerContainer.setImagePullPolicy("IfNotPresent");
		workerContainer.setPorts(convertContainerPort());
		addVolumeMounts(workerContainer, datasets);
		addVolumeMounts(workerContainer, models);
		addCloneCodeVolumeMounts(workerContainer, codes);
		return workerContainer;
	}

	@Override
	public Resources createLauncherResources() {
		Resources resources = new Resources();
		Map<String, IntOrString> resourceMap = Map.of(
			"cpu", new IntOrString(String.valueOf(this.launcherCpuRequest)),
			"memory", new IntOrString(this.launcherMemRequest + "Gi"));
		resources.setRequests(resourceMap);
		resources.setLimits(resourceMap);
		return resources;
	}

	@Override
	public Resources createWorkerResources() {
		Resources resources = new Resources();
		Map<String, IntOrString> resourceMap = Map.of(
			"cpu", new IntOrString(String.valueOf(this.workerCpuRequest)),
			"memory", new IntOrString(this.workerMemRequest + "Gi"),
			"nvidia.com/gpu", new IntOrString(this.workerGpuRequest));
		resources.setRequests(resourceMap);
		resources.setLimits(resourceMap);
		return resources;
	}

	@Override
	public MPIJob createResource() {
		MPIJob mpiJob = new MPIJob();
		mpiJob.setMetadata(createMeta());
		mpiJob.setSpec(createSpec());
		return mpiJob;
	}

	@Override
	protected ObjectMeta createMeta() {
		jobName = getUniqueResourceName();
		return new ObjectMetaBuilder()
			.withName(jobName)
			.withNamespace(workspace)
			.withAnnotations(getAnnotationMap())
			.withLabels(getLabelMap())
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKLOAD;
	}

	private Metadata getMpiReplicasMetadata() {
		Metadata metadata = new Metadata();
		metadata.setAnnotations(Map.of("sidecar.istio.io/inject", String.valueOf(false)));
		return metadata;
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
		annotationMap.put(AnnotationField.NODE_NAME.getField(), this.nodeName);
		annotationMap.put(AnnotationField.GPU_NAME.getField(), this.gpuName);
		annotationMap.put(AnnotationField.GPU_ONE_PER_MEMORY.getField(),
			ValidUtils.isNullOrZero(this.gpuOnePerMemory) ? "" : String.valueOf(this.gpuOnePerMemory));
		annotationMap.put(AnnotationField.GPU_TYPE.getField(), gpuType.name());
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
		if (!CollectionUtils.isEmpty(datasets)) {
			this.datasets.forEach(dataset -> addVolumeMap(map, "ds-", dataset.id()));
		}
		if (!CollectionUtils.isEmpty(models)) {
			this.models.forEach(model -> addVolumeMap(map, "md-", model.id()));
		}
		if (!CollectionUtils.isEmpty(codes)) {
			this.codes.forEach(code -> addVolumeMap(map, "cd-", code.id()));
		}
		return map;
	}

	private void addVolumeMap(Map<String, String> map, String prefix, Long id) {
		if (!ValidUtils.isNullOrZero(id)) {
			map.put(prefix + id, "true");
		}
	}

}
