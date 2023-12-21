package com.xiilab.modulek8s.workload.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xiilab.modulek8s.common.enumeration.CodeRepositoryType;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.modulek8s.workload.dto.request.CodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.EnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.PortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.VolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.vo.JobVO;

@SpringBootTest
class WorkloadRepositoryImplTest {

	@Autowired
	WorkloadRepositoryImpl workloadRepo;

	@Test
	void createBatchJobWorkload() {
		// List<PortReqDTO> portReqDTOList = new ArrayList<>();
		// portReqDTOList.add(new PortReqDTO("port1", 8080));

		List<EnvReqDTO> envList = new ArrayList<>();
		envList.add(new EnvReqDTO("TEST", "TEST"));

		List<PortReqDTO> portList = new ArrayList<>();
		portList.add(new PortReqDTO("port1", 8080));
		//portMap.put("port1", 8080);

		List<VolumeReqDTO> envReqDtoList = new ArrayList<>();
		envReqDtoList.add(
			new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume1", "vo-2370c9b3-ee2d-4acc-8add-7bdc187d1a9d", 5, "/usr/local/etc")
		);
		envReqDtoList.add(
			new VolumeReqDTO(StorageType.NFS, VolumeSelectionType.NEW, "volume2", "vo-422e4d40-3500-47df-ba74-b5851ab33eff", 5, "/usr/local/src/code")
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
			.image("perl:5.38.2-slim")
			.ports(portList)
			.envs(envList)
			.command("/bin/bash -c 'while true; do ping -c 4 localhost; sleep 5; done'")
			.volumes(envReqDtoList)
			.cpuRequest(1.9123312132f)
			.gpuRequest(1)
			.memRequest(1.5123312132132f)
			.codes(codeReqs)
			.build();

		JobVO workloadReqDTOToJobCreateVO = createWorkloadReqDTO.toJobVO();
		JobResDTO batchJobWorkload = workloadRepo.createBatchJobWorkload(workloadReqDTOToJobCreateVO);
	}
}
