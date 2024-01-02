package com.xiilab.modulek8s.workload.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadModuleServiceImpl implements WorkloadModuleService {
	private final WorkloadRepository workloadRepository;

	public ModuleBatchJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return workloadRepository.createBatchJobWorkload(moduleCreateWorkloadReqDTO.toBatchJobVO());
	}

	@Override
	public ModuleInteractiveJobResDTO createInteractiveJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return workloadRepository.createInteractiveJobWorkload(moduleCreateWorkloadReqDTO.toInteractiveJobVO());
	}
}
