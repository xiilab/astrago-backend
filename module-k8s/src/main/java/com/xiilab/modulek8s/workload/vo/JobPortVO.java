package com.xiilab.modulek8s.workload.vo;

public record JobPortVO(
	String name,    // 포트명
	Integer port    // 포트번호
) {
}
