package com.xiilab.modulek8s.node.repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.NodeErrorCode;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.node.dto.MigMixedDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.MIGProduct;
import com.xiilab.modulek8s.node.enumeration.MIGStrategy;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
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
	@Value("${mig-profile-path}")
	private String migProfilePath;

	@Override
	public List<ResponseDTO.NodeDTO> getNodeList() {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			List<Node> nodes = client.nodes().list().getItems();

			return nodes.stream().filter(node ->
					node.getMetadata().getLabels().get(GPU_NAME) != null)
				.map(node ->
					ResponseDTO.NodeDTO.builder()
						.nodeName(node.getMetadata().getName())
						.gpuName(node.getMetadata().getLabels().get(GPU_NAME))
						.gpuCount(node.getMetadata().getLabels().get(GPU_COUNT))
						.build()).toList();
		}
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

	private Node getNode(String nodeName) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			return client.nodes().list().getItems().stream().filter(node ->
				node.getMetadata().getName().equals(nodeName)).findFirst().orElseThrow();
		}
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

