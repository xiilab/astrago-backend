package com.xiilab.modulek8s.workload.secret.vo;

import com.xiilab.modulek8s.workload.enums.CredentialType;

public record CredentialVO (
	String workspaceName,
	Long credentialId,
	String credentialLoginId,
	String credentialLoginPw,
	String credentialName,
	CredentialType credentialType
){
}
