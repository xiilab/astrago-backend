package com.xiilab.servercore.board.service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.BoardErrorCode;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;
import com.xiilab.modulek8sdb.board.repository.BoardRepository;
import com.xiilab.servercore.board.dto.BoardReqDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;

	@Override
	@Transactional
	public Long saveBoard(BoardReqDTO.Edit.SaveBoard saveBoardReqDTO) {
		BoardEntity saveBoardEntity = BoardEntity.saveBoard()
			.title(saveBoardReqDTO.getTitle())
			.contents(saveBoardReqDTO.getContents())
			.popUpYN(saveBoardReqDTO.getPopUpYN())
			.popupStartDTM(saveBoardReqDTO.getPopUpStartDTM())
			.popupEndDTM(saveBoardReqDTO.getPopUpEndDTM())
			.boardType(saveBoardReqDTO.getBoardType())
			.build();

		boardRepository.save(saveBoardEntity);
		return saveBoardEntity.getBoardId();
	}

	@Override
	@Transactional
	public void updateBoardById(Long boardId, BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO) {
		BoardEntity findBoardEntity = boardRepository.findById(boardId)
			.orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_NOTICE));

		findBoardEntity.updateBoard(
			updateBoardReqDTO.getTitle(),
			updateBoardReqDTO.getContents(),
			updateBoardReqDTO.getBoardType(),
			updateBoardReqDTO.getPopUpYN(),
			updateBoardReqDTO.getPopUpStartDTM(),
			updateBoardReqDTO.getPopUpEndDTM()
		);
	}

	@Override
	public Object findBoards(SortType sortType, String searchText, Pageable pageable) {
		return boardRepository.findBoards(sortType, searchText, pageable);
	}

	@Override
	public Object findBoardById() {
		return null;
	}

	@Override
	public void deleteBoard() {

	}
}
