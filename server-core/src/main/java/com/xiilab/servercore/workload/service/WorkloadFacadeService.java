package com.xiilab.servercore.workload.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.common.dto.UserInfoDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadFacadeService {
	private final WorkloadModuleFacadeService workloadModuleFacadeService;

	public void deleteBatchHobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName,
			workloadName, WorkloadType.BATCH);
		FileUtils.saveLogFile(log, workloadName,userInfoDTO.getUserName());
		workloadModuleFacadeService.deleteBatchHobWorkload(workSpaceName, workloadName);
	}

	public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName,
			workloadName, WorkloadType.INTERACTIVE);
		FileUtils.saveLogFile(log, workloadName,userInfoDTO.getUserName());
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}
}

