package com.xiilab.modulek8s.workload.dto.request;

import java.io.File;

import com.xiilab.modulek8s.common.enumeration.CodeRepositoryType;
import com.xiilab.modulek8s.workload.vo.JobCodeVO;

public record CodeReqDTO(
	CodeRepositoryType codeRepositoryType,  // repository 타입
	// String userName,        // userName (private 타입에서 사용)
	// String token,           // 토큰 (private 타입에서 사용)
	String repositoryURL,   // repository URL
	String branch,          // repository branch
	String mountPath        // 소스코드 마운트 경로
){
	public JobCodeVO toJobCodeVO() {
		String projectName = repositoryURL.substring(repositoryURL.lastIndexOf("/") + 1).split("\\.")[0];
		return new JobCodeVO(codeRepositoryType, repositoryURL, branch, mountPath + File.separator + projectName);
	}
}
