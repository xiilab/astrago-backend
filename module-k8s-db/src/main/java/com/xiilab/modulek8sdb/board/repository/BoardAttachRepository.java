package com.xiilab.modulek8sdb.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulek8sdb.board.entity.BoardAttachedFileEntity;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;

public interface BoardAttachRepository extends JpaRepository<BoardAttachedFileEntity, Long>, BoardRepositoryCustom{
/*	@Transactional
	@Modifying
	@Query("delete from BoardAttachedFileEntity b where b.boardAttachedFileId in :boardAttachedFileIds")
	void deleteBoardAttachedFileByIds(@Param("boardAttachedFileIds") List<Long> boardAttachedFileIds);*/
	@Transactional
	@Modifying
	@Query("delete from BoardAttachedFileEntity b where b.boardEntity = ?1")
	void deleteByBoardEntity(BoardEntity boardEntity);
	@Query("select b from BoardAttachedFileEntity b where b.boardEntity = ?1 and b.deleteYN = 'N'")
	List<BoardAttachedFileEntity> findByBoardEntity(BoardEntity boardEntity);
}
