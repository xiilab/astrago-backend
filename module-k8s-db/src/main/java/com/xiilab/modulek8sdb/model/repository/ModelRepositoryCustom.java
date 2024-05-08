package com.xiilab.modulek8sdb.model.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.model.entity.Model;

public interface ModelRepositoryCustom {
	Page<Model> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType authType,
		RepositorySearchCondition repositorySearchCondition, PageMode pageMode);

	Model getModelWithStorage(Long modelId);
	List<Model> findByAuthority(String userId, AuthType userAuth);
}
