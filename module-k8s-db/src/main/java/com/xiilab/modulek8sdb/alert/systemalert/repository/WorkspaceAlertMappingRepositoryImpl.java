package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.xiilab.modulek8sdb.alert.systemalert.entity.QAlertEntity.*;
import static com.xiilab.modulek8sdb.alert.systemalert.entity.QWorkspaceAlertMappingEntity.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertMappingEntity;
import com.xiilab.modulecommon.alert.enums.AlertRole;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkspaceAlertMappingRepositoryImpl implements WorkspaceAlertMappingRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<WorkspaceAlertMappingEntity> getWorkspaceAlertMappingByWorkspaceResourceNameAndAlertRole(String workspaceResourceName, String userId, AlertRole alertRole){
		return queryFactory.selectFrom(workspaceAlertMappingEntity)
			.join(workspaceAlertMappingEntity.alert, alertEntity)
			.fetchJoin()
			.where(workspaceAlertMappingEntity.userId.eq(userId),
				workspaceAlertMappingEntity.workspaceResourceName.eq(workspaceResourceName),
				alertRoleEq(alertRole))
			.fetch();
	}

	@Override
	public List<WorkspaceAlertMappingEntity> getWorkspaceAlertMappingByAlertId(Long alertId, String workspaceResourceName) {
		return queryFactory.selectFrom(workspaceAlertMappingEntity)
			.where(workspaceAlertMappingEntity.alert.alertId.eq(alertId),
				workspaceAlertMappingEntity.workspaceResourceName.eq(workspaceResourceName))
			.fetch();
	}

	private static BooleanExpression alertRoleEq(AlertRole alertRole) {
		if(alertRole == AlertRole.OWNER){
			return alertEntity.alertRole.eq(AlertRole.OWNER).or(alertEntity.alertRole.eq(AlertRole.USER));
		}
		return alertEntity.alertRole.eq(AlertRole.USER);
	}
}
