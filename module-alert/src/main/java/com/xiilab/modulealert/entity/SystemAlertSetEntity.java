package com.xiilab.modulealert.entity;

import com.xiilab.modulealert.dto.SystemAlertSetDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_SYSTEM_ALERT_SETTING")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SystemAlertSetEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_SETTING_ID")
	private Long id;
	@Column(name = "WORKSPACE_NAME")
	private String workspaceName;
	@Column(name = "WORKLOAD_START_ALERT")
	private boolean workloadStartAlert;
	@Column(name = "WORKLOAD_END_ALERT")
	private boolean workloadEndAlert;
	@Column(name = "WORKLOAD_ERROR_ALERT")
	private boolean workloadErrorAlert;
	@Column(name = "RESOURCE_APPROVAL_ALERT")
	private boolean resourceApprovalAlert;

	public SystemAlertSetEntity updateAlertSet(SystemAlertSetDTO systemAlertSetDTO){
		this.workloadStartAlert = systemAlertSetDTO.isWorkloadStartAlert();
		this.workloadEndAlert = systemAlertSetDTO.isWorkloadEndAlert();
		this.workloadErrorAlert = systemAlertSetDTO.isWorkloadErrorAlert();
		this.resourceApprovalAlert = systemAlertSetDTO.isResourceApprovalAlert();
		return this;
	}
}
