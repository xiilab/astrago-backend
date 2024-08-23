package com.xiilab.modulek8sdb.deploy.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulek8sdb.deploy.dto.DeploySearchCondition;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;

public interface DeployRepositoryCustom {
	PageImpl<DeployEntity> getDeploys(String workspaceResourceName, DeploySearchCondition deploySearchCondition, PageRequest pageRequest);

	PageImpl<DeployEntity> getDeploysUsingModel(PageRequest pageRequest, Long modelRepoId);
}
