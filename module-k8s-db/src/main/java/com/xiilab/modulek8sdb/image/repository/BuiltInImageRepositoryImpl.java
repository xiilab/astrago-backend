package com.xiilab.modulek8sdb.image.repository;


import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.QBuiltInImageEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BuiltInImageRepositoryImpl implements BuiltInImageRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<BuiltInImageEntity> findByType(WorkloadType type) {
		return queryFactory.selectFrom(QBuiltInImageEntity.builtInImageEntity)
			.where(eqType(type))
			.fetch();
	}

	private BooleanExpression eqType(WorkloadType type) {
		return !ObjectUtils.isEmpty(type)? QBuiltInImageEntity.builtInImageEntity.type.eq(type) : null;
	}
}
