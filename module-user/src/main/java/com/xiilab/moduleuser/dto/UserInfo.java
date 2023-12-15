package com.xiilab.moduleuser.dto;

import java.time.LocalDate;
import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
	private String id;
	private String userName;
	private String email;
	private LocalDate joinDate;
	private SignUpMethod signUpMethod;
	private AuthType auth;
	private List<String> groups;
	private List<String> workspaces;

	public UserInfo(UserRepresentation userRep, List<GroupRepresentation> groupReps) {
		this.id = userRep.getId();
		this.userName = userRep.getUsername();
		this.email = userRep.getEmail();
		this.joinDate = null;
		this.signUpMethod = null;
		this.auth = userRep.getRealmRoles() != null ? AuthType.valueOf(userRep.getRealmRoles().get(0)) : null;
		this.groups = groupReps.stream()
			.filter(group -> group.getPath().contains("/account/"))
			.map(GroupRepresentation::getName)
			.toList();
		this.workspaces = groupReps.stream()
			.filter(group -> group.getPath().contains("/ws/"))
			.map(group -> group.getPath().split("/ws/")[1])
			.toList();
	}

	public UserInfo(UserRepresentation userRep) {
		this.id = userRep.getId();
		this.userName = userRep.getUsername();
		this.email = userRep.getEmail();
		this.joinDate = null;
		this.signUpMethod = null;
		this.auth = userRep.getRealmRoles() != null ? AuthType.valueOf(userRep.getRealmRoles().get(0)) : null;
	}
}
