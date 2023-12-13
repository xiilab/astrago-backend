package com.xiilab.modulek8s.workload.dto;

public record ImageDTO(
        String imageName,   // 이미지 이름
        String imageTag    // 이미지 태그
) {
}
