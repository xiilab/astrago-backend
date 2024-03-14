package com.xiilab.modulek8sdb.hub.repository;

import static com.xiilab.modulek8sdb.hub.entity.QHubCategoryMappingEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;

import static com.xiilab.modulek8sdb.hub.entity.QHubEntity.*;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRepositoryCustomImpl implements HubRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<HubEntity> findHubs(String searchText, Pageable pageable) {
		Long totalCount = queryFactory.select(hubEntity.count())
			.from(hubEntity)
			.where(
				likeSearchText(searchText)
			)
			.fetchOne();

		JPAQuery<HubEntity> query = queryFactory.selectFrom(hubEntity)
			.where(
				likeSearchText(searchText)
			);

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<HubEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}

	private BooleanExpression likeSearchText(String searchText) {
		return StringUtils.hasText(searchText) ? hubEntity.title.like("%" + searchText)
			.and(hubEntity.description.like("%" + searchText))
			: null;
	}
}
