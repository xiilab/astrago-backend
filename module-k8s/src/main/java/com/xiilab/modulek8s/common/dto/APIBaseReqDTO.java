package com.xiilab.modulek8s.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class APIBaseReqDTO {
	//사용자가 실제 입력한 name
	@NotNull
	// @Pattern(regexp = "^[^-_]*$")
	protected String name;
	//resource에 대한 설명
	@NotNull
	protected String description;
}
