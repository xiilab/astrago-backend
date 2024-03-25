package com.xiilab.servercore.alert.systemalert.dto.response;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;

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
	private AlertStatus emailYN;

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
			.emailYN(!CollectionUtils.isEmpty(alertEntity.getAdminAlertMappingEntities()) ?
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
