package com.xiilab.modulek8sdb.preset.repository;

import org.springframework.data.domain.Page;

import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulek8sdb.preset.entity.ResourcePresetEntity;

public interface ResourcePresetRepositoryCustom {
	Page<ResourcePresetEntity> findResourcePresets(Integer page, Integer size, NodeType nodeType);
}
