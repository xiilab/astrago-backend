package com.xiilab.modulek8s.workload.service;

import java.util.List;

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
	public ModuleInteractiveJobResDTO createInteractiveJobWorkload(
		ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		return workloadRepository.createInteractiveJobWorkload(moduleCreateWorkloadReqDTO.toInteractiveJobVO());
	}

	@Override
	public ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public List<ModuleBatchJobResDTO> getBatchJobWorkloadList(String workSpaceName) {
		return workloadRepository.getBatchJobWorkloadList(workSpaceName);
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveJobWorkloadList(String workSpaceName) {
		return workloadRepository.getInteractiveJobWorkloadList(workSpaceName);
	}

	@Override
	public String deleteBatchJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.deleteBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public String deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}
}
