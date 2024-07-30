package com.xiilab.modulek8s.node;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;

@SpringBootTest(classes = K8sInfoPicker.class)
public class K8sInfoPickerTests {

	@Test
	@DisplayName("H100 OEM PCIE 80GB Server의 GPU Chipset을 정상적으로 추출 할 수 있다.")
	void extractGpuChipsetTest() {
		Node testNode = new NodeBuilder()
			.withNewMetadata()
			.withLabels(Map.of(
				"nvidia.com/gpu.product", "NVIDIA-H100-PCIe",
				"nvidia.com/gpu.memory", "81559"
			))
			.endMetadata()
			.build();

		String gpuChipset = K8sInfoPicker.extractGpuChipset(testNode);

		Assertions.assertEquals("H100-80GB", gpuChipset);
	}

	@Test
	@DisplayName("H100 OEM SXM5 80GB Server의 GPU Chipset을 정상적으로 추출 할 수 있다.")
	void extractOEMH10080GBSxm5ChipsetTest() {
		Node testNode = new NodeBuilder()
			.withNewMetadata()
			.withLabels(Map.of(
				"nvidia.com/gpu.product", "NVIDIA-H100-SXM5",
				"nvidia.com/gpu.memory", "81559"
			))
			.endMetadata()
			.build();

		String gpuChipset = K8sInfoPicker.extractGpuChipset(testNode);

		Assertions.assertEquals("H100-80GB", gpuChipset);
	}

	@Test
	@DisplayName("DGX A100 80GB의 서버의 GPU Chipset을 정상적으로 추출 할 수 있다.")
	void extractDGXA10080GBChipsetTest() {
		Node testNode = new NodeBuilder()
			.withNewMetadata()
			.withLabels(Map.of(
				"nvidia.com/gpu.product", "NVIDIA-A100-SXM4-80GB",
				"nvidia.com/gpu.memory", "40448"
			))
			.endMetadata()
			.build();

		String gpuChipset = K8sInfoPicker.extractGpuChipset(testNode);

		Assertions.assertEquals("A100-80GB", gpuChipset);
	}

	@Test
	@DisplayName("A30의 GPU Chipset을 정상적으로 추출 할 수 있다.")
	void extractA30GpuChipsetTest() {
		Node testNode = new NodeBuilder()
			.withNewMetadata()
			.withLabels(Map.of(
				"nvidia.com/gpu.product", "A30",
				"nvidia.com/gpu.memory", "25000"
			))
			.endMetadata()
			.build();
		String gpuChipset = K8sInfoPicker.extractGpuChipset(testNode);

		Assertions.assertEquals("A30-24GB", gpuChipset);
	}

}
