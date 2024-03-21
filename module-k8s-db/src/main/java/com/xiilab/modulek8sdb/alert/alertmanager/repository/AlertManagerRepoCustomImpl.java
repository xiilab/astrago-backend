package com.xiilab.modulek8sdb.alert.alertmanager.repository;

import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerEntity.*;
import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerReceiveEntity.*;
import static com.xiilab.modulek8sdb.alert.alertmanager.entity.QAlertManagerUserEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
	public Page<AlertManagerReceiveEntity> getAlertManagerReceiveList(String categoryType, String search,
		LocalDateTime startDate, LocalDateTime endDate, String userId, Pageable pageable){

		List<AlertManagerReceiveEntity> receiveEntityList = queryFactory
			.selectFrom(alertManagerReceiveEntity)
			.leftJoin(alertManagerEntity)
			.on(alertManagerEntity.id.eq(alertManagerReceiveEntity.alertManager.id))
			.leftJoin(alertManagerUserEntity)
			.on(alertManagerUserEntity.alertManager.id.eq(alertManagerEntity.id))
			.where(alertManagerUserEntity.userId.eq(userId),
				eqSearch(search),
				eqCategoryType(categoryType),
				ltStartDate(endDate), gtEndDate(startDate)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		//count를 위한 쿼리
		Long count = queryFactory
			.select(alertManagerReceiveEntity.countDistinct())
			.from(alertManagerReceiveEntity)
			.leftJoin(alertManagerEntity)
			.on(alertManagerEntity.id.eq(alertManagerReceiveEntity.alertManager.id))
			.leftJoin(alertManagerUserEntity)
			.on(alertManagerUserEntity.alertManager.id.eq(alertManagerEntity.id))
			.where(alertManagerUserEntity.userId.eq(userId),
				eqSearch(search),
				eqCategoryType(categoryType),
				ltStartDate(endDate), gtEndDate(startDate)
			).fetchOne();

		return new PageImpl<>(receiveEntityList, pageable, count);
	}

	private BooleanExpression eqSearch(String search) {
		if (search == null) {
			return null;
		}
		return alertManagerReceiveEntity.nodeName.contains(search).or(alertManagerReceiveEntity.nodeIp.contains(search));
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
