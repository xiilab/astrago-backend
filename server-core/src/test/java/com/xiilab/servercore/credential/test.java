// package com.xiilab.servercore.credential;
//
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.BDDMockito.*;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Set;
//
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
//
// import com.xiilab.modulek8s.common.dto.PageDTO;
// import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
// import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
// import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
// import com.xiilab.moduleuser.dto.UserInfoDTO;
// import com.xiilab.servercore.pin.service.PinService;
// import com.xiilab.servercore.workload.enumeration.WorkloadSortCondition;
// import com.xiilab.servercore.workload.service.WorkloadFacadeService;
//
// @SpringBootTest
// @ExtendWith(MockitoExtension.class)
// class test {
// 	@Autowired
// 	WorkloadFacadeService workloadFacadeService;
// 	@MockBean
// 	PinService pinService;
// 	@MockBean
// 	WorkloadModuleService workloadModuleService;
//
// 	@Test
// 	void test() {
// 		List<ModuleBatchJobResDTO> testResult = new ArrayList<>();
//
// 		for (int i = 0; i < 98; i++) {
// 			testResult.add(ModuleBatchJobResDTO.builder()
// 				.uid(String.valueOf(i))
// 				.name("김연훈" + i)
// 				.age(i)
// 				.status(WorkloadStatus.RUNNING)
// 				.build());
// 		}
//
// 		// given(pinService.getUserWorkloadPinList(any(), any())).willReturn(Set.of("1", "3"));
// 		given(pinService.getUserWorkloadPinList(any(), any())).willReturn(Set.of("1","2","3","5"));
// 		given(workloadModuleService.getBatchJobWorkloadList(any())).willReturn(testResult);
// 		PageDTO test = workloadFacadeService.getBatchWorkloadByCondition(null, "김연훈", null,
// 			WorkloadSortCondition.AGE_ASC,
// 			16, UserInfoDTO.builder().id("1").build());
// 		test.getContent();
// 	}
// }
