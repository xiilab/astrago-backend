package com.xiilab.modulek8sdb.alert.systemalert.entity;

import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AlertEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_ID")
	private Long alertId;
	@Column(name = "ALERT_NAME")
	private String alertName;
	@Enumerated(EnumType.STRING)
	@Column(name = "ALERT_TYPE")
	private SystemAlertType alertType;
	@Column(name = "ALERT_ROLE")
	@Enumerated(EnumType.STRING)
	private AlertRole alertRole;

	@Builder
	public AlertEntity(String alertName, SystemAlertType alertType, String alertTitle,
		String alertContent,
		AlertRole alertRole) {
		this.alertName = alertName;
		this.alertType = alertType;
		this.alertRole = alertRole;
	}
}
