package com.xiilab.modulek8sdb.workload.history.repository;

import static com.xiilab.modulek8sdb.workload.history.entity.QExperimentEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QWorkloadEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExperimentCustomRepo {
	private final JPAQueryFactory jpaQueryFactory;

	public Page<ExperimentDTO> getExperiments(String searchCondition, WorkloadStatus workloadStatus,
		Pageable pageable) {
		List<ExperimentDTO> content = jpaQueryFactory.select(Projections.constructor(ExperimentDTO.class,
				workloadEntity.name,
				workloadEntity.resourceName,
				workloadEntity.workspaceName,
				experimentEntity.uuid,
				workloadEntity.workloadStatus,
				experimentEntity.isViewYN
			))
			.from(experimentEntity)
			.leftJoin(workloadEntity)
			.on(workloadEntity.eq(experimentEntity.workload))
			.where(containsSearchCondition(searchCondition), eqWorkloadStatus(workloadStatus))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		JPAQuery<Long> countQuery = jpaQueryFactory.select(experimentEntity.count())
			.from(experimentEntity)
			.leftJoin(workloadEntity)
			.on(workloadEntity.eq(experimentEntity.workload))
			.where(containsSearchCondition(searchCondition), eqWorkloadStatus(workloadStatus));

		return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
	}

	private BooleanExpression containsSearchCondition(String searchCondition) {
		return StringUtils.hasText(searchCondition) ? workloadEntity.name.contains(searchCondition) : null;
	}

	private BooleanExpression eqWorkloadStatus(WorkloadStatus workloadStatus) {
		return workloadStatus != null ? workloadEntity.workloadStatus.eq(workloadStatus) : null;
	}
}
