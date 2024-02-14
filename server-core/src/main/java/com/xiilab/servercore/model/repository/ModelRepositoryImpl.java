package com.xiilab.servercore.model.repository;



import static com.xiilab.servercore.model.entity.QModel.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.moduleuser.enumeration.AuthType;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.model.entity.Model;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ModelRepositoryImpl implements ModelRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Model> findByAuthorityWithPaging(PageRequest pageRequest, UserInfoDTO userInfoDTO) {
		List<Model> models = queryFactory.selectFrom(model)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.offset(pageRequest.getOffset())
			.limit(pageRequest.getPageSize())
			.orderBy(model.regDate.desc())
			.fetch();

		Long count = queryFactory.select(model.count())
			.from(model)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
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
	public List<Model> findByAuthority(UserInfoDTO userInfoDTO) {
		List<Model> models = queryFactory.selectFrom(model)
			.where(creatorEq(userInfoDTO.getId(), userInfoDTO.getAuth()))
			.fetch();
		return models;
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
