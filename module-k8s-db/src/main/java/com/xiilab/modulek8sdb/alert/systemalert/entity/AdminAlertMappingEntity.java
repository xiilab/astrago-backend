package com.xiilab.modulek8sdb.alert.systemalert.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@DynamicUpdate
@Entity(name = "TB_ADMIN_ALERT_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdminAlertMappingEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ADMIN_ALERT_MAPPING_ID")
	private Long adminAlertMappingId;

	@Column(name = "ADMIN_ID")
	private String adminId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALERT_ID")
	private AlertEntity alert;

	@Enumerated(EnumType.STRING)
	@Column(name = "SYSTEM_ALERT_STATUS")
	private AlertStatus systemAlertStatus;

	@Enumerated(EnumType.STRING)
	@Column(name = "EMAIL_ALERT_STATUS")
	private AlertStatus emailAlertStatus;

	@Builder(builderClassName = "saveBuilder", builderMethodName = "saveBuilder")
	public AdminAlertMappingEntity(Long adminAlertMappingId, String adminId, AlertEntity alert,
		AlertStatus systemAlertStatus, AlertStatus emailAlertStatus) {
		this.adminAlertMappingId = adminAlertMappingId;
		this.adminId = adminId;
		this.alert = alert;
		this.systemAlertStatus = systemAlertStatus;
		this.emailAlertStatus = emailAlertStatus;
	}

	public void updateAlertMappingEntity(AlertStatus emailAlertStatus, AlertStatus systemAlertStatus) {
		this.emailAlertStatus = emailAlertStatus;
		this.systemAlertStatus = systemAlertStatus;
	}
}
