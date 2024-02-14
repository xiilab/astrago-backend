package com.xiilab.modulek8s.workload.vo;

import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.enums.ImageType;

public record JobImageVO(
	String name,
	ImageType imageType,
	CredentialVO credentialVO
) {
	public JobImageVO(String name, ImageType imageType) {
		this(name, imageType, null);
	}
}
