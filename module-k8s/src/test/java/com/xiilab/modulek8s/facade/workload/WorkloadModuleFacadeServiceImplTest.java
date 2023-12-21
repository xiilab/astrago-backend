package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.common.enumeration.CodeRepositoryType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.service.service.ServiceService;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.*;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class WorkloadModuleFacadeServiceImplTest {

	@Autowired
	WorkloadService workloadService;
	@Autowired
	VolumeService volumeService;
	@Autowired
	StorageClassService storageClassService;
	@Autowired
	ServiceService serviceService;
	@Autowired
	WorkloadModuleFacadeServiceImpl workloadModuleFacadeService;

	@Test
	void createBatchJobWorkload() {
		List<EnvReqDTO> envList = new ArrayList<>();
		envList.add(new EnvReqDTO("TEST", "TEST"));

		List<PortReqDTO> portList = new ArrayList<>();
		portList.add(new PortReqDTO("port1", 8080));

		List<VolumeReqDTO> envReqDtoList = new ArrayList<>();
		envReqDtoList.add(
				new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume1", null, 5, "/usr/local/etc", "nfs-csi")
		);
		envReqDtoList.add(
				new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume2", null, 5, "/usr/local/src/code", "nfs-csi")
		);

		List<CodeReqDTO> codeReqs = new ArrayList<>();
		codeReqs.add(new CodeReqDTO(CodeRepositoryType.PUBLIC, "https://github.com/mfvanek/spring5-mvc-opentracing.git",
			"master", "/mnt"));

		CreateWorkloadReqDTO createWorkloadReqDTO = CreateWorkloadReqDTO.builder()
			.name("사용자 지정 이름")
			.description("Hello world!")
			.workloadType(WorkloadType.BATCH)
			.creatorName("SJO")
			.workspace("yc-test-ns")
			.creator("test1234")
			.image("ubuntu")
			.ports(portList)
			.envs(envList)
			.command("perl -Mbignum=bpi -wle print bpi(2000)")
			.command("/bin/bash")
			.volumes(envReqDtoList)
			.cpuRequest(0.5123312132f)
				.gpuRequest(1)
			.memRequest(0.5123312132132f)
			.codes(codeReqs)
			.build();

		workloadModuleFacadeService.createBatchJobWorkload(createWorkloadReqDTO);
		System.out.println("createWorkloadReqDTO = " + createWorkloadReqDTO);
	}
}
