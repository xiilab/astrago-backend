package com.xiilab.modulek8sdb.credential.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
		List<CredentialEntity> result;

		long totalCount = queryFactory.select(QCredentialEntity.credentialEntity.count())
			.from(QCredentialEntity.credentialEntity)
			.where(QCredentialEntity.credentialEntity.id.in(ids))
			.fetchOne();

		if (pageable != null) {
			result = queryFactory.selectFrom(QCredentialEntity.credentialEntity)
				.where(QCredentialEntity.credentialEntity.id.in(ids))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		} else {
			result = queryFactory.selectFrom(QCredentialEntity.credentialEntity)
				.where(QCredentialEntity.credentialEntity.id.in(ids))
				.fetch();
		}

		return new PageImpl<>(result, pageable, totalCount);
	}
}
