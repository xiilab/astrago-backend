package com.xiilab.modulek8sdb.volume.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.volume.entity.Volume;

public interface VolumeRepositoryCustom {
	Page<Volume> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType authType,
		RepositorySearchCondition repositorySearchCondition, PageMode pageMode);

	Volume getVolumeWithStorage(Long modelId);
	List<Volume> findByAuthority(String userId, AuthType userAuth);
}
