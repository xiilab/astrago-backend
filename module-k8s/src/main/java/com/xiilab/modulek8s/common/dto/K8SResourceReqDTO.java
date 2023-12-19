package com.xiilab.modulek8s.common.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class K8SResourceReqDTO {
	//사용자가 실제 입력한 name
	@Pattern(regexp = "^[^-_]*$")
	String name;
	//resource에 대한 설명
	String description;
	String creatorName;
	String creator;

	public K8SResourceReqDTO(String name, String description, String creatorName, String creator) {
		this.name = name;
		this.description = description;
		this.creatorName = creatorName;
		this.creator = creator;
	}
}
