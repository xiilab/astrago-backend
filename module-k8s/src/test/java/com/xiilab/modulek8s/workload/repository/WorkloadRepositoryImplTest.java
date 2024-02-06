// package com.xiilab.modulek8s.workload.repository;
//
// import static org.mockito.BDDMockito.*;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.boot.test.mock.mockito.MockBean;
//
// import com.xiilab.modulek8s.common.enumeration.CodeRepositoryType;
// import com.xiilab.modulek8s.common.enumeration.StorageType;
// import com.xiilab.modulek8s.config.K8sAdapter;
// import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
// import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
// import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
// import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
// import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
// import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
// import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
// import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
// import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
// import com.xiilab.modulek8s.workload.enums.WorkloadType;
//
// import io.fabric8.kubernetes.api.model.apps.DeploymentList;
// import io.fabric8.kubernetes.api.model.batch.v1.JobList;
// import io.fabric8.kubernetes.client.KubernetesClient;
// import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
// import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
//
// @ExtendWith(MockitoExtension.class)
// @EnableKubernetesMockClient(crud = true)
// class WorkloadRepositoryImplTest {
// 	KubernetesMockServer server;
// 	KubernetesClient client;
// 	@Mock
// 	K8sAdapter k8sAdapter;
// 	@InjectMocks
// 	WorkloadRepositoryImpl workloadRepository;
//
// 	@BeforeEach
// 	void setUp() {
// 		client = server.createClient();
// 		given(k8sAdapter.configServer()).willReturn(client);
// 	}
//
// 	@AfterEach
// 	void destroy() {
// 		if (client != null) {
// 			client.close();
// 		}
// 		if (server != null) {
// 			server.destroy();
// 		}
// 	}
//
// 	@Test
// 	void createBatchJobWorkload() {
// 		ModuleCreateWorkloadReqDTO createWorkloadReqDTO = createModuleWorkloadReqDTO();
//
// 		workloadRepository.createBatchJobWorkload(createWorkloadReqDTO.toBatchJobVO());
//
// 		client = server.createClient();
// 		JobList batchJobList = client.batch().v1().jobs().list();
// 		Assertions.assertThat(batchJobList.getItems()).hasSize(1);
// 	}
//
// 	@Test
// 	void createInteractiveJobWorkload() {
// 		ModuleCreateWorkloadReqDTO createWorkloadReqDTO = createModuleWorkloadReqDTO();
//
// 		workloadRepository.createInteractiveJobWorkload(createWorkloadReqDTO.toInteractiveJobVO());
//
// 		client = server.createClient();
// 		DeploymentList list = client.apps().deployments().list();
// 		Assertions.assertThat(list.getItems()).hasSize(1);
// 	}
//
// 	private ModuleCreateWorkloadReqDTO createModuleWorkloadReqDTO() {
// 		List<ModuleEnvReqDTO> envList = new ArrayList<>();
// 		envList.add(new ModuleEnvReqDTO("TEST", "TEST"));
//
// 		List<ModulePortReqDTO> portList = new ArrayList<>();
// 		portList.add(new ModulePortReqDTO("port1", 8080));
// 		portList.add(new ModulePortReqDTO("port3", 8089));
//
// 		List<ModuleVolumeReqDTO> volumeList = new ArrayList<>();
// 		volumeList.add(
// 			new ModuleVolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume1", "test", 5, "/usr/local/etc",
// 				"nfs-csi")
// 		);
// 		volumeList.add(
// 			new ModuleVolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume2", "test", 5,
// 				"/usr/local/src/code", "nfs-csi")
// 		);
//
// 		List<ModuleCodeReqDTO> codeReqs = new ArrayList<>();
// 		codeReqs.add(
// 			new ModuleCodeReqDTO(CodeRepositoryType.PUBLIC, "https://github.com/mfvanek/spring5-mvc-opentracing.git",
// 				"master", "/mnt"));
//
// 		ModuleCreateWorkloadReqDTO createWorkloadReqDTO = ModuleCreateWorkloadReqDTO.builder()
// 			.varName("사용자 지정 이름")
// 			.description("Hello world!")
// 			.workloadType(WorkloadType.BATCH)
// 			.creatorName("SJO")
// 			.workspace("test")
// 			.creator("test1234")
// 			.image(new ModuleImageReqDTO("ubuntu", "latest"))
// 			//.image("ubuntu")
// 			.ports(portList)
// 			.envs(envList)
// 			.command("while true; do echo hello; sleep 10;done")
// 			.volumes(volumeList)
// 			.cpuRequest(0.5123312132f)
// 			.gpuRequest(1)
// 			.memRequest(0.5123312132132f)
// 			.codes(codeReqs)
// 			.build();
// 		return createWorkloadReqDTO;
// 	}
// }
