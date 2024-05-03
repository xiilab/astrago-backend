package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.querydsl.core.group.GroupBy.*;
import static com.xiilab.modulek8sdb.alert.systemalert.entity.QAdminAlertMappingEntity.*;
import static com.xiilab.modulek8sdb.alert.systemalert.entity.QAlertEntity.*;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AdminAlertMappingEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.AlertEntity;
import com.xiilab.modulecommon.alert.enums.AlertRole;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlertRepositoryImpl implements AlertRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<AlertEntity> getWorkspaceAlertsByOwnerRole() {
		return queryFactory.selectFrom(alertEntity)
			.where(
				alertEntity.alertRole.eq(AlertRole.OWNER).or(alertEntity.alertRole.eq(AlertRole.USER)),
				alertEntity.alertType.eq(AlertType.WORKSPACE)
					.or(alertEntity.alertType.eq(AlertType.WORKLOAD))
					.or(alertEntity.alertType.eq(
						AlertType.RESOURCE))
			).fetch();
	}

	// @Override
	// public List<AlertEntity> findAdminAlertMappings(String adminId) {
	// 	List<AlertEntity> alertEntities = queryFactory.selectFrom(alertEntity)
	// 		.leftJoin(alertEntity.adminAlertMappingEntities, adminAlertMappingEntity)
	// 		.on(adminAlertMappingEntity.adminId.eq(adminId))
	// 		.where(alertEntity.alertRole.eq(AlertRole.ADMIN))
	// 		.fetch();
	// 	alertEntities
	// }
}
