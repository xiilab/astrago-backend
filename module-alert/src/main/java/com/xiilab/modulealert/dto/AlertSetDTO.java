package com.xiilab.modulealert.dto;

import com.xiilab.modulealert.entity.AlertSetEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlertSetDTO {

	private String workspaceName;
	private boolean workloadStartAlert;
	private boolean workloadEndAlert;
	private boolean workloadErrorAlert;
	private boolean resourceApprovalAlert;

	@Getter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends AlertSetDTO {
		private Long id;

		public static ResponseDTO convertResponseDTO(AlertSetEntity alertSetEntity){
			return ResponseDTO.builder()
				.id(alertSetEntity.getId())
				.workspaceName(alertSetEntity.getWorkspaceName())
				.workloadStartAlert(alertSetEntity.isWorkloadStartAlert())
				.workloadEndAlert(alertSetEntity.isWorkloadEndAlert())
				.workloadErrorAlert(alertSetEntity.isWorkloadErrorAlert())
				.resourceApprovalAlert(alertSetEntity.isResourceApprovalAlert())
				.build();
		}
	}

	public static AlertSetEntity convertEntity(AlertSetDTO alertSetDTO){
		return AlertSetEntity.builder()
			.workspaceName(alertSetDTO.getWorkspaceName())
			.workloadStartAlert(alertSetDTO.isWorkloadStartAlert())
			.workloadEndAlert(alertSetDTO.isWorkloadEndAlert())
			.workloadErrorAlert(alertSetDTO.isWorkloadErrorAlert())
			.resourceApprovalAlert(alertSetDTO.isResourceApprovalAlert())
			.build();
	}


}
