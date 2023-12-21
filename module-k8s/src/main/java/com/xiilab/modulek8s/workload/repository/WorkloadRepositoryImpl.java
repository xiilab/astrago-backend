package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.vo.JobVO;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WorkloadRepositoryImpl implements WorkloadRepository {
	private final K8sAdapter k8sAdapter;
	@Override
	public JobResDTO createBatchJobWorkload(JobVO jobVO) {
		Job resource = (Job)createResource(jobVO.createResource());
		return new JobResDTO(resource);
	}

	@Override
	public JobResDTO createInteractiveJobWorkload(JobVO jobVO) {
		return null;
	}

    @Override
    public JobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
        Job job = (Job) getBatchJob(workSpaceName, workloadName);
        return new JobResDTO(job);
    }

    @Override
    public WorkloadResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
        Deployment deployment = (Deployment) getInteractiveJob(workSpaceName, workloadName);
        return null;
//        return new IntJobDTO(deployment);
    }

    @Override
    public List<WorkloadResDTO> getBatchJobWorkloadList(String workSpaceName) {
        JobList batchJobList = getBatchJobList(workSpaceName);
        return batchJobList.getItems().stream()
                .map(JobResDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkloadResDTO> getInteractiveJobWorkloadList(String workSpaceName) {
        DeploymentList interactiveJobList = getInteractiveJobList(workSpaceName);
//        return interactiveJobList.getItems().stream().map(IntJobDTO::new).toList();
        return null;
    }

    @Override
    public WorkloadResDTO updateInteractiveJobWorkload(CreateWorkloadReqDTO workloadReqDTO) {
//        Deployment deployment = updateInteractiveJob(workloadReqDTO);
//        return new IntJobDTO(deployment);
        return null;
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

//    private Deployment updateInteractiveJob(JobResDTO jobReqDTO) {
//        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
//            return kubernetesClient.apps().deployments().inNamespace(jobReqDTO.getWorkspace()).withName(jobReqDTO.getName())
//                    .edit(jobReqDTO::updateResource);
//        }
//    }

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
