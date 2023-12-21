package com.xiilab.modulek8s.workload.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadServiceImpl implements WorkloadService{
	private final WorkloadRepository workloadRepository;

	public JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO) {
		return workloadRepository.createBatchJobWorkload(createWorkloadReqDTO.toJobVO());
	}
}
