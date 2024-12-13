package com.xiilab.serverbatch.job;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulek8s.workload.dto.ResourceOptimizationTargetDTO;
import com.xiilab.modulek8sdb.smtp.repository.SmtpRepository;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;
import com.xiilab.moduleuser.service.UserService;

@SpringBootTest(classes = {ResourceOptimizationJob.class})
public class ResourceOptimizationJobTests {
	@Autowired
	private ResourceOptimizationJob resourceOptimizationJob;
	@MockBean
	private PrometheusService prometheusService;
	@MockBean
	private MailService mailService;
	@MockBean
	private UserService userService;
	@MockBean
	private SmtpRepository smtpRepository;

	@Test
	@DisplayName("podName의 and조건이 정상적으로 동작한다.")
	void getOptimizationAndJobTests() {
		//given
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_CPU), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test2").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build()
			));
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_GPU), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test4").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test5").build()
			));
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_MEM), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test7").build()
			));
		//when
		List<ResourceOptimizationTargetDTO> underResourcePodList = resourceOptimizationJob.getUnderResourcePodList(
			1, 1,
			1, 1, true);
		//then
		Assertions.assertEquals(2, underResourcePodList.size());
	}

	@Test
	@DisplayName("podName의 or조건이 정상적으로 동작한다.")
	void getOptimizationOrJobTests() {
		//given
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_CPU), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test2").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build()
			));
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_GPU), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test4").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test5").build()
			));
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_MEM), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test7").build()
			));
		//when
		List<ResourceOptimizationTargetDTO> underResourcePodList = resourceOptimizationJob.getUnderResourcePodList(
			1, 1,
			1, 1, false);
		//then
		Assertions.assertEquals(6, underResourcePodList.size());

	}

	@Test
	@DisplayName("prometheus에서 빈 값이 넘어와도 에러가 발생하지 않는다.")
	void emptyArrayErrorTest() {
		//given
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_CPU), any(), any(), any()))
			.thenReturn(List.of(
				ResponseDTO.RealTimeDTO.builder().podName("test1").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test2").build(),
				ResponseDTO.RealTimeDTO.builder().podName("test3").build()
			));
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_GPU), any(), any(), any())).thenReturn(
			List.of());
		when(prometheusService.getRealTimeMetric(eq(Promql.RESOURCE_OPTIMIZATION_MEM), any(), any(), any())).thenReturn(
			List.of());
		//when
		List<ResourceOptimizationTargetDTO> underResourcePodList = resourceOptimizationJob.getUnderResourcePodList(
			1, 1,
			1, 1, true);
		//then
		Assertions.assertEquals(0, underResourcePodList.size());
	}
}
