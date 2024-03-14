package com.xiilab.moduleuser.dto;

import com.xiilab.moduleuser.enums.UserCreatedAt;
import com.xiilab.moduleuser.enums.UserEnable;
import com.xiilab.moduleuser.enums.UserSort;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class UserSearchCondition {
	private String searchText;
	private UserSort userSort;

	public UserSearchCondition(String searchText, UserSort userSort) {
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.userSort = userSort == null ? UserSort.CREATED_AT_ASC : userSort;

	}
}
