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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.common.enums.RepositorySortType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatasetRepositoryImpl implements DatasetRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Dataset> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType userAuth,
		RepositorySearchCondition repositorySearchCondition, PageMode pageMode) {
		RepositorySortType sortType = repositorySearchCondition.getSort();
		OrderSpecifier<? extends Serializable> sort =
			sortType == RepositorySortType.NAME ? dataset.datasetName.desc() :
				sortType == RepositorySortType.CREATED_AT ? dataset.regDate.desc() : dataset.datasetSize.desc();

		List<Dataset> datasets = queryFactory.selectFrom(dataset)
			.where(
				creatorEq(userId, userAuth, pageMode),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				datasetNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYNEqN()
			)
			.orderBy(sort)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long count = queryFactory.select(dataset.count())
			.from(dataset)
			.where(
				creatorEq(userId, userAuth, pageMode),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				datasetNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYNEqN()
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
			.where(creatorEq(userId, userAuth, PageMode.USER),
				deleteYNEqN())
			.orderBy(dataset.regDate.desc())
			.fetch();
		return datasets;
	}

	@Override
	public Dataset getDatasetWithStorage(Long datasetId) {
		return queryFactory.selectFrom(dataset)
			.where(datasetIdEq(datasetId),
				deleteYNEqN())
			.fetchOne();
	}

	private static BooleanExpression deleteYNEqN() {
		return dataset.deleteYn.eq(DeleteYN.N);
	}

	private Predicate datasetIdEq(Long datasetId) {
		return datasetId != null ? dataset.datasetId.eq(datasetId) : null;
	}

	private Predicate creatorEq(String creator, AuthType authType, PageMode pageMode) {
		if (authType == AuthType.ROLE_USER || pageMode == PageMode.USER) {
			return StringUtils.hasText(creator) ? dataset.regUser.regUserId.eq(creator) : null;
		}
		return null;
	}
}
