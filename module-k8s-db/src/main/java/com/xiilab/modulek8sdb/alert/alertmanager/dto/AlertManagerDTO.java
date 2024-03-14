package com.xiilab.modulek8sdb.alert.alertmanager.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerCategoryEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerNodeEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerUserEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.enumeration.AlertManagerCategoryType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AlertManagerDTO {
	protected String alertName; // 알림 이름
	protected boolean emailYN; // 이메일 수신 여부
	protected boolean systemYN; // 시스템 수신 여부
	protected List<AlertManagerDTO.NodeDTO> nodeDTOList; // 노드 리스트
	protected List<AlertManagerDTO.CategoryDTO> categoryDTOList; // 항목 리스트

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	public static class RequestDTO extends AlertManagerDTO{
		private List<String> userIdList;

		public AlertManagerEntity convertEntity(){
			return AlertManagerEntity.builder()
				.alertName(this.getAlertName())
				.emailYN(this.isEmailYN())
				.systemYN(this.isSystemYN())
				.alertEnable(true)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends AlertManagerDTO{
		private long id;
		protected boolean alertEnable; // 알림 수신 여부
		private List<AlertManagerDTO.UserDTO> userDTOList;
		private List<AlertManagerReceiveDTO.ResponseDTO> alertManagerReceiveDTOList;
		@Builder(builderClassName = "toDTOBuilder", builderMethodName = "toDTOBuilder")
		ResponseDTO(AlertManagerEntity alertManager) {
			this.id = alertManager.getId();
			this.alertName = alertManager.getAlertName();
			this.emailYN = alertManager.isEmailYN();
			this.systemYN = alertManager.isSystemYN();
			this.alertEnable = alertManager.isAlertEnable();
			this.nodeDTOList = Objects.nonNull(alertManager.getAlertManagerNodeEntityList()) ?
				alertManager.getAlertManagerNodeEntityList().stream().map(nodeEntity ->
					AlertManagerDTO.NodeDTO.toDTOBuilder().nodeEntity(nodeEntity).build()).toList() : new ArrayList<>();
			this.categoryDTOList = Objects.nonNull(alertManager.getAlertManagerCategoryEntityList()) ?
				alertManager.getAlertManagerCategoryEntityList().stream().map(alertManagerCategoryEntity ->
					CategoryDTO.toDTOBuilder().alertManagerCategoryEntity(alertManagerCategoryEntity).build()).toList() : new ArrayList<>();
			this.userDTOList = Objects.nonNull(alertManager.getAlertManagerUserEntityList()) ?
				alertManager.getAlertManagerUserEntityList().stream().map(userEntity ->
					AlertManagerDTO.UserDTO.toDTOBuilder().userEntity(userEntity).build()).toList() : new ArrayList<>();
			this.alertManagerReceiveDTOList = Objects.nonNull(alertManager.getAlertList()) ?
				alertManager.getAlertList().stream().map(alertManagerReceiveEntity ->
					AlertManagerReceiveDTO.ResponseDTO.responseDTOBuilder().alertManagerReceiveEntity(alertManagerReceiveEntity).alertManagerEntity(alertManagerReceiveEntity.getAlertManager()).build()).toList() : new ArrayList<>();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CategoryDTO {
		private long id;
		@Enumerated(EnumType.STRING)
		private AlertManagerCategoryType categoryType; // item 항목
		private String operator;
		private String maximum; // 한계점
		private String durationTime; // 지속시간

		@Builder(builderMethodName = "toDTOBuilder", builderClassName = "toDTOBuilder")
		public CategoryDTO(AlertManagerCategoryEntity alertManagerCategoryEntity) {
			this.id = alertManagerCategoryEntity.getId();
			this.operator = alertManagerCategoryEntity.getOperator();
			this.categoryType = alertManagerCategoryEntity.getAlertManagerCategoryType();
			this.maximum = alertManagerCategoryEntity.getMaximum();
			this.durationTime = alertManagerCategoryEntity.getDurationTime();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NodeDTO {
		private long id;
		private String nodeName;
		private String nodeIp;

		@Builder(builderMethodName = "toDTOBuilder", builderClassName = "toDTOBuilder")
		public NodeDTO(AlertManagerNodeEntity nodeEntity){
			this.id = nodeEntity.getId();
			this.nodeName = nodeEntity.getNodeName();
			this.nodeIp = nodeEntity.getNodeIp();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserDTO {
		private long id;
		private String userId;
		private String userName; // 사용자 이름
		private String email; // 사용자 Email
		private String firstName;
		private String lastName;

		@Builder(builderMethodName = "toDTOBuilder", builderClassName = "toDTOBuilder")
		public UserDTO(AlertManagerUserEntity userEntity){
			this.id = userEntity.getId();
			this.userId = userEntity.getUserId();
			this.userName = userEntity.getUserName();
			this.email = userEntity.getEmail();
			this.firstName = userEntity.getFirstName();
			this.lastName = userEntity.getLastName();
		}
	}

}
