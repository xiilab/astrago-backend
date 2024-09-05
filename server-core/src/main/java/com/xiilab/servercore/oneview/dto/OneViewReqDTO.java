package com.xiilab.servercore.oneview.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class OneViewReqDTO {
	private String userName;
	private String password;

	@Getter
	@NoArgsConstructor
	public static class SaveOneViewSetting extends OneViewReqDTO {
		@Pattern(regexp = "^https:\\/\\/(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$", message = "서버 주소 패턴이 잘못되었습니다.")
		private String apiServerAddress;
	}

	@Getter
	@NoArgsConstructor
	@SuperBuilder
	public static class LoginSession extends OneViewReqDTO {
		private String userName;
		private String password;
	}
}
