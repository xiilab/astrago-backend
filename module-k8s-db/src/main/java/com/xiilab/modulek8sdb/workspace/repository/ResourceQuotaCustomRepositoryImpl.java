package com.xiilab.modulek8sdb.workspace.repository;

import static com.xiilab.modulek8sdb.workspace.entity.QResourceQuotaEntity.*;

import org.springframework.stereotype.Repository;

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
			.where(resourceQuotaEntity.workspace.eq(name))
			.orderBy(resourceQuotaEntity.regDate.desc())
			.limit(1)
			.fetchOne();
	}
}
