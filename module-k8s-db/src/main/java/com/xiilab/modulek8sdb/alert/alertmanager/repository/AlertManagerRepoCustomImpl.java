package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerEntity.*;
import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerReceiveEntity.*;
import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerUserEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.alert.alertmanager.entity.AlertManagerReceiveEntity;
import com.xiilab.modulek8sdb.alert.alertmanager.enumeration.AlertManagerCategoryType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AlertManagerRepoCustomImpl implements AlertManagerRepoCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<AlertManagerReceiveEntity> getAlertManagerReceiveList(String categoryType, String search, LocalDateTime startDate, LocalDateTime endDate, String userId){
		return queryFactory
			.selectFrom(alertManagerReceiveEntity)
			.leftJoin(alertManagerEntity)
			.on(alertManagerEntity.id.eq(alertManagerReceiveEntity.alertManager.id))
			.leftJoin(alertManagerUserEntity)
			.on(alertManagerUserEntity.alertManager.id.eq(alertManagerEntity.id))
			.where(alertManagerUserEntity.userId.eq(userId),
				eqSearch(search),
				eqCategoryType(categoryType),
				ltStartDate(endDate), gtEndDate(startDate)
			).fetch();
	}

	private BooleanExpression eqSearch(String search) {
		if (search == null) {
			return null;
		}
		return alertManagerEntity.alertName.contains(search);
	}

	private BooleanExpression eqCategoryType(String categoryType) {
		if (categoryType == null) {
			return null;
		}
		return alertManagerReceiveEntity.categoryType.eq(AlertManagerCategoryType.valueOf(categoryType));
	}

	public BooleanExpression ltStartDate(LocalDateTime endDate) {
		return endDate == null ? null : alertManagerReceiveEntity.realTime.lt(endDate);
	}

	public BooleanExpression gtEndDate(LocalDateTime startDate) {
		return startDate == null ? null : alertManagerReceiveEntity.realTime.gt(startDate);
	}
}
