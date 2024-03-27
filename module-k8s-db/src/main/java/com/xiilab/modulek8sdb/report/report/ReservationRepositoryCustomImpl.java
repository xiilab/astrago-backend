package com.xiilab.modulek8sdb.report.report;

import static com.xiilab.modulek8sdb.report.entity.QReportReservationEntity.*;
import static com.xiilab.modulek8sdb.report.entity.QReportReservationHistoryEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.report.dto.ReportReservationDTO;
import com.xiilab.modulek8sdb.report.entity.ReportReservationEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryCustomImpl implements ReservationRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ReportReservationEntity> getReportReservationList(String userId, Pageable pageable) {

		List<ReportReservationEntity> reportReservationEntityList = queryFactory.selectFrom(reportReservationEntity)
			.where(reportReservationEntity.regUser.regUserId.eq(userId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(reportReservationEntity.countDistinct())
			.from(reportReservationEntity)
			.where(reportReservationEntity.regUser.regUserId.eq(userId))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchCount();
		return new PageImpl<>(reportReservationEntityList, pageable, count);
	}

	@Override
	public Page<ReportReservationDTO.ReceiveDTO> getReportReceiveList(String userId, Pageable pageable) {
		Expression<Boolean> resultExpression = Expressions.booleanTemplate(
			"IF(SUM(IF({0} = false, 1, 0)) > 0, false, true)",
			reportReservationHistoryEntity.result);

		Expression<String> timeGroupExpression = Expressions.stringTemplate(
			"DATE_FORMAT({0}, {1})", reportReservationHistoryEntity.transferDate, "%Y-%m-%d %H:%i:%s");

		List<ReportReservationDTO.ReceiveDTO> receiveDTOList = queryFactory
			.select(Projections.constructor(ReportReservationDTO.ReceiveDTO.class,
				reportReservationEntity.id,
				reportReservationEntity.name,
				reportReservationHistoryEntity.count(),
				timeGroupExpression,
				resultExpression))
			.from(reportReservationHistoryEntity)
			.leftJoin(reportReservationEntity)
			.on(reportReservationEntity.id.eq(reportReservationHistoryEntity.report.id))
			.where(reportReservationEntity.regUser.regUserId.eq(userId))
			.groupBy(timeGroupExpression, reportReservationEntity.id)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long count = queryFactory
			.select(reportReservationHistoryEntity.id.count())
			.from(reportReservationHistoryEntity)
			.leftJoin(reportReservationEntity)
			.on(reportReservationEntity.id.eq(reportReservationHistoryEntity.report.id))
			.where(reportReservationEntity.regUser.regUserId.eq(userId))
			.groupBy(timeGroupExpression, reportReservationEntity.id)
			.fetchCount();

		return new PageImpl<>(receiveDTOList, pageable, count);
	}

	@Override
	public ReportReservationEntity getReportReceiveListById(long id, String userId) {
		return queryFactory.selectFrom(reportReservationEntity)
			.leftJoin(reportReservationHistoryEntity)
			.on(reportReservationEntity.id.eq(reportReservationHistoryEntity.report.id))
			.where(reportReservationEntity.id.eq(id),
				reportReservationEntity.regUser.regUserId.eq(userId))
			.fetchOne();
	}

}
