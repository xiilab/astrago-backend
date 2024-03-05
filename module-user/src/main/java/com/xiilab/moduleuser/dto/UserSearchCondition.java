package com.xiilab.moduleuser.dto;

import com.xiilab.moduleuser.enums.UserCreatedAt;
import com.xiilab.moduleuser.enums.UserEnable;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class UserSearchCondition {
	private String searchText;
	private UserCreatedAt createdAt;
	private UserEnable userEnable;

	public UserSearchCondition(String searchText, UserCreatedAt createdAt, UserEnable userEnable) {
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.createdAt = createdAt == null ? UserCreatedAt.DESC : createdAt;
		this.userEnable =  userEnable == null ? null : userEnable;
	}
}
