package com.xiilab.servercore.credential.dto;

import java.time.LocalDateTime;

import com.xiilab.servercore.credential.entity.CredentialEntity;
import com.xiilab.servercore.credential.enumeration.CredentialType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CredentialResDTO {
	private long id;
	private String name;
	private CredentialType type;
	private String creator;
	private LocalDateTime createdAt;

	public CredentialResDTO(CredentialEntity credential) {
		this.id = credential.getId();
		this.name = credential.getName();
		this.type = credential.getType();
		this.creator = credential.getUser().getName();
		this.createdAt = credential.getCreatedAt();
	}

	@Getter
	@SuperBuilder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CredentialInfo extends CredentialResDTO {
		private String loginId;
		private String loginPw;

		public CredentialInfo(CredentialEntity credential) {
			super(credential.getId(), credential.getName(), credential.getType(), credential.getUser().getName(), credential.getCreatedAt());
			this.loginId = credential.getLoginId();
			this.loginPw = credential.getLoginPw();
		}
	}
}
