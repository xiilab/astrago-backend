package com.xiilab.moduleuser.dto;

import com.xiilab.modulecommon.enums.AuthType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
	private String firstName;
	private String lastName;
	private AuthType auth;
	private String password;
}
