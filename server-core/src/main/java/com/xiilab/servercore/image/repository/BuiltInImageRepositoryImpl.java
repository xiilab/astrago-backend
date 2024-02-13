package com.xiilab.servercore.image.repository;

import static com.xiilab.servercore.image.entity.QBuiltInImageEntity.*;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.image.entity.BuiltInImageEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BuiltInImageRepositoryImpl implements BuiltInImageRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<BuiltInImageEntity> findByType(WorkloadType type) {
		return queryFactory.selectFrom(builtInImageEntity)
			.where(eqType(type))
			.fetch();
	}

	private BooleanExpression eqType(WorkloadType type) {
		return !ObjectUtils.isEmpty(type)? builtInImageEntity.type.eq(type) : null;
	}
}
