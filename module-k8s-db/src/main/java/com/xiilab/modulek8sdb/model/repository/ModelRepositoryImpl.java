package com.xiilab.modulek8sdb.model.repository;



import static com.xiilab.modulek8sdb.dataset.entity.QDataset.*;
import static com.xiilab.modulek8sdb.model.entity.QModel.*;

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
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.common.enums.RepositorySortType;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulek8sdb.model.entity.Model;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ModelRepositoryImpl implements ModelRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Model> findByAuthorityWithPaging(PageRequest pageRequest,String userId, AuthType userAuth,
		RepositorySearchCondition repositorySearchCondition) {
		RepositorySortType sortType = repositorySearchCondition.getSort();

		OrderSpecifier<? extends Serializable> sort =
			sortType == RepositorySortType.NAME ? model.modelName.desc() :
				sortType == RepositorySortType.CREATED_AT ? model.regDate.desc() : model.modelSize.desc();

		List<Model> models = queryFactory.selectFrom(model)
			.where(
				creatorEq(userId, userAuth),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				modelNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYnEqN()
			)
			.orderBy(sort)
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.fetch();

		Long count = queryFactory.select(model.count())
			.from(model)
			.where(
				creatorEq(userId, userAuth),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				modelNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYnEqN()
			)
			.fetchOne();
		return new PageImpl<>(models, pageRequest, count);
	}

	@Override
	public Model getModelWithStorage(Long modelId) {
		return queryFactory.selectFrom(model)
			.where(modelIdEq(modelId))
			.fetchOne();
	}

	@Override
	public List<Model> findByAuthority(String userId, AuthType userAuth) {
		List<Model> models = queryFactory.selectFrom(model)
			.where(creatorEq(userId, userAuth),
				deleteYnEqN())
			.fetch();
		return models;
	}

	private static BooleanExpression deleteYnEqN() {
		return model.deleteYn.eq(DeleteYN.N);
	}

	private Predicate modelNameOrCreatorNameContains(String searchText) {
		return StringUtils.hasText(searchText) ? model.regUser.regUserRealName.contains(searchText)
			.or(model.modelName.contains(searchText)) : null;
	}

	private Predicate repositoryDivisionEq(RepositoryDivision repositoryDivision) {
		return repositoryDivision != null ? model.division.eq(repositoryDivision) : null;
	}

	private Predicate modelIdEq(Long modelId) {
		return modelId != null ? model.modelId.eq(modelId) : null;
	}

	private Predicate creatorEq(String creator, AuthType authType) {
		if(authType == AuthType.ROLE_USER){
			return StringUtils.hasText(creator) ? model.regUser.regUserId.eq(creator) : null;
		}
		return null;
	}
}
