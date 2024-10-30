package com.xiilab.modulek8sdb.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulek8sdb.board.entity.BoardAttachedFileEntity;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;

public interface BoardRepositoryCustom {
	Page<BoardEntity> findBoards(SortType sortType, String searchText, Pageable pageable);
	void saveAll(List<BoardAttachedFileEntity> boardAttachedFileEntityList);
}
