package com.xiilab.servercore.alert.systemalert.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertEventType;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
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

	// protected FindSystemAlertResDTO(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate, LocalDateTime modDate
	// , Long id, String title, String message, SystemAlertType systemAlertType, SystemAlertEventType systemAlertEventType, ReadYN readYN){
	// 	super(regUserId, regUserName, regUserRealName, regDate, modDate);
	// 	this.id = id;
	// 	this.title = title;
	// 	this.message = message;
	// 	this.systemAlertType = systemAlertType;
	// 	this.systemAlertEventType = systemAlertEventType;
	// 	this.readYN = readYN;
	// }

	public static FindSystemAlertResDTO of(SystemAlertEntity systemAlertEntity) {
		return FindSystemAlertResDTO.builder()
			.id(systemAlertEntity.getId())
			.title(systemAlertEntity.getTitle())
			.message(systemAlertEntity.getMessage())
			.systemAlertType(systemAlertEntity.getSystemAlertType())
			.systemAlertEventType(systemAlertEntity.getSystemAlertEventType())
			.readYN(systemAlertEntity.getReadYN())
			.regUserId(systemAlertEntity.getRegUser().getRegUserId())
			.regUserName(systemAlertEntity.getRegUser().getRegUserName())
			.regUserRealName(systemAlertEntity.getRegUser().getRegUserRealName())
			.regDate(systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
			.modDate(systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
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
					.regDate(systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.modDate(systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
					.build();
			}
	}

	@Getter
	@Builder
	public static class SystemAlerts {
		private List<FindSystemAlertResDTO> systemAlerts;
		private Long totalCount;

		public static FindSystemAlertResDTO.SystemAlerts from(List<SystemAlertEntity> systemAlertEntities, long totalCount) {
			return SystemAlerts.builder()
				.systemAlerts(systemAlertEntities.stream().map(FindSystemAlertResDTO::of).toList())
				.totalCount(totalCount)
				.build();
		}
	}
}
