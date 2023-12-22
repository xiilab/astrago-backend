package com.xiilab.modulek8s.workload.service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadServiceImpl implements WorkloadService{
	private final WorkloadRepository workloadRepository;

	public JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO) {
		return workloadRepository.createBatchJobWorkload(createWorkloadReqDTO.toJobVO());
	}

	@Override
	public WorkloadResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO) {
		return workloadRepository.createInteractiveJobWorkload(createWorkloadReqDTO.toJobVO());
	}

	@Override
	public JobResDTO getBatchJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public WorkloadResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName) {
		return workloadRepository.getInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public List<WorkloadResDTO> getBatchJobWorkloadList(String workSpaceName) {
		return workloadRepository.getBatchJobWorkloadList(workSpaceName);
	}

	@Override
	public List<WorkloadResDTO> getInteractiveJobWorkloadList(String workSpaceName) {
		return workloadRepository.getInteractiveJobWorkloadList(workSpaceName);
	}

	@Override
	public WorkloadResDTO updateInteractiveJobWorkload(CreateWorkloadReqDTO workloadReqDTO) {
		return workloadRepository.updateInteractiveJobWorkload(workloadReqDTO);
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
