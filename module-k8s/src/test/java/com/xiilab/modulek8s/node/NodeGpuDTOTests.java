package com.xiilab.modulek8s.node;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.xiilab.modulek8s.node.dto.NodeGpuDTO;

@SpringBootTest
class NodeGpuDTOTests {
	@Test
	@DisplayName("mig req dto 객체를 configmap에 넣기 위해 map으로 변환 테스트")
	void migReqConvertMapTest() {
		NodeGpuDTO.MIGRequestDTO migRequestDTO1 = new NodeGpuDTO.MIGRequestDTO(
			List.of(0, 1, 2, 3),
			true,
			Map.of("1g.12gb", 4, "3g.48gb", 1));
		NodeGpuDTO.MIGRequestDTO migRequestDTO2 = new NodeGpuDTO.MIGRequestDTO(
			List.of(4, 5, 6),
			true,
			Map.of("1g.12gb", 7));
		NodeGpuDTO.MIGRequestDTO migRequestDTO3 = new NodeGpuDTO.MIGRequestDTO(
			List.of(7, 8),
			false, null);
		NodeGpuDTO dgxh100 = new NodeGpuDTO("dgxh100", List.of(migRequestDTO1, migRequestDTO2, migRequestDTO3));

		List<Object> actual = dgxh100.convertMap();
		String migKey = dgxh100.getMigKey();
		assertThat(migKey).isEqualTo("custom-dgxh100");
		assertThat(actual).hasSize(3);
		assertThat(((Map<String, Object>)actual.get(0)).get("mig-enabled")).isEqualTo(true);
		assertThat(((Map<String, Object>)actual.get(0)).get("devices")).isEqualTo(List.of(0, 1, 2, 3));
		assertThat(((Map<String, Object>)actual.get(0)).get("mig-devices")).isEqualTo(
			Map.of("1g.12gb", 4, "3g.48gb", 1));
		assertThat(((Map<String, Object>)actual.get(1)).get("mig-enabled")).isEqualTo(true);
		assertThat(((Map<String, Object>)actual.get(1)).get("devices")).isEqualTo(List.of(4, 5, 6));
		assertThat(((Map<String, Object>)actual.get(1)).get("mig-devices")).isEqualTo(Map.of("1g.12gb", 7));
		assertThat(((Map<String, Object>)actual.get(2)).get("mig-enabled")).isEqualTo(false);
		assertThat(((Map<String, Object>)actual.get(2)).get("devices")).isEqualTo(List.of(7, 8));
	}
}
