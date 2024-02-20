package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulecommon.enums.CredentialType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

public record ModuleCredentialReqDTO(
	Long credentialId,
	String credentialLoginId,
	String credentialLoginPw,
	String credentialName,
	CredentialType credentialType
) {

	public CredentialVO toCredentialVO(String workspaceName) {
		return new CredentialVO(workspaceName, credentialId, credentialLoginId, credentialLoginPw,
			credentialName, credentialType);
	}
}
