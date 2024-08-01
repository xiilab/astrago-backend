package com.xiilab.modulek8sdb.volume.repository;

import static com.xiilab.modulek8sdb.volume.entity.QVolume.*;

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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.OutputVolumeYN;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.modulek8sdb.common.enums.RepositorySortType;
import com.xiilab.modulek8sdb.volume.entity.Volume;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VolumeRepositoryImpl implements VolumeRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	private static BooleanExpression deleteYnEqN() {
		return volume.deleteYn.eq(DeleteYN.N);
	}

	@Override
	public Page<Volume> findByAuthorityWithPaging(PageRequest pageRequest, String userId, AuthType userAuth,
		RepositorySearchCondition repositorySearchCondition, PageMode pageMode) {
		RepositorySortType sortType = repositorySearchCondition.getSort();

		OrderSpecifier<? extends Serializable> sort =
			sortType == RepositorySortType.NAME ? volume.volumeName.desc() :
				sortType == RepositorySortType.CREATED_AT ? volume.regDate.desc() : volume.volumeSize.desc();

		Long totalCount = queryFactory.select(volume.count())
			.from(volume)
			.where(
				creatorEq(userId, userAuth, pageMode),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				volumeNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYnEqN(),
				outputVoulmeYNEq(repositorySearchCondition.getOutputVolumeYN())
			)
			.fetchOne();

		JPAQuery<Volume> query = queryFactory.selectFrom(volume)
			.where(
				creatorEq(userId, userAuth, pageMode),
				repositoryDivisionEq(repositorySearchCondition.getRepositoryDivision()),
				volumeNameOrCreatorNameContains(repositorySearchCondition.getSearchText()),
				deleteYnEqN(),
				outputVoulmeYNEq(repositorySearchCondition.getOutputVolumeYN())
			)
			.orderBy(sort);

		if (pageRequest != null) {
			query.offset(pageRequest.getPageNumber())
				.limit(pageRequest.getPageSize());
		} else {
			pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
		}

		List<Volume> result = query.fetch();
		return new PageImpl<>(result, pageRequest, totalCount);
	}

	@Override
	public Volume getVolumeWithStorage(Long volumeId) {
		return queryFactory.selectFrom(volume)
			.where(volumeIdEq(volumeId),
				deleteYnEqN())
			.fetchOne();
	}

	@Override
	public List<Volume> findByAuthority(String userId, AuthType userAuth) {
		List<Volume> volumes = queryFactory.selectFrom(volume)
			.where(creatorEq(userId, userAuth, PageMode.USER),
				deleteYnEqN())
			.orderBy(volume.regDate.desc())
			.fetch();
		return volumes;
	}

	private Predicate volumeNameOrCreatorNameContains(String searchText) {
		return StringUtils.hasText(searchText) ? volume.regUser.regUserRealName.contains(searchText)
			.or(volume.volumeName.contains(searchText)) : null;
	}

	private Predicate repositoryDivisionEq(RepositoryDivision repositoryDivision) {
		return repositoryDivision != null ? volume.division.eq(repositoryDivision) : null;
	}

	private Predicate volumeIdEq(Long volumeId) {
		return volumeId != null ? volume.volumeId.eq(volumeId) : null;
	}

	private Predicate creatorEq(String creator, AuthType authType, PageMode pageMode) {
		if (authType == AuthType.ROLE_USER || pageMode == PageMode.USER) {
			return StringUtils.hasText(creator) ? volume.regUser.regUserId.eq(creator) : null;
		}
		return null;
	}

	private Predicate outputVoulmeYNEq(OutputVolumeYN outputVolumeYN) {
		return outputVolumeYN != null ? volume.outputVolumeYN.eq(outputVolumeYN) : null;
	}
}
