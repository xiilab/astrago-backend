package com.xiilab.modulek8sdb.alert.systemalert.entity;

import java.time.LocalDateTime;

import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertEventType;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;

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

@Entity(name = "TB_SYSTEM_ALERT")
// @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// @AllArgsConstructor
@Getter
public class SystemAlertEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_ID")
	private Long id;
	@Column(name = "TITLE")
	private String title;
	@Column(name = "MESSAGE")
	private String message;
	@Column(name = "RECIPIENT_ID")
	private String recipientId;
	@Column(name = "SENDER_ID")
	private String senderId;
	@Enumerated(EnumType.STRING)
	@Column(name = "ALERT_TYPE")
	private SystemAlertType systemAlertType;
	@Enumerated(EnumType.STRING)
	@Column(name = "ALERT_EVENT_TYPE")
	private SystemAlertEventType systemAlertEventType;
	@Enumerated(EnumType.STRING)
	@Column(name = "READ_YN")
	private ReadYN readYN;

	@Builder
	public SystemAlertEntity(RegUser regUser, LocalDateTime regDate,
		LocalDateTime modDate, Long id, String title, String message, String recipientId, String senderId,
		SystemAlertType systemAlertType, SystemAlertEventType systemAlertEventType, ReadYN readYN) {
		super(regUser, regDate, modDate);
		this.id = id;
		this.title = title;
		this.message = message;
		this.recipientId = recipientId;
		this.senderId = senderId;
		this.systemAlertType = systemAlertType;
		this.systemAlertEventType = systemAlertEventType;
		this.readYN = readYN;
	}

	public void readAlert(){
		this.readYN = ReadYN.Y;
	}
}
