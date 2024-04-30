package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.xiilab.modulek8sdb.alert.systemalert.entity.QAdminAlertMappingEntity.*;
import static com.xiilab.modulek8sdb.alert.systemalert.entity.QAlertEntity.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.alert.enums.AlertType;
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

	@Override
	public List<AlertEntity> findAdminAlertMappings(String adminId) {
		return queryFactory.selectDistinct(alertEntity)
			.from(alertEntity)
			.leftJoin(adminAlertMappingEntity)
			.on(
				alertEntity.alertId.eq(adminAlertMappingEntity.alert.alertId)
					.and(adminAlertMappingEntity.adminId.eq(adminId))
			)
			.fetchJoin()
			.where(
				alertEntity.alertRole.eq(AlertRole.ADMIN)
			).fetch();
	}

	// private BooleanExpression eqAdminId(String adminId) {
	// 	return StringUtils.hasText(adminId)?
	// 		alertEntity.adminAlertMappingEntities
	// }
}
