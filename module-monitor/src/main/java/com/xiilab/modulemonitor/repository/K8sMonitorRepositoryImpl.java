package com.xiilab.modulemonitor.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulemonitor.config.K8sAdapter;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.service.K8sMonitorRepository;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class K8sMonitorRepositoryImpl implements K8sMonitorRepository {

	private final K8sAdapter k8sAdapter;

	@Override
	public List<ResponseDTO.RealTimeDTO> getK8sMetricsByQuery(RequestDTO requestDTO) {
		String metricName = requestDTO.metricName();
		if(metricName.equals("WL_ERROR_COUNT")){
			return getWorkloadErrorCount(requestDTO);
		}else if(metricName.equals("NODE_ERROR_COUNT")){
			return getNodeErrorCount();
		}else {
			return List.of(ResponseDTO.RealTimeDTO.builder().build());
		}
	}

	/**
	 * Workload Error 개수 조회
	 * @param requestDTO namespace, pod의 정보가 담긴 객체
	 * @return 조회된 Node Error Count
	 */
	private List<ResponseDTO.RealTimeDTO> getWorkloadErrorCount(RequestDTO requestDTO){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<String> targetReasons = Arrays.asList("CrashLoopBackOff", "Completed", "ContainerCreating",
				"ImagePullBackOff", "ErrImagePull", "InvalidImageName");

			List<Pod> items = new ArrayList<>();
			if (!requestDTO.namespace().isEmpty() && !requestDTO.podName().isEmpty()) {
				Pod specificPod = kubernetesClient.pods().inNamespace(requestDTO.namespace()).withName(requestDTO.podName()).get();
				if (specificPod != null) {
					items.add(specificPod);
				}
			} else if (!requestDTO.namespace().isEmpty()) {
				items.addAll(kubernetesClient.pods().inNamespace(requestDTO.namespace()).list().getItems());
			} else if (!requestDTO.podName().isEmpty()) {
				Pod specificPod = kubernetesClient.pods().withName(requestDTO.podName()).get();
				if (specificPod != null) {
					items.add(specificPod);
				}
			} else {
				items.addAll(kubernetesClient.pods().list().getItems());
			}

			long count = items.stream()
				.map(pod -> {
					try {
						return pod.getStatus().getContainerStatuses().get(0).getState().getWaiting().getReason();
					} catch (NullPointerException | IndexOutOfBoundsException | IllegalStateException e) {
						return null;
					}
				})
				.filter(reason -> reason != null && targetReasons.contains(reason))
				.count();

			return List.of(ResponseDTO.RealTimeDTO.builder()
				.metricName("WL_ERROR_COOUNT")
				.value(String.valueOf(count))
				.build());
		}
	}
	private List<ResponseDTO.RealTimeDTO> getNodeErrorCount(){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {

			long nodeErrorCount = kubernetesClient.nodes().list().getItems().stream()
				.filter(node -> node.getStatus().getConditions().stream()
					.noneMatch(nodeCondition -> nodeCondition.getType().equals("Ready")))
				.count();

			return List.of(ResponseDTO.RealTimeDTO.builder()
				.metricName("NODE_ERROR_COUNT")
				.value(String.valueOf(nodeErrorCount))
				.build());

		}
	}
}
