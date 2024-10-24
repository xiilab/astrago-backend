package com.xiilab.modulek8sdb.board.repository;

import static com.xiilab.modulek8sdb.board.entity.QBoardEntity.*;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom{
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<BoardEntity> findBoards(SortType sortType, String searchText, Pageable pageable) {
		Long totalCount = getBoardTotalCount(searchText);
		List<BoardEntity> result = getBoards(sortType, searchText, pageable);

		return new PageImpl<>(result, pageable, totalCount);
	}

	private Long getBoardTotalCount(String searchText) {
		return queryFactory.select(boardEntity.count())
			.from(boardEntity)
			.where(
				likeSearchText(searchText)
			)
			.fetchOne();
	}

	private List<BoardEntity> getBoards(SortType sortType, String searchText, Pageable pageable) {
		JPAQuery<BoardEntity> findBoardEntityQuery = queryFactory.selectFrom(boardEntity)
			.where(
				likeSearchText(searchText)
			)
			.orderBy(createOrderSpecifier(sortType));

		if (pageable != null) {
			findBoardEntityQuery.offset(pageable.getOffset())
				.limit(pageable.getPageSize());
		} else {
			pageable = PageRequest.of(0, Integer.MAX_VALUE);
		}

		return findBoardEntityQuery.fetch();
	}

	private BooleanExpression likeSearchText(String searchText) {
		return StringUtils.hasText(searchText) ? boardEntity.title.contains(searchText)
			.or(boardEntity.contents.contains(searchText))
			: null;
	}

	private OrderSpecifier createOrderSpecifier(SortType sortType) {
		return switch (sortType) {
			case LATEST -> new OrderSpecifier<>(Order.DESC, boardEntity.regDate);
			case OLDEST -> new OrderSpecifier<>(Order.ASC, boardEntity.regDate);
			case NAME -> new OrderSpecifier<>(Order.DESC, boardEntity.title);
		};
	}
}
