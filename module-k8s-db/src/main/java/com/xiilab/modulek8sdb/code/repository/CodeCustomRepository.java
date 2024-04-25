package com.xiilab.modulek8sdb.code.repository;

import static com.xiilab.modulek8sdb.code.entity.QCodeEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulek8sdb.code.dto.CodeSearchCondition;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CodeCustomRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public Page<CodeEntity> getCodeListByCondition(
		String userId, String workspaceResourceName, RepositoryType repositoryType, Pageable pageable,
		CodeSearchCondition codeSearchCondition) {
		JPAQuery<CodeEntity> query = jpaQueryFactory.selectFrom(codeEntity)
			.where(
				eqUserId(userId),
				eqWorkspaceResourceName(workspaceResourceName),
				eqRepositoryType(repositoryType),
				eqCodeType(codeSearchCondition.getCodeType()),
				containsCodeUrl(codeSearchCondition.getSearchText()),
				codeEntity.deleteYn.eq(DeleteYN.N)
			);

		// 정렬 조건 추가
		for (Sort.Order order : pageable.getSort()) {
			PathBuilder pathBuilder = new PathBuilder<>(codeEntity.getType(), codeEntity.getMetadata());
			query.orderBy(new OrderSpecifier<>(order.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(order.getProperty())));
		}

		List<CodeEntity> content = query
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		int total = jpaQueryFactory.selectFrom(codeEntity)
			.where(
				eqUserId(userId),
				eqWorkspaceResourceName(workspaceResourceName),
				eqRepositoryType(repositoryType),
				eqCodeType(codeSearchCondition.getCodeType()),
				containsCodeUrl(codeSearchCondition.getSearchText()),
				codeEntity.deleteYn.eq(DeleteYN.N)
			)
			.fetch().size();

		return new PageImpl<>(content, pageable, total);
	}

	private static BooleanExpression containsCodeUrl(String searchText) {
		return !StringUtils.isEmpty(searchText) ? codeEntity.codeURL.toLowerCase().contains(searchText.toLowerCase()) : null;
	}

	private static BooleanExpression eqCodeType(CodeType codeType) {
		return codeType == null ? null : codeEntity.codeType.eq(codeType);
	}

	private BooleanExpression eqUserId(String userId) {
		return !StringUtils.isEmpty(userId) ? codeEntity.regUser.regUserId.eq(userId) :
			null; // Querydsl은 null 조건을 무시합니다.
	}

	private BooleanExpression eqWorkspaceResourceName(String workspaceResourceName) {
		return !StringUtils.isEmpty(workspaceResourceName) ?
			codeEntity.workspaceResourceName.eq(workspaceResourceName) : null;
	}

	private BooleanExpression eqRepositoryType(RepositoryType repositoryType) {
		return repositoryType != null ? codeEntity.repositoryType.eq(repositoryType) : null;
	}

}
