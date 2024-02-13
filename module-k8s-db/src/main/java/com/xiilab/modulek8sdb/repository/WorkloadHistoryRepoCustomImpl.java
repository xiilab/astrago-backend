package com.xiilab.modulek8sdb.repository;


import static com.xiilab.modulek8sdb.entity.QJobEntity.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WorkloadHistoryRepoCustomImpl implements WorkloadHistoryRepoCusotm {
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
				eqWorkloadType(workloadType)
			).fetch();
	}

	private BooleanExpression eqName(String searchName) {
		if (searchName == null) {
			return null;
		}
		return jobEntity.name.eq(searchName);
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
		return jobEntity.workspaceName.eq(workspaceName);
	}
}
