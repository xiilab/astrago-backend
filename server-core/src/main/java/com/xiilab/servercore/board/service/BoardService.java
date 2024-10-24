package com.xiilab.servercore.board.service;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.servercore.board.dto.BoardReqDTO;

public interface BoardService {
	Long saveBoard(BoardReqDTO.Edit.SaveBoard saveBoardReqDTO);
	void updateBoardById(Long boardId, BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO);
	Object findBoards(SortType sortType, String searchText, Pageable pageable);
	Object findBoardById();
	void deleteBoard();
}
