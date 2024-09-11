package com.xiilab.modulek8sdb.dataset.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;

public interface DatasetRepositoryCustom {
	Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType authType,
		RepositorySearchCondition repositorySearchCondition, PageMode pageMode, Set<String> joinedWorkspaceResourceNames);

	Dataset getDatasetWithStorage(Long datasetId);
	List<Dataset> findByAuthority(String userId, AuthType userAuth);
}
