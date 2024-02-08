package com.xiilab.servercore.code.entity;

import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.common.entity.BaseEntity;
import com.xiilab.servercore.credential.entity.CredentialEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_CODE")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CodeEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODE_ID")
	private Long id;								// CODE ID
	@Column(name = "CODE_TYPE")
	@Enumerated(value = EnumType.STRING)
	private CodeType codeType;
	@Column(name = "CODE_URL")
	private String codeURL;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREDENTIAL_ID")
	private CredentialEntity credentialEntity; 		// credential Entity

	public enum CodeType{
		GIT_HUB,
		GIT_LAB
	}

	@Builder(builderClassName = "dtoConverter", builderMethodName = "dtoConverter")
	CodeEntity(CodeReqDTO codeReqDTO, CredentialEntity credentialEntity) {
		this.codeType = codeReqDTO.getCodeType();
		this.codeURL = codeReqDTO.getCodeURL();
		this.credentialEntity = credentialEntity;
	}
}
