// package com.xiilab.modulek8s.workload.svc.repository;
//
// import static org.junit.jupiter.api.Assertions.*;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.ContextConfiguration;
//
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.xiilab.modulek8s.TestConfiguration;
// import com.xiilab.modulek8s.config.K8sAdapter;
// import com.xiilab.modulek8s.facade.storage.StorageModuleServiceImpl;
// import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeServiceImpl;
//
// import io.fabric8.kubernetes.api.model.ServiceList;
//
// @SpringBootTest
// @ContextConfiguration(classes = TestConfiguration.class)
// class SvcRepositoryImplTest {
// 	@Autowired
// 	private K8sAdapter k8sAdapter;
// 	@Autowired
// 	private SvcRepository svcRepository;
//
// 	@Test
// 	void getServiceByResourceName() {
// 		String workspaceResourceName = "ws-7524bdd8-5b1a-4c2f-92f0-a75b94a6f9e3";
// 		String workloadResourceName = "wl-075f13f4-52a3-4907-98cd-d2fc21573da6";
//
// 		ServiceList servicesByResourceName = svcRepository.getServicesByResourceName(workspaceResourceName,
// 			workloadResourceName);
// 		System.out.println();
// 		System.out.println();
// 		System.out.println();
// 	}
// }
