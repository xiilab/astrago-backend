package com.xiilab.modulek8s.workload.dto;

import lombok.Builder;

@Builder
public record WorkloadDTO(
        String workloadType,    // 워크로드 타입
        String workloadName,    // 워크로드 이름
        String description,     // 워크로드 설명
        String command,         // 워크로드 명령
        int gpuRequest,         // 워크로드 gpu 요청량
        int cpuRequest,         // 워크로드 cpu 요청량
        int memRequest          // 워크로드 mem 요청량
) {
}
