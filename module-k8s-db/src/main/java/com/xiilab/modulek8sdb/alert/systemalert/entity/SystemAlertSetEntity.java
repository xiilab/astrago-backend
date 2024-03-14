package com.xiilab.modulek8sdb.alert.systemalert.entity;

import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_SYSTEM_ALERT_SETTING")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class SystemAlertSetEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "LICENSE_SYSTEM_YN")
	private boolean licenseSystemYN;
	@Column(name = "LICENSE_EMAIL_YN")
	private boolean licenseEmailYN;
	@Column(name = "USER_SYSTEM_YN")
	private boolean userSystemYN;
	@Column(name = "USER_EMAIL_YN")
	private boolean userEmailYN;
	@Column(name = "NODE_SYSTEM_YN")
	private boolean nodeSystemYN;
	@Column(name = "NODE_EMAIL_YN")
	private boolean nodeEmailYN;
	@Column(name = "WORKSPACE_PRODUCE_SYSTEM_YN")
	private boolean wsProduceSystemYN;
	@Column(name = "WORKSPACE_PRODUCE_EMAIL_YN")
	private boolean wsProduceEmailYN;
	@Column(name = "RESOURCE_OVER_SYSTEM_YN")
	private boolean resourceOverSystemYN;
	@Column(name = "RESOURCE_OVER_EMAIL_YN")
	private boolean resourceOverEmailYN;
	@Column(name = "WORKSPACE_RESOURCE_SYSTEM_YN")
	private boolean wsResourceSystemYN;
	@Column(name = "WORKSPACE_RESOURCE_EMAIL_YN")
	private boolean wsResourceEmailYN;


	public void updateSystemAlertSet(SystemAlertSetDTO alertSetDTO){
		this.licenseSystemYN = alertSetDTO.isLicenseSystemYN();
		this.licenseEmailYN = alertSetDTO.isLicenseEmailYN();
		this.userSystemYN = alertSetDTO.isUserSystemYN();
		this.userEmailYN = alertSetDTO.isUserEmailYN();
		this.nodeSystemYN = alertSetDTO.isNodeSystemYN();
		this.nodeEmailYN = alertSetDTO.isNodeEmailYN();
		this.wsProduceSystemYN = alertSetDTO.isWsProduceSystemYN();
		this.wsProduceEmailYN = alertSetDTO.isWsProduceEmailYN();
		this.resourceOverSystemYN = alertSetDTO.isResourceOverSystemYN();
		this.resourceOverEmailYN = alertSetDTO.isResourceOverEmailYN();
		this.wsResourceSystemYN = alertSetDTO.isWsResourceSystemYN();
		this.wsResourceEmailYN = alertSetDTO.isWsResourceEmailYN();
	}
}
