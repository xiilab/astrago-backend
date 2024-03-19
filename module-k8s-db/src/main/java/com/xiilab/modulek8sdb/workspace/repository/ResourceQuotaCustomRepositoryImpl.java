package com.xiilab.modulek8sdb.workspace.repository;

import static com.xiilab.modulek8sdb.workspace.entity.QResourceQuotaEntity.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResourceQuotaCustomRepositoryImpl implements ResourceQuotaCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public ResourceQuotaEntity findByWorkspaceRecently(String name) {
		return jpaQueryFactory
			.selectFrom(resourceQuotaEntity)
			.where(resourceQuotaEntity.workspaceResourceName.eq(name))
			.orderBy(resourceQuotaEntity.regDate.desc())
			.limit(1)
			.fetchOne();
	}

	@Override
	public List<ResourceQuotaEntity> findResourceQuotaByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
		return jpaQueryFactory
			.selectFrom(resourceQuotaEntity)
			.where(
				ltStartDate(endDate), gtEndDate(startDate)
			).fetch();
	}

	public BooleanExpression ltStartDate(LocalDateTime endDate) {
		return endDate == null ? null : resourceQuotaEntity.regDate.lt(endDate);
	}

	public BooleanExpression gtEndDate(LocalDateTime startDate) {
		return startDate == null ? null : resourceQuotaEntity.regDate.gt(startDate);
	}
}
