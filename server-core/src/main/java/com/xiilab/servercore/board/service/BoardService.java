package com.xiilab.servercore.board.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.servercore.board.dto.BoardReqDTO;
import com.xiilab.servercore.board.dto.BoardResDTO;

public interface BoardService {
	Long saveBoard(BoardReqDTO.Edit.SaveBoard saveBoardReqDTO, List<MultipartFile> attachedFiles);
	void updateBoardById(Long boardId, BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO, List<MultipartFile> attachedFiles);
	BoardResDTO.FindBoards findBoards(SortType sortType, String searchText, Pageable pageable);
	BoardResDTO.FindBoard findBoardById(long boardId);
	void deleteBoardById(long id);
	void deleteBoardByIds(List<Long> ids);
	String saveContentsFile(MultipartFile contentsFile, String id);
	byte[] getContentsFile(String saveFileName, String id);
	String getSaveBoardAttachedFileFullPath(long boardAttachedFileId);
}
