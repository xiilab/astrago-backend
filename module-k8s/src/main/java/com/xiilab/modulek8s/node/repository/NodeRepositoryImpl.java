package com.xiilab.modulek8s.node.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.MPSStatus;
import com.xiilab.modulecommon.enums.MigStatus;
import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.NodeErrorCode;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.MPSGpuDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.MIGStrategy;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NodeRepositoryImpl implements NodeRepository {
	private final K8sAdapter k8sAdapter;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final String GPU_NAME = "nvidia.com/gpu.product";
	private final String MIG_GPU_NAME = "nvidia.com/mig.gpu.product";
	private final String GPU_COUNT = "nvidia.com/gpu.count";
	private final String MPS_GPU_COUNT = "nvidia.com/gpu.replicas";
	private final String GPU_DRIVER_VER_MAJOR = "nvidia.com/cuda.driver.major";
	private final String GPU_DRIVER_VER_MINOR = "nvidia.com/cuda.driver.minor";
	private final String GPU_DRIVER_VER_REV = "nvidia.com/cuda.driver.rev";
	private final String GPU_MEMORY = "nvidia.com/gpu.memory";
	private final String GPU = "nvidia.com/gpu";
	private final String MPS_GPU = "nvidia.com/gpu.shared";

	private final String MIG_CONFIG = "nvidia.com/mig.config.state";
	private final String MIG_CAPABLE = "nvidia.com/mig.capable";
	private final String MPS_CONFIG = "mps_capable";
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
	private final String MIG_STRATEGY = "nvidia.com/mig.strategy";
	private final String MPS_CAPABLE = "nvidia.com/mps.capable";

	@Override
	public List<Node> getNodes(boolean isWorker) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			if (isWorker) {
				return client.nodes()
					.list()
					.getItems()
					.stream()
					.filter(node -> !node.getMetadata().getLabels().containsKey("node-role.kubernetes.io/control-plane")
						&& (Objects.isNull(node.getSpec().getUnschedulable()) || !Boolean.TRUE.equals(
						node.getSpec().getUnschedulable())))
					.toList();
			} else {
				return client.nodes().list().getItems().stream().toList();
			}

			// !Objects.isNull(node.getSpec().getUnschedulable())
			// 	&& node.getSpec().getUnschedulable().booleanValue() == Boolean.TRUE
		}
	}

	@Override
	public ResponseDTO.NodeGPUs getNodeGPUs(NodeType nodeType) {
		List<Node> nodes = getNodes(true);
		if (CollectionUtils.isEmpty(nodes)) {
			throw new RestApiException(NodeErrorCode.NOT_FOUND_WORKER_NODE);
		}

		Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> migGPUMap = new HashMap<>();
		Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> mpsGPUMap = new HashMap<>();
		Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> normalGPUMap = new HashMap<>();

		for (Node node : nodes) {
			List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos = new ArrayList<>();
			String nodeName = node.getMetadata().getName();
			String gpuName = node.getMetadata().getLabels().get(GPU_NAME);
			Integer notMpsGPUCount = Integer.parseInt(node.getMetadata().getLabels().getOrDefault(GPU_COUNT, "0"));

			if (nodeType == NodeType.SINGLE) {
				if (isActiveMIG(node) && node.getMetadata()
					.getLabels()
					.containsKey(MIG_STRATEGY)) {    // MIG 적용 여부 && MIG 전략 키 존재여부 확인
					putMigGpuMap(migGPUMap, node, gpuInfos, nodeName, gpuName, notMpsGPUCount);
				} else if (isActiveMPS(node)) { // MIG 적용 여부 확인
					putMpsGpuMap(mpsGPUMap, node, gpuInfos, nodeName, gpuName);
				} else {
					putGpuMap(normalGPUMap, gpuInfos, nodeName, gpuName, notMpsGPUCount);
				}
			} else {    // 멀티노드일때, 분할안된 GPU만 반환
				if (isActiveMPS(node) || isActiveMIG(node)) {
					continue;
				}

				putGpuMap(normalGPUMap, gpuInfos, nodeName, gpuName, notMpsGPUCount);
			}
		}

		return ResponseDTO.NodeGPUs.builder().normalGPU(normalGPUMap).migGPU(migGPUMap).mpsGPU(mpsGPUMap).build();
	}

	@Override
	public ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize, String searchText) {
		List<ResponseDTO.NodeDTO> nodeDtos = new ArrayList<>();
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Node> nodes = client.nodes().list().getItems().stream().filter(node -> {
				if (searchText == null || searchText.isBlank()) {
					return true;
				}
				return node.getMetadata().getName().toLowerCase().contains(searchText);
			}).toList();

			for (Node node : nodes) {
				boolean migCapable = getMigCapable(node);
				boolean mpsCapable = getMpsCapable(node);
				boolean isActiveMIG = isActiveMIG(node);
				boolean isActiveMPS = isActiveMPS(node);
				boolean migStatus = getMigStatus(node);
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
					.isActiveMIG(isActiveMIG)
					.mpsCapable(mpsCapable)
					.isActiveMPS(isActiveMPS)
					.migStatus(migStatus)
					.build();
				nodeDtos.add(dto);
			}
		}
		int totalCount = nodeDtos.size();
		int startIndex = (pageNo - 1) * pageSize;
		int endIndex = Math.min(startIndex + pageSize, totalCount);
		int totalPageSize = (int)Math.ceil((double)totalCount / pageSize);

		if (startIndex >= totalCount || endIndex <= startIndex) {
			// 페이지 범위를 벗어나면 빈 리스트 반환
			return ResponseDTO.PageNodeDTO.builder()
				.nodes(null)
				.totalCount(totalCount)
				.totalPageCount(totalPageSize)
				.build();
		}
		return ResponseDTO.PageNodeDTO.builder()
			.nodes(nodeDtos.subList(startIndex, endIndex))
			.totalCount(totalCount)
			.totalPageCount(totalPageSize)
			.build();
	}

	private boolean isActiveMIG(Node node) {
		// product에 "MIG"가 포함되어 있거나 라벨에 "mig-"가 포함되어 있을 경우
		String gpuName = node.getMetadata().getLabels().get(GPU_NAME);
		return "MIG".contains(gpuName) || node.getMetadata()
			.getLabels()
			.keySet()
			.stream()
			.anyMatch(key -> key.contains("mig-"));
	}

	private boolean isActiveMPS(Node node) {
		if (!node.getMetadata().getLabels().containsKey(MPS_CAPABLE)) {
			return false;
		}

		String mpsCapable = node.getMetadata().getLabels().get(MPS_CAPABLE);
		String mpsStatus = node.getMetadata().getLabels().getOrDefault("mps_status", null);
		return Boolean.parseBoolean(mpsCapable) && MPSStatus.COMPLETE.name().equals(mpsStatus);
	}

	private boolean isStatus(List<NodeCondition> conditions) {
		boolean status = true;
		for (NodeCondition condition : conditions) {
			String type = condition.getType();
			String conditionStatus = condition.getStatus();

			if ((type.equalsIgnoreCase(NETWORK_UNAVAILABLE) || type.equalsIgnoreCase(MEMORY_PRESSURE)
				|| type.equalsIgnoreCase(DISK_PRESSURE) || type.equalsIgnoreCase(PID_PRESSURE))
				&& !conditionStatus.equalsIgnoreCase("false")) {
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
	public MIGProfileDTO getNodeMIGProfiles(String nodeName, int giCount) {
		Node node = getNode(nodeName);
		if (!getMigCapable(node)) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		//node의 gpu productName을 조회한다.
		String gpuProductName = getGPUProductName(node);
		//해당 node에 장착된 gpu가 가능한 mig profile을 리턴한다.
		return getNodeMIGProfileFromJson(gpuProductName, giCount);
	}

	@Override
	public void updateMIGProfile(String nodeName, String option) {
		Node node = getNode(nodeName);
		//해당 node가 mig를 지원하는지 체크
		if (!getMigCapable(node)) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		//할당된 프로젝트가 존재하는지 체크한다. - true : 사용중인 워크로드 존재
		if (nodeAssignWorkloadCount(nodeName)) {
			throw new K8sException(NodeErrorCode.NODE_IN_USE_NOT_MIG);
		}
		updateMIGConfig(nodeName, option);

	}

	@Override
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
			int gpuCount = 0;
			if (node.getMetadata().getLabels().containsKey("nvidia.com/mig-count")) {
				gpuCount = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/mig-count"));
			}
			boolean mpsCheck = false;
			if (node.getMetadata().getLabels().get("mps_capable") != null) {
				mpsCheck = Boolean.parseBoolean(node.getMetadata().getLabels().get("mps_capable"));
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
				.capacityGpu(gpuCount > 0 ? String.valueOf(gpuCount) :
					mpsCheck ? String.valueOf(capacity.get(MPS_GPU)) : getNonNullValueOrZero(capacity.get(GPU)))
				.build();
			ResponseDTO.NodeResourceInfo.Allocatable allocatableResource = ResponseDTO.NodeResourceInfo.Allocatable.builder()
				.allocatableCpu(getNonNullValueOrZero(allocatable.get(CPU)))
				.allocatableEphemeralStorage(getNonNullValueOrZero(allocatable.get(EPHEMERAL_STORAGE)))
				.allocatableHugepages1Gi(getNonNullValueOrZero(allocatable.get(HUGEPAGES_1Gi)))
				.allocatableHugepages2Mi(getNonNullValueOrZero(allocatable.get(HUGEPAGES_2Mi)))
				.allocatableMemory(getNonNullValueOrZero(allocatable.get(MEMORY)))
				.allocatablePods(getNonNullValueOrZero(allocatable.get(PODS)))
				.allocatableGpu(gpuCount > 0 ? String.valueOf(gpuCount) :
					mpsCheck ? String.valueOf(capacity.get(MPS_GPU)) : getNonNullValueOrZero(capacity.get(GPU)))
				.build();

			String version = null;
			if (!(labels.get(GPU_DRIVER_VER_MAJOR) == null || labels.get(GPU_DRIVER_VER_MINOR) == null
				|| labels.get(GPU_DRIVER_VER_REV) == null)) {
				version = labels.get(GPU_DRIVER_VER_MAJOR) + "." + labels.get(GPU_DRIVER_VER_MINOR) + "." + labels.get(
					GPU_DRIVER_VER_REV);
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
			client.nodes()
				.withName(resourceName)
				.edit(node -> new NodeBuilder(node).editSpec()
					.withUnschedulable(scheduleType.name().equalsIgnoreCase("ON") ? false : true)
					.endSpec()
					.build());
		}
	}

	private String getNonNullValueOrZero(Quantity resource) {
		return resource != null ? (resource.getAmount() + resource.getFormat()) : "0";
	}

	private boolean nodeAssignWorkloadCount(String nodeName) {
		boolean isGpuUsed = false;
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Pod> pods = client.pods().inAnyNamespace().list().getItems();
			for (Pod pod : pods) {
				if (isPodOnNodeAndRunning(pod, nodeName)) {
					if (isGpuUsed(pod)) {
						isGpuUsed = true;
						break;
					}
				}
			}
		}
		return isGpuUsed;
	}

	private boolean isPodOnNodeAndRunning(Pod pod, String nodeName) {
		return pod.getSpec().getNodeName() != null && pod.getSpec().getNodeName().equals(nodeName) && "Running".equals(
			pod.getStatus().getPhase());
	}

	private boolean isGpuUsed(Pod pod) {
		Quantity gpuQuantity = getGpuQuantity(pod, "nvidia.com/gpu");
		Quantity sharedGpuQuantity = getGpuQuantity(pod, "nvidia.com/gpu.shared");

		return isGpuQuantityUsed(gpuQuantity) || isGpuQuantityUsed(sharedGpuQuantity);
	}

	private Quantity getGpuQuantity(Pod pod, String resourceName) {
		return pod.getSpec().getContainers().get(0).getResources().getRequests().get(resourceName);
	}

	private boolean isGpuQuantityUsed(Quantity gpuQuantity) {
		if (gpuQuantity != null) {
			int gpuCount = Integer.parseInt(gpuQuantity.getAmount());
			return gpuCount > 0;
		}
		return false;
	}

	/**
	 * 해당 node가 mig가 가능한지 확인하는 메소드
	 *
	 * @param node
	 * @return
	 */
	private boolean getMigCapable(Node node) {
		String capable = node.getMetadata().getLabels().get("nvidia.com/mig.capable");
		if (!Objects.nonNull(capable)) {
			String migState = node.getMetadata().getLabels().get("nvidia.com/mig.config.state");
			return migState != null;
		}
		return Boolean.parseBoolean(capable);
	}

	private boolean getMpsCapable(Node node) {
		String gpuType = node.getMetadata().getLabels().get("nvidia.com/gpu.family");
		if (Objects.nonNull(gpuType) && gpuType.equalsIgnoreCase("volta")) {
			return true;
		}
		return false;
	}

	/**
	 * mig 설정 적용 유무 확인
	 * @param node
	 * @return
	 */
	private boolean getMigStatus(Node node) {
		String migCapable = node.getMetadata().getLabels().get("mig_capable") != null ?
			node.getMetadata().getLabels().get("mig_capable") : "false";
		return Boolean.parseBoolean(migCapable);
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
	@Override
	public MIGProfileDTO getNodeMIGProfileFromJson(String productName, int giCount) {
		try {
			String chipset = extractGpuChipset(productName);
			List<Map<String, Integer>> resProfile = new ArrayList<>();
			//MIGProfile.json을 읽어온다.
			InputStream inputStream = this.getClass()
				.getResourceAsStream(String.format("/migProfile/%s.json", chipset));
			//json 파일을 읽어옴
			MIGProfileDTO migProfileList = objectMapper.readValue(inputStream, MIGProfileDTO.class);

			List<Map<String, Integer>> profiles = migProfileList.getProfile();
			for (Map<String, Integer> profile : profiles) {
				int sum = profile.values().stream().toList().stream().mapToInt(Integer::intValue).sum();
				if (sum == giCount && profile.size() == 1) {
					resProfile.add(profile);
				}
			}
			return new MIGProfileDTO(productName, resProfile);
		} catch (IOException e) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		} catch (NullPointerException e) {
			throw new K8sException(NodeErrorCode.MIG_PROFILE_NOT_EXIST);
		}
	}

	@Override
	public int getMIGProfileGICount(String productName, String profileName) throws IOException {
		try {
			String chipset = extractGpuChipset(productName);
			//MIGProfile.json을 읽어온다.
			org.springframework.core.io.Resource resource = ResourcePatternUtils.getResourcePatternResolver(
				new DefaultResourceLoader()).getResource(String.format("classpath:migProfile/%s.json", chipset));
			File file = resource.getFile();
			//mig profile 파일이 존재하지 않을 경우 exception 발생시킴
			if (!file.exists()) {
				throw new K8sException(NodeErrorCode.MIG_PROFILE_NOT_EXIST);
			}
			//json 파일을 읽어옴
			MIGProfileDTO migProfileList = objectMapper.readValue(file, MIGProfileDTO.class);
			List<Map<String, Integer>> profiles = migProfileList.getProfile();

		} catch (IOException e) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		return 0;
	}

	/**
	 * 해당 노드에 선택한 mig profile을 적용하는 메소드
	 *
	 * @param nodeName mig profile을 적용할 nodeName
	 * @param profile  적용할 mig profile
	 */
	private void updateMIGConfig(String nodeName, String profile) {
		//적용할 node를 불러온다.
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Resource<Node> node = kubernetesClient.nodes().withName(nodeName);
			HashMap<String, String> migConfig = new HashMap<>();
			migConfig.put("nvidia.com/mig.config", profile);
			if (profile.equals("all-disabled")) {
				migConfig.put("mig_capable", "false");
			} else {
				migConfig.put("mig_capable", "true");
			}
			node.edit(n -> new NodeBuilder(n).editMetadata().addToLabels(migConfig).endMetadata().build());
		}
	}

	@Override
	public void updateMigProfile(MIGGpuDTO MIGGpuDTO) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		Yaml yaml = new Yaml(options);
		Map<String, String> migConfigMapData = getMigConfigMapData();
		String migConfigSTR = migConfigMapData.get("config.yaml");
		Map<String, Object> convertResult = yaml.load(migConfigSTR);
		Map<String, Object> migConfigs = (Map<String, Object>)convertResult.get("mig-configs");
		migConfigs.put(MIGGpuDTO.getMigKey(), MIGGpuDTO.convertToMap());
		convertResult.put("mig-configs", migConfigs);
		updateMigConfigMap(Map.of("config.yaml", yaml.dump(convertResult)));
	}

	@Override
	public MIGGpuDTO.MIGInfoStatus getNodeMigStatus(String nodeName) {
		Node node = getNode(nodeName);
		if (!getMigCapable(node)) {
			throw new K8sException(NodeErrorCode.NOT_SUPPORTED_GPU);
		}
		String migProfile = node.getMetadata().getLabels().get("nvidia.com/mig.config");
		String migProfileStatus = node.getMetadata().getLabels().get("nvidia.com/mig.config.state");
		Map<String, Object> migConfigMap = getMigConfigMap();
		if (migProfile.equals("all-disabled") || CollectionUtils.isEmpty(
			(List<Map<String, Object>>)migConfigMap.get(migProfile))) {
			int gpuCount = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.count"));
			List<Integer> gpuIndex = new ArrayList<>();
			for (int i = 0; i < gpuCount; i++) {
				gpuIndex.add(i);
			}
			return MIGGpuDTO.MIGInfoStatus.builder()
				.nodeName(node.getMetadata().getName())
				.migInfos(List.of(MIGGpuDTO.MIGInfoDTO.builder().gpuIndexs(gpuIndex).migEnable(false).build()))
				.gpuProduct(extractGpuChipset(node.getMetadata().getLabels().get(GPU_NAME)))
				.status(MigStatus.valueOf(migProfileStatus.toUpperCase()))
				.build();
		} else {
			List<Map<String, Object>> rawMIGInfo = (List<Map<String, Object>>)migConfigMap.get(migProfile);
			return MIGGpuDTO.MIGInfoStatus.builder()
				.nodeName(node.getMetadata().getName())
				.migInfos(convertMapToMIGInfo(rawMIGInfo))
				.gpuProduct(extractGpuChipset(node.getMetadata().getLabels().get(MIG_GPU_NAME)))
				.status(MigStatus.valueOf(migProfileStatus.toUpperCase()))
				.build();
		}
	}

	@Override
	public void updateNodeLabel(String nodeName, Map<String, String> labels) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.nodes()
				.withName(nodeName)
				.edit(node -> new NodeBuilder(node).editMetadata().addToLabels(labels).endMetadata().build());
		}
	}

	@Override
	public void saveGpuProductTOLabel(String nodeName) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			Node node = kubernetesClient.nodes().withName(nodeName).get();
			String gpuProduct = node.getMetadata().getLabels().get(GPU_NAME);
			if (!StringUtils.isBlank(gpuProduct)) {
				updateNodeLabel(nodeName, Map.of(MIG_GPU_NAME, gpuProduct));
			}
		}
	}

	@Override
	public void restartMIGManager() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			List<Pod> items = kubernetesClient.pods().inNamespace("gpu-operator").list().getItems();
			for (Pod pod : items) {
				String name = pod.getMetadata().getName();
				if (name.contains("nvidia-mig-manager")) {
					kubernetesClient.resource(pod).delete();
				}
			}
		}
	}

	@Override
	public MPSGpuDTO.MPSInfoDTO getMpsConfig(String nodeName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Node node = client.nodes().withName(nodeName).get();
			if (node == null) {
				throw new RestApiException(NodeErrorCode.NODE_NOT_FOUND);
			}
			String gpu = node.getMetadata().getLabels().get("nvidia.com/gpu.product"); // gpu 종류
			int gpuCnt = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.count")); // gpu 개수
			String gpuType = node.getMetadata().getLabels().get("nvidia.com/gpu.family"); // gpu 종류(volta 등)
			String mpsStatus = node.getMetadata().getLabels().get("mps_status") == null ? MPSStatus.COMPLETE.name() : node.getMetadata().getLabels().get("mps_status"); // mps 상태

			int mpsReplicas = node.getMetadata().getLabels().get("nvidia.com/gpu.replicas") != null ? Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.replicas")) : 1; // mps 설정 개수
			String mpsCapable = node.getMetadata().getLabels().get("nvidia.com/mps.capable") != null ? node.getMetadata().getLabels().get("nvidia.com/mps.capable") : "false"; // mps 설정 유무
			String customMpsCapable = node.getMetadata().getLabels().get("mps_capable") != null ? node.getMetadata().getLabels().get("mps_capable") : "false"; // mps 설정 유무

			MPSStatus status = MPSStatus.COMPLETE.name().equalsIgnoreCase(mpsStatus) ? MPSStatus.COMPLETE : MPSStatus.UPDATING;
			if(status == MPSStatus.UPDATING){
				status = customMpsCapable.equalsIgnoreCase("true") ? MPSStatus.UPDATING_ON : MPSStatus.UPDATING_OFF;
			}
			return MPSGpuDTO.MPSInfoDTO.builder()
				.nodeName(nodeName)
				.gpuName(gpu)
				.gpuCnt(gpuCnt)
				.mpsCapable(Boolean.parseBoolean(mpsCapable))
				.mpsReplicas(mpsReplicas)
				.mpsStatus(status)
				.mpsMaxReplicas(gpuType.equalsIgnoreCase("volta") ? 48 : 16)
				.build();
		}
	}

	@Override
	public void setMpsConfig(MPSGpuDTO.SetMPSDTO setMPSDTO) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			String nodeName = setMPSDTO.getNodeName();
			//volta 검사해야함
			Node nodeInfo = client.nodes().withName(nodeName).get();
			String gpuType = nodeInfo.getMetadata().getLabels().get("nvidia.com/gpu.family"); // gpu 종류(volta 등)
			String migCapable = nodeInfo.getMetadata().getLabels().get(MIG_CAPABLE) != null ?
				nodeInfo.getMetadata().getLabels().get(MIG_CAPABLE) : "false"; // gpu 종류(volta 등)
			if (!gpuType.equalsIgnoreCase("volta")) {
				throw new K8sException(NodeErrorCode.NOT_SUPPORTED_MPS_GPU);
			}
			if (migCapable.equalsIgnoreCase("true")) {
				throw new K8sException(NodeErrorCode.NODE_IN_USE_NOT_MPS);
			}
			//해당 노드에 생성된 Pod중 gpu를 사용하고있는 pod가 있는지 체크
			boolean usedWorkloadCheck = nodeAssignWorkloadCount(nodeName);
			if (usedWorkloadCheck) {
				throw new K8sException(NodeErrorCode.NOT_SUPPORTED_MPS_WITH_MIG);
			}

			if(!setMPSDTO.isMpsCapable()){
				client.nodes().withName(nodeName).edit(node -> new NodeBuilder(node)
					.editMetadata()
					.removeFromLabels("nvidia.com/device-plugin.config")
					.addToLabels("mps_status", "UPDATING")
					.addToLabels("mps_capable", "false")
					.endMetadata()
					.build());
			}else{
				client.nodes().withName(nodeName).edit(node -> new NodeBuilder(node)
					.editMetadata()
					.addToLabels("nvidia.com/device-plugin.config", "mps_" + setMPSDTO.getMpsReplicas())
					.addToLabels("mps_status", "UPDATING")
					.addToLabels("mps_capable", "true")
					.endMetadata()
					.build());
			}
		}
	}

	private List<MIGGpuDTO.MIGInfoDTO> convertMapToMIGInfo(List<Map<String, Object>> migInfoList) {
		if (CollectionUtils.isEmpty(migInfoList)) {
			return null;
		} else {
			return migInfoList.stream().map(migInfo -> {
				boolean migEnabled = (boolean)migInfo.get("mig-enabled");
				List<Integer> devices = (List<Integer>)migInfo.get("devices");
				if (migEnabled == false) {
					return MIGGpuDTO.MIGInfoDTO.builder()
						.migEnable(migEnabled)
						.gpuIndexs(devices)
						.build();
				} else {
					Map<String, Integer> migProfiles = (Map<String, Integer>)migInfo.get("mig-devices");
					return MIGGpuDTO.MIGInfoDTO.builder()
						.migEnable(migEnabled)
						.gpuIndexs(devices)
						.profile(migProfiles)
						.build();
				}
			}).toList();
		}
	}

	private void updateMigConfigMap(Map<String, String> data) {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			kubernetesClient.configMaps()
				.inNamespace("gpu-operator")
				.withName("custom-mig-parted-config")
				.edit(config ->
					config.edit()
						.addToData(data).build());
		}
	}

	private Map<String, String> getMigConfigMapData() {
		try (KubernetesClient kubernetesClient = k8sAdapter.configServer()) {
			return kubernetesClient.configMaps()
				.inNamespace("gpu-operator")
				.withName("custom-mig-parted-config")
				.get()
				.getData();
		}
	}

	public Map<String, Object> getMigConfigMap() {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		Yaml yaml = new Yaml(options);
		Map<String, String> migConfigMapData = getMigConfigMapData();
		String migConfigSTR = migConfigMapData.get("config.yaml");
		Map<String, Object> convertResult = yaml.load(migConfigSTR);
		Map<String, Object> migConfigs = (Map<String, Object>)convertResult.get("mig-configs");
		return migConfigs;
	}

	private String extractGpuChipset(String value) {
		StringBuilder chipset = null;
		String[] split = value.split("-");
		Set<String> strings = Set.of("A30", "A100", "H100");
		for (String s : split) {
			if (strings.contains(s)) {
				chipset = new StringBuilder(s);
			}
			if (s.contains("GB")) {
				chipset.append("-").append(s);
			}
		}
		return chipset.toString();
	}

	/**
	 * worker node들의 gpu 드라이버 버전 정보 조회
	 */
	@Override
	public List<ResponseDTO.WorkerNodeDriverInfo> getWorkerNodeDriverInfos() {
		List<ResponseDTO.WorkerNodeDriverInfo> workerNodeDriverInfos = new ArrayList<>();
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Node> items = client.nodes().list().getItems();
			for (Node item : items) {
				boolean isWorkerNode = item.getMetadata().getLabels().containsKey(ROLE);
				if (!isWorkerNode) {
					String driverMajor = item.getMetadata().getLabels().get("nvidia.com/cuda.driver.major");
					String driverMinor = item.getMetadata().getLabels().get("nvidia.com/cuda.driver.minor");
					String driverRev = item.getMetadata().getLabels().get("nvidia.com/cuda.driver.rev");
					String computeMajor = item.getMetadata().getLabels().get("nvidia.com/gpu.compute.major");
					String computeMinor = item.getMetadata().getLabels().get("nvidia.com/gpu.compute.minor");
					ResponseDTO.WorkerNodeDriverInfo workerNodeDriverInfo = ResponseDTO.WorkerNodeDriverInfo.builder()
						.driverMajor(driverMajor)
						.driverMinor(driverMinor)
						.driverRev(driverRev)
						.computeMajor(computeMajor)
						.computeMinor(computeMinor)
						.build();
					workerNodeDriverInfos.add(workerNodeDriverInfo);
				}
			}
		}
		return workerNodeDriverInfos;
	}

	private void putMpsGpuMap(Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> mpsGPUMap, Node node,
		List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos, String nodeName, String gpuName) {
		String mpsGPUCount = node.getMetadata().getLabels().getOrDefault(MPS_GPU_COUNT, "0");
		putGpuMap(mpsGPUMap, gpuInfos, nodeName, gpuName, Integer.parseInt(mpsGPUCount));
	}

	private void putMigGpuMap(Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> migGPUMap, Node node,
		List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos, String nodeName, String gpuName, Integer notMpsGPUCount) {
		if (isMIGSingleStrategy(node)) {
			putGpuMap(migGPUMap, gpuInfos, nodeName, gpuName, notMpsGPUCount);
		} else {
			List<String> migGpuKeys = node.getMetadata()
				.getLabels()
				.keySet()
				.stream()
				.filter(key -> key.startsWith("nvidia.com/mig-") && key.endsWith(".count"))
				.toList();
			for (String migGpuKey : migGpuKeys) {
				String migName =
					gpuName + "/" + migGpuKey.substring(migGpuKey.lastIndexOf("nvidia.com/") + 1,
						migGpuKey.indexOf(".count"));
				Integer mixedGpuCount = Integer.parseInt(node.getMetadata().getLabels().get(migGpuKey));
				gpuInfos.add(new ResponseDTO.NodeGPUs.GPUInfo(migName, mixedGpuCount));
			}

			migGPUMap.put(nodeName, gpuInfos);
		}
	}

	private static void putGpuMap(Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> normalGPUMap,
		List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos, String nodeName, String gpuName, Integer notMpsGPUCount) {
		gpuInfos.add(new ResponseDTO.NodeGPUs.GPUInfo(gpuName, notMpsGPUCount));
		normalGPUMap.put(nodeName, gpuInfos);
	}

	/**
	 * MIG 전략 "single"인지 확인
	 */
	private boolean isMIGSingleStrategy(Node node) {
		if (!node.getMetadata().getLabels().containsKey(MIG_STRATEGY)) {
			return false;
		}

		return MIGStrategy.SINGLE.name().equals(node.getMetadata().getLabels().get(MIG_STRATEGY).toUpperCase());
	}

}

