package com.xiilab.modulek8sdb.alert.systemalert.entity;

import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;

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

@Entity(name = "TB_WORKSPACE_ALERT_SETTING")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class WorkspaceAlertSetEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
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

	public WorkspaceAlertSetEntity updateWorkspaceAlertSet(WorkspaceAlertSetDTO workspaceAlertSetDTO){
		this.workloadStartAlert = workspaceAlertSetDTO.isWorkloadStartAlert();
		this.workloadEndAlert = workspaceAlertSetDTO.isWorkloadEndAlert();
		this.workloadErrorAlert = workspaceAlertSetDTO.isWorkloadErrorAlert();
		this.resourceApprovalAlert = workspaceAlertSetDTO.isResourceApprovalAlert();
		return this;
	}
}
