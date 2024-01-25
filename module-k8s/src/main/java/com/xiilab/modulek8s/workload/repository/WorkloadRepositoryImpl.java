package com.xiilab.modulek8s.workload.repository;

import java.util.List;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.DeploymentVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WorkloadRepositoryImpl implements WorkloadRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public ModuleBatchJobResDTO createBatchJobWorkload(BatchJobVO batchJobVO) {
		Job resource = (Job)createResource(batchJobVO.createResource());
		return new ModuleBatchJobResDTO(resource);
	}

	@Override
	public ModuleInteractiveJobResDTO createInteractiveJobWorkload(InteractiveJobVO interactiveJobVOJobVO) {
		Deployment resource = (Deployment)createResource(interactiveJobVOJobVO.createResource());
		return new ModuleInteractiveJobResDTO(resource);
	}

	@Override
	public void createConnectTestDeployment(ConnectTestDTO connectTestDTO) {
		DeploymentVO deployment = DeploymentVO.dtoToEntity(connectTestDTO);
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//테스트용 deployment 생성
			kubernetesClient.resource(deployment.createResource()).create();
		}
	}

	@Override
	public boolean testConnectPodIsAvailable(String connectTestLabelName, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			//테스트용 pod 조회
			Pod connectPod = kubernetesClient.pods()
				.inNamespace(namespace)
				.withLabel("app", connectTestLabelName)
				.list()
				.getItems()
				.get(0);
			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			log.info(connectPod.getMetadata().getName());
			log.info(connectPod.getMetadata().getLabels().get("app"));
			log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			List<PodCondition> conditions = connectPod.getStatus().getConditions();
			boolean isAvailable = false;
			for (PodCondition condition : conditions) {
				log.info(condition.getStatus());
				String status = condition.getStatus();
				isAvailable = "true".equalsIgnoreCase(status) ? true : false;
				if(!isAvailable){
					break;
				}
			}
			return isAvailable;
		}
	}

	@Override
	public void deleteConnectTestDeployment(String deploymentName, String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps().deployments().inNamespace(namespace).withName(deploymentName).delete();
		}
	}

	@Override
	public void editAstragoDeployment(EditAstragoDeployment editAstragoDeployment) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Volume vol = new VolumeBuilder()
				.withName(editAstragoDeployment.getVolumeLabelSelectorName())
				.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(editAstragoDeployment.getPvcName(), false))
				.build();
			kubernetesClient.apps().deployments().inNamespace(editAstragoDeployment.getNamespace()).withName(editAstragoDeployment.getAstragoDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editSpec()
					.editOrNewTemplate()
					.editSpec()
					.addAllToVolumes(List.of(vol))
					.editContainer(0)
					.addNewVolumeMount()
					.withName(editAstragoDeployment.getAstragoDeploymentName())
					.withMountPath(editAstragoDeployment.getHostPath())
					.endVolumeMount()
					.endContainer()
					.endSpec()
					.endTemplate()
					.endSpec()
					.build());
		}
	}

	@Override
	public ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
		Job job = getBatchJob(workSpaceName, workloadName);
		return new ModuleBatchJobResDTO(job);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		Deployment deployment = getInteractiveJob(workSpaceName, workloadName);
		return new ModuleInteractiveJobResDTO(deployment);
	}

	@Override
	public List<ModuleWorkloadResDTO> getBatchJobWorkloadList(String workSpaceName) {
		JobList batchJobList = getBatchJobList(workSpaceName);
		return batchJobList.getItems().stream()
			.map(ModuleBatchJobResDTO::new)
			.collect(Collectors.toList());
	}

	@Override
	public List<ModuleWorkloadResDTO> getInteractiveJobWorkloadList(String workSpaceName) {
		DeploymentList interactiveJobList = getInteractiveJobList(workSpaceName);
		return interactiveJobList.getItems().stream()
			.map(ModuleInteractiveJobResDTO::new)
			.collect(Collectors.toList());
	}

	@Override
	public String deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		return deleteJob(workSpaceName, workloadName);
	}

	@Override
	public String deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return deleteInteractiveJob(workSpaceName, workloadName);
	}

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}

	private Job getBatchJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	private Deployment getInteractiveJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).get();
		}
	}

	private JobList getBatchJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).list();
		}
	}

	private DeploymentList getInteractiveJobList(String workSpaceName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.apps().deployments().inNamespace(workSpaceName).list();
		}
	}

	private String deleteJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).delete();
			return workloadName;
		}
	}

	private String deleteInteractiveJob(String workSpaceName, String workloadName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).delete();
			return workloadName;
		}
	}

}
