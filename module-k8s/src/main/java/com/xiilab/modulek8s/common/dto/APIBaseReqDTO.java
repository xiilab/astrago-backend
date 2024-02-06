package com.xiilab.modulek8s.common.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class APIBaseReqDTO {
	//사용자가 실제 입력한 varName
	@Pattern(regexp = "^[^-_]*$")
	String name;
	//resource에 대한 설명
	String description;
}
