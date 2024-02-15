package com.xiilab.modulek8sdb.code.dto;

import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.enums.CodeType;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.credential.dto.CredentialResDTO;

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
	private CodeType codeType;
	private String codeURL;
	private String workspaceResourceName;
	private CredentialResDTO credentialResDTO;
	private RegUser creator;


	public CodeResDTO(CodeEntity codeEntity) {
		this.id = codeEntity.getId();
		this.codeType = codeEntity.getCodeType();
		this.codeURL = codeEntity.getCodeURL();
		this.workspaceResourceName = codeEntity.getWorkspaceResourceName();
		this.credentialResDTO = codeEntity.getCredentialEntity() == null ? null : new CredentialResDTO(codeEntity.getCredentialEntity());
		this.creator = codeEntity.getRegUser();
	}
}
