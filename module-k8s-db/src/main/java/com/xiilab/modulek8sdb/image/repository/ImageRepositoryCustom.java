package com.xiilab.modulek8sdb.image.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulecommon.enums.ImageType;

public interface ImageRepositoryCustom {
	Page<ImageEntity> findByType(ImageType imageType, WorkloadType type, Pageable pageable);
}
