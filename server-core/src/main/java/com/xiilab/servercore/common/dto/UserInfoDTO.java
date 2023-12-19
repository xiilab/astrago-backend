package com.xiilab.servercore.common.dto;

import java.time.LocalDate;
import java.util.List;

import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.SignUpMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoDTO {
	private String id;
	private String userName;
	private String email;
	private LocalDate joinDate;
	private SignUpMethod signUpMethod;
	private AuthType auth;
	private List<String> groups;
	private List<String> workspaces;
	private String userRealName;

	@Builder
	public UserInfoDTO(String id, String userName, String email, LocalDate joinDate, SignUpMethod signUpMethod,
		AuthType auth, List<String> groups, List<String> workspaces, String userRealName) {
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.joinDate = joinDate;
		this.signUpMethod = signUpMethod;
		this.auth = auth;
		this.groups = groups;
		this.workspaces = workspaces;
		this.userRealName = userRealName;
	}

}
