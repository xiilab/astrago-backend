package com.xiilab.modulek8s.workload.service;

import com.xiilab.modulek8s.workload.dto.WorkloadDTO;

import java.util.List;

public interface WorkloadInterface {

    //워크로드 생성
    WorkloadDTO createWorkload();
    //워크로드 리스트 조회
    List<WorkloadDTO> getWorkloadList();
    //워크로드 상세 조회
    WorkloadDTO getWorkload();
    //워크로드 삭제
    void deleteWorkload();
}
