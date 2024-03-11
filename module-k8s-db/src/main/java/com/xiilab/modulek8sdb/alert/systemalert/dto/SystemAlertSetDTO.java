package com.xiilab.modulek8sdb.alert.systemalert.dto;

import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SystemAlertSetDTO {

	private String workspaceName;
	private boolean workloadStartAlert;
	private boolean workloadEndAlert;
	private boolean workloadErrorAlert;
	private boolean resourceApprovalAlert;

	@Getter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseDTO extends SystemAlertSetDTO {
		private Long id;

		public static ResponseDTO convertResponseDTO(SystemAlertSetEntity systemAlertSetEntity){
			return ResponseDTO.builder()
				.id(systemAlertSetEntity.getId())
				.workspaceName(systemAlertSetEntity.getWorkspaceName())
				.workloadStartAlert(systemAlertSetEntity.isWorkloadStartAlert())
				.workloadEndAlert(systemAlertSetEntity.isWorkloadEndAlert())
				.workloadErrorAlert(systemAlertSetEntity.isWorkloadErrorAlert())
				.resourceApprovalAlert(systemAlertSetEntity.isResourceApprovalAlert())
				.build();
		}
	}

}
