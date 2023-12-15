package com.xiilab.moduleuser.vo;

import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.xiilab.moduleuser.dto.AuthType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReqVO {
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	private String password;

	public UserRepresentation convertUserRep() {
		UserRepresentation userRepresentation = new UserRepresentation();
		userRepresentation.setFirstName(this.firstName);
		userRepresentation.setLastName(this.lastName);
		userRepresentation.setEmail(this.email);
		userRepresentation.setUsername(username);
		userRepresentation.setEnabled(false);
		userRepresentation.setAttributes(Map.of("approvalYN", List.of(String.valueOf(false))));
		userRepresentation.setRealmRoles(List.of(AuthType.ROLE_USER.name()));
		return userRepresentation;
	}

	public CredentialRepresentation createCredentialRep() {
		CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
		credentialRepresentation.setValue(this.password);
		credentialRepresentation.setTemporary(false);
		return credentialRepresentation;
	}
}
