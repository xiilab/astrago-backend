package com.xiilab.modulek8s.workload.vo;

import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

public record JobCodeVO(
	//CodeRepositoryType codeRepositoryType,  // repository 타입
	// String userName,        // userName (private 타입에서 사용)
	// String token,           // 토큰 (private 타입에서 사용)
	Long id,
	String repositoryURL,   // repository URL
	String branch,          // repository branch
	String mountPath,       // 소스코드 마운트 경로
	String initContainerImageUrl,       // init 컨테이너에 쓰일 이미지 주소
	CredentialVO credentialVO
) {
	public JobCodeVO(Long codeId, String repositoryURL, String branch, String mountPath, String initContainerImageUrl) {
		this(codeId, repositoryURL, branch, mountPath, initContainerImageUrl, null);
	}
}
