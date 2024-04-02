package com.xiilab.moduleuser.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.AuthType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserInfo {
	private String id;
	private String userName;
	private String email;
	private LocalDateTime joinDate;
	private SignUpMethod signUpMethod;
	private AuthType auth;
	private List<String> groups;
	private List<String> workspaces;
	private String firstName;
	private String lastName;

	public UserInfo(UserRepresentation userRep, List<GroupRepresentation> groupReps) {
		this.id = userRep.getId();
		this.userName = userRep.getUsername();
		this.firstName = userRep.getFirstName();
		this.lastName = userRep.getLastName();
		this.email = userRep.getEmail();
		this.joinDate = convertUnixToLocalDateTime(userRep.getCreatedTimestamp());
		this.signUpMethod = null;
		this.auth = getUserRole(userRep.getRealmRoles());
		if (!CollectionUtils.isEmpty(groupReps)) {
			this.groups = groupReps.stream()
				.filter(group -> group.getPath().contains("/account/"))
				.map(GroupRepresentation::getName)
				.toList();
			this.workspaces = groupReps.stream()
				.filter(group -> group.getPath().contains("/ws/"))
				.map(group -> group.getPath().split("/ws/")[1])
				.toList();
		}
	}

	public UserInfo(UserRepresentation userRep) {
		this.id = userRep.getId();
		this.userName = userRep.getUsername();
		this.email = userRep.getEmail();
		this.joinDate = convertUnixToLocalDateTime(userRep.getCreatedTimestamp());
		this.signUpMethod = null;
		this.auth = getUserRole(userRep.getRealmRoles());
	}

	private LocalDateTime convertUnixToLocalDateTime(Long unixStr) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(unixStr),
			TimeZone.getDefault().toZoneId());
	}

	private AuthType getUserRole(List<String> roles) {
		//admin이면 admin 권한 넣고 user는 user넣고
		return roles != null && roles.contains(AuthType.ROLE_ADMIN.name()) ? AuthType.ROLE_ADMIN : AuthType.ROLE_USER;
	}
}
