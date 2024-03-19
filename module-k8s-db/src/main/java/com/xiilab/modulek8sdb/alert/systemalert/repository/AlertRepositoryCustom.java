package com.xiilab.modulek8sdb.alert.systemalert.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.AlertRole;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.model.entity.Model;

public interface AlertRepositoryCustom {
	List<AlertEntity> getWorkspaceAlertsByOwnerRole();

}
