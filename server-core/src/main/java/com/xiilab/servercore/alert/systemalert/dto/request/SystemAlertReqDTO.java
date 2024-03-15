package com.xiilab.servercore.alert.systemalert.dto.request;

import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertEventType;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

		@Builder
		public SaveSystemAlert(String title, String message, String recipientId, String senderId,
			SystemAlertType systemAlertType, SystemAlertEventType systemAlertEventType, ReadYN readYN) {
			this.title = title;
			this.message = message;
			this.recipientId = recipientId;
			this.senderId = senderId;
			this.systemAlertType = systemAlertType;
			this.systemAlertEventType = systemAlertEventType;
			if (readYN == null) {
				this.readYN = ReadYN.N;
			}
		}
	}
}
