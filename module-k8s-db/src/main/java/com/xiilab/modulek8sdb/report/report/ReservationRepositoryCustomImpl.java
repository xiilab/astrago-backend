package com.xiilab.modulek8sdb.report.report;

import static com.xiilab.modulek8sdb.report.entity.QReportReservationEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
			.fetchOne();

		return new PageImpl<>(reportReservationEntityList, pageable, count);
	}

}
