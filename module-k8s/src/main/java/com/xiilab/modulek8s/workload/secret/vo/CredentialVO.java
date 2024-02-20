package com.xiilab.modulek8s.workload.secret.vo;

import com.xiilab.modulecommon.enums.CredentialType;

public record CredentialVO (
	String workspaceName,
	Long credentialId,
	String credentialLoginId,
	String credentialLoginPw,
	String credentialName,
	CredentialType credentialType
){
}
