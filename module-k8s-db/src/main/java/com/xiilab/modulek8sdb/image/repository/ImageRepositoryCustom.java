package com.xiilab.modulek8sdb.image.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulecommon.enums.ImageType;

public interface ImageRepositoryCustom {
	List<ImageEntity> findByType(ImageType imageType, WorkloadType type);
}
