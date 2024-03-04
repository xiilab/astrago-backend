package com.xiilab.modulek8s.workload.vo;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

public record JobImageVO(
	Long id,
	String name,
	ImageType imageType,
	CredentialVO credentialVO
) {
	public JobImageVO(Long id, String name, ImageType imageType) {
		this(id, name, imageType, null);
	}
}
