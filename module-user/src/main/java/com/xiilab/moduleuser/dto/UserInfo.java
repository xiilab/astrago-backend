package com.xiilab.moduleuser.dto;

import java.time.LocalDate;
import java.util.List;

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

	public UserInfo(UserRepresentation userRepresentation) {
		this.id = userRepresentation.getId();
		this.userName = userRepresentation.getUsername();
		this.email = userRepresentation.getEmail();
		this.joinDate = null;
		this.signUpMethod = null;
		this.auth = AuthType.valueOf(userRepresentation.getRealmRoles().get(0));
		this.groups = userRepresentation.getGroups();
		this.workspaces = userRepresentation.getGroups();
	}
}
