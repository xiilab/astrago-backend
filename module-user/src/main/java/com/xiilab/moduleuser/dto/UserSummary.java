package com.xiilab.moduleuser.dto;

import lombok.Getter;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDate;

@Getter
public class UserSummary{
	private final String uid;
	private final String fullName;
	private final String name;
	private final String email;
	private final SignUpMethod signUpMethod;
	private final LocalDate joinDate;
	private final boolean enable;

	public UserSummary(UserRepresentation userRepresentation) {
		this.uid = userRepresentation.getId();
		this.fullName = userRepresentation.getLastName() + userRepresentation.getFirstName();
		this.name = userRepresentation.getUsername();
		this.email = userRepresentation.getEmail();
		this.signUpMethod = null;
		this.joinDate = null;
		this.enable = userRepresentation.isEnabled();
	}
}
