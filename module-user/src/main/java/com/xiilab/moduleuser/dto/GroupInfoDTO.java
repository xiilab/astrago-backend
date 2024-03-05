package com.xiilab.moduleuser.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;

import lombok.Getter;

@Getter
public class GroupInfoDTO {
	private String uid;
	private String name;
	private LocalDate createdDate;
	private String createdBy;
	private String description;
	// private List<GroupUserDTO> users;

	public GroupInfoDTO(GroupResource groupResource) {
		GroupRepresentation representation = groupResource.toRepresentation();
		Map<String, List<String>> attributes = representation.getAttributes();
		this.uid = representation.getId();
		this.name = representation.getName();
		if (attributes != null) {
			this.description = attributes.get("description") != null ? attributes.get("description").get(0) : null;
			this.createdDate =
				attributes.get("createdDate") != null ? LocalDate.parse(attributes.get("createdDate").get(0)) : null;
			this.createdBy =
				attributes.get("createdBy") != null ? attributes.get("createdBy").get(0) : null;
		}
		// this.users = groupUsers.stream().map(user
		// 	-> GroupUserDTO.builder()
		// 	.id(user.getId())
		// 	.name(user.getUserName())
		// 	.email(user.getEmail())
		// 	.build()).toList();
	}
}
