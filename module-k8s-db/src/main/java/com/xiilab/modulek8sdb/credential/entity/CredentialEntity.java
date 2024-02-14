package com.xiilab.modulek8sdb.credential.entity;

import java.time.LocalDateTime;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.credential.dto.CredentialReqDTO;
import com.xiilab.modulek8sdb.credential.enumeration.CredentialType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_CREDENTIAL")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
// @SuperBuilder
public class CredentialEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CREDENTIAL_ID")
	private Long id;
	@Column(name = "CREDENTIAL_NAME")
	private String name;
	@Column(name = "CREDENTIAL_DESCRIPTION")
	private String description;
	@Column(name = "CREDENTIAL_LOGIN_ID")
	private String loginId;
	@Column(name = "CREDENTIAL_LOGIN_PW")
	private String loginPw;

	@Column(name = "CREDENTIAL_CREATED_AT")
	private LocalDateTime createdAt;
	@Column(name = "CREDENTIAL_TYPE")
	@Enumerated(value = EnumType.STRING)
	private CredentialType type;

	@Builder(builderClassName = "dtoConverter", builderMethodName = "dtoConverter")
	CredentialEntity(CredentialReqDTO credentialReqDTO) {
		this.name = credentialReqDTO.getName();
		this.description = credentialReqDTO.getDescription();
		this.loginId = credentialReqDTO.getId();
		this.loginPw = credentialReqDTO.getPw();
		this.type = credentialReqDTO.getType();
		this.createdAt = LocalDateTime.now();
	}

	public void updateInfo(CredentialReqDTO.UpdateDTO updateDTO) {
		this.name = updateDTO.getName();
		this.description = updateDTO.getDescription();
		this.loginId = updateDTO.getId();
		this.loginPw = updateDTO.getPw();
	}
}
