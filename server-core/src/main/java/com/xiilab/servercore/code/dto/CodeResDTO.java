package com.xiilab.servercore.code.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CodeErrorCode;
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
	private String defaultPath;
	private String cmd;
	private Map<String,String> codeArgs;

	public CodeResDTO(CodeEntity codeEntity) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			this.id = codeEntity.getId();
			this.title = codeEntity.getTitle();
			this.codeType = codeEntity.getCodeType();
			this.codeURL = codeEntity.getCodeURL();
			this.workspaceResourceName = codeEntity.getWorkspaceResourceName();
			this.credentialResDTO = codeEntity.getCredentialEntity() == null ? null :
				new CredentialResDTO(codeEntity.getCredentialEntity());
			this.regUser = codeEntity.getRegUser();
			this.regDate = codeEntity.getRegDate();
			this.defaultPath = codeEntity.getCodeDefaultMountPath();
			this.cmd = codeEntity.getCmd();
			this.codeArgs = codeEntity.getCodeArgs() != null ?
				objectMapper.readValue(codeEntity.getCodeArgs(), new TypeReference<>() {}) : null;
		} catch (JsonProcessingException e) {
			throw new RestApiException(CodeErrorCode.FAILED_JSON_TO_MAP);
		}
	}
}
