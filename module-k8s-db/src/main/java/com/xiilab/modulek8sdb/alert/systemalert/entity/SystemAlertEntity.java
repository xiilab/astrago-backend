package com.xiilab.modulek8sdb.alert.systemalert.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Type;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.alert.enums.AlertEventType;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_SYSTEM_ALERT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
	private AlertType alertType;
	@Enumerated(EnumType.STRING)
	@Column(name = "ALERT_EVENT_TYPE")
	private AlertEventType alertEventType;
	@Enumerated(EnumType.STRING)
	@Column(name = "READ_YN")
	private ReadYN readYN;

	@Enumerated(EnumType.STRING)
	@Column(name = "ALERT_ROLE")
	private AlertRole alertRole;

	@Type(JsonType.class)
	@Column(name = "PAGE_NAVI_PARAM", columnDefinition = "VARCHAR(1000)")
	private PageNaviParam pageNaviParam;

	@Builder
	public SystemAlertEntity(RegUser regUser, LocalDateTime regDate,
		LocalDateTime modDate, Long id, String title, String message, String recipientId, String senderId,
		AlertType alertType, AlertEventType alertEventType, ReadYN readYN, AlertRole alertRole, PageNaviParam pageNaviParam) {
		super(regUser, regDate, modDate);
		this.id = id;
		this.title = title;
		this.message = message;
		this.recipientId = recipientId;
		this.senderId = senderId;
		this.alertType = alertType;
		this.alertEventType = alertEventType;
		this.readYN = readYN;
		this.alertRole = alertRole;
		this.regDate = regDate;
		this.modDate = modDate;
		this.pageNaviParam = pageNaviParam;
	}

	public void readAlert(){
		this.readYN = ReadYN.Y;
	}
}
