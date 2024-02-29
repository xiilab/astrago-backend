package com.xiilab.modulealert.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.xiilab.modulealert.entity.AlertCategoryEntity;
import com.xiilab.modulealert.entity.AlertManagerEntity;
import com.xiilab.modulealert.entity.AlertNodeEntity;
import com.xiilab.modulealert.entity.AlertUserEntity;
import com.xiilab.modulealert.enumeration.CategoryType;

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
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends AlertManagerDTO{
		private long id;
		private List<AlertManagerDTO.UserDTO> userDTOList;

		@Builder(builderClassName = "toDTOBuilder", builderMethodName = "toDTOBuilder")
		ResponseDTO(AlertManagerEntity alertManager) {
			this.id = alertManager.getId();
			this.alertName = alertManager.getAlertName();
			this.emailYN = alertManager.isEmailYN();
			this.systemYN = alertManager.isSystemYN();
			this.nodeDTOList = Objects.nonNull(alertManager.getAlertNodeEntityList()) ?
				alertManager.getAlertNodeEntityList().stream().map(nodeEntity ->
					AlertManagerDTO.NodeDTO.toDTOBuilder().nodeEntity(nodeEntity).build()).toList() : new ArrayList<>();
			this.categoryDTOList = Objects.nonNull(alertManager.getAlertCategoryEntityList()) ?
				alertManager.getAlertCategoryEntityList().stream().map(alertCategoryEntity ->
					AlertManagerDTO.CategoryDTO.toDTOBuilder().alertCategoryEntity(alertCategoryEntity).build()).toList() : new ArrayList<>();
			this.userDTOList = Objects.nonNull(alertManager.getAlertUserEntityList()) ?
				alertManager.getAlertUserEntityList().stream().map(userEntity ->
					AlertManagerDTO.UserDTO.toDTOBuilder().userEntity(userEntity).build()).toList() : new ArrayList<>();

		}
	}


	@Builder
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CategoryDTO {
		private long id;
		@Enumerated(EnumType.STRING)
		private CategoryType categoryType; // item 항목
		private String operator;
		private String maximum; // 한계점
		private String durationTime; // 지속시간

		@Builder(builderMethodName = "toDTOBuilder", builderClassName = "toDTOBuilder")
		public CategoryDTO(AlertCategoryEntity alertCategoryEntity) {
			this.id = alertCategoryEntity.getId();
			this.operator = alertCategoryEntity.getOperator();
			this.categoryType = alertCategoryEntity.getCategoryType();
			this.maximum = alertCategoryEntity.getMaximum();
			this.durationTime = alertCategoryEntity.getDurationTime();
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
		public NodeDTO(AlertNodeEntity nodeEntity){
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
		public UserDTO(AlertUserEntity userEntity){
			this.id = userEntity.getId();
			this.userId = userEntity.getUserId();
			this.userName = userEntity.getUserName();
			this.email = userEntity.getEmail();
			this.firstName = userEntity.getFirstName();
			this.lastName = userEntity.getLastName();
		}
	}
}
