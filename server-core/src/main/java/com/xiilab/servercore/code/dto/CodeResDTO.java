package com.xiilab.servercore.code.dto;

import java.time.LocalDateTime;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.common.entity.RegUser;
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
	private String title;
	private CodeType codeType;
	private String codeURL;
	private String workspaceResourceName;
	private CredentialResDTO credentialResDTO;
	private RegUser regUser;
	private LocalDateTime regDate;

	public CodeResDTO(CodeEntity codeEntity) {
		this.id = codeEntity.getId();
		this.title = codeEntity.getTitle();
		this.codeType = codeEntity.getCodeType();
		this.codeURL = codeEntity.getCodeURL();
		this.workspaceResourceName = codeEntity.getWorkspaceResourceName();
		this.credentialResDTO = codeEntity.getCredentialEntity() == null ? null : new CredentialResDTO(codeEntity.getCredentialEntity());
		this.regUser = codeEntity.getRegUser();
		this.regDate = codeEntity.getRegDate();
	}
}
