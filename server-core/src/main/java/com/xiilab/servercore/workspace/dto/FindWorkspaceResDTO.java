package com.xiilab.servercore.workspace.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FindWorkspaceResDTO {
	@Getter
	@Builder
	public static class JoinedWorkspaceDetail {
		private String workspaceName;
		private String workspaceResourceName;
		private LocalDateTime createAt;
		private String description;
		private boolean pinYn;

		public static FindWorkspaceResDTO.JoinedWorkspaceDetail from(WorkspaceTotalDTO workspaceTotalDTO, boolean pinYN) {
			return JoinedWorkspaceDetail.builder()
				.workspaceName(workspaceTotalDTO.getName())
				.workspaceResourceName(workspaceTotalDTO.getResourceName())
				.description(workspaceTotalDTO.getDescription())
				.createAt(workspaceTotalDTO.getCreateAt())
				.pinYn(pinYN)
				.build();
		}
	}
}
