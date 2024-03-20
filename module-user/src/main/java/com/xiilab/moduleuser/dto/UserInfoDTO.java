package com.xiilab.moduleuser.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// import com.xiilab.modulecommon.enums.AuthType;
// import com.xiilab.moduleuser.dto.SignUpMethod;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.WorkspaceRole;

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
		if(auth == AuthType.ROLE_ADMIN){
			return true;
		}
		if(workspaces == null){
			return false;
		}
		List<String> workspaces = this.workspaces.stream()
			.filter(ws -> ws.contains("/owner"))
			.map(group -> group.split("/")[0])
			.toList();
		return workspaces.contains(workspaceName);
	}
	//workspace 접근권한 체크
	public boolean isAccessAuthorityWorkspace(String workspaceName){
		if(auth == AuthType.ROLE_ADMIN){
			return true;
		}
		if(workspaces == null){
			return false;
		}
		List<String> workspaces = this.workspaces.stream()
			.filter(ws -> ws.contains("/owner") || ws.contains("/user"))
			.map(group -> group.split("/")[0])
			.toList();
		return workspaces.contains(workspaceName);
	}
	// 일반 유저의 workspace 접근권한 체크
	public boolean isAccessAuthorityWorkspaceNotAdmin(String workspaceName){
		if(workspaces == null){
			return false;
		}
		List<String> workspaces = this.workspaces.stream()
			.filter(ws -> ws.contains("/owner") || ws.contains("/user"))
			.map(group -> group.split("/")[0])
			.toList();
		return workspaces.contains(workspaceName);
	}
	public WorkspaceRole getWorkspaceAuthority(String workspaceName){
		List<String> workspaces = this.workspaces.stream()
			.filter(ws -> ws.contains("/owner"))
			.map(group -> group.split("/")[0])
			.toList();
		return workspaces.contains(workspaceName) ? WorkspaceRole.ROLE_OWNER : WorkspaceRole.ROLE_USER;
	}
}
