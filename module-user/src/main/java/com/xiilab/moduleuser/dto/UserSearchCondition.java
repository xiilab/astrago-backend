package com.xiilab.moduleuser.dto;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.moduleuser.enums.UserSort;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSearchCondition {
	private String searchText;
	private UserSort userSort;
	private AuthType userAuth;

	public UserSearchCondition(String searchText, UserSort userSort) {
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.userSort = userSort == null ? UserSort.CREATED_AT_ASC : userSort;
		this.userAuth = userAuth == null ? AuthType.ROLE_ADMIN : AuthType.ROLE_USER;
	}
}
