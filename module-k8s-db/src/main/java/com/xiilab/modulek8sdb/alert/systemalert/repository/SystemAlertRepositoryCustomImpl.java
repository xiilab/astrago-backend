package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.xiilab.modulek8sdb.alert.systemalert.entity.QSystemAlertEntity.*;
import static com.xiilab.modulek8sdb.hub.entity.QHubEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
// import com.xiilab.modulek8sdb.hub.entity.HubEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SystemAlertRepositoryCustomImpl implements SystemAlertRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<SystemAlertEntity> findAlerts(String recipientId, Pageable pageable) {
		Long totalCount = queryFactory.select(systemAlertEntity.count())
			.from(systemAlertEntity)
			.where(
				eqRecipientId(recipientId)
			)
			.fetchOne();

		JPAQuery<SystemAlertEntity> query = queryFactory.selectFrom(systemAlertEntity)
			.where(
				eqRecipientId(recipientId)
			);

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<SystemAlertEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}

	private BooleanExpression eqRecipientId(String recipientId) {
		return !StringUtils.hasText(recipientId)? systemAlertEntity.recipientId.eq(recipientId) : null;
	}
}
