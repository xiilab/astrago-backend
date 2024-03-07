package com.xiilab.modulek8sdb.hub.repository;

import java.util.List;

import static com.xiilab.modulek8sdb.hub.entity.QHubCategoryMappingEntity.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubCategoryMappingRepositoryImpl implements HubCategoryMappingRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<HubCategoryMappingEntity> findHubs(List<String> categoryNames, List<Long> hubIds, Pageable pageable) {
		Long totalCount= queryFactory.select(hubCategoryMappingEntity.count())
			.from(hubCategoryMappingEntity)
			.join(hubCategoryMappingEntity.hubCategoryEntity)
			.join(hubCategoryMappingEntity.hubEntity)
			.where(
				eqCategoryNames(categoryNames),
				eqHubIds(hubIds)
			)
			.fetchOne();

		JPAQuery<HubCategoryMappingEntity> query = queryFactory.selectFrom(hubCategoryMappingEntity)
			.join(hubCategoryMappingEntity.hubCategoryEntity).fetchJoin()
			.join(hubCategoryMappingEntity.hubEntity).fetchJoin()
			.where(
				eqCategoryNames(categoryNames),
				eqHubIds(hubIds)
			);

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<HubCategoryMappingEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}

	private BooleanExpression eqCategoryNames(List<String> categoryNames) {
		return !CollectionUtils.isEmpty(categoryNames) ? hubCategoryMappingEntity.hubCategoryEntity.name.in(categoryNames) : null;
	}

	private BooleanExpression eqHubIds(List<Long> hubIds) {
		return !CollectionUtils.isEmpty(hubIds) ? hubCategoryMappingEntity.hubEntity.hubId.in(hubIds) : null;
	}
}
