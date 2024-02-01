package com.xiilab.moduleuser.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSummary{
	private final String uid;
	private final String fullName;
	private final String name;
	private final String email;
	private final SignUpMethod signUpMethod;
	private final LocalDate joinDate;
	private final boolean enable;
	private final List<UserGroupDTO> userGroupDTOList;

	public UserSummary(UserRepresentation userRepresentation, List<GroupRepresentation> groupRepresentationList) {
		this.uid = userRepresentation.getId();
		this.fullName = userRepresentation.getLastName() + userRepresentation.getFirstName();
		this.name = userRepresentation.getUsername();
		this.email = userRepresentation.getEmail();
		this.signUpMethod = null;
		this.joinDate = null;
		this.enable = userRepresentation.isEnabled();
		this.userGroupDTOList = groupRepresentationList != null ?
			groupRepresentationList.stream().filter(groupRepresentation -> !groupRepresentation.getName().contains("ws")).map(groupRepresentation ->
				UserGroupDTO.builder().groupId(groupRepresentation.getId()).groupName(groupRepresentation.getName()).build()).toList() : new ArrayList<>();
	}

	@Builder
	@Getter
	public static class UserGroupDTO {
		private String groupId;
		private String groupName;
	}
}