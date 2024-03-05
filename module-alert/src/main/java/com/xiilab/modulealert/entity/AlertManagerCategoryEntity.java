package com.xiilab.modulealert.entity;

import com.xiilab.modulealert.enumeration.AlertManagerCategoryType;

import jakarta.persistence.Column;
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

@Entity(name = "TB_ALERT_MANAGER_CATEGORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlertManagerCategoryEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_MANAGER_CATEGORY_ID")
	private Long id;
	@Enumerated(EnumType.STRING)
	private AlertManagerCategoryType alertManagerCategoryType; // item 항목
	private String operator; // 연산자
	private String maximum; // 한계점
	private String durationTime; // 지속시간
	@ManyToOne
	private AlertManagerEntity alertManager;


}
