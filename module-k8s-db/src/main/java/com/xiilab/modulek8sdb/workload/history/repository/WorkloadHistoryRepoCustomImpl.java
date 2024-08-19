package com.xiilab.modulek8sdb.workload.history.repository;

import static com.xiilab.modulek8sdb.workload.history.entity.QDevelopEntity.*;
import static com.xiilab.modulek8sdb.workload.history.entity.QWorkloadEntity.*;

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
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkloadHistoryRepoCustomImpl implements WorkloadHistoryRepoCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public WorkloadEntity findByWorkspaceNameRecently(String workspace, String username) {
		return queryFactory
			.selectFrom(workloadEntity)
			.where(workloadEntity.workspaceResourceName.eq(workspace).and(workloadEntity.creatorName.eq(username)),
				notInWorkloadType(WorkloadType.DEPLOY))
			.orderBy(workloadEntity.createdAt.desc())
			.limit(1)
			.fetchOne();
	}

	@Override
	public List<WorkloadEntity> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList,
		WorkloadType workloadType, WorkloadSortCondition sortCondition) {
		return queryFactory.selectFrom(workloadEntity)
			.where(workloadEntity.resourceName.in(pinResourceNameList),
				eqWorkloadType(workloadType),
				notInWorkloadType(WorkloadType.DEPLOY))
			.orderBy(createOrderSpecifier(sortCondition))
			.fetch();
	}


	@Override
	public Page<WorkloadEntity> getOverViewWorkloadList(String workspaceName, WorkloadType workloadType,
		String searchName,
		String userId, List<String> pinResourceNameList, WorkloadSortCondition workloadSortCondition, PageRequest pageRequest,
		WorkloadStatus workloadStatus) {
		List<WorkloadEntity> jobEntities = queryFactory.selectFrom(workloadEntity)
			.where(
				resourceNameNotIn(pinResourceNameList),
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqUserId(userId),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				workloadEntity.deleteYN.eq(DeleteYN.N),
				notInWorkloadType(WorkloadType.DEPLOY)
			).orderBy(createOrderSpecifier(workloadSortCondition))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(workloadEntity.count())
			.from(workloadEntity)
			.where(
				resourceNameNotIn(pinResourceNameList),
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqUserId(userId),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				workloadEntity.deleteYN.eq(DeleteYN.N),
				notInWorkloadType(WorkloadType.DEPLOY)
			).fetchOne();

		return new PageImpl<>(jobEntities, pageRequest, totalCount);
	}

	@Override
	public Page<WorkloadEntity> getAdminWorkloadList(String workspaceName, WorkloadType workloadType, String searchName,
		WorkloadSortCondition workloadSortCondition, PageRequest pageRequest, WorkloadStatus workloadStatus) {
		List<WorkloadEntity> jobEntities = queryFactory.selectFrom(workloadEntity)
			.where(
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				workloadEntity.deleteYN.eq(DeleteYN.N),
				notInWorkloadType(WorkloadType.DEPLOY)
			).orderBy(createOrderSpecifier(workloadSortCondition))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long totalCount = queryFactory.select(workloadEntity.count())
			.from(workloadEntity)
			.where(
				eqWorkspaceName(workspaceName),
				eqName(searchName),
				eqWorkloadType(workloadType),
				workloadStatusEq(workloadStatus),
				workloadEntity.deleteYN.eq(DeleteYN.N),
				notInWorkloadType(WorkloadType.DEPLOY)
			).fetchOne();
		return new PageImpl<>(jobEntities, pageRequest, totalCount);
	}

	private Predicate notInWorkloadType(WorkloadType workloadType) {
		return workloadEntity.workloadType.notIn(workloadType);

	}

	private Predicate workloadStatusEq(WorkloadStatus workloadStatus){
		return workloadStatus != null ? workloadEntity.workloadStatus.eq(workloadStatus) : null;
	}
	private Predicate resourceNameNotIn(List<String> resourceNames){
		return resourceNames.size() != 0 ? workloadEntity.resourceName.notIn(resourceNames) : null;
	}
	private OrderSpecifier createOrderSpecifier(WorkloadSortCondition sortType) {
		return switch (sortType) {
			case AGE_ASC -> new OrderSpecifier<>(Order.ASC, workloadEntity.createdAt);
			case AGE_DESC -> new OrderSpecifier<>(Order.DESC, workloadEntity.createdAt);
			case REMAIN_TIME_ASC -> new OrderSpecifier<>(Order.ASC, developEntity.remainTime);
			case REMAIN_TIME_DESC -> new OrderSpecifier<>(Order.DESC, developEntity.remainTime);
		};
	}
	private BooleanExpression eqName(String searchName) {
		if (searchName == null) {
			return null;
		}
		return workloadEntity.name.contains(searchName);
	}

	private BooleanExpression eqUserId(String userId) {
		if (userId == null) {
			return null;
		}
		return workloadEntity.creatorId.eq(userId);
	}

	private BooleanExpression eqWorkloadType(WorkloadType workloadType) {
		if (workloadType == null) {
			return null;
		}
		return workloadEntity.workloadType.eq(workloadType);
	}

	private BooleanExpression eqWorkspaceName(String workspaceName) {
		if (workspaceName == null) {
			return null;
		}
		return workloadEntity.workspaceResourceName.eq(workspaceName);
	}
}
