package com.xiilab.servercore.alert.systemalert.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulecommon.alert.enums.AlertStatus;
import com.xiilab.modulecommon.alert.enums.SystemAlertEventType;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;

import lombok.AllArgsConstructor;
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
		private SystemAlertType systemAlertType;
		private SystemAlertEventType systemAlertEventType;
		private ReadYN readYN;
		private AlertRole alertRole;

		@Builder
		public SaveSystemAlert(String title, String message, String recipientId, String senderId,
			SystemAlertType systemAlertType, SystemAlertEventType systemAlertEventType, ReadYN readYN, AlertRole alertRole) {
			this.title = title;
			this.message = message;
			this.recipientId = recipientId;
			this.senderId = senderId;
			this.systemAlertType = systemAlertType;
			this.systemAlertEventType = systemAlertEventType;
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
	@AllArgsConstructor
	public static class FindSearchCondition {
		private SystemAlertType systemAlertType;
		private ReadYN readYN;
		private String searchText;
		private AlertRole alertRole;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime searchStartDate;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime searchEndDate;
	}

}
