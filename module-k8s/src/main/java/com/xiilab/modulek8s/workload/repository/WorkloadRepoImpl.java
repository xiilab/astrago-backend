package com.xiilab.modulek8s.workload.repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.JobReqDTO;
import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadReq;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    public JobResDTO getBatchJobWorkload(String workSapceName, String workloadName) {
        Job job = (Job) getBatchJob(workSapceName, workloadName);
        return new JobResDTO(job);
    }

    @Override
    public WorkloadRes getInteractiveJobWorkload(String workSpaceName, String workloadName) {
        Deployment deployment = (Deployment) getInteractiveJob(workloadName, workSpaceName);
        return null;
//        return new IntJobDTO(deployment);
    }

    @Override
    public void deleteBatchJobWorkload(String workSpaceName, String workloadName) {
        deleteJob(workloadName, workSpaceName);
    }

    @Override
    public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
        deleteInteractiveJob(workloadName, workSpaceName);
    }


    private HasMetadata createResource(HasMetadata hasMetadata) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            return kubernetesClient.resource(hasMetadata).create();
        }
    }

    private HasMetadata getBatchJob(String workloadName, String workSpaceName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            return kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).get();
        }
    }

    private HasMetadata getInteractiveJob(String workloadName, String workSpaceName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            return kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).get();
        }
    }

    private void deleteJob(String workloadName, String workSpaceName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            kubernetesClient.batch().v1().jobs().inNamespace(workSpaceName).withName(workloadName).delete();
        }
    }

    private void deleteInteractiveJob(String workloadName, String workSpaceName) {
        try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
            kubernetesClient.apps().deployments().inNamespace(workSpaceName).withName(workloadName).delete();
        }
    }

}
