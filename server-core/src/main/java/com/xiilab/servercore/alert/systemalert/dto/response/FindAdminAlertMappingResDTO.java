package com.xiilab.servercore.alert.systemalert.dto.response;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertStatus;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindAdminAlertMappingResDTO {
	private String title;
	private Long alertId;
	private Long adminAlertMappingId;
	private SystemAlertType systemAlertType;
	private AlertRole alertRole;
	private AlertStatus systemYN;
	private AlertStatus emilYN;

	public static FindAdminAlertMappingResDTO of(AlertEntity alertEntity) {
		return FindAdminAlertMappingResDTO.builder()
			.alertId(alertEntity.getAlertId())
			.adminAlertMappingId(!CollectionUtils.isEmpty(alertEntity.getAdminAlertMappingEntities()) ?
				alertEntity.getAdminAlertMappingEntities().get(0).getAdminAlertMappingId() : null)
			.title(alertEntity.getAlertName())
			.systemAlertType(alertEntity.getAlertType())
			.alertRole(alertEntity.getAlertRole())
			.systemYN(!CollectionUtils.isEmpty(alertEntity.getAdminAlertMappingEntities()) ?
				alertEntity.getAdminAlertMappingEntities().get(0).getSystemAlertStatus() : AlertStatus.OFF)
			.emilYN(!CollectionUtils.isEmpty(alertEntity.getAdminAlertMappingEntities()) ?
				alertEntity.getAdminAlertMappingEntities().get(0).getEmailAlertStatus() : AlertStatus.OFF)
			.build();
	}

	@Getter
	@SuperBuilder
	public static class AdminAlertMappings {
		private List<FindAdminAlertMappingResDTO> adminAlertMappingsDTO;
		private long totalCount;

		public static AdminAlertMappings from(List<AlertEntity> alertEntities, long totalCount) {
			return AdminAlertMappings.builder()
				.adminAlertMappingsDTO(alertEntities.stream().map(FindAdminAlertMappingResDTO::of).toList())
				.totalCount(totalCount)
				.build();
		}

	}
}