package com.xiilab.modulek8sdb.hub.repository;

import static com.xiilab.modulek8sdb.hub.entity.QHubCategoryMappingEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;
import com.xiilab.modulek8sdb.hub.enums.HubLabelType;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubCategoryMappingRepositoryImpl implements HubCategoryMappingRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<HubCategoryMappingEntity> findHubCategoryMapping(String searchText, List<String> categoryNames,
		List<Long> hubIds, Pageable pageable) {
		Long totalCount = queryFactory.select(hubCategoryMappingEntity.count())
			.from(hubCategoryMappingEntity)
			.join(hubCategoryMappingEntity.hubCategoryEntity)
			.join(hubCategoryMappingEntity.hubEntity)
			.where(
				likeSearchText(searchText),
				eqCategoryNames(categoryNames),
				eqHubIds(hubIds)
			)
			.fetchOne();

		JPAQuery<HubCategoryMappingEntity> query = queryFactory.selectFrom(hubCategoryMappingEntity)
			.join(hubCategoryMappingEntity.hubCategoryEntity).fetchJoin()
			.join(hubCategoryMappingEntity.hubEntity).fetchJoin()
			.where(
				likeSearchText(searchText),
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
		if (!CollectionUtils.isEmpty(categoryNames)) {
			List<HubLabelType> hubLabelTypes = categoryNames.stream()
				.map(HubLabelType::valueOf) // Assuming HubLabelType is the enum class
				.toList();
			return hubCategoryMappingEntity.hubCategoryEntity.hubLabelType.in(hubLabelTypes);
		} else {
			return null;
		}
	}

	private BooleanExpression eqHubIds(List<Long> hubIds) {
		return !CollectionUtils.isEmpty(hubIds) ? hubCategoryMappingEntity.hubEntity.hubId.in(hubIds) : null;
	}

	private BooleanExpression likeSearchText(String searchText) {
		return StringUtils.hasText(searchText) ? hubCategoryMappingEntity.hubEntity.title.contains(searchText)
			.or(hubCategoryMappingEntity.hubEntity.description.contains(searchText))
			: null;
	}
}
