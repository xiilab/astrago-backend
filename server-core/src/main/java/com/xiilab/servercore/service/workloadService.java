package com.xiilab.servercore.service;

import com.xiilab.modulek8s.workload.service.WorkloadInterface;
import com.xiilab.modulek8s.workload.dto.WorkloadDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class workloadService implements WorkloadInterface {

    @Override
    public WorkloadDTO createWorkload() {
        return null;
    }

    @Override
    public List<WorkloadDTO> getWorkloadList() {
        return null;
    }

    @Override
    public WorkloadDTO getWorkload() {
        return null;
    }

    @Override
    public void deleteWorkload() {

    }
}
