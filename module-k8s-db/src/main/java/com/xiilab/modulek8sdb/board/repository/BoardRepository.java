package com.xiilab.modulek8sdb.board.repository;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.xiilab.modulek8sdb.board.entity.BoardEntity;

public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardRepositoryCustom{
	@Query("select count(b) from BoardEntity b where b.boardId in ?1")
	int countByBoardIdIn(Collection<Long> boardIds);

	@Modifying
	@Query("update BoardEntity b set b.deleteYN = 'Y' where b.boardId in ?1")
	void deleteAllById(Collection<Long> boardIds);
}
