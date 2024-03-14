package com.xiilab.modulek8sdb.alert.alertmanager.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerNodeEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerReceiveEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.enumeration.AlertManagerCategoryType;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AlertManagerReceiveDTO {
	protected Long id;
	protected String value; // 임계값
	protected String currentTime; // 발생 시간
	protected Long alertManagerId;
	protected LocalDateTime realTime;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReceiveDTO extends AlertManagerReceiveDTO{
		private String nodeName; // 발생 노드 이름
		private String alertName; // 발생 알림 이름
		private String nodeIp;  // 설정 값 DTO List
		private String categoryType; // categoryType

		@Builder(builderMethodName = "receiveDTOBuilder", builderClassName = "receiveDTOBuilder")
		public ReceiveDTO(AlertManagerReceiveEntity alertManagerReceiveEntity){
			this.id = alertManagerReceiveEntity.getId();
			this.nodeName = alertManagerReceiveEntity.getNodeName();
			this.alertManagerId = alertManagerReceiveEntity.getAlertManager().getId();
			this.alertName = alertManagerReceiveEntity.getAlertName();
			this.categoryType = alertManagerReceiveEntity.getCategoryType().getTypeValue();
			this.value = Objects.equals(alertManagerReceiveEntity.getCategoryType().getTypeValue(),
				AlertManagerCategoryType.GPU_TEMP.getTypeValue()) ?
				alertManagerReceiveEntity.getThreshold() + "°C" : alertManagerReceiveEntity.getThreshold() + "%";
			this.currentTime = alertManagerReceiveEntity.getCurrentTime();
			this.realTime = alertManagerReceiveEntity.getRealTime();
			this.nodeIp = CollectionUtils.isEmpty(alertManagerReceiveEntity.getAlertManager().getAlertManagerNodeEntityList()) ?
				"해당 노드의 IP가 없습니다." : alertManagerReceiveEntity.getAlertManager().getAlertManagerNodeEntityList().stream()
				.filter(node ->
					node.getNodeName().equals(alertManagerReceiveEntity.getNodeName()) && !StringUtils.isEmpty(node.getNodeIp()))
				.map(AlertManagerNodeEntity::getNodeIp)
				.findFirst().orElse("해당 노드의 IP가 없습니다.");
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResponseDTO extends AlertManagerReceiveDTO {
		private AlertManagerDTO.CategoryDTO categoryDTO;  // 설정 값 DTO List
		private AlertManagerDTO.NodeDTO nodeDTO;
		private boolean result;
		@Builder(builderMethodName = "responseDTOBuilder", builderClassName = "responseDTOBuilder")
		public ResponseDTO(AlertManagerEntity alertManagerEntity, AlertManagerReceiveEntity alertManagerReceiveEntity) {
			this.id = alertManagerEntity.getId();
			this.alertManagerId = alertManagerReceiveEntity.getAlertManager().getId();
			this.value = alertManagerReceiveEntity.getThreshold();
			this.currentTime = alertManagerReceiveEntity.getCurrentTime();
			this.realTime = alertManagerReceiveEntity.getRealTime();
			this.result = alertManagerReceiveEntity.isResult();
			this.nodeDTO = alertManagerEntity.getAlertManagerNodeEntityList().stream()
				.filter(nodeEntity -> nodeEntity.getNodeName().equals(alertManagerReceiveEntity.getNodeName()))
				.map(nodeEntity -> AlertManagerDTO.NodeDTO.toDTOBuilder().nodeEntity(nodeEntity).build())
				.findFirst().orElse(AlertManagerDTO.NodeDTO.builder().build());
			this.categoryDTO = alertManagerEntity.getAlertManagerCategoryEntityList().stream()
				.filter(categoryEntity -> categoryEntity.getAlertManagerCategoryType().equals(alertManagerReceiveEntity.getCategoryType()))
				.map(categoryEntity -> AlertManagerDTO.CategoryDTO.toDTOBuilder().alertManagerCategoryEntity(categoryEntity).build())
				.findFirst().orElse(AlertManagerDTO.CategoryDTO.builder().build());
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class historyDTO extends ResponseDTO {
		private AlertManagerDTO.CategoryDTO categoryDTO;
		private AlertManagerDTO.NodeDTO nodeDTO;
		private SystemAlertDTO systemAlertDTO;
		@Builder(builderMethodName = "historyDTOBuilder", builderClassName = "historyDTOBuilder")
		public historyDTO(AlertManagerEntity alertManagerEntity, AlertManagerReceiveEntity alertManagerReceiveEntity, SystemAlertDTO systemAlertDTO){
			this.currentTime = alertManagerReceiveEntity.getCurrentTime();
			this.nodeDTO = alertManagerEntity.getAlertManagerNodeEntityList().stream()
				.filter(nodeEntity -> nodeEntity.getNodeName().equals(alertManagerReceiveEntity.getNodeName()))
				.map(nodeEntity -> AlertManagerDTO.NodeDTO.toDTOBuilder().nodeEntity(nodeEntity).build())
				.findFirst().orElse(AlertManagerDTO.NodeDTO.builder().build());
			this.categoryDTO = alertManagerEntity.getAlertManagerCategoryEntityList().stream()
				.filter(categoryEntity -> categoryEntity.getAlertManagerCategoryType().equals(alertManagerReceiveEntity.getCategoryType()))
				.map(categoryEntity -> AlertManagerDTO.CategoryDTO.toDTOBuilder().alertManagerCategoryEntity(categoryEntity).build())
				.findFirst().orElse(AlertManagerDTO.CategoryDTO.builder().build());
			this.value = alertManagerReceiveEntity.getThreshold();
			this.systemAlertDTO = systemAlertDTO;

		}
	}

}
