package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
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
    public ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
        Job job = (Job) getBatchJob(workSpaceName, workloadName);
        return new ModuleBatchJobResDTO(job);
    }


    @Override
    public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
        Deployment deployment = (Deployment) getInteractiveJob(workSpaceName, workloadName);
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

    private HasMetadata getBatchJob(String workSpaceName, String workloadName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get();
        }
    }

    private HasMetadata getInteractiveJob(String workSpaceName, String workloadName) {
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
