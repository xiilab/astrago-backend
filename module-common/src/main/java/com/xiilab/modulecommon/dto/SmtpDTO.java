package com.xiilab.modulecommon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmtpDTO {
	private String host;
	private int port;
	private String username;
	private String password;

}
