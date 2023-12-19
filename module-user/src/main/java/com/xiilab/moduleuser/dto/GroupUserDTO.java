package com.xiilab.moduleuser.dto;

import org.keycloak.representations.idm.UserRepresentation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupUserDTO {
	private String id;
	private String name;
	private String email;

	public GroupUserDTO(UserRepresentation userRep) {
		this.id = userRep.getId();
		this.name = userRep.getUsername();
		this.email = userRep.getEmail();
	}
}
