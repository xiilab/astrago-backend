package com.xiilab.modulek8s.workspace.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceReqVO;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceDTO {
	@Getter
	@AllArgsConstructor
	public static class RequestDTO {
		private String name;
		private String description;
		private LocalDateTime createAt;
		private String creatorName;
		private String creatorId;

		public WorkspaceReqVO convertToVO() {
			return WorkspaceReqVO.builder()
				.name(this.name)
				.description(this.description)
				.createdAt(createAt)
				.creatorUserName(creatorName)
				.creatorId(creatorId)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateDTO {
		private String name;
		private String description;
	}

	@Getter
	@AllArgsConstructor
	public static class ResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private String creatorId;
		private String creatorName;
		private LocalDateTime createdAt;
	}

	@Getter
	public static class TotalResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private boolean isPinYN;
		private AgeDTO age;
		private ModuleWorkloadResDTO recentlyWorkload;

		public TotalResponseDTO(String id, String name, String resourceName, String description, boolean isPinYN,
			LocalDateTime createdTime,
			ModuleWorkloadResDTO recentlyWorkload) {
			this.id = id;
			this.name = name;
			this.resourceName = resourceName;
			this.description = description;
			this.isPinYN = isPinYN;
			this.age = new AgeDTO(createdTime);
			this.recentlyWorkload = recentlyWorkload;
		}
	}
}
