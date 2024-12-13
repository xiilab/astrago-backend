package com.xiilab.servercore.alert.systemalert.dto.response;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulecommon.alert.enums.AlertEventType;
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
	private AlertType alertType;
	private AlertEventType alertEventType;
	private ReadYN readYN;
	private AlertRole alertRole;
	private PageNaviParam pageNaviParam;

	public static FindSystemAlertResDTO of(SystemAlertEntity systemAlertEntity) {
		return FindSystemAlertResDTO.builder()
			.id(systemAlertEntity.getId())
			.title(systemAlertEntity.getTitle())
			.message(systemAlertEntity.getMessage())
			.alertType(systemAlertEntity.getAlertType())
			.alertEventType(systemAlertEntity.getAlertEventType())
			.pageNaviParam(systemAlertEntity.getPageNaviParam())
			.readYN(systemAlertEntity.getReadYN())
			.alertRole(systemAlertEntity.getAlertRole())
			.regUserId(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserId() : null)
			.regUserName(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserName() : null)
			.regUserRealName(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserRealName() : null)
			.regDate(!ObjectUtils.isEmpty(systemAlertEntity.getRegDate())? systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
			.modDate(!ObjectUtils.isEmpty(systemAlertEntity.getModDate())? systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
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
				.alertType(systemAlertEntity.getAlertType())
				.alertEventType(systemAlertEntity.getAlertEventType())
				.pageNaviParam(systemAlertEntity.getPageNaviParam())
				.readYN(systemAlertEntity.getReadYN())
				.recipientId(systemAlertEntity.getRecipientId())
				.senderId(systemAlertEntity.getSenderId())
				.regUserId(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserId() : null)
				.regUserName(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserName() : null)
				.regUserRealName(!ObjectUtils.isEmpty(systemAlertEntity.getRegUser())? systemAlertEntity.getRegUser().getRegUserRealName() : null)
				.regDate(!ObjectUtils.isEmpty(systemAlertEntity.getRegDate())? systemAlertEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
				.modDate(!ObjectUtils.isEmpty(systemAlertEntity.getModDate())? systemAlertEntity.getModDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
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
