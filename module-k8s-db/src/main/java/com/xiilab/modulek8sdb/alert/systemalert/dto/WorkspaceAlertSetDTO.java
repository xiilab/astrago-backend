package com.xiilab.modulek8sdb.alert.systemalert.dto;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertSetEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceAlertSetDTO {

	private String workspaceName;
	private boolean workloadStartAlert;
	private boolean workloadEndAlert;
	private boolean workloadErrorAlert;
	private boolean resourceApprovalAlert;

	@Getter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends WorkspaceAlertSetDTO {
		private Long id;

		public static ResponseDTO convertResponseDTO(WorkspaceAlertSetEntity workspaceAlertSetEntity){
			return ResponseDTO.builder()
				.id(workspaceAlertSetEntity.getId())
				.workspaceName(workspaceAlertSetEntity.getWorkspaceName())
				.workloadStartAlert(workspaceAlertSetEntity.isWorkloadStartAlert())
				.workloadEndAlert(workspaceAlertSetEntity.isWorkloadEndAlert())
				.workloadErrorAlert(workspaceAlertSetEntity.isWorkloadErrorAlert())
				.resourceApprovalAlert(workspaceAlertSetEntity.isResourceApprovalAlert())
				.build();
		}
	}

}
