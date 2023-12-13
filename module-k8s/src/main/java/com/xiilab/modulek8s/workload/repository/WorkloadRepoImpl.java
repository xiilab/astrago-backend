package com.xiilab.modulek8s.workload.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.JobReqVODTO;
import com.xiilab.modulek8s.workload.dto.JobResDTO;
import com.xiilab.modulek8s.workload.dto.WorkloadReqVO;
import com.xiilab.modulek8s.workload.dto.WorkloadRes;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkloadRepoImpl implements WorkloadRepo{
	private final K8sAdapter k8sAdapter;
	@Override
	public JobResDTO createBatchJobWorkload(JobReqVODTO jobReqDTO) {
		Job resource = (Job)createResource(jobReqDTO.createResource());
		return new JobResDTO(resource);
	}

	@Override
	public WorkloadRes createInteractiveJobWorkload(WorkloadReqVO workloadReqVODTO) {
		return null;
	}

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}
}
