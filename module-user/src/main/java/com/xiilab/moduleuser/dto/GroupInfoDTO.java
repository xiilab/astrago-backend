package com.xiilab.moduleuser.dto;

import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;

import lombok.Getter;

@Getter
public class GroupInfoDTO {
	private String uid;
	private String name;
	private String description;
	private List<GroupUserDTO> users;

	public GroupInfoDTO(GroupResource groupResource, List<UserInfo> groupUsers) {
		GroupRepresentation representation = groupResource.toRepresentation();
		Map<String, List<String>> attributes = representation.getAttributes();
		this.uid = representation.getId();
		this.name = representation.getName();
		if (attributes != null) {
			this.description = attributes.get("description") != null ? attributes.get("description").get(0) : null;
		}
		this.users = groupUsers.stream().map(user
			-> new GroupUserDTO(user.getId(), user.getUserName(), user.getEmail())).toList();
	}
}
