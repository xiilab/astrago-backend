package com.xiilab.servercore.oneview.dto;

import lombok.Getter;

@Getter
public abstract class OneViewResDTO {
	@Getter
	public static class FindOneViewSetting {
		private String minimumVersion;
		private Integer currentVersion;
	}

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
