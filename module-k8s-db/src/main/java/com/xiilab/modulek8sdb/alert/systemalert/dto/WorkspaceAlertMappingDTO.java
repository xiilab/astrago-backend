package com.xiilab.modulek8sdb.alert.systemalert.dto;

import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.AlertType;

import lombok.Getter;

@Getter
public class WorkspaceAlertMappingDTO {
	private Long workspaceAlertMappingId;
	private String alertName;
	private AlertStatus systemAlertStatus;
	private AlertStatus emailAlertStatus;
	private AlertType alertType;

	public WorkspaceAlertMappingDTO(WorkspaceAlertMappingEntity workspaceAlertMappingEntity) {
		this.workspaceAlertMappingId = workspaceAlertMappingEntity.getWorkspaceAlertMappingId();
		this.alertName = workspaceAlertMappingEntity.getAlert().getAlertName();
		this.systemAlertStatus = workspaceAlertMappingEntity.getSystemAlertStatus();
		this.emailAlertStatus = workspaceAlertMappingEntity.getEmailAlertStatus();
		this.alertType = workspaceAlertMappingEntity.getAlert().getAlertType();
	}
}
