package com.xiilab.modulek8sdb.dataset.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.dataset.entity.QDataset;
import com.xiilab.moduleuser.enumeration.AuthType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.moduleuser.dto.UserInfoDTO;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatasetRepositoryImpl implements DatasetRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO) {
		List<Dataset> datasets = queryFactory.selectFrom(QDataset.dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.orderBy(QDataset.dataset.regDate.desc())
			.fetch();

		Long count = queryFactory.select(QDataset.dataset.count())
			.from(QDataset.dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.fetchOne();
		return new PageImpl<>(datasets, pageRequest, count);
	}


	@Override
	public List<Dataset> findByAuthority(UserInfoDTO userInfoDTO) {
		List<Dataset> datasets = queryFactory.selectFrom(QDataset.dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.fetch();
		return datasets;
	}

	@Override
	public Dataset getDatasetWithStorage(Long datasetId) {
		return queryFactory.selectFrom(QDataset.dataset)
			.where(datasetIdEq(datasetId))
			.fetchOne();
	}

	private Predicate datasetIdEq(Long datasetId) {
		return datasetId != null ? QDataset.dataset.datasetId.eq(datasetId) : null;
	}

	private Predicate creatorEq(String creator, AuthType authType) {
		if(authType == AuthType.ROLE_USER){
			return StringUtils.hasText(creator) ? QDataset.dataset.regUser.regUserId.eq(creator) : null;
		}
		return null;
	}
}
