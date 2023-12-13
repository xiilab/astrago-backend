package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
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

	@Override
	public List<UserSummary> getUserList() {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> userList = realmClient.users().list();
		return userList.stream().map(UserSummary::new).toList();

	}

	@Override
	public UserInfo getUserByUserName(String userName) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		UserRepresentation userRepresentation = realmClient.users().search(userName).get(0);
		return new UserInfo(userRepresentation);

	}

	@Override
	public List<UserSummary> getWaitingApprovalUserList() {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> userList = realmClient.users().searchByAttributes("approvalYN");
		return userList.stream().map(UserSummary::new).toList();
	}

	@Override
	public void updateUserActivation(String userId, boolean activationYN) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		UserResource userResource = realmClient.users().get(userId);
		UserRepresentation representation = userResource.toRepresentation();
		representation.setEnabled(activationYN);
		userResource.update(representation);
	}

	@Override
	public void updateUserApproval(String userId, boolean approvalYN) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		UserResource userResource = realmClient.users().get(userId);
		UserRepresentation representation = userResource.toRepresentation();
		Map<String, List<String>> attributes = representation.getAttributes();
		attributes.put("approvalYN",List.of(String.valueOf(approvalYN)));
		representation.setAttributes(attributes);
		userResource.update(representation);
	}
}
