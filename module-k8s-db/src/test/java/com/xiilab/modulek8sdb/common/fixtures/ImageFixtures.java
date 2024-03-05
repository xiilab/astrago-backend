package com.xiilab.modulek8sdb.common.fixtures;

import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulecommon.enums.ImageType;

public class ImageFixtures {
	public static BuiltInImageEntity FIRST_BUILT_IN_IMAGE() {
		return BuiltInImageEntity.builder()
			.imageName("test:v1")
			.repositoryAuthType(RepositoryAuthType.PUBLIC)
			.imageType(ImageType.BUILT)
			.workloadType(WorkloadType.BATCH)
			.title("nginx 이미지")
			.description("nginx 이미지 입니다.")
			.thumbnailSavePath("/usr/local/[uuid].png")
			.thumbnailSaveFileName("test.png")
			.build();
	}
}
