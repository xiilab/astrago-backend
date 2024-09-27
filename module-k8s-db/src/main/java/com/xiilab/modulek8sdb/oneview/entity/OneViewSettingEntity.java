package com.xiilab.modulek8sdb.oneview.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "TB_ONEVIEW_SETTING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OneViewSettingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ONEVIEW_ID")
	private Long id;

	@Column(name = "API_SERVER_ADDRESS")
	private String apiServerAddress;

	@Column(name = "USER_NAME")
	private String userName;

	@Column(name = "PASSWORD")
	private String password;

	@Column(name = "API_VERSION")
	private Integer apiVersion;

	@Column(name = "CONNECTION_FAILED_COUNT")
	private Integer connectionFailedCount;

	public void updateConnectionFailed(Integer connectionFailedCount) {
		this.connectionFailedCount = connectionFailedCount;
	}
}
