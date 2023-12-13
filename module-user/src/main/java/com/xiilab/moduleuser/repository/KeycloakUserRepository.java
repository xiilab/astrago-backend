package com.xiilab.moduleuser.repository;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeycloakUserRepository implements UserRepository {
	private final KeycloakConfig keycloakConfig;
	@Value("${keycloak.realm}")
	private String realm;

	@Override
	public List<UserSummary> getUserList() {
		try (Keycloak keycloakClient = keycloakConfig.getKeycloakClient()) {
			List<UserRepresentation> list = keycloakClient.realm(realm).users().list();
			return list.stream().map(UserSummary::new).toList();
		}
	}

	// public UserInfo getUserById(String id) {
	// 	try (Keycloak keycloakClient = keycloakConfig.getKeycloakClient()) {
	// 		keycloakClient.realm(realm).users().get(id);
	// 	}
	// }

	@Override
	public UserInfo getUserByUserName(String username) {
		try (Keycloak keycloakClient = keycloakConfig.getKeycloakClient()) {
			UserRepresentation userRepresentation = keycloakClient.realm(realm).users().search(username).get(0);
			return new UserInfo(userRepresentation);
		}
	}
}
