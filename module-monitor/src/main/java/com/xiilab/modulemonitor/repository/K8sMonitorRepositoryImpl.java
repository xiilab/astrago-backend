package com.xiilab.modulemonitor.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulemonitor.config.MonitorK8sAdapter;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.K8sErrorStatus;

import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class K8sMonitorRepositoryImpl implements K8sMonitorRepository {

	private final MonitorK8sAdapter monitorK8SAdapter;
	private final String CPU = "CPU";
	private final String MEM = "memory";
	private final String REQUEST_MEM = "requests.memory";
	private final String NVIDIA_GPU = "requests.nvidia.com/gpu";
	private final String REQUEST_CPU = "requests.cpu";
	private final String GPU = "GPU";
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			return kubernetesClient.pods().inNamespace(namespace).list().getItems().size();
		}
	}

	/**
	 * K8s에서 발생한 Event List 조회하는 메소드
	 * @return 조회된 EventList
	 */
	@Override
	public List<ResponseDTO.EventDTO> getEventList() {
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			List<Event> events = kubernetesClient.v1().events().inAnyNamespace().list().getItems();
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {

			List<Event> events = kubernetesClient.v1().events().inNamespace(namespace).list().getItems().stream()
				.filter(event -> event.getKind().equalsIgnoreCase("pod"))
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
				.time(event.getEventTime() == null ? event.getLastTimestamp() : event.getEventTime().getTime())
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			return kubernetesClient.nodes().list().getItems().stream().map(node ->
				ResponseDTO.NodeResponseDTO.builder()
					.nodeName(node.getMetadata().getName())
					.ip(node.getStatus().getAddresses().get(0).getAddress())
					.status(node.getStatus()
						.getConditions()
						.stream()
						.filter(nodeCondition -> nodeCondition.getStatus().equals("True"))
						.toList()
						.stream().findFirst().orElse(new NodeCondition().toBuilder().withType("NotReady").build())
						.getType())
					.build()).toList();
		}
	}

	/**
	 * 워크스페이스 리소스 정보 조회 메소드
	 */
	@Override
	public List<ResponseDTO.WorkloadResponseDTO> getWlList(){
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			// K8s 워크로드 리스트 조회
			return kubernetesClient.pods().list().getItems().stream().map(pod ->
				ResponseDTO.WorkloadResponseDTO.builder()
					.wlName(pod.getMetadata().getName())
					.wsName(pod.getMetadata().getNamespace())
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
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
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
				.gpuUsed(resourceQuota.getUsed().get(NVIDIA_GPU).toString())
				.gpuHard(resourceQuota.getHard().get(NVIDIA_GPU).toString())
				.cpuUsed(resourceQuota.getUsed().get(REQUEST_CPU).toString())
				.cpuHard(resourceQuota.getHard().get(REQUEST_CPU).toString())
				.memUsed(resourceQuota.getUsed().get(REQUEST_MEM).toString())
				.memHard(resourceQuota.getHard().get(REQUEST_MEM).toString())
				.workloadResponseDTOS(wlList)
				.build();
		}
	}

	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterCPU(String nodeName, double cpuUsage){
		try(KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()){
			List<Node> nodeList = getNodeList(nodeName);
			List<Pod> podList = kubernetesClient.pods().inAnyNamespace().list().getItems();

			// 모든 노드의 CPU total 값을 합산
			long totalCpuCapacity = totalCapacity(nodeList, CPU);

			// 모든 노드의 cpuRequest 값을 합산
			String totalCpuRequests = totalRequests(nodeList, podList, CPU);
			String request = DataConverterUtil.roundToString(totalCpuRequests);

			return ResponseDTO.ResponseClusterDTO.builder()
				.name(CPU)
				.total(totalCpuCapacity)
				.cpuRequest(DataConverterUtil.roundToNearestHalf(Double.parseDouble(request)) / 1000)
				.cpuUsage(DataConverterUtil.roundToNearestHalf((totalCpuCapacity * cpuUsage) / 100))
				.build();
		}
	}

	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterMEM(String nodeName, String memUsage){
		try(KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()){
			List<Node> nodeList = getNodeList(nodeName);
			List<Pod> podList = kubernetesClient.pods().inAnyNamespace().list().getItems();

			// 모든 노드의 CPU total 값을 합산
			long totalMemCapacity = totalCapacity(nodeList, "MEM");

			// 모든 노드의 cpuRequest 값을 합산
			String totalMemRequests = totalRequests(nodeList, podList, "MEM");

			return ResponseDTO.ResponseClusterDTO.builder()
				.name(MEM)
				.total(totalMemCapacity * 1024)
				.request(Long.parseLong(totalMemRequests)  * 1024)
				.usage(Long.parseLong(memUsage)  * 1024)
				.build();
		}
	}
	@Override
	public ResponseDTO.ResponseClusterDTO getDashboardClusterGPU(String nodeName){
		try(KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()){
			List<Node> nodeList = getNodeList(nodeName);
			List<Pod> podList = kubernetesClient.pods().inAnyNamespace().list().getItems();

			// 모든 노드의 GPU total 값을 합산
			long totalGpuCapacity = totalCapacity(nodeList, GPU);

			// 모든 노드의 cpuRequest 값을 합산
			String totalGpuRequests = totalRequests(nodeList, podList, GPU);

			return ResponseDTO.ResponseClusterDTO.builder()
				.name(GPU)
				.total(totalGpuCapacity)
				.usage(Long.parseLong(totalGpuRequests))
				.build();
		}
	}

	@Override
	public ResponseDTO.ResponseClusterResourceDTO getClusterTotalResource() {
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			List<Node> nodeList = kubernetesClient.nodes().list().getItems();
			List<Pod> podList = kubernetesClient.pods().list().getItems();
			int cpu = (int)totalCapacity(nodeList, CPU);
			int mem = (int)totalCapacity(nodeList, "MEM");
			int gpu = (int)totalCapacity(nodeList, GPU);
			String totalCpuRequests = totalRequests(nodeList, podList, CPU);
			String totalMemRequests = totalRequests(nodeList, podList, "MEM");
			String totalGpuRequests = totalRequests(nodeList, podList, GPU);
			return new ResponseDTO.ResponseClusterResourceDTO(cpu, mem, gpu);
		}
	}

	private List<Node> getNodeList(String nodeName){
		try(KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			if (!StringUtils.isEmpty(nodeName)) {
				return List.of(kubernetesClient.nodes().withName(nodeName).get());
			} else {
				return kubernetesClient.nodes().list().getItems();
			}
		}
	}

	private long totalCapacity(List<Node> nodeList, String resourceName){
		return switch (resourceName) {
			case CPU ->
				nodeList.stream()
					.mapToInt(node ->
						Integer.parseInt(node.getStatus().getCapacity().get("cpu").toString()))
					.sum();
			case "MEM" ->
				nodeList.stream()
					.mapToLong(node ->
						Integer.parseInt(node.getStatus().getCapacity().get(MEM).toString().replace("Ki", "")))
					.sum();
			case GPU ->
				nodeList.stream()
					.filter(node -> Objects.nonNull(node.getStatus().getCapacity().get("nvidia.com/gpu")))
					.mapToInt(node ->
						Integer.parseInt(node.getStatus().getCapacity().get("nvidia.com/gpu").toString()))
					.sum();
			default -> 0;
		};
	}

	private String totalRequests(List<Node> nodeList, List<Pod> podList, String resourceName){
		return switch (resourceName) {
			case "CPU" ->
				nodeList.stream()
					.map(node -> {
						List<Pod> runningPodsOnNode = podList.stream()
							.filter(pod ->
								!StringUtils.isEmpty(pod.getSpec().getNodeName()) && pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
							.toList();

						return calculateTotalCpuRequests(runningPodsOnNode);
					})
					.reduce("0", (x, y) ->
						String.valueOf(DataConverterUtil.parseAndSum(x, y)));
			case GPU ->
				nodeList.stream()
					.map(node -> {
						List<Pod> runningPodsOnNode = podList.stream()
							.filter(pod ->
								!StringUtils.isEmpty(pod.getSpec().getNodeName()) && pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
							.toList();

						return calculateTotalGpuRequests(runningPodsOnNode);
					}).reduce("0", (x, y) -> String.valueOf(Integer.parseInt(x) + Integer.parseInt(y)));
			case "MEM" ->
				nodeList.stream()
					.map(node -> {
						List<Pod> runningPodsOnNode = podList.stream()
							.filter(pod ->
								!StringUtils.isEmpty(pod.getSpec().getNodeName()) && pod.getSpec().getNodeName().equals(node.getMetadata().getName()))
							.toList();

						return calculateTotalMemRequests(runningPodsOnNode);
					})
					.reduce("0", (x, y) ->
						String.valueOf(DataConverterUtil.parseAndSum(x, y)));
			default -> "";
		};
	}

	private String calculateTotalCpuRequests(List<Pod> podList) {
		String containerResult = podList.stream()
			.flatMap(pod -> pod.getSpec().getContainers().stream())
			.filter(container -> container.getResources().getRequests().get("cpu") != null)
			.map(container -> {
				String cpuAmount = container.getResources().getRequests().get("cpu").getAmount();
				String cpuFormat = container.getResources().getRequests().get("cpu").getFormat();
				if (cpuFormat.isBlank()) {
					return cpuAmount + "000";
				} else {
					return cpuAmount;
				}
			})
			.reduce("0", (acc, val) -> String.valueOf(Integer.parseInt(acc) + Integer.parseInt(val)));

		String initContainerResult = podList.stream()
			.flatMap(pod -> pod.getSpec().getInitContainers().stream())
			.filter(container -> container.getResources().getRequests().get("cpu") != null)
			.map(container -> {
				String cpuAmount = container.getResources().getRequests().get("cpu").getAmount();
				String cpuFormat = container.getResources().getRequests().get("cpu").getFormat();
				if (cpuFormat.isBlank()) {
					return cpuAmount + "000";
				} else {
					return cpuAmount;
				}
			})
			.reduce("0", (acc, val) -> String.valueOf(Integer.parseInt(acc) + Integer.parseInt(val)));

		return String.valueOf(Integer.parseInt(containerResult) + Integer.parseInt(initContainerResult));
	}

	private String calculateTotalGpuRequests(List<Pod> podList) {
		String containerResult = podList.stream()
			.flatMap(pod -> pod.getSpec().getContainers().stream())
			.filter(container -> container.getResources().getRequests().get("nvidia.com/gpu") != null)
			.map(container -> {
				String cpuAmount = container.getResources().getRequests().get("nvidia.com/gpu").getAmount();
				return cpuAmount;
			})
			.reduce("0", (acc, val) -> String.valueOf(Integer.parseInt(acc) + Integer.parseInt(val)));

		return String.valueOf(Integer.parseInt(containerResult));
	}
	private String calculateTotalMemRequests(List<Pod> podList) {
		String containerResult = podList.stream()
			.flatMap(pod -> pod.getSpec().getContainers().stream())
			.filter(container -> container.getResources().getRequests().get(MEM) != null)
			.map(container -> {
				String memAmount = container.getResources().getRequests().get(MEM).getAmount();
				String memFormat = container.getResources().getRequests().get(MEM).getFormat();
				long memInKiB = DataConverterUtil.convertToKiB(memAmount, memFormat);
				return String.valueOf(memInKiB);
			})
			.reduce("0", (acc, val) -> String.valueOf(Long.parseLong(acc) + Long.parseLong(val)));

		String initContainerResult = podList.stream()
			.flatMap(pod -> pod.getSpec().getInitContainers().stream())
			.filter(container -> container.getResources().getRequests().get(MEM) != null)
			.map(container -> {
				String memAmount = container.getResources().getRequests().get(MEM).getAmount();
				String memFormat = container.getResources().getRequests().get(MEM).getFormat();
				long memInKiB = DataConverterUtil.convertToKiB(memAmount, memFormat);
				return String.valueOf(memInKiB);
			})
			.reduce("0", (acc, val) -> String.valueOf(Long.parseLong(acc) + Long.parseLong(val)));

		return String.valueOf(Integer.parseInt(containerResult) + Integer.parseInt(initContainerResult));
	}

	@Override
	public Map<String, Map<String, Long>> getClusterReason(long minute) {
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			// 이벤트 리스트 불러오기
			List<io.fabric8.kubernetes.api.model.events.v1.Event> eventList = kubernetesClient.events()
				.v1()
				.events()
				.list()
				.getItems();
			// 현재 시간 UTC 조회
			Instant now = Instant.now().truncatedTo(ChronoUnit.MINUTES);
			List<ResponseDTO.ClusterReasonDTO> clusterReasonDTOList = eventList.stream()
				.filter(event -> event.getType().equals("Warning"))
				.map(event -> {
						String eventTime =
							!StringUtils.isBlank(event.getDeprecatedLastTimestamp()) ? event.getDeprecatedLastTimestamp() :
								event.getEventTime().getTime();

						long fewMinutesAgo = DataConverterUtil.fewMinutesAgo(now, eventTime);
						return ResponseDTO.ClusterReasonDTO.builder()
							.reason(event.getReason())
							.time(DataConverterUtil.datetimeFormatter(fewMinutesAgo))
							.lastSEEN(fewMinutesAgo)
							.build();
					}
				).toList();
			// lastSEEN으로 정렬된 맵 반환
			return clusterReasonDTOList.stream()
				.filter(clusterReasonDTO -> clusterReasonDTO.lastSEEN() < minute)
				.collect(Collectors.groupingBy(ResponseDTO.ClusterReasonDTO::time,
					Collectors.groupingBy(ResponseDTO.ClusterReasonDTO::reason, Collectors.counting())
				))
				.entrySet().stream()
				.sorted(Map.Entry.comparingByKey()) // 시간별로 정렬
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
					(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		}
	}

	@Override
	public String getNodeName(String podName, String namespace){
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			return Objects.nonNull(kubernetesClient.pods().inNamespace(namespace).withName(podName).get())?
				kubernetesClient.pods().inNamespace(namespace).withName(podName).get().getSpec().getNodeName() != null ?
					kubernetesClient.pods().inNamespace(namespace).withName(podName).get().getSpec().getNodeName() : ""
				: "";
		}
	}

	@Override
	public ResponseDTO.ClusterPodInfo getClusterPendingAndFailPod(String podName, String namespace){
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
				Pod pod = kubernetesClient.pods().inNamespace(namespace).withName(podName).get();

			String reason = "none";

			if (pod != null) {
				if (!pod.getStatus().getContainerStatuses().isEmpty()) {
					String containerReason = pod.getStatus().getContainerStatuses().get(0).getState().getWaiting() != null ?
						pod.getStatus().getContainerStatuses().get(0).getState().getWaiting().getReason() : null ;
					reason = containerReason != null ? containerReason : pod.getStatus().getReason();
				} else {
					reason = pod.getStatus().getReason();
				}
			}
			return ResponseDTO.ClusterPodInfo.builder()
				.podName(podName)
				.nodeName(pod.getSpec().getNodeName())
				.status(pod.getStatus().getPhase())
				.reason(reason)
				.build();
		}
	}

	@Override
	public String getWorkspaceName(String workspaceName){
		try (KubernetesClient kubernetesClient = monitorK8SAdapter.configServer()) {
			return kubernetesClient.namespaces().withName(workspaceName).get().getMetadata().getAnnotations().get("name");
		}
	}


}
