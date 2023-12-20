package com.xiilab.servercore.common.dto;

import java.time.LocalDateTime;
import java.util.List;

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
}
