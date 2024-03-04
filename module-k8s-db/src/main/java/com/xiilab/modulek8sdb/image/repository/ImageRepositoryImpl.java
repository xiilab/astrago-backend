package com.xiilab.modulek8sdb.image.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulecommon.enums.ImageType;

import static com.xiilab.modulek8sdb.image.entity.QImageEntity.*;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ImageEntity> findByType(ImageType imageType, WorkloadType workloadType) {
		return queryFactory.selectFrom(imageEntity)
			.where(
				eqImageType(imageType),
				eqWorkloadType(workloadType)
				)
			.fetch();
	}

	private BooleanExpression eqImageType(ImageType imageType) {
		return !ObjectUtils.isEmpty(imageType)? imageEntity.imageType.eq(imageType) : null;
	}

	private BooleanExpression eqWorkloadType(WorkloadType workloadType) {
		return !ObjectUtils.isEmpty(workloadType)? imageEntity.workloadType.eq(workloadType) : null;
	}
}
