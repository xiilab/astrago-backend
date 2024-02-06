package com.xiilab.moduleuser.dto;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GroupUserDTO {
	private String id;
	private String name;
	private String email;
	private String firstName;
	private String lastName;

	public GroupUserDTO(UserRepresentation userRep) {
		this.id = userRep.getId();
		this.name = userRep.getUsername();
		this.email = userRep.getEmail();
		this.firstName = userRep.getFirstName();
		this.lastName = userRep.getLastName();
	}
}
