package com.xiilab.modulek8sdb.credential.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulecommon.enums.CredentialType;

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
	private String description;
	private CredentialType type;
	private String creator;
	private LocalDateTime createdAt;

	public CredentialResDTO(CredentialEntity credential) {
		this.id = credential.getId();
		this.name = credential.getName();
		this.description = credential.getDescription();
		this.type = credential.getType();
		this.creator = credential.getRegUser().getRegUserName();
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
			super(credential.getId(), credential.getName(), credential.getDescription(), credential.getType(),
				credential.getRegUser().getRegUserName(), credential.getCreatedAt());
			this.loginId = credential.getLoginId();
			this.loginPw = credential.getLoginPw();
		}
	}
}
