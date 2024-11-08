package com.xiilab.modulek8sdb.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.PopUpYN;
import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;

public interface BoardRepositoryCustom {
	Page<BoardEntity> findBoards(SortType sortType, String searchText, PopUpYN popUpYN, Pageable pageable);
	// void saveAll(List<BoardAttachedFileEntity> boardAttachedFileEntityList);
}
