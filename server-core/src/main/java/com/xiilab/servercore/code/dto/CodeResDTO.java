package com.xiilab.servercore.code.dto;

import com.xiilab.servercore.code.entity.CodeEntity;
import com.xiilab.servercore.credential.dto.CredentialResDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeResDTO {
	private long id;
	private CodeEntity.CodeType codeType;
	private String codeURL;
	private CredentialResDTO credentialResDTO;

	public CodeResDTO(CodeEntity codeEntity) {
		this.id = codeEntity.getId();
		this.codeType = codeEntity.getCodeType();
		this.codeURL = codeEntity.getCodeURL();
		this.credentialResDTO = codeEntity.getCredentialEntity() == null ? null : new CredentialResDTO(codeEntity.getCredentialEntity());
	}
}
