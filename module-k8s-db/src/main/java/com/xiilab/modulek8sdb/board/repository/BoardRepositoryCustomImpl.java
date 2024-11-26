package com.xiilab.modulek8sdb.board.repository;

import static com.xiilab.modulek8sdb.board.entity.QBoardEntity.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xiilab.modulecommon.enums.PopUpYN;
import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Page<BoardEntity> findBoards(SortType sortType, String searchText, PopUpYN popUpYN, Pageable pageable) {
		Long totalCount = getBoardTotalCount(searchText, popUpYN);
		List<BoardEntity> result = getBoards(sortType, searchText, popUpYN, pageable);

		return new PageImpl<>(result, pageable, totalCount);
	}
	private Long getBoardTotalCount(String searchText, PopUpYN popUpYN) {
		return queryFactory.select(boardEntity.count())
			.from(boardEntity)
			.where(
				likeSearchText(searchText),
				eqPopupYN(popUpYN),
				boardEntity.deleteYN.eq(DeleteYN.N)
			)
			.fetchOne();
	}

	private List<BoardEntity> getBoards(SortType sortType, String searchText, PopUpYN popUpYN, Pageable pageable) {
		JPAQuery<BoardEntity> findBoardEntityQuery = queryFactory.selectFrom(boardEntity)
			.where(
				likeSearchText(searchText),
				eqPopupYN(popUpYN),
				boardEntity.deleteYN.eq(DeleteYN.N)
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

	private BooleanExpression eqPopupYN(PopUpYN popUpYN) {
		return !Objects.isNull(popUpYN)? boardEntity.popUpYN.eq(popUpYN) : null;
	}

	private OrderSpecifier<?> createOrderSpecifier(SortType sortType) {
		// 기본 정렬을 정할 수 있는 맵 생성
		Map<SortType, OrderSpecifier<?>> orderSpecifiers = Map.of(
			SortType.LATEST, new OrderSpecifier<>(Order.DESC, boardEntity.regDate),
			SortType.OLDEST, new OrderSpecifier<>(Order.ASC, boardEntity.regDate),
			SortType.NAME, new OrderSpecifier<>(Order.DESC, boardEntity.title)
		);

		if (sortType == null) {
			return orderSpecifiers.get(SortType.LATEST);
		} else {
			// 정렬 타입에 따라 OrderSpecifier 반환
			return orderSpecifiers.getOrDefault(sortType, new OrderSpecifier<>(Order.DESC, boardEntity.regDate));
		}
	}

	/*@Override
	public void saveAll(List<BoardAttachedFileEntity> boardAttachedFileEntities) {
		final String sql = """
                    INSERT INTO TB_BOARD_ATTACHED_FILE (BOARD_ID, ORIGIN_FILENAME, SAVE_FILENAME, SAVE_PATH, DATA_SIZE, FILE_EXTENSION)
                    VALUES (:boardId, :originFileName, :saveFileName, :savePath, :dataSize, :fileExtension)
                """;
		MapSqlParameterSource[] parameterSources = getBoardAttachedFileEntitiesToSqlParameterSources(
			boardAttachedFileEntities);

		namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
	}

	private MapSqlParameterSource[] getBoardAttachedFileEntitiesToSqlParameterSources(List<BoardAttachedFileEntity> boardAttachedFileEntities) {
		return boardAttachedFileEntities.stream()
			.map(this::getBoardAttachedFileEntityToSqlParameterSource)
			.toArray(MapSqlParameterSource[]::new);
	}

	private MapSqlParameterSource getBoardAttachedFileEntityToSqlParameterSource(BoardAttachedFileEntity boardAttachedFileEntity) {
		return new MapSqlParameterSource()
			.addValue("boardId", boardAttachedFileEntity.getBoardEntity().getBoardId())
			.addValue("originFileName", boardAttachedFileEntity.getOriginFileName())
			.addValue("saveFileName", boardAttachedFileEntity.getSaveFileName())
			.addValue("savePath", boardAttachedFileEntity.getSavePath())
			.addValue("dataSize", boardAttachedFileEntity.getDataSize())
			.addValue("fileExtension", boardAttachedFileEntity.getFileExtension())
			.addValue("deleteYN", boardAttachedFileEntity.getDeleteYN());
	}*/
}
