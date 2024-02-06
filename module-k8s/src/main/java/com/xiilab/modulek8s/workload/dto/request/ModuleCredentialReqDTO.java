package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.workload.enums.CredentialType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

public record ModuleCredentialReqDTO(
	Long credentialId,
	String credentialLoginId,
	String credentialLoginEmail,
	String credentialLoginPw,
	String credentialName,
	CredentialType credentialType
) {

	public CredentialVO toCredentialVO(String workspaceName) {
		return new CredentialVO(workspaceName, credentialId, credentialLoginId, credentialLoginEmail, credentialLoginPw,
			credentialName, credentialType);
	}
}
