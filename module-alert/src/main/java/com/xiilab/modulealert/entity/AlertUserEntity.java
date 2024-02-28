package com.xiilab.modulealert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT_USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlertUserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_USER_ID")
	private Long id;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "USER_NAME")
	private String userName;
	@Column(name = "USER_FIRST_NAME")
	private String firstName;
	@Column(name = "USER_LAST_NAME")
	private String lastName;
	@Column(name = "USER_EMAIL")
	private String email;
	@ManyToOne
	private AlertManagerEntity alertManager;
}
