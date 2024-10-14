package com.xiilab.modulek8sdb.credential.dto;

import com.xiilab.modulecommon.enums.CredentialType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CredentialReqDTO {
	private String name;
	private String description;
	private String id;
	private String pw;
	private String privateRegistryUrl;
	private CredentialType type;
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UpdateDTO {
		private String name;
		private String description;
		private String id;
		private String pw;
		private String privateRegistryUrl;
	}
}
