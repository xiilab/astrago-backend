// package com.xiilab.modulek8s.node;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.xiilab.modulek8s.config.K8sAdapter;
// import com.xiilab.modulek8s.node.dto.MigMixedDTO;
// import com.xiilab.modulek8s.node.enumeration.MIGProduct;
// import com.xiilab.modulek8s.node.enumeration.MIGStrategy;
// import com.xiilab.modulek8s.node.repository.NodeRepository;
//
// import io.fabric8.kubernetes.api.model.Node;
// import io.fabric8.kubernetes.api.model.NodeAddress;
// import io.fabric8.kubernetes.api.model.NodeStatus;
// import io.fabric8.kubernetes.api.model.ObjectMeta;
// import io.fabric8.kubernetes.client.KubernetesClient;
// import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
// import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
//
// @SpringBootTest
// @ActiveProfiles("test")
// @EnableKubernetesMockClient(crud = true)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// class NodeServiceTest {
// 	KubernetesMockServer server;
// 	KubernetesClient client;
// 	@MockBean
// 	K8sAdapter k8sAdapter;
// 	@MockBean
// 	private ObjectMapper objectMapper;
// 	@Autowired
// 	private NodeRepository nodeRepository;
//
// 	@BeforeEach
// 	void mock_서버_세팅() {
// 		//mockserver
// 		given(k8sAdapter.configServer()).willReturn(client);
// 	}
//
// 	@AfterEach
// 	public void afterEach() {
// 		server.destroy();
// 	}
//
// 	/**
// 	 * 테스트를 위한 Dummy GPU NodeService
// 	 *
// 	 * @return
// 	 */
// 	Node createGPUDummyNode() {
// 		Node gpuDummyNode = createGeneralDummyNode();
// 		Map<String, String> labels = gpuDummyNode.getMetadata().getLabels();
// 		labels.put("nvidia.com/gpu.count", "3");
// 		labels.put("nvidia.com/gpu.present", "true");
// 		labels.put("kubernetes.io/hostname", "gpu-t4");
// 		labels.put("nvidia.com/gpu.product", "t4");
// 		labels.put("nvidia.com/gpu.memory", "12194");
// 		labels.put("nvidia.com/mig.strategy","single");
// 		return gpuDummyNode;
// 	}
//
// 	Node createSingle1GMIGDummyNode() {
// 		Node migDummyNode = createGeneralDummyNode();
// 		Map<String, String> labels = migDummyNode.getMetadata().getLabels();
// 		labels.put("nvidia.com/gpu.product", "A100-80GB-PCIe-MIG-1g.10gb");
// 		labels.put("nvidia.com/gpu.present", "true");
// 		labels.put("nvidia.com/gpu.memory", "9728");
// 		labels.put("nvidia.com/gpu.count", "56");
// 		labels.put("nvidia.com/gpu.slices.ci", "1");
// 		labels.put("nvidia.com/gpu.slices.gi", "1");
// 		labels.put("nvidia.com/mig.capable", "true");
// 		labels.put("nvidia.com/mig.strategy", "single");
//
// 		return migDummyNode;
// 	}
//
// 	Node createMixedMIGDummyNode() {
// 		Node migDummyNode = createGeneralDummyNode();
// 		Map<String, String> labels = migDummyNode.getMetadata().getLabels();
// 		labels.put("nvidia.com/gpu.product", "A100-SXM4-40GB");
// 		labels.put("nvidia.com/gpu.count", "3");
// 		labels.put("nvidia.com/mig-1g.5gb.count", "6");
// 		labels.put("nvidia.com/mig-1g.5gb.memory", "20096");
// 		labels.put("nvidia.com/mig-1g.5gb.slices.ci", "1");
// 		labels.put("nvidia.com/mig-1g.5gb.slices.gi", "1");
// 		labels.put("nvidia.com/mig-2g.10gb.count", "3");
// 		labels.put("nvidia.com/mig-2g.10gb.slices.ci", "2");
// 		labels.put("nvidia.com/mig-2g.10gb.slices.gi", "2");
// 		labels.put("nvidia.com/mig-2g.10gb.memory", "20096");
// 		labels.put("nvidia.com/mig-3g.20gb.count", "3");
// 		labels.put("nvidia.com/mig-3g.20gb.slices.ci", "3");
// 		labels.put("nvidia.com/mig-3g.20gb.slices.gi", "3");
// 		labels.put("nvidia.com/mig-3g.20gb.memory", "20096");
// 		labels.put("nvidia.com/mig.capable", "true");
// 		labels.put("nvidia.com/mig.strategy", "mixed");
// 		labels.put("nvidia.com/gpu.present", "true");
//
// 		return migDummyNode;
// 	}
//
// 	/**
// 	 * 테스트를 위한 더미일반노드
// 	 *
// 	 * @return
// 	 */
// 	Node createGeneralDummyNode() {
// 		Node node = new Node();
// 		ObjectMeta objectMeta = new ObjectMeta();
// 		Map<String, String> label = new HashMap<>();
// 		label.put("kubernetes.io/hostname", "macmini");
// 		objectMeta.setName("macmini");
// 		objectMeta.setLabels(label);
// 		node.setMetadata(objectMeta);
// 		NodeStatus nodeStatus = new NodeStatus();
// 		nodeStatus.setAddresses(List.of(new NodeAddress("127.0.0.1", "InternalIP")));
// 		node.setStatus(nodeStatus);
// 		return node;
// 	}
//
// 	@Test
// 	@DisplayName("gpu product 조회 테스트")
// 	void getGpuProduct() {
// 		String product = "A100-80GB-PCIe-MIG-1g.10gb";
// 		MIGProduct gpuProduct = MIGProduct.getGpuProduct(product);
// 		assertThat(gpuProduct).isEqualTo(MIGProduct.A100_80GB);
// 		String product1 = "A100-SXM4-40GB";
// 		MIGProduct gpuProduct1 = MIGProduct.getGpuProduct(product1);
// 		assertThat(gpuProduct1).isEqualTo(MIGProduct.A100_40GB);
// 	}
//
// 	@Test
// 	@DisplayName("gpu count 테스트")
// 	void getGPUCountTest() {
// 		//GPU 노드일때 테스트
// 		//given
// 		Node gpuDummyNode = createGPUDummyNode();
// 		//when&&then
// 		assertEquals(3, nodeRepository.getGPUCount(gpuDummyNode));
//
// 		//MIG 노드일때 테스트
// 		//given
// 		Node mixedMIGDummyNode = createMixedMIGDummyNode();
// 		//when&&then
// 		assertEquals(3, nodeRepository.getGPUCount(mixedMIGDummyNode));
//
// 		//일반 노드일때 테스트
// 		//given
// 		Node gerneralDummyNode = createGeneralDummyNode();
// 		//when&then
// 		assertThat(nodeRepository.getGPUCount(gerneralDummyNode)).isZero();
//
// 		//given
// 		Node nullNode = null;
// 		//when&&then
// 		assertThat(nodeRepository.getGPUCount(nullNode)).isZero();
// 	}
// 	@Test
// 	@DisplayName("mig strategy single 검사 테스트")
// 	void getNodeMIGSingleEnabledTest() {
// 		Node migDummyNode = createSingle1GMIGDummyNode();
// 		MIGStrategy nodeMIGEnbaled = nodeRepository.getNodeMIGOnOffYN(migDummyNode.getMetadata().getLabels());
// 		assertThat(nodeMIGEnbaled).isEqualTo(MIGStrategy.SINGLE);
// 	}
//
// 	@Test
// 	@DisplayName("mig strategy mixed 검사 테스트")
// 	void getNodeMIGMixedEnabledTest() {
// 		Node mixedMIGDummyNode = createMixedMIGDummyNode();
// 		MIGStrategy nodeMIGEnbaled1 = nodeRepository.getNodeMIGOnOffYN(mixedMIGDummyNode.getMetadata().getLabels());
// 		assertThat(nodeMIGEnbaled1).isEqualTo(MIGStrategy.MIXED);
// 	}
//
// 	@Test
// 	@DisplayName("일반 노드 mig 검사 테스트")
// 	void getNodeMigGeneralEnabledTest() {
// 		Node gpuDummyNode = createGPUDummyNode();
// 		MIGStrategy nodeMIGEnbaled2 = nodeRepository.getNodeMIGOnOffYN(gpuDummyNode.getMetadata().getLabels());
// 		assertThat(nodeMIGEnbaled2).isNull();
// 	}
//
// 	@Test
// 	@DisplayName("mixed인 노드의 정보 가져오는 테스트")
// 	void getMigMixedInfoTest() {
// 		Node mixedMIGDummyNode = createMixedMIGDummyNode();
// 		List<MigMixedDTO> migMixedInfo = nodeRepository.getMigMixedInfo(mixedMIGDummyNode);
//
// 		assertThat(migMixedInfo).hasSize(3);
// 		assertThat(migMixedInfo.get(0).count()).isEqualTo(6);
// 		assertThat(migMixedInfo.get(1).count()).isEqualTo(3);
// 		assertThat(migMixedInfo.get(2).count()).isEqualTo(3);
// 	}
//
//
// }
