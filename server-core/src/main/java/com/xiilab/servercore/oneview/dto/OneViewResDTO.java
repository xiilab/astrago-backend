package com.xiilab.servercore.oneview.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public abstract class OneViewResDTO {
	@Getter
	@AllArgsConstructor
	public static class FindOneViewSetting {
		private String apiServerAddress;
	}

	public static class APIResponse {
		@Getter
		public static class FindApiVersion {
			private String minimumVersion;
			private Integer currentVersion;
		}

		@Getter
		public static class SessionToken {
			private String sessionID;
		}
	}
}
