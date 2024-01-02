package com.xiilab.modulek8s.workload.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

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

	private HasMetadata createResource(HasMetadata hasMetadata) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.resource(hasMetadata).create();
		}
	}
}
