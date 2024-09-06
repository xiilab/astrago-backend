package com.xiilab.modulek8s.workload.utils;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.xiilab.modulek8s.common.utils.K8sInfoPicker;

import io.fabric8.kubernetes.api.model.Quantity;

public class K8sInfoPickerTests {
	@Test
	@DisplayName("소수점 메로리를 할당한 것을 정상적으로 변환할 수 있다.")
	void convertMEMFloatConvert() {
		float m = K8sInfoPicker.convertMEMQuantity(new Quantity("3650722201600", "m"));
		assertThat(m).isEqualTo(3.4F);
	}

	@Test
	@DisplayName("정수 메모리를 할당한 것을 정상적으로 변환할 수 있다.")
	void convertMEMQuantityConvert() {
		float m = K8sInfoPicker.convertMEMQuantity(new Quantity("3", "Gi"));
		assertThat(m).isEqualTo(3F);
	}

	@Test
	@DisplayName("소수점 CPU 할당한 것을 정상적으로 변환 할 수 있다.")
	void convertCPUFloatQuantityConvert() {
		float m = K8sInfoPicker.convertCPUQuantity(new Quantity("1200", "m"));
		assertThat(m).isEqualTo(1.2F);
	}

	@Test
	@DisplayName("정수 CPU 할당한 것을 정상적으로 변환 할 수 있다.")
	void convertCPUQuantityConvert() {
		float m = K8sInfoPicker.convertCPUQuantity(new Quantity("1"));
		assertThat(m).isEqualTo(1F);
	}
}
