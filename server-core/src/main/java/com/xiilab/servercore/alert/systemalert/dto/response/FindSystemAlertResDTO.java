package com.xiilab.servercore.alert.systemalert.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulecommon.alert.enums.SystemAlertEventType;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindSystemAlertResDTO extends ResDTO {
	private Long id;
	private String title;
	private String message;
	private SystemAlertType systemAlertType;
	private SystemAlertEventType systemAlertEventType;
	private ReadYN readYN;
	private AlertRole alertRole;

	public static FindSystemAlertResDTO of(SystemAlertEntity systemAlertEntity) {
		return FindSystemAlertResDTO.builder()
			.id(systemAlertEntity.getId())
			.title(systemAlertEntity.getTitle())
			.message(systemAlertEntity.getMessage())
			.systemAlertType(systemAlertEntity.getSystemAlertType())
			.systemAlertEventType(systemAlertEntity.getSystemAlertEventType())
			.readYN(systemAlertEntity.getReadYN())
			.alertRole(systemAlertEntity.getAlertRole())
			.regUserId(systemAlertEntity.getRegUser().getRegUserId())
			.regUserName(systemAlertEntity.getRegUser().getRegUserName())
			.regUserRealName(systemAlertEntity.getRegUser().getRegUserRealName())
			.regDate(systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
			.modDate(systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
			.readYN(systemAlertEntity.getReadYN())
			.build();
	}

	@Getter
	@SuperBuilder
	public static class SystemAlertDetail extends FindSystemAlertResDTO {
		private String recipientId;
		private String senderId;

		public static FindSystemAlertResDTO.SystemAlertDetail from(SystemAlertEntity systemAlertEntity) {
			return FindSystemAlertResDTO.SystemAlertDetail.builder()
				.id(systemAlertEntity.getId())
				.title(systemAlertEntity.getTitle())
				.message(systemAlertEntity.getMessage())
				.systemAlertType(systemAlertEntity.getSystemAlertType())
				.systemAlertEventType(systemAlertEntity.getSystemAlertEventType())
				.readYN(systemAlertEntity.getReadYN())
				.recipientId(systemAlertEntity.getRecipientId())
				.senderId(systemAlertEntity.getSenderId())
				.regUserId(systemAlertEntity.getRegUser().getRegUserId())
				.regUserName(systemAlertEntity.getRegUser().getRegUserName())
				.regUserRealName(systemAlertEntity.getRegUser().getRegUserRealName())
				.regDate(systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build();
		}
	}

	@Getter
	@Builder
	public static class SystemAlerts {
		private List<FindSystemAlertResDTO> systemAlerts;
		private long allTotalCount;
		private long userTotalCount;
		private long workspaceTotalCount;
		private long workloadTotalCount;
		private long licenseTotalCount;
		private long nodeTotalCount;
		private long memberTotalCount;
		private long resourceTotalCount;

		public static FindSystemAlertResDTO.SystemAlerts from(List<SystemAlertEntity> systemAlertEntities,
			long allTotalCount, long userTotalCount, long workspaceTotalCount, long workloadTotalCount,
			long licenseTotalCount, long nodeTotalCount, long memberTotalCount, long resourceTotalCount) {
			return SystemAlerts.builder()
				.systemAlerts(systemAlertEntities.stream().map(FindSystemAlertResDTO::of).toList())
				.allTotalCount(allTotalCount)
				.userTotalCount(userTotalCount)
				.workspaceTotalCount(workspaceTotalCount)
				.workloadTotalCount(workloadTotalCount)
				.licenseTotalCount(licenseTotalCount)
				.nodeTotalCount(nodeTotalCount)
				.memberTotalCount(memberTotalCount)
				.resourceTotalCount(resourceTotalCount)
				.build();
		}
	}
}
