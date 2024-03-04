package com.xiilab.modulek8s.workload.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulek8s.workload.vo.JobImageVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
public class ModuleImageReqDTO {
	@Setter
	private Long id;
	private String name;
	private ImageType type;
	private RepositoryAuthType repositoryAuthType;
	private Long credentialId;
	@Setter
	@JsonIgnore
	private ModuleCredentialReqDTO credentialReqDTO;

	public JobImageVO toJobImageVO(String workspaceName) {
		if (repositoryAuthType == RepositoryAuthType.PRIVATE && credentialReqDTO != null) {
			return new JobImageVO(id, name, type, credentialReqDTO.toCredentialVO(workspaceName));
		} else {
			return new JobImageVO(id, name, type);
		}
	}
}
