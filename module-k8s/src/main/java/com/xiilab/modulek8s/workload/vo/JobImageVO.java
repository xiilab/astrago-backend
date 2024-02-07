package com.xiilab.modulek8s.workload.vo;

import com.xiilab.modulek8s.workload.enums.RepositoryType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.enums.ImageType;

public record JobImageVO(
	String name,
	String tag,
	ImageType imageType,
	CredentialVO credentialVO
) {
	public JobImageVO(String name, String tag, ImageType imageType) {
		this(name, tag, imageType, null);
	}
}
