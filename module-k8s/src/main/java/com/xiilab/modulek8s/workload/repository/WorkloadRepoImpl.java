package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.JobReqDTO;
import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadReq;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentList;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobList;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class WorkloadRepoImpl implements WorkloadRepo {
    private final K8sAdapter k8sAdapter;

    @Override
    public JobResDTO createBatchJobWorkload(JobReqDTO jobReqDTO) {
        Job resource = (Job) createResource(jobReqDTO.createResource());
        return new JobResDTO(resource);
    }

    @Override
    public WorkloadRes createInteractiveJobWorkload(WorkloadReq workloadReqDTO) {
        return null;
    }

    @Override
    public JobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
        Job job = (Job) getBatchJob(workSpaceName, workloadName);
        return new JobResDTO(job);
    }

    @Override
    public WorkloadRes getInteractiveJobWorkload(String workSpaceName, String workloadName) {
        Deployment deployment = (Deployment) getInteractiveJob(workSpaceName, workloadName);
        return null;
//        return new IntJobDTO(deployment);
    }

    @Override
    public List<JobResDTO> getBatchJobWorkloadList(String workSpaceName) {
        JobList batchJobList = getBatchJobList(workSpaceName);
        return batchJobList.getItems().stream().map(JobResDTO::new).toList();
    }

    @Override
    public List<WorkloadRes> getInteractiveJobWorkloadList(String workSpaceName) {
        DeploymentList interactiveJobList = getInteractiveJobList(workSpaceName);
//        return interactiveJobList.getItems().stream().map(IntJobDTO::new).toList();
        return null;
    }

    @Override
    public WorkloadRes updateInteractiveJobWorkload(WorkloadReq workloadReqDTO) {
//        Deployment deployment = updateInteractiveJob(workloadReqDTO);
//        return new IntJobDTO(deployment);
        return null;
    }

    @Override
    public void deleteBatchJobWorkload(String workSpaceName, String workloadName) {
        deleteJob(workSpaceName, workloadName);
    }

    @Override
    public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
        deleteInteractiveJob(workSpaceName, workloadName);
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

    private Deployment updateInteractiveJob(JobReqDTO jobReqDTO) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            return kubernetesClient.apps().deployments().inNamespace(jobReqDTO.getWorkspace()).withName(jobReqDTO.getName())
                    .edit(jobReqDTO::updateResource);
        }
    }

    private void deleteJob(String workSpaceName, String workloadName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).delete();
        }
    }

    private void deleteInteractiveJob(String workSpaceName, String workloadName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).delete();
        }
    }

}
