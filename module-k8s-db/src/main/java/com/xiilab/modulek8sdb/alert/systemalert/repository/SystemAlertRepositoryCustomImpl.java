package com.xiilab.modulek8sdb.alert.systemalert.repository;

import static com.xiilab.modulek8sdb.alert.systemalert.entity.QSystemAlertEntity.*;

import java.time.LocalDate;
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
import com.xiilab.modulecommon.enums.ReadYN;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulecommon.alert.enums.SystemAlertType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SystemAlertRepositoryCustomImpl implements SystemAlertRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<SystemAlertEntity> findAlerts(String recipientId, SystemAlertType systemAlertType, ReadYN readYN,
		LocalDateTime searchStartDate, LocalDateTime searchEndDate, Pageable pageable) {
		Long totalCount = queryFactory.select(systemAlertEntity.count())
			.from(systemAlertEntity)
			.where(
				eqRecipientId(recipientId),
				eqSystemAlertType(systemAlertType),
				eqReadYn(readYN),
				betweenRegDate(searchStartDate, searchEndDate)
			)
			.fetchOne();

		JPAQuery<SystemAlertEntity> query = queryFactory.selectFrom(systemAlertEntity)
			.where(
				eqRecipientId(recipientId),
				eqSystemAlertType(systemAlertType),
				eqReadYn(readYN),
				betweenRegDate(searchStartDate, searchEndDate)
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
		return StringUtils.hasText(recipientId) ? systemAlertEntity.recipientId.eq(recipientId) : null;
	}

	private BooleanExpression eqSystemAlertType(SystemAlertType systemAlertType) {
		return !ObjectUtils.isEmpty(systemAlertType) ? systemAlertEntity.systemAlertType.eq(systemAlertType) : null;
	}

	private BooleanExpression eqReadYn(ReadYN readYN) {
		return !ObjectUtils.isEmpty(readYN) ? systemAlertEntity.readYN.eq(readYN) : null;
	}

	private BooleanExpression betweenRegDate(LocalDateTime searchStartDate, LocalDateTime searchEndDate) {
		return !ObjectUtils.isEmpty(searchStartDate) && !ObjectUtils.isEmpty(searchEndDate) ?
			systemAlertEntity.regDate.between(searchStartDate, searchEndDate) : null;
	}
}
