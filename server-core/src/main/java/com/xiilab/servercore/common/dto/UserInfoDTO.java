package com.xiilab.servercore.common.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.SignUpMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoDTO {
	private String id;
	private String userName;
	private String email;
	private LocalDateTime joinDate;
	private SignUpMethod signUpMethod;
	private AuthType auth;
	private List<String> groups;
	private List<String> workspaces;
	private String userRealName;

	public Set<String> getWorkspaceList() {
		if (workspaces != null && !workspaces.isEmpty()) {
			return workspaces.stream().map(group -> group.split("/")[0]).collect(Collectors.toSet());
		} else {
			return new HashSet<>();
		}
	}

	public Map<String, String> getUserWorkspace() {
		if (workspaces != null && !workspaces.isEmpty()) {
			return workspaces.stream().map(workspace -> workspace.split("/")).collect(Collectors.toMap(
				v -> v[0],
				v -> v[1]
			));
		} else {
			return new HashMap<>();
		}
	}
}
