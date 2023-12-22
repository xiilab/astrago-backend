package com.xiilab.servercore.workload.service;

import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkloadFacadeServiceImpl implements WorkloadFacadeService {

    private final WorkloadService workloadService;


    @Override
    public void createBatchJob(CreateWorkloadReqDTO createWorkloadReqDTO) {
        workloadService.createBatchJobWorkload(createWorkloadReqDTO);
    }

    @Override
    public void createInteractiveJob(CreateWorkloadReqDTO createWorkloadReqDTO) {
        workloadService.createInteractiveJobWorkload(createWorkloadReqDTO);
    }

    /**
     * batch job workload 조회
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    @Override
    public JobResDTO getBatchJob(String workSpaceName, String workloadName) {
        return workloadService.getBatchJobWorkload(workSpaceName, workloadName);
    }

    @Override
    public WorkloadResDTO getInteractiveJob(String workSpaceName, String workloadName) {
        return workloadService.getInteractiveJobWorkload(workSpaceName, workloadName);
    }

    /**
     * batch job workload 삭제
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    @Override
    public String deleteBatchJob(String workSpaceName, String workloadName) {
        return workloadService.deleteBatchJobWorkload(workSpaceName, workloadName);
    }

    /**
     * interactive job workload 삭제
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    @Override
    public String deleteInteractiveJob(String workSpaceName, String workloadName) {
        return workloadService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
    }

    /**
     * workload list 조회
     *
     * @param workSpaceName
     * @return
     */
    @Override
    public List<WorkloadResDTO> getWorkloadList(String workSpaceName) {
        List<WorkloadResDTO> workloadList = new ArrayList<>();
        List<WorkloadResDTO> jobWorkloadList = workloadService.getBatchJobWorkloadList(workSpaceName);
        List<WorkloadResDTO> workloadResList = workloadService.getInteractiveJobWorkloadList(workSpaceName);

        if (!jobWorkloadList.isEmpty()) {
            workloadList.addAll(jobWorkloadList);
        }
//        if(!workloadResList.isEmpty()) {
        if (workloadResList != null) {
            workloadList.addAll(workloadResList);
        }
        if (!workloadList.isEmpty()) {
            return workloadList.stream().sorted(Comparator.comparing(K8SResourceResVO::getCreatedAt)).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
