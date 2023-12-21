package com.xiilab.moduleuser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupReqDTO {
	private String name;
	private String description;
	private String createdBy;
	private String createdUserId;
	private List<String> users;
}
