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
import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class K8sMonitorRepositoryImpl implements K8sMonitorService {

	private final K8sAdapter k8sAdapter;
	private final List<K8sErrorStatus> targetReasons = Arrays.asList(
		K8sErrorStatus.CrashLoopBackOff,
		K8sErrorStatus.ImagePullBackOff,
		K8sErrorStatus.ErrImagePull,
		K8sErrorStatus.InvalidImageName
	);

	/**
	 * Workload Error 개수 조회
	 * @param namespace 조회될 Namespace
	 * @return 조회된 Workload Error Count
	 */
	@Override
	public long getWorkloadErrorCount(String namespace) {
		List<Pod> pods = new ArrayList<>();
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			pods = kubernetesClient.pods().inNamespace(namespace).list().getItems();
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
	 * Workspace에 생성된 Workload Count하는 메소드
	 * @param namespace 조회될 Workspace name
	 * @return 해당 Workspace에 생성된 Workload
	 */
	@Override
	public long getWorkloadCountByNamespace(String namespace) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.pods().inNamespace(namespace).list().getItems().size();
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

	/**
	 * K8s에서 발생한 이벤트 EventDTO로 변환하는 메소드
	 * @param eventList 변환될 K8s Event List
	 */
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

	/**
	 * 노드 전체 리스트 조회 메소드
	 */
	@Override
	public List<ResponseDTO.NodeResponseDTO> getNodeList(){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.nodes().list().getItems().stream().map(node ->
				ResponseDTO.NodeResponseDTO.builder()
					.nodeName(node.getMetadata().getName())
					.ip(node.getStatus().getAddresses().get(0).getAddress())
					.status(node.getStatus().getConditions().stream().filter(nodeCondition -> nodeCondition.getStatus().equals("True")).toList().get(0).getType())
					.build()).toList();
		}
	}

	/**
	 * 워크스페이스 리소스 정보 조회 메소드
	 */
	@Override
	public List<ResponseDTO.WorkloadResponseDTO> getWlList(){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			// K8s 워크로드 리스트 조회
			return kubernetesClient.pods().list().getItems().stream().map(pod ->
				ResponseDTO.WorkloadResponseDTO.builder()
					.wlName(pod.getMetadata().getNamespace())
					.status(pod.getStatus().getPhase())
					.build()).toList();
		}
	}
	/**
	 * 해당 WS의 Resource Info 조회 API
	 * @param namespace 조회될 WS name
	 * @return CPU,GPU,MEM등의 ResourceQuota, 상태별 워크로드 리스트
	 */
	@Override
	public ResponseDTO.WorkspaceResponseDTO getWlList(String namespace){
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			// namespace의 resourceQuota 조회
			ResourceQuotaStatus resourceQuota = kubernetesClient.resourceQuotas()
				.inNamespace(namespace)
				.list()
				.getItems()
				.get(0)
				.getStatus();
			// Workspace별 Workload List
			List<ResponseDTO.WorkloadResponseDTO> wlList = kubernetesClient.pods().list().getItems().stream()
				.filter(pod -> pod.getMetadata().getNamespace().equals(namespace))
				.map(pod -> ResponseDTO.WorkloadResponseDTO.builder()
					.wlName(pod.getMetadata().getNamespace())
					.status(pod.getStatus().getPhase())
					.build()).toList();

			return ResponseDTO.WorkspaceResponseDTO.builder()
				.wsName(namespace)
				.gpuUsed(resourceQuota.getUsed().get("requests.nvidia.com/gpu").toString())
				.gpuHard(resourceQuota.getHard().get("requests.nvidia.com/gpu").toString())
				.cpuUsed(resourceQuota.getUsed().get("requests.cpu").toString())
				.cpuHard(resourceQuota.getHard().get("requests.cpu").toString())
				.memUsed(resourceQuota.getUsed().get("requests.memory").toString())
				.memHard(resourceQuota.getHard().get("requests.memory").toString())
				.workloadResponseDTOS(wlList)
				.build();
		}
	}
}
