package com.xiilab.servercore.user.dto;

import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserResDTO {
	@Getter
	@SuperBuilder
	public static class FindResourceUsage extends ResDTO {
		private WorkloadResDTO.UserResourceUsage userResourceUsage;

		public static FindResourceUsage of(WorkloadResDTO.UserResourceUsage userResourceUsage, UserSummary userSummary) {
			return FindResourceUsage.builder()
				.userResourceUsage(userResourceUsage)
				.regUserId(userSummary.getUid())
				.regUserName(userSummary.getName())
				.regUserRealName(userSummary.getFullName())
				.build();
		}
	}
}
