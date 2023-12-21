package com.xiilab.modulek8s.workload.dto;

import lombok.Builder;

@Builder
public record CodeDTO(
        String repositoryType,  // repository 타입
        String userName,        // userName (private 타입에서 사용)
        String token,           // 토큰 (private 타입에서 사용)
        String repositoryURL,   // repository URL
        String branch,          // repository branch
        String mountPath        // 소스코드 마운트 경로
) {
}
