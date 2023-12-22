package com.xiilab.servercore.workload.service;

import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;

import java.util.List;

public interface WorkloadFacadeService {
    void createBatchJob(CreateWorkloadReqDTO createWorkloadReqDTO);

    void createInteractiveJob(CreateWorkloadReqDTO createWorkloadReqDTO);

    JobResDTO getBatchJob(String workSpaceName, String workloadName);

    WorkloadResDTO getInteractiveJob(String workSpaceName, String workloadName);

    String deleteBatchJob(String workSpaceName, String workloadName);

    String deleteInteractiveJob(String workSpaceName, String workloadName);

    List<WorkloadResDTO> getWorkloadList(String workSpaceName);
}
