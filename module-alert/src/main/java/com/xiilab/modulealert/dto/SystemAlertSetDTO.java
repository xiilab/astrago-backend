package com.xiilab.modulealert.dto;

import com.xiilab.modulealert.entity.SystemAlertSetEntity;

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
	public static class ResponseDTOSystem extends SystemAlertSetDTO {
		private Long id;

		public static ResponseDTOSystem convertResponseDTO(SystemAlertSetEntity systemAlertSetEntity){
			return ResponseDTOSystem.builder()
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
