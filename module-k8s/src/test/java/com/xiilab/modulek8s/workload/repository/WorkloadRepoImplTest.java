package com.xiilab.modulek8s.workload.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.EnvReqDTO;
import com.xiilab.modulek8s.workload.dto.JobReqVO;
import com.xiilab.modulek8s.workload.dto.request.PortReqDTO;

@SpringBootTest
class WorkloadRepoImplTest {

	@Autowired
	WorkloadRepoImpl workloadRepo;

	@Test
	void createBatchJobWorkload() {
		List<PortReqDTO> portReqDTOList = new ArrayList<>();
		portReqDTOList.add(new PortReqDTO("port1", 8080));

		List<EnvReqDTO> envReqDtoList = new ArrayList<>();
		envReqDtoList.add(new EnvReqDTO("TEST", "TEST1"));
		System.out.println("UUID.randomUUID().toString().substring(0, 16) = " + UUID.randomUUID().toString().substring(0, 16));


		//create
		//core controller
		CreateWorkloadReqDTO createWorkloadReqDTO = CreateWorkloadReqDTO.builder()
			.workspace("default")
			.name("사용자 지정 이름")
			.description("Hello :q!world!")
			.creator("test1234")
			.creatorName("SJO")
			.image("perl")
			.ports(portReqDTOList)
			.envs(envReqDtoList)
			.command("/bin/bash")
			.build();

		//core service
		JobReqVO workloadReqDTOToJobReqVO = createWorkloadReqDTO.createWorkloadReqDtoToJobReqVO();
		workloadRepo.createBatchJobWorkload(workloadReqDTOToJobReqVO);

		//////////////////////////////////////////////////////////////
		//update
		//a workspace update -> name, description




	}
}
