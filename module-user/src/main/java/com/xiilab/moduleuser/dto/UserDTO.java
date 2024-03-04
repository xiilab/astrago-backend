package com.xiilab.moduleuser.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserDTO {


	@Getter
	@Builder
	public static class PageUsersDTO{
		private List<UserSummary> users;
		private int totalCount;
	}
}
