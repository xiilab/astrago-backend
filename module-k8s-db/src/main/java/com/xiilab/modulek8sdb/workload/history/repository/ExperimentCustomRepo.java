package com.xiilab.modulek8sdb.workload.history.repository;

import static com.xiilab.modulek8sdb.label.entity.QLabelEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QExperimentEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QLabelExperimentMappingEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QWorkloadEntity.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.workload.history.dto.ExperimentQueryResult;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ExperimentCustomRepo {
	private final JPAQueryFactory jpaQueryFactory;

	public Page<ExperimentQueryResult> getExperiments(String searchCondition, String workspace, String userId,
		WorkloadStatus status, Pageable pageable) {
		// Main query to fetch experiment details with workload details
		List<ExperimentQueryResult.Experiment> experiments = jpaQueryFactory.select(Projections.constructor(
				ExperimentQueryResult.Experiment.class,
				experimentEntity.uuid,
				workloadEntity.name,
				workloadEntity.resourceName,
				workloadEntity.workspaceName,
				workloadEntity.workloadStatus,
				workloadEntity.creatorName,
				experimentEntity.isViewYN
			))
			.from(experimentEntity)
			.leftJoin(workloadEntity).on(workloadEntity.eq(experimentEntity.workload))
			.where(
				containsSearchCondition(searchCondition),
				eqWorkspace(workspace),
				eqUserId(userId),
				eqStatus(status)
			)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// Fetch labels for the retrieved experiments
		List<Tuple> labelMappings = jpaQueryFactory.select(
				labelExperimentMappingEntity.experiment.uuid,
				labelEntity.id,
				labelEntity.name,
				labelEntity.colorCode,
				labelEntity.colorName
			)
			.from(labelExperimentMappingEntity)
			.leftJoin(labelEntity).on(labelExperimentMappingEntity.label.eq(labelEntity))
			.where(labelExperimentMappingEntity.experiment.uuid.in(
				experiments.stream().map(ExperimentQueryResult.Experiment::getId).collect(Collectors.toList())))
			.fetch();

		// Map labels to experiments
		Map<String, List<ExperimentQueryResult.LabelDTO>> labelMap = labelMappings.stream()
			.filter(tuple -> tuple.get(0, String.class) != null)
			.collect(Collectors.groupingBy(
				tuple -> tuple.get(0, String.class),
				Collectors.mapping(tuple -> new ExperimentQueryResult.LabelDTO(
					tuple.get(labelEntity.id),
					tuple.get(labelEntity.name),
					tuple.get(labelEntity.colorCode),
					tuple.get(labelEntity.colorName)
				), Collectors.toList())
			));

		List<ExperimentQueryResult> experimentQueryResults = experiments.stream().map(exp
				-> new ExperimentQueryResult(
				exp.getId(),
				exp.getWorkloadName(),
				exp.getWorkloadResourceName(),
				exp.getWorkspaceName(),
				exp.getStatus(),
				exp.getUsername(),
				exp.isView(),
				labelMap.get(exp.getId())
			))
			.toList();

		// Count query for pagination
		JPAQuery<Long> countQuery = jpaQueryFactory.select(experimentEntity.count())
			.from(experimentEntity)
			.leftJoin(workloadEntity).on(workloadEntity.eq(experimentEntity.workload))
			.where(
				containsSearchCondition(searchCondition),
				eqWorkspace(workspace),
				eqUserId(userId),
				eqStatus(status)
			);

		return PageableExecutionUtils.getPage(experimentQueryResults, pageable, countQuery::fetchOne);
	}

	private BooleanExpression containsSearchCondition(String searchCondition) {
		return StringUtils.hasText(searchCondition) ? workloadEntity.name.contains(searchCondition) : null;
	}

	private BooleanExpression eqWorkspace(String workspace) {
		return StringUtils.hasText(workspace) ? workloadEntity.workspaceResourceName.eq(workspace) : null;
	}

	private BooleanExpression eqUserId(String userId) {
		return workloadEntity.creatorId.eq(userId);
	}

	private BooleanExpression eqStatus(WorkloadStatus status) {
		return status != null ? workloadEntity.workloadStatus.eq(status) : null;
	}
}
