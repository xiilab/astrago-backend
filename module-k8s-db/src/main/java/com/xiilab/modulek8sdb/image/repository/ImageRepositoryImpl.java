package com.xiilab.modulek8sdb.image.repository;

import static com.xiilab.modulek8sdb.image.entity.QImageEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ImageEntity> findByImages(ImageType imageType, WorkloadType workloadType, boolean multiNode, Pageable pageable) {
		Long totalCount = queryFactory.select(imageEntity.count())
			.from(imageEntity)
			.where(
				eqImageType(imageType),
				eqWorkloadType(workloadType),
				eqMultiNodeYN(multiNode)
			).fetchOne();

		JPAQuery<ImageEntity> query = queryFactory.selectFrom(imageEntity)
			.where(
				eqImageType(imageType),
				eqWorkloadType(workloadType),
				eqMultiNodeYN(multiNode)
			);

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<ImageEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}

	private BooleanExpression eqImageType(ImageType imageType) {
		return !ObjectUtils.isEmpty(imageType) ? imageEntity.imageType.eq(imageType) : null;
	}

	private BooleanExpression eqWorkloadType(WorkloadType workloadType) {
		return !ObjectUtils.isEmpty(workloadType) ? imageEntity.workloadType.eq(workloadType) : null;
	}
	private BooleanExpression eqMultiNodeYN(boolean multiNodeYN) {
		return !ObjectUtils.isEmpty(multiNodeYN) ? imageEntity.multiNode.eq(multiNodeYN) : null;
	}
}
