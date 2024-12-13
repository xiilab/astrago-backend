package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.xiilab.modulek8sdb.alert.systemalert.entity.QSystemAlertEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.AlertType;
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SystemAlertRepositoryCustomImpl implements SystemAlertRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<SystemAlertEntity> findAlerts(String recipientId, AlertType alertType, AlertRole alertRole, ReadYN readYN,
		String searchText,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate, Pageable pageable) {
		Long totalCount = queryFactory.select(systemAlertEntity.count())
			.from(systemAlertEntity)
			.where(
				eqRecipientId(recipientId),
				eqSystemAlertType(alertType),
				eqReadYn(readYN),
				likeSearchText(searchText),
				betweenRegDate(searchStartDate, searchEndDate),
				eqAlertRole(alertRole)
			)
			.fetchOne();

		JPAQuery<SystemAlertEntity> query = queryFactory.selectFrom(systemAlertEntity)
			.where(
				eqRecipientId(recipientId),
				eqSystemAlertType(alertType),
				eqReadYn(readYN),
				likeSearchText(searchText),
				betweenRegDate(searchStartDate, searchEndDate),
				eqAlertRole(alertRole)
			);

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		query.orderBy(systemAlertEntity.regDate.desc());
		List<SystemAlertEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}

	@Override
	public List<SystemAlertEntity> getAllSystemAlertList(String recipientId, AlertRole alertRole) {
		return queryFactory.selectFrom(systemAlertEntity)
			.where(
				eqRecipientId(recipientId),
				eqReadYn(ReadYN.N),
				eqAlertRole(alertRole)
			).fetch();
	}

	private BooleanExpression eqRecipientId(String recipientId) {
		return StringUtils.hasText(recipientId) ? systemAlertEntity.recipientId.eq(recipientId) : null;
	}

	private BooleanExpression eqSystemAlertType(AlertType alertType) {
		return !ObjectUtils.isEmpty(alertType) ? systemAlertEntity.alertType.eq(alertType) : null;
	}

	private BooleanExpression eqReadYn(ReadYN readYN) {
		return !ObjectUtils.isEmpty(readYN) ? systemAlertEntity.readYN.eq(readYN) : null;
	}

	private BooleanExpression betweenRegDate(LocalDateTime searchStartDate, LocalDateTime searchEndDate) {
		return !ObjectUtils.isEmpty(searchStartDate) && !ObjectUtils.isEmpty(searchEndDate) ?
			systemAlertEntity.regDate.between(searchStartDate, searchEndDate) : null;
	}

	private BooleanExpression likeSearchText(String searchText) {
		return StringUtils.hasText(searchText) ? systemAlertEntity.title.contains(searchText)
			.or(systemAlertEntity.message.contains(searchText))
			: null;
	}

	private BooleanExpression eqAlertRole(AlertRole alertRole) {
		if (alertRole == AlertRole.USER) {
			return systemAlertEntity.alertRole.eq(AlertRole.USER)
				.or(systemAlertEntity.alertRole.eq(AlertRole.OWNER));
		} else if (alertRole == AlertRole.ADMIN) {
			return systemAlertEntity.alertRole.eq(AlertRole.ADMIN);
		} else {
			return null;
		}
	}
}
