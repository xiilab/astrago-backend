package com.xiilab.modulek8s.workload.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.dto.WorkloadDTO;
import com.xiilab.modulemonitor.config.K8sAdapter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadService implements WorkloadInterface{
    private final K8sAdapter k8sAdapter;

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
