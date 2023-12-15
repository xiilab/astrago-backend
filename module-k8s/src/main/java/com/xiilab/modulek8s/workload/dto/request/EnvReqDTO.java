package com.xiilab.modulek8s.workload.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnvReqDTO {
	private String name;
	private String value;
}
