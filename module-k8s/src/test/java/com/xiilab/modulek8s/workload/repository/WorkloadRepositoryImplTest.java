package com.xiilab.modulek8s.workload.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;

@ExtendWith(MockitoExtension.class)
@EnableKubernetesMockClient(crud = true)
class WorkloadRepositoryImplTest {
	KubernetesMockServer server;
	KubernetesClient client;
	@Spy
	K8sAdapter k8sAdapter;
	@InjectMocks
	WorkloadRepositoryImpl workloadRepository;

	@BeforeEach
	void setUp() {
		client = k8sAdapter.configServer();
		// client = server.createClient();
		// given(k8sAdapter.configServer()).willReturn(client);
	}

	@AfterEach
	void destroy() {
		if (client != null) {
			client.close();
		}
		if (server != null) {
			server.destroy();
		}
	}

	/*@Test
	void createBatchJobWorkload() {
		ModuleCreateWorkloadReqDTO createWorkloadReqDTO = createModuleWorkloadReqDTO();

		workloadRepository.createBatchJobWorkload(createWorkloadReqDTO.toBatchJobVO());

		client = server.createClient();
		JobList batchJobList = client.batch().v1().jobs().list();
		Assertions.assertThat(batchJobList.getItems()).hasSize(1);
	}

	@Test
	void createInteractiveJobWorkload() {
		ModuleCreateWorkloadReqDTO createWorkloadReqDTO = createModuleWorkloadReqDTO();

		workloadRepository.createInteractiveJobWorkload(createWorkloadReqDTO.toInteractiveJobVO());

		client = server.createClient();
		DeploymentList list = client.apps().deployments().list();
		Assertions.assertThat(list.getItems()).hasSize(1);
	}*/

	@Test
	void findWorkloadResourceUsageListByUserId() {
		List<Job> jobList = client.batch()
			.v1()
			.jobs()
			.inAnyNamespace()
			.withLabel("creator-id", "a222e289-baea-4d6c-bfa1-71d19e7154ea")
			.list()
			.getItems();
		// jobList.getItems().get(0).getSpec().getTemplate().getSpec().getContainers().get(0).getResources().getRequests().getOrDefault("cpu")
		List<Deployment> deploymentList = client.apps()
			.deployments()
			.inAnyNamespace()
			.withLabel("creator-id", "a222e289-baea-4d6c-bfa1-71d19e7154ea")
			.list()
			.getItems();

		int totalCpuAmount = 0;
		int totalMemoryAmount = 0;
		int totalNormalGpuAmount = 0;
		int totalDivisionGpuAmount = 0;

		for (Job job : jobList) {
			Map<String, Quantity> jobRequests = Optional.ofNullable(job
				.getSpec()
				.getTemplate()
				.getSpec()
				.getContainers()
				.get(0)
				.getResources()
				.getRequests()
			).orElseGet(Collections::emptyMap);

			totalCpuAmount += parseCpu(jobRequests.get("cpu"));
			totalMemoryAmount += parseMemory(jobRequests.get("memory"));
			totalNormalGpuAmount += sumGpuResources(jobRequests, false);
			totalDivisionGpuAmount += sumGpuResources(jobRequests, true);
		}

		for (Deployment deployment : deploymentList) {
			Map<String, Quantity> deploymentRequests = Optional.ofNullable(deployment
				.getSpec()
				.getTemplate()
				.getSpec()
				.getContainers()
				.get(0)
				.getResources()
				.getRequests()
			).orElseGet(Collections::emptyMap);

			totalCpuAmount += parseCpu(deploymentRequests.get("cpu"));
			totalMemoryAmount += parseMemory(deploymentRequests.get("memory"));
			totalNormalGpuAmount += sumGpuResources(deploymentRequests, false);
			totalDivisionGpuAmount += sumGpuResources(deploymentRequests, true);
		}
	}

	private int parseCpu(Quantity quantity) {
		return Optional.ofNullable(quantity)
			.map(Quantity::getAmount)
			.map(Integer::parseInt)
			.orElse(0);
	}

	private int parseMemory(Quantity quantity) {
		return Optional.ofNullable(quantity)
			.map(Quantity::getAmount)
			.map(amount -> amount.replaceAll("[^0-9]", "")) // Gi 등 단위를 제거
			.map(Integer::parseInt)
			.orElse(0);
	}

	private int sumGpuResources(Map<String, Quantity> requests, boolean includeDivision) {
		return requests.entrySet().stream()
			.filter(entry -> includeDivision ? entry.getKey().startsWith("nvidia.com/gpu") : entry.getKey().equals("nvidia.com/gpu"))
			.mapToInt(entry -> Optional.ofNullable(entry.getValue())
				.map(Quantity::getAmount)
				.map(Integer::parseInt)
				.orElse(0))
			.sum();
	}
}
