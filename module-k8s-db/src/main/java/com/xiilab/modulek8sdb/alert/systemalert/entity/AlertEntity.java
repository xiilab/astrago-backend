package com.xiilab.modulek8sdb.alert.systemalert.entity;

import com.xiilab.modulecommon.alert.enums.AlertEventType;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT")
@Table(name = "TB_ALERT")
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
	private AlertType alertType;
	@Column(name = "ALERT_ROLE")
	@Enumerated(EnumType.STRING)
	private AlertRole alertRole;

	// @OneToMany(mappedBy = "alert", fetch = FetchType.LAZY)
	// private List<AdminAlertMappingEntity> adminAlertMappingEntities;

	@Column(name = "ALERT_EVENT_TYPE")
	@Enumerated(EnumType.STRING)
	private AlertEventType alertEventType;

	@Builder
	public AlertEntity(String alertName, AlertType alertType,
		AlertRole alertRole,
		AlertEventType alertEventType) {
		this.alertName = alertName;
		this.alertType = alertType;
		this.alertRole = alertRole;
		this.alertEventType = alertEventType;
	}
}
