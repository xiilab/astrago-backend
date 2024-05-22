package com.xiilab.modulek8sdb.workload.history.repository;

import static com.xiilab.modulek8sdb.workload.history.entity.QJobEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QWorkloadEntity.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkloadHistoryRepoCustomImpl implements WorkloadHistoryRepoCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<JobEntity> findBatchWorkloadHistoryByCondition(String workspaceName, String searchName, String userId,
		WorkloadType workloadType) {
		return queryFactory
			.selectFrom(jobEntity)
			.where(
				jobEntity.workloadType.eq(workloadType),
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqUserId(userId),
				eqWorkloadType(workloadType),
				jobEntity.deleteYN.eq(DeleteYN.N)
			).fetch();
	}

	@Override
	public JobEntity findByWorkspaceNameRecently(String workspace, String username) {
		return queryFactory
			.selectFrom(jobEntity)
			.where(jobEntity.workspaceResourceName.eq(workspace).and(jobEntity.creatorName.eq(username)))
			.orderBy(jobEntity.createdAt.desc())
			.limit(1)
			.fetchOne();
	}

	@Override
	public List<JobEntity> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList,
		WorkloadType workloadType, WorkloadSortCondition sortCondition) {
		return queryFactory.selectFrom(jobEntity)
			.where(jobEntity.resourceName.in(pinResourceNameList),
				jobEntity.workloadType.eq(workloadType))
			.orderBy(createOrderSpecifier(sortCondition))
			.fetch();
	}

	@Override
	public Page<JobEntity> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType, String searchName,
		String userId, List<String> pinResourceNameList, WorkloadSortCondition workloadSortCondition,
		PageRequest pageRequest,
		WorkloadStatus workloadStatus) {
		List<JobEntity> jobEntities = queryFactory.selectFrom(jobEntity)
			.where(
				resourceNameNotIn(pinResourceNameList),
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqUserId(userId),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				jobEntity.deleteYN.eq(DeleteYN.N)
			).orderBy(createOrderSpecifier(workloadSortCondition))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(jobEntity.count())
			.from(jobEntity)
			.where(
				resourceNameNotIn(pinResourceNameList),
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqUserId(userId),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				jobEntity.deleteYN.eq(DeleteYN.N)
			).fetchOne();
		return new PageImpl<>(jobEntities, pageRequest, totalCount);
	}

	@Override
	public List<WorkloadEntity> getWorkloadsByWorkspaceIdsAndBetweenCreatedAt(List<String> workspaceIds,
		LocalDate startDate,
		LocalDate endDate) {
		LocalDateTime startTime = startDate.atStartOfDay();
		LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
		return queryFactory.selectFrom(workloadEntity)
			.where(
				workloadEntity.workspaceResourceName.in(workspaceIds),
				workloadEntity.createdAt.between(startTime, endTime)
			)
			.orderBy(workloadEntity.creatorRealName.asc())
			.fetch();
	}

	private Predicate workloadStatusEq(WorkloadStatus workloadStatus) {
		return workloadStatus != null ? jobEntity.workloadStatus.eq(workloadStatus) : null;
	}

	private Predicate resourceNameNotIn(List<String> resourceNames) {
		return resourceNames.size() != 0 ? jobEntity.resourceName.notIn(resourceNames) : null;
	}

	private OrderSpecifier createOrderSpecifier(WorkloadSortCondition sortType) {
		return switch (sortType) {
			case AGE_ASC -> new OrderSpecifier<>(Order.ASC, jobEntity.createdAt);
			case AGE_DESC -> new OrderSpecifier<>(Order.DESC, jobEntity.createdAt);
			case REMAIN_TIME_ASC -> new OrderSpecifier<>(Order.ASC, jobEntity.remainTime);
			case REMAIN_TIME_DESC -> new OrderSpecifier<>(Order.DESC, jobEntity.remainTime);
		};
	}

	private BooleanExpression eqName(String searchName) {
		if (searchName == null) {
			return null;
		}
		return jobEntity.name.contains(searchName);
	}

	private BooleanExpression eqUserId(String userId) {
		if (userId == null) {
			return null;
		}
		return jobEntity.creatorId.eq(userId);
	}

	private BooleanExpression eqWorkloadType(WorkloadType workloadType) {
		if (workloadType == null) {
			return null;
		}
		return jobEntity.workloadType.eq(workloadType);
	}

	private BooleanExpression eqWorkspaceName(String workspaceName) {
		if (workspaceName == null) {
			return null;
		}
		return jobEntity.workspaceResourceName.eq(workspaceName);
	}
}
