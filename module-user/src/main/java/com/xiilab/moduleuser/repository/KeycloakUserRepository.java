package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserRepository implements UserRepository {
	private final KeycloakConfig keycloakConfig;

	@Override
	public UserInfo joinUser(UserReqVO userReqVO) {
		Response response = keycloakConfig.getRealmClient().users().create(userReqVO.convertUserRep());
		if (response.getStatus() != 200 && response.getStatus() != 201) {
			throw new IllegalArgumentException(response.getStatusInfo().getReasonPhrase());
		}
		log.info(response.getStatusInfo().getReasonPhrase());
		UserRepresentation userRep = getUserByUsername(userReqVO.getUsername());
		UserResource userResource = getUserResourceById(userRep.getId());
		userResource.resetPassword(userReqVO.createCredentialRep());
		return new UserInfo(userResource.toRepresentation());
	}

	@Override
	public List<UserSummary> getUserList() {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> userList = realmClient.users().list().stream().filter(user
				-> user.getAttributes() != null
				&& user.getAttributes().containsKey("approvalYN")
				&& user.getAttributes().containsValue(List.of("true")))
			.toList();
		return userList.stream().map(UserSummary::new).toList();
	}

	@Override
	public UserInfo getUserInfoById(String userId) {
		UserResource userResource = getUserResourceById(userId);
		List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
		RoleRepresentation roleRepresentation = roleRepresentations.stream()
			.filter(role -> role.getName().contains("ROLE_"))
			.toList()
			.get(0);
		List<GroupRepresentation> groupList = userResource.groups();
		UserRepresentation userRepresentation = userResource.toRepresentation();
		userRepresentation.setRealmRoles(List.of(roleRepresentation.getName()));
		return new UserInfo(userRepresentation, groupList);
	}

	@Override
	public List<UserSummary> getUserListSearchByAttribute(String attribute) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> userList = realmClient.users().searchByAttributes(attribute);
		return userList.stream().map(UserSummary::new).toList();
	}

	@Override
	public void updateUserAttribute(String userId, Map<String, String> map) {
		UserResource userResource = getUserResourceById(userId);
		UserRepresentation representation = userResource.toRepresentation();
		Map<String, List<String>> attributes = representation.getAttributes();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			attributes.put(key, List.of(value));
		}
		userResource.update(representation);
	}

	@Override
	public void updateUserActivationYN(String userId, boolean activationYN) {
		UserResource userResource = getUserResourceById(userId);
		UserRepresentation representation = userResource.toRepresentation();
		representation.setEnabled(activationYN);
		userResource.update(representation);
	}

	@Override
	public void deleteUserById(String userId) {
		getUserResourceById(userId).remove();
	}

	@Override
	public void updateUserRole(String userId, AuthType authType) {
		UserResource userResource = getUserResourceById(userId);
		RoleRepresentation roleRepresentation = getRolerepByName(authType.name());
		userResource.roles().realmLevel().add(List.of(roleRepresentation));
	}

	@Override
	public void joinGroup(String groupId, String userId) {
		UserResource userResource = getUserResourceById(userId);
		userResource.joinGroup(groupId);
	}

	private UserResource getUserResourceById(String userId) {
		return keycloakConfig.getRealmClient().users().get(userId);
	}

	private UserRepresentation getUserByUsername(String username) {
		return keycloakConfig.getRealmClient().users().search(username).get(0);
	}

	private RoleRepresentation getRolerepByName(String roleName) {
		return keycloakConfig.getRealmClient().roles().get(roleName).toRepresentation();
	}
}
