package com.xiilab.servercore.workload.service;

import com.xiilab.modulek8s.common.vo.K8SResourceResVO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.repository.WorkloadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkloadService {

    private final WorkloadRepository workloadRepository;


    /**
     * batch job workload 조회
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    public JobResDTO getBatchJob(String workSpaceName, String workloadName) {
        return workloadRepository.getBatchJobWorkload(workSpaceName, workloadName);
    }

    /**
     * batch job workload 삭제
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    public String deleteBatchJob(String workSpaceName, String workloadName) {
        return workloadRepository.deleteBatchJobWorkload(workSpaceName, workloadName);
    }

    /**
     * interactive job workload 삭제
     *
     * @param workSpaceName
     * @param workloadName
     * @return
     */
    public String deleteInteractiveJob(String workSpaceName, String workloadName) {
        return workloadRepository.deleteInteractiveJobWorkload(workSpaceName, workloadName);
    }

    /**
     * workload list 조회
     * @param workSpaceName
     * @return
     */
    public List<WorkloadResDTO> getWorkloadList(String workSpaceName) {
        List<WorkloadResDTO> workloadList = new ArrayList<>();
        List<WorkloadResDTO> jobWorkloadList = workloadRepository.getBatchJobWorkloadList(workSpaceName);
        List<WorkloadResDTO> workloadResList = workloadRepository.getInteractiveJobWorkloadList(workSpaceName);

        if(!jobWorkloadList.isEmpty()) {
            workloadList.addAll(jobWorkloadList);
        }
//        if(!workloadResList.isEmpty()) {
        if(workloadResList != null) {
            workloadList.addAll(workloadResList);
        }
        if (!workloadList.isEmpty()) {
            return workloadList.stream().sorted(Comparator.comparing(K8SResourceResVO::getCreatedAt)).collect(Collectors.toList());
        } else {
            return null;
        }
    }
}
