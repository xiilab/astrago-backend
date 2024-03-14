package com.xiilab.moduleuser.dto;

import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AddWorkspaceUsersDTO {
	private List<String> userIds;
	private List<String> groupIds;


}
