package com.xiilab.modulealert.entity;

import java.time.LocalDateTime;

import com.xiilab.modulealert.enumeration.AlertManagerCategoryType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT_MANAGER_RECEIVE_ENTITY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlertManagerReceiveEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nodeName; // 발생 노드 이름
	private String alertName; // 발생 알림 이름
	@Enumerated(EnumType.STRING)
	private AlertManagerCategoryType categoryType; // categoryType
	private String threshold; // 임계값
	private LocalDateTime realTime; // 발생 시간 localDateTime
	private String currentTime; // 발생 시간 ex) 3월 7 화요일 오후 12:18
	private String nodeIp;
	@ManyToOne
	private AlertManagerEntity alertManager;

}
