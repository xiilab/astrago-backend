package com.xiilab.moduleuser.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import com.xiilab.modulecommon.enums.AuthType;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSummary{
	private final String uid;
	private final String fullName;
	private final String name;
	private final String email;
	private final SignUpPath signUpPath;
	private final LocalDate joinDate;
	private final String enable;
	private final String approval;
	private final List<UserGroupDTO> userGroupDTOList;
	private AuthType authType;
	private String group;

	public UserSummary(UserRepresentation userRepresentation, List<GroupRepresentation> groupRepresentationList) {
		this.uid = userRepresentation.getId();
		this.fullName = userRepresentation.getLastName() + userRepresentation.getFirstName();
		this.name = userRepresentation.getUsername();
		this.email = userRepresentation.getEmail();
		this.signUpPath = userRepresentation.getAttributes().get("signUpPath") != null ? SignUpPath.valueOf(userRepresentation.getAttributes().get("signUpPath").get(0)) : null;
		// 에포크 시간을 Instant로 변환
		Instant instant = Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp());
		// 특정 시간대에 맞춰 LocalDateTime으로 변환
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		this.joinDate = localDateTime.toLocalDate();
		this.enable = String.valueOf(userRepresentation.isEnabled());
		this.approval = userRepresentation.getAttributes().get("approvalYN").get(0);
		this.userGroupDTOList = groupRepresentationList != null ?
			groupRepresentationList.stream().filter(groupRepresentation -> !groupRepresentation.getName().contains("ws")).map(groupRepresentation ->
				UserGroupDTO.builder().groupId(groupRepresentation.getId()).groupName(groupRepresentation.getName()).build()).toList() : new ArrayList<>();
	}
	public UserSummary(UserRepresentation userRepresentation) {
		this.uid = userRepresentation.getId();
		this.fullName = userRepresentation.getLastName() + userRepresentation.getFirstName();
		this.name = userRepresentation.getUsername();
		this.email = userRepresentation.getEmail();
		this.signUpPath = userRepresentation.getAttributes().get("signUpPath") != null ? SignUpPath.valueOf(userRepresentation.getAttributes().get("signUpPath").get(0)) : null;
		// 에포크 시간을 Instant로 변환
		Instant instant = Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp());
		// 특정 시간대에 맞춰 LocalDateTime으로 변환
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		this.joinDate = localDateTime.toLocalDate();
		this.enable = String.valueOf(userRepresentation.isEnabled());
		this.approval = userRepresentation.getAttributes().get("approvalYN").get(0);
		this.userGroupDTOList = null;
	}
	public void setAuthType(AuthType authType){
		this.authType = authType;
	}
	public void setGroup(String group){
		this.group = group;
	}
	@Builder
	@Getter
	public static class UserGroupDTO {
		private String groupId;
		private String groupName;
	}
}