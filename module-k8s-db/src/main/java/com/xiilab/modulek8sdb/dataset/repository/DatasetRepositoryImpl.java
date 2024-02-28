package com.xiilab.modulek8sdb.dataset.repository;



import static com.xiilab.modulek8sdb.dataset.entity.QDataset.*;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulek8sdb.common.enums.RepositorySortType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatasetRepositoryImpl implements DatasetRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType userAuth,
		RepositorySearchCondition repositorySearchCondition) {
		RepositorySortType sortType = repositorySearchCondition.getSort();
		OrderSpecifier<? extends Serializable> sort =
			sortType == RepositorySortType.NAME ? dataset.datasetName.desc() : dataset.regDate.desc();

		List<Dataset> datasets = queryFactory.selectFrom(dataset)
			.where(
				creatorEq(userId, userAuth),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				datasetNameOrCreatorNameContains(repositorySearchCondition.getSearchText())
			)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.orderBy(sort)
			.fetch();

		Long count = queryFactory.select(dataset.count())
			.from(dataset)
			.where(
				creatorEq(userId, userAuth),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				datasetNameOrCreatorNameContains(repositorySearchCondition.getSearchText())
			)
			.fetchOne();
		return new PageImpl<>(datasets, pageRequest, count);
	}

	private Predicate datasetNameOrCreatorNameContains(String searchText) {
		return StringUtils.hasText(searchText) ? dataset.regUser.regUserRealName.contains(searchText)
			.or(dataset.datasetName.contains(searchText)) : null;
	}

	private Predicate repositoryDivisionEq(RepositoryDivision repositoryDivision) {
		return repositoryDivision != null ? dataset.division.eq(repositoryDivision) : null;
	}

	@Override
	public List<Dataset> findByAuthority(String userId, AuthType userAuth) {
		List<Dataset> datasets = queryFactory.selectFrom(dataset)
			.where(creatorEq(userId, userAuth))
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
