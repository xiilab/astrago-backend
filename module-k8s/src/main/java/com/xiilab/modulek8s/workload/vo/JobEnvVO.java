package com.xiilab.modulek8s.workload.vo;

public record JobEnvVO(
	String varName,    // 변수명
	String value    // 값
) {
}
