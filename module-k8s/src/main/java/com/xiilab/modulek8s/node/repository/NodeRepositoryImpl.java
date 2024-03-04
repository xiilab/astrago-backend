package com.xiilab.modulek8s.node.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.NodeErrorCode;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.node.dto.MigMixedDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.MIGProduct;
import com.xiilab.modulek8s.node.enumeration.MIGStrategy;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
	private final K8sAdapter k8sAdapter;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String GPU_NAME = "nvidia.com/gpu.product";
	private final String GPU_COUNT = "nvidia.com/gpu.count";
	private final String GPU_DRIVER_VER_MAJOR = "nvidia.com/cuda.driver.major";
	private final String GPU_DRIVER_VER_MINOR = "nvidia.com/cuda.driver.minor";
	private final String GPU_DRIVER_VER_REV = "nvidia.com/cuda.driver.rev";
	private final String GPU_MEMORY = "nvidia.com/gpu.memory";
	private final String GPU = "nvidia.com/gpu";
	private final String CPU = "cpu";
	private final String EPHEMERAL_STORAGE = "ephemeral-storage";
	private final String HUGEPAGES_1Gi = "hugepages-1Gi";
	private final String HUGEPAGES_2Mi = "hugepages-2Mi";
	private final String MEMORY = "memory";
	private final String PODS = "pods";
	private final String HOST_NAME = "kubernetes.io/hostname";
	private final String ROLE = "node-role.kubernetes.io/control-plane";
	private final String NETWORK_UNAVAILABLE = "NetworkUnavailable";
	private final String MEMORY_PRESSURE = "MemoryPressure";
	private final String DISK_PRESSURE = "DiskPressure";
	private final String PID_PRESSURE = "PIDPressure";
	private final String READY = "Ready";
	@Value("${mig-profile-path}")
	private String migProfilePath;

	@Override
	public ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize) {
		List<ResponseDTO.NodeDTO> nodeDtos = new ArrayList<>();
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Node> nodes = client.nodes().list().getItems();

			for (Node node : nodes) {
				boolean migCapable = getMigCapable(node);
				List<NodeCondition> conditions = node.getStatus().getConditions();
				boolean status = isStatus(conditions);
				ResponseDTO.NodeDTO dto = ResponseDTO.NodeDTO.builder()
					.nodeName(node.getMetadata().getName())
					.gpuCount(node.getMetadata().getLabels().get(GPU_COUNT) == null ? 0 :
						Integer.parseInt(node.getMetadata().getLabels().get(GPU_COUNT)))
					.ip(node.getStatus().getAddresses().get(0).getAddress())
					.status(status)
					.age(new AgeDTO(node.getMetadata().getCreationTimestamp()))
					.schedulable(
						node.getSpec().getUnschedulable() == null || node.getSpec().getUnschedulable() == false ? true :
							false)
					.migCapable(migCapable)
					.build();
				nodeDtos.add(dto);
			}
		}
		int totalCount = nodeDtos.size();
		int startIndex = (pageNo - 1) * pageSize;
		int endIndex = Math.min(startIndex + pageSize, totalCount);

		if (startIndex >= totalCount || endIndex <= startIndex) {
			// 페이지 범위를 벗어나면 빈 리스트 반환
			return ResponseDTO.PageNodeDTO.builder()
				.nodes(null)
				.totalCount(totalCount)
				.build();
		}
		return ResponseDTO.PageNodeDTO.builder()
			.nodes(nodeDtos.subList(startIndex, endIndex))
			.totalCount(totalCount)
			.build();
	}

	private boolean isStatus(List<NodeCondition> conditions) {
		boolean status = true;
		for (NodeCondition condition : conditions) {
			String type = condition.getType();
			String conditionStatus = condition.getStatus();

			if ((type.equalsIgnoreCase(NETWORK_UNAVAILABLE) ||
				type.equalsIgnoreCase(MEMORY_PRESSURE) ||
				type.equalsIgnoreCase(DISK_PRESSURE) ||
				type.equalsIgnoreCase(PID_PRESSURE)) &&
				!conditionStatus.equalsIgnoreCase("false")) {
				status = false;
				break;
			}

			if (type.equalsIgnoreCase(READY) && !conditionStatus.equalsIgnoreCase("true")) {
				status = false;
				break;
			}
		}
		return status;
	}

	@Override
	public ResponseDTO.MIGProfile getNodeMIGProfiles(String nodeName) {
		Node node = getNode(nodeName);
		if (!getMigCapable(node)) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		//node의 gpu productName을 조회한다.
		String gpuProductName = getGPUProductName(node);
		//해당 node에 장착된 gpu가 가능한 mig profile을 리턴한다.
		return getNodeMIGProfileFromJson(gpuProductName);
	}

	@Override
	public void updateMIGAllProfile(String nodeName, String option) {
		Node node = getNode(nodeName);
		//해당 node가 mig를 지원하는지 체크
		if (!getMigCapable(node)) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		//할당된 프로젝트가 존재하는지 체크한다.
		if (nodeAssignWorkloadCount(nodeName) > 0) {
			throw new K8sException(NodeErrorCode.NODE_IN_USE_NOT_MIG);
		}
		updateMIGConfig(nodeName, "all-" + option);

	}

	@Override
	public int getGPUCount(Node node) {
		try {
			return node.getMetadata()
				.getLabels()
				.entrySet()
				.stream()
				.filter(entry -> entry.getKey().contains("gpu.count"))
				.mapToInt(map -> Integer.parseInt(map.getValue()))
				.sum();
		} catch (Exception e) {
			return 0;
		}
	}

	public Node getNode(String nodeName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Node node = client.nodes().withName(nodeName).get();
			if (node == null) {
				throw new K8sException(NodeErrorCode.NODE_NOT_FOUND);
			}
			return node;
		}
	}

	public ResponseDTO.NodeInfo getNodeByResourceName(String nodeName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Node node = client.nodes().withName(nodeName).get();
			if (node == null) {
				throw new K8sException(NodeErrorCode.NODE_NOT_FOUND);
			}
			List<NodeCondition> conditions = node.getStatus().getConditions();

			String role = node.getMetadata().getLabels().containsKey(ROLE) ? "control-plane" : "data-plane";
			NodeSystemInfo nodeInfo = node.getStatus().getNodeInfo();
			ResponseDTO.NodeInfo dto = ResponseDTO.NodeInfo.builder()
				.nodeName(node.getMetadata().getName())
				.ip(node.getStatus().getAddresses().get(0).getAddress())
				.hostName(node.getMetadata().getLabels().get(HOST_NAME))
				.nodeCondition(conditions)
				.role(role)
				.creationTimestamp(node.getMetadata().getCreationTimestamp())
				.nodeSystemInfo(nodeInfo)
				.build();

			return dto;
		}
	}

	@Override
	public ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Node node = client.nodes().withName(resourceName).get();
			if (node == null) {
				throw new K8sException(NodeErrorCode.NODE_NOT_FOUND);
			}
			Map<String, String> labels = node.getMetadata().getLabels();
			Map<String, Quantity> capacity = node.getStatus().getCapacity();
			Map<String, Quantity> allocatable = node.getStatus().getAllocatable();

			ResponseDTO.NodeResourceInfo.Capacity capacityResource = ResponseDTO.NodeResourceInfo.Capacity.builder()
				.capacityCpu(getNonNullValueOrZero(capacity.get(CPU)))
				.capacityEphemeralStorage(getNonNullValueOrZero(capacity.get(EPHEMERAL_STORAGE)))
				.capacityHugepages1Gi(getNonNullValueOrZero(capacity.get(HUGEPAGES_1Gi)))
				.capacityHugepages2Mi(getNonNullValueOrZero(capacity.get(HUGEPAGES_2Mi)))
				.capacityMemory(getNonNullValueOrZero(capacity.get(MEMORY)))
				.capacityPods(getNonNullValueOrZero(capacity.get(PODS)))
				.capacityGpu(getNonNullValueOrZero(capacity.get(GPU)))
				.build();
			ResponseDTO.NodeResourceInfo.Allocatable allocatableResource = ResponseDTO.NodeResourceInfo.Allocatable.builder()
				.allocatableCpu(getNonNullValueOrZero(allocatable.get(CPU)))
				.allocatableEphemeralStorage(getNonNullValueOrZero(allocatable.get(EPHEMERAL_STORAGE)))
				.allocatableHugepages1Gi(getNonNullValueOrZero(allocatable.get(HUGEPAGES_1Gi)))
				.allocatableHugepages2Mi(getNonNullValueOrZero(allocatable.get(HUGEPAGES_2Mi)))
				.allocatableMemory(getNonNullValueOrZero(allocatable.get(MEMORY)))
				.allocatablePods(getNonNullValueOrZero(allocatable.get(PODS)))
				.allocatableGpu(getNonNullValueOrZero(allocatable.get(GPU)))
				.build();

			String version = null;
			if (!(labels.get(GPU_DRIVER_VER_MAJOR) == null || labels.get(GPU_DRIVER_VER_MINOR) == null || labels.get(
				GPU_DRIVER_VER_REV) == null)) {
				version = labels.get(GPU_DRIVER_VER_MAJOR) + "." + labels.get(GPU_DRIVER_VER_MINOR) + "." + labels.get(GPU_DRIVER_VER_REV);
			}

			ResponseDTO.NodeResourceInfo nodeResourceInfo = ResponseDTO.NodeResourceInfo.builder()
				.gpuType(labels.get(GPU_NAME))
				.gpuMem(labels.get(GPU_MEMORY))
				.gpuCount(labels.get(GPU_COUNT))
				.gpuDriverVersion(version)
				.capacity(capacityResource)
				.allocatable(allocatableResource)
				.build();
			return nodeResourceInfo;
		}
	}

	@Override
	public void setSchedule(String resourceName, ScheduleType scheduleType) {
		getNode(resourceName);
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.nodes().withName(resourceName).edit(node -> new NodeBuilder(node)
				.editSpec()
				.withUnschedulable(scheduleType.name().equalsIgnoreCase("ON") ? false : true)
				.endSpec()
				.build());
		}
	}

	private String getNonNullValueOrZero(Quantity resource) {
		return resource != null ? (resource.getAmount() + resource.getFormat()) : "0";
	}

	private long nodeAssignWorkloadCount(String nodeName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			return client.pods()
				.list()
				.getItems()
				.stream()
				.filter(
					pod -> pod.getSpec().getNodeName().equals(nodeName) && pod.getStatus().getPhase().equals("Running"))
				.count();
		}
	}

	/**
	 * 해당 node가 mig가 가능한지 확인하는 메소드
	 *
	 * @param node
	 * @return
	 */
	private boolean getMigCapable(Node node) {
		String capable = node.getMetadata().getLabels().get("nvidia.com/mig.capable");
		return Boolean.parseBoolean(capable);
	}

	/**
	 * 해당 노드의 gpu의 productName을 리턴하는 메소드
	 *
	 * @param node node
	 * @return productName
	 */
	private String getGPUProductName(Node node) {
		return node.getMetadata().getLabels().get(GPU_NAME);
	}

	/**
	 * node에 장착된 gpu productname을 받아 MIGProfile로 리턴해주는 메소드
	 * NVIDIA-A100-SMP4-80GB -> A100_80GB
	 *
	 * @param productName 조회할 gpu productName
	 * @return
	 * @throws IOException
	 */
	public ResponseDTO.MIGProfile getNodeMIGProfileFromJson(String productName) {
		try {
			//MIGProfile.json을 읽어온다.
			File file = new File(migProfilePath);
			//mig profile 파일이 존재하지 않을 경우 exception 발생시킴
			if (!file.exists()) {
				throw new K8sException(NodeErrorCode.MIG_PROFILE_NOT_EXIST);
			}
			//json 파일을 읽어옴
			ResponseDTO.MIGProfileList migProfileList = objectMapper.readValue(file, ResponseDTO.MIGProfileList.class);
			//리스트에서 productName으로 검색하여 해당하는 profile을 리턴한다.
			return migProfileList.migProfiles().stream().filter(mig ->
					mig.migProduct().equals(MIGProduct.getGpuProduct(productName)))
				.findFirst()
				.orElseThrow(() -> new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU));
		} catch (IOException e) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
	}

	/**
	 * 해당 노드에 선택한 mig profile을 적용하는 메소드
	 *
	 * @param nodeName mig profile을 적용할 nodeName
	 * @param profile  적용할 mig profile
	 */
	private void updateMIGConfig(String nodeName, String profile) {
		//적용할 node를 불러온다.
		Resource<Node> node = getNodeResource(nodeName);
		HashMap<String, String> migConfig = new HashMap<>();
		migConfig.put("nvidia.com/mig.config", profile);
		node.edit(n ->
			new NodeBuilder(n).editMetadata().addToLabels(migConfig).endMetadata().build());
	}

	/**
	 * k8s Server에서 node resource를 조회하는 메소드
	 *
	 * @param nodeName 조회할 nodeName
	 * @return
	 */
	public Resource<Node> getNodeResource(String nodeName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.nodes().withName(nodeName);
		}
	}

	/**
	 * node의 라벨을 보고 해당 노드가 MIG on/off 여부와 strategy를 조회하는 메소드
	 *
	 * @param nodeLabels 노드의 label
	 * @return MigStrategy, null일 경우 일반 노드거나 MIG off
	 */
	public MIGStrategy getNodeMIGOnOffYN(Map<String, String> nodeLabels) {
		if (nodeLabels.keySet().stream().anyMatch(key -> key.contains("gb.slices.ci"))) {
			return MIGStrategy.MIXED;
		} else if (nodeLabels.containsKey("nvidia.com/gpu.slices.ci")) {
			return MIGStrategy.SINGLE;
		} else {
			return null;
		}
	}

	/**
	 * 노드의 mig mixed profile 정보를 조회하는 메소드
	 *
	 * @param node 조회할 node
	 * @return
	 */
	public List<MigMixedDTO> getMigMixedInfo(Node node) {
		Map<String, String> labels = node.getMetadata().getLabels();
		List<String> migProfileList = labels.keySet().stream()
			.filter(key -> key.contains("gb.count"))
			.map(key -> key.split(".count")[0])
			.sorted()
			.toList();

		return migProfileList.stream().map(profile ->
				MigMixedDTO.builder()
					.name(profile.split("nvidia.com/")[1])
					.count(Integer.parseInt(labels.get(profile + ".count")))
					.memory(labels.get(profile + ".memory"))
					.build())
			.toList();
	}
}

