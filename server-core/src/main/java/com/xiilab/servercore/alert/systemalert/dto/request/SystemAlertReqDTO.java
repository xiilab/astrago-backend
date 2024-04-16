package com.xiilab.servercore.alert.systemalert.dto.request;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.xiilab.modulecommon.alert.enums.AlertEventType;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.util.ValidUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class SystemAlertReqDTO {
	@Getter
	@NoArgsConstructor
	public static class SaveSystemAlert {
		private String title;
		private String message;
		private String recipientId;
		private String senderId;
		private AlertType alertType;
		private AlertEventType alertEventType;
		private ReadYN readYN;
		private AlertRole alertRole;

		@Builder
		public SaveSystemAlert(String title, String message, String recipientId, String senderId,
			AlertType alertType, AlertEventType alertEventType, ReadYN readYN, AlertRole alertRole) {
			this.title = title;
			this.message = message;
			this.recipientId = recipientId;
			this.senderId = senderId;
			this.alertType = alertType;
			this.alertEventType = alertEventType;
			this.alertRole = alertRole;
			if (readYN == null) {
				this.readYN = ReadYN.N;
			}
		}
	}

	@Getter
	@NoArgsConstructor
	public static class SaveAdminAlertMappings {
		private Long alertId;
		private Long adminAlertMappingId;
		// private String adminId;
		private AlertStatus emailAlertStatus;
		private AlertStatus systemAlertStatus;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class FindSearchCondition {
		private AlertType alertType;
		private ReadYN readYN;
		private String searchText;
		private AlertRole alertRole;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime searchStartDate;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime searchEndDate;
		private Integer page;
		private Integer size;

		public void setPage(Integer page) {
			this.page = !ValidUtils.isNullOrZero(page)? page - 1 : null;
		}
	}

}
