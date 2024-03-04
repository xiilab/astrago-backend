package com.xiilab.moduleuser.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.AuthType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDTO {

	@Getter
	@Builder
	public static class PageUsersDTO {
		private List<UserSummary> users;
		private int totalCount;
	}

	@Getter
	public static class UserInfo {
		private String id;
		private String userName;
		private String email;
		private LocalDate joinDate;
		private SignUpPath signUpPath;
		private AuthType auth;
		private String enable;
		private String approval;
		private List<String> groups;
		private List<String> workspaces;
		private String firstName;
		private String lastName;

		public UserInfo(UserRepresentation userRepresentation, List<GroupRepresentation> groupReps) {
			this.id = userRepresentation.getId();
			this.userName = userRepresentation.getUsername();
			this.firstName = userRepresentation.getFirstName();
			this.lastName = userRepresentation.getLastName();
			this.email = userRepresentation.getEmail();
			this.signUpPath = userRepresentation.getAttributes().get("signUpPath") != null ? SignUpPath.valueOf(userRepresentation.getAttributes().get("signUpPath").get(0)) : null;
			// 에포크 시간을 Instant로 변환
			Instant instant = Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp());
			// 특정 시간대에 맞춰 LocalDateTime으로 변환
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			this.joinDate = localDateTime.toLocalDate();
			this.enable = String.valueOf(userRepresentation.isEnabled());
			this.approval = userRepresentation.getAttributes().get("approvalYN").get(0);
			this.auth = getUserRole(userRepresentation.getRealmRoles());
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

		private AuthType getUserRole(List<String> roles) {
			return roles != null ? AuthType.valueOf(roles.get(0)) : null;
		}
	}
}
