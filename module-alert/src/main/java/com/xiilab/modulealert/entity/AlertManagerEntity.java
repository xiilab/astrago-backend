package com.xiilab.modulealert.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulealert.dto.AlertManagerDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ALERT_MANAGER")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class AlertManagerEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALERT_MANAGER_ID")
	private Long id;
	@Column(name = "ALERT_NAME")
	private String alertName;
	@Column(name = "EMAIL_YN")
	private boolean emailYN;
	@Column(name = "SYSTEM_YN")
	private boolean systemYN;
	@Builder.Default
	@OneToMany(mappedBy = "alertManager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<AlertManagerCategoryEntity> alertManagerCategoryEntityList = new ArrayList<>(); // 알림 받을 category List
	@Builder.Default
	@OneToMany(mappedBy = "alertManager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<AlertManagerUserEntity> alertManagerUserEntityList = new ArrayList<>(); // 알림 받을 user List
	@Builder.Default
	@OneToMany(mappedBy = "alertManager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<AlertManagerNodeEntity> alertManagerNodeEntityList = new ArrayList<>(); // 알림 받을 node List
	@Builder.Default
	@OneToMany(mappedBy = "alertManager", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<AlertManagerReceiveEntity> alertList = new ArrayList<>(); // AlertManager에서 발생한 alert List

	public void addCategory(List<AlertManagerDTO.CategoryDTO> categoryDTODTOList){
		if(Objects.nonNull(categoryDTODTOList)){
			List<AlertManagerCategoryEntity> alertManagerCategoryEntityList = categoryDTODTOList.stream().map(categoryDTO ->
				AlertManagerCategoryEntity.builder()
					.operator(categoryDTO.getOperator())
					.alertManagerCategoryType(categoryDTO.getCategoryType())
					.durationTime(categoryDTO.getDurationTime())
					.maximum(categoryDTO.getMaximum())
					.alertManager(this)
					.build()).toList();

			this.alertManagerCategoryEntityList.addAll(alertManagerCategoryEntityList);
		}
	}
	public void addUser(List<AlertManagerDTO.UserDTO> userDTOList){
		if(Objects.nonNull(userDTOList)){
			List<AlertManagerUserEntity> userEntityList = userDTOList.stream().map(userDTO ->
				AlertManagerUserEntity.builder()
					.userId(userDTO.getUserId())
					.userName(userDTO.getUserName())
					.firstName(userDTO.getFirstName())
					.lastName(userDTO.getLastName())
					.email(userDTO.getEmail())
					.alertManager(this).build()).toList();
			this.alertManagerUserEntityList.addAll(userEntityList);
		}
	}

	public void addNode(List<AlertManagerDTO.NodeDTO> nodeDTOList){
		if(Objects.nonNull(nodeDTOList)){
			List<AlertManagerNodeEntity> nodeEntityList = nodeDTOList.stream().map(nodeDTO ->
				AlertManagerNodeEntity.builder()
					.nodeName(nodeDTO.getNodeName())
					.nodeIp(nodeDTO.getNodeIp())
					.alertManager(this).build()).toList();
			this.alertManagerNodeEntityList.addAll(nodeEntityList);
		}
	}

	public void updateAlertManager(AlertManagerDTO.RequestDTO requestDTO){
		this.alertName = requestDTO.getAlertName();
		this.emailYN = requestDTO.isEmailYN();
		this.systemYN = requestDTO.isSystemYN();
	}
}
