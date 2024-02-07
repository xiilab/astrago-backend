package com.xiilab.servercore.dataset.repository;


import static com.xiilab.servercore.dataset.entity.QDataset.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.entity.Dataset;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatasetRepositoryImpl implements DatasetRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO) {
		List<Dataset> datasets = queryFactory.selectFrom(dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long count = queryFactory.select(dataset.count())
			.from(dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.fetchOne();
		return new PageImpl<>(datasets, pageRequest, count);
	}


	@Override
	public List<Dataset> findByAuthority(UserInfoDTO userInfoDTO) {
		List<Dataset> datasets = queryFactory.selectFrom(dataset)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.fetch();
		return datasets;
	}

	@Override
	public Dataset getDatasetWithStorage(Long datasetId) {
		return queryFactory.selectFrom(dataset)
			.where(datasetIdEq(datasetId))
			.fetchOne();
	}

	private Predicate datasetIdEq(Long datasetId) {
		return datasetId != null ? dataset.datasetId.eq(datasetId) : null;
	}

	private Predicate creatorEq(String creator, AuthType authType) {
		if(authType == AuthType.ROLE_USER){
			return StringUtils.hasText(creator) ? dataset.regUser.regUserId.eq(creator) : null;
		}
		return null;
	}
}
