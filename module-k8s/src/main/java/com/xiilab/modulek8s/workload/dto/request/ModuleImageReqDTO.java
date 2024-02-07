package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.common.enumeration.RepositoryAuthType;
import com.xiilab.modulek8s.workload.enums.ImageType;
import com.xiilab.modulek8s.workload.enums.RepositoryType;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

public record ModuleImageReqDTO(
	String name,
	String tag,
	ImageType type,
	RepositoryAuthType repositoryAuthType,
	ModuleCredentialReqDTO credentialReqDTO
){
	public JobImageVO toJobImageVO(String workspaceName) {
		if (repositoryAuthType == RepositoryAuthType.PRIVATE && credentialReqDTO != null) {
			return new JobImageVO(name, tag, type, credentialReqDTO.toCredentialVO(workspaceName));
		} else {
			return new JobImageVO(name, tag, type);
		}
	}
}

