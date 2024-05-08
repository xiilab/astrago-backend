package com.xiilab.servercore.alert.systemalert.dto.response;

import java.util.List;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindAdminAlertMappingResDTO {
	private String title;
	private Long alertId;
	private Long adminAlertMappingId;
	private AlertType alertType;
	private AlertRole alertRole;
	private AlertStatus systemYN;
	private AlertStatus emailYN;

	public static FindAdminAlertMappingResDTO of(AlertEntity alertEntity) {
		return FindAdminAlertMappingResDTO.builder()
			.alertId(alertEntity.getAlertId())
			.adminAlertMappingId(null)
			.title(alertEntity.getAlertName())
			.alertType(alertEntity.getAlertType())
			.alertRole(alertEntity.getAlertRole())
			.systemYN(AlertStatus.OFF)
			.emailYN(AlertStatus.OFF)
			.build();
	}

	public static FindAdminAlertMappingResDTO of(AdminAlertMappingEntity adminAlertMappingEntity) {
		return FindAdminAlertMappingResDTO.builder()
			.alertId(adminAlertMappingEntity.getAlert().getAlertId())
			.adminAlertMappingId(adminAlertMappingEntity.getAdminAlertMappingId())
			.title(adminAlertMappingEntity.getAlert().getAlertName())
			.alertType(adminAlertMappingEntity.getAlert().getAlertType())
			.alertRole(adminAlertMappingEntity.getAlert().getAlertRole())
			.systemYN(adminAlertMappingEntity.getSystemAlertStatus())
			.emailYN(adminAlertMappingEntity.getEmailAlertStatus())
			.build();
	}

	@Getter
	@SuperBuilder
	public static class AdminAlertMappings {
		private List<FindAdminAlertMappingResDTO> adminAlertMappingsDTO;
		private long totalCount;

		public static AdminAlertMappings fromDefaultAlerts(List<AlertEntity> alertEntities, long totalCount) {
			return AdminAlertMappings.builder()
				.adminAlertMappingsDTO(alertEntities.stream().map(FindAdminAlertMappingResDTO::of).toList())
				.totalCount(totalCount)
				.build();
		}

		public static AdminAlertMappings fromAdminAlertsMappings(
			List<AdminAlertMappingEntity> adminAlertMappingEntities, long totalCount) {
			return AdminAlertMappings.builder()
				.adminAlertMappingsDTO(adminAlertMappingEntities.stream().map(FindAdminAlertMappingResDTO::of).toList())
				.totalCount(totalCount)
				.build();
		}

	}
}
