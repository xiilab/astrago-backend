package com.xiilab.servercore.common.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
	private String email;
	private LocalDateTime joinDate;
	private SignUpMethod signUpMethod;
	private AuthType auth;
	private List<String> groups;
	private List<String> workspaces;
	private String userName;
	private String userFullName;

	public Set<String> getWorkspaceList(boolean isMyWorkspace) {
		if (workspaces != null && !workspaces.isEmpty()) {
			if (isMyWorkspace) {
				//내가 생성한 워크스페이스만 리턴
				return getMyWorkspace();
			} else {
				//내가 생성했거나, 멤버로 속해있는 워크스페이스 리턴
				return workspaces.stream().map(group -> group.split("/")[0]).collect(Collectors.toSet());
			}
		} else {
			return new HashSet<>();
		}
	}

	private Set<String> getMyWorkspace() {
		return workspaces.stream()
			.filter(ws -> ws.contains("/owner"))
			.map(group -> group.split("/")[0])
			.collect(Collectors.toSet());
	}

	public boolean isMyWorkspace(String workspaceName){
		List<String> workspaces = this.workspaces.stream()
			.filter(ws -> ws.contains("/owner"))
			.map(group -> group.split("/")[0])
			.toList();
		return workspaces.contains(workspaceName);
	}
}
