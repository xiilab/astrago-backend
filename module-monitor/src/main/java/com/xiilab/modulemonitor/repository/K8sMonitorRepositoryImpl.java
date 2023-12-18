package com.xiilab.modulemonitor.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulemonitor.config.K8sAdapter;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.K8sErrorStatus;
import com.xiilab.modulemonitor.service.K8sMonitorService;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class K8sMonitorRepositoryImpl implements K8sMonitorService {

	private final K8sAdapter k8sAdapter;
	List<Pod> pods = new ArrayList<>();
	private final List<K8sErrorStatus> targetReasons = Arrays.asList(
		K8sErrorStatus.CrashLoopBackOff,
		K8sErrorStatus.ImagePullBackOff,
		K8sErrorStatus.ErrImagePull,
		K8sErrorStatus.InvalidImageName
	);

	/**
	 * Workload Error 개수 조회
	 * @return 조회된 Workload Error Count
	 */
	@Override
	public long getWorkloadErrorCount() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			pods = kubernetesClient.pods().list().getItems();
		}
		// pods List중 Error Count 조회
		return convertErrorCountByPodList(pods);
	}

	/**
	 * Workload Error 개수 조회
	 * @param namespace 조회될 Namespace
	 * @return 조회된 Workload Error Count
	 */
	@Override
	public long getWorkloadErrorCount(String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			pods = kubernetesClient.pods().inNamespace(namespace).list().getItems();
		}
		// pods List중 Error Count 조회
		return convertErrorCountByPodList(pods);
	}

	/**
	 * Workload Error 개수 조회
	 * @param namespace 조회될 Namespace
	 * @param podName 조회될 podName
	 * @return 조회된 Workload Error Count
	 */
	@Override
	public long getWorkloadErrorCount(String namespace, String podName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Pod specificPod = kubernetesClient.pods()
				.inNamespace(namespace)
				.withName(podName)
				.get();
			if (specificPod != null) {
				pods.add(specificPod);
			}
		}
		// pods List중 Error Count 조회
		return convertErrorCountByPodList(pods);
	}

	/**
	 * POD List중에 Error Count 조회하는 API
	 * @param pods Error 체크할 POD List
	 * @return 조회된 Error Count
	 */
	private long convertErrorCountByPodList(List<Pod> pods) {
		return pods.stream()
			.map(pod -> {
				try {
					return pod.getStatus().getContainerStatuses().get(0).getState().getWaiting().getReason();
				} catch (NullPointerException | IndexOutOfBoundsException | IllegalStateException e) {
					return null;
				}
			})
			.filter(reason -> reason != null && targetReasons.contains(K8sErrorStatus.valueOf(reason)))
			.count();
	}

	/**
	 * Node에서 발생된 Error Count 조경
	 * @return
	 */
	@Override
	public long getNodeErrorCount() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.nodes().list().getItems().stream()
				.filter(node -> node.getStatus().getConditions().stream()
					.noneMatch(nodeCondition -> nodeCondition.getType().equals("Ready")))
				.count();
		}
	}

	/**
	 * K8s에서 발생한 Event List 조회하는 메소드
	 * @return 조회된 EventList
	 */
	@Override
	public List<ResponseDTO.EventDTO> getEventList() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Event> events = kubernetesClient.v1().events().list().getItems();
			return eventToDTO(events);
		}
	}

	/**
	 * K8s에서 발생한 Event List 조회하는 메소드
	 * @param namespace 조회할 NameSpace
	 * @return 조회된 EventList
	 */
	@Override
	public List<ResponseDTO.EventDTO> getEventList(String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Event> events = kubernetesClient.v1().events().inNamespace(namespace).list().getItems();
			return eventToDTO(events);
		}
	}

	/**
	 * K8s에서 발생한 Event List 조회하는 메소드
	 * @param namespace 조회할 NameSpace
	 * @param podName 조회할 podName
	 * @return 조회된 EventList
	 */
	@Override
	public List<ResponseDTO.EventDTO> getEventList(String namespace, String podName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Event> events = kubernetesClient.v1().events().inNamespace(namespace).list().getItems().stream()
				.filter(event ->
					event.getInvolvedObject().getName().equals(podName)
				).toList();
			return eventToDTO(events);
		}
	}

	private List<ResponseDTO.EventDTO> eventToDTO(List<Event> eventList) {
		return eventList.stream().map(event ->
			ResponseDTO.EventDTO.builder()
				.type(event.getType())
				.workloadName(event.getMetadata().getNamespace())
				.time(event.getEventTime() == null ? "" : event.getEventTime().getTime())
				.reason(event.getReason())
				.message(event.getMessage())
				.build()
		).toList();
	}
}
