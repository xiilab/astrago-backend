package com.xiilab.modulek8sdb.preset.repository;

import static com.xiilab.modulek8sdb.preset.entity.QResourcePresetEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulek8sdb.preset.entity.ResourcePresetEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ResourcePresetRepositoryCustomImpl implements ResourcePresetRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ResourcePresetEntity> findResourcePresets(Integer page, Integer size, NodeType nodeType) {
		Long totalCount = queryFactory.select(resourcePresetEntity.count())
			.from(resourcePresetEntity)
			.where(
				eqNodeType(nodeType)
			)
			.fetchOne();

		List<ResourcePresetEntity> result = queryFactory.selectFrom(resourcePresetEntity)
			.where(
				eqNodeType(nodeType)
			)
			.offset(page)
			.limit(size)
			.fetch();

		return new PageImpl<>(result, PageRequest.of(page, size), totalCount);
	}

	private void createPageRequest(Integer page, Integer size) {

	}

	private BooleanExpression eqNodeType(NodeType nodeType) {
		return !ObjectUtils.isEmpty(nodeType) ? resourcePresetEntity.nodeType.eq(nodeType) : null;
	}
}
