package com.xiilab.modulek8sdb.image.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

public interface ImageRepositoryCustom {
	Page<ImageEntity> findByImages(ImageType imageType, WorkloadType type, boolean multiNode, Pageable pageable);
}
