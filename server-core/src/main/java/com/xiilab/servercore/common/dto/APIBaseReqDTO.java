package com.xiilab.servercore.common.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public abstract class APIBaseReqDTO {
	//사용자가 실제 입력한 name
	@Pattern(regexp = "^[^-_]*$")
	String name;
	//resource에 대한 설명
	String description;

	public APIBaseReqDTO() {
	}

	public APIBaseReqDTO(String name, String description) {
		this.name = name;
		this.description = description;
	}
}
