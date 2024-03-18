package com.xiilab.modulek8sdb.alert.systemalert.entity;

import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_WORKSPACE_ALERT_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WorkspaceAlertMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WORKSPACE_ALERT_SETTING_ID")
	private Long workspaceAlertSetId;

	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;

	@Column(name = "USER_ID")
	private String userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALERT_ID")
	private AlertEntity alert;

	@Enumerated(EnumType.STRING)
	@Column(name = "SYSTEM_ALERT_STATUS")
	private AlertStatus systemAlertStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "EMAIL_ALERT_STATUS")
	private AlertStatus emailAlertStatus;

	@Builder
	public WorkspaceAlertMappingEntity(String workspaceResourceName, String userId,
		AlertEntity alert,
		AlertStatus systemAlertStatus, AlertStatus emailAlertStatus) {
		this.workspaceResourceName = workspaceResourceName;
		this.userId = userId;
		this.alert = alert;
		this.systemAlertStatus = systemAlertStatus;
		this.emailAlertStatus = emailAlertStatus;
	}
}
