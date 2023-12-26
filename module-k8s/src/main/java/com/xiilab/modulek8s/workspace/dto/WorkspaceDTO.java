package com.xiilab.modulek8s.workspace.dto;

import java.time.LocalDateTime;

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
		private String creator;

		public WorkspaceReqVO convertToVO() {
			return WorkspaceReqVO.builder()
				.name(this.name)
				.description(this.description)
				.createdAt(createAt)
				.creatorName(creatorName)
				.creator(creator)
				.build();
		}
	}

	@Getter
	@AllArgsConstructor
	public static class ResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
	}

}
