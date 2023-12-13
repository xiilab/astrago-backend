package com.xiilab.moduleuser;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.repository.UserRepository;

@SpringBootTest
class TestCode {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private KeycloakConfig keycloakConfig;

	@Test
	void Test() {
		List<UserSummary> userList = userRepository.getUserList();
		System.out.println(userList.size());
	}

	@Test
	void asdfasdf() {
		Keycloak keycloakClient = keycloakConfig.getKeycloakClient();
		List<UserRepresentation> list = keycloakClient.realm("myrealm").users().list();
		list.get(1);
	}
}
