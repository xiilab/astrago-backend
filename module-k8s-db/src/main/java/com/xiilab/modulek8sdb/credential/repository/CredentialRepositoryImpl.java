package com.xiilab.modulek8sdb.credential.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.entity.QCredentialEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CredentialRepositoryImpl implements CredentialRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CredentialEntity> findByIdIn(Collection<Long> ids, Pageable pageable) {
		Long totalCount = queryFactory.select(QCredentialEntity.credentialEntity.count())
			.from(QCredentialEntity.credentialEntity)
			.where(QCredentialEntity.credentialEntity.id.in(ids))
			.fetchOne();

		JPAQuery<CredentialEntity> query = queryFactory.selectFrom(QCredentialEntity.credentialEntity)
			.where(QCredentialEntity.credentialEntity.id.in(ids));

		if (pageable != null) {
			query.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<CredentialEntity> result = query.fetch();

		return new PageImpl<>(result, pageable, totalCount);
	}
}
