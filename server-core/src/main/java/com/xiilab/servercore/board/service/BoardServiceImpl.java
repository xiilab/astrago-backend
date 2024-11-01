package com.xiilab.servercore.board.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.BoardErrorCode;
import com.xiilab.modulecommon.service.FileUploadService;
import com.xiilab.modulek8sdb.board.entity.BoardAttachedFileEntity;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;
import com.xiilab.modulek8sdb.board.repository.BoardAttachRepository;
import com.xiilab.modulek8sdb.board.repository.BoardRepository;
import com.xiilab.servercore.board.dto.BoardReqDTO;
import com.xiilab.servercore.board.dto.BoardResDTO;
import com.xiilab.servercore.utils.PageableUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;
	private final BoardAttachRepository boardAttachRepository;
	private final FileUploadService fileUploadService;
	@Value("${editor.attach-file-upload-path}")
	private String attachFileUploadPath;
	@Value("${editor.content-file-upload-path}")
	private String contentFileUploadPath;

	@Override
	@Transactional
	public Long saveBoard(BoardReqDTO.Edit.SaveBoard saveBoardReqDTO, List<MultipartFile> attachedFiles) {
		BoardEntity saveBoardEntity = saveBoardEntity(saveBoardReqDTO);
		saveAttachedFiles(attachedFiles, saveBoardEntity);

		//게시글 본문 html src 경로 수정 uuid > boardId
		String replaceContents = saveBoardEntity.getContents()
			.replaceAll("UUID_[0-9]{10}", String.valueOf(saveBoardEntity.getBoardId()));
		saveBoardEntity.updateContents(replaceContents);

		// 파일 디렉토리명 수정
		File beforeContentsDir = new File(fileUploadService.getRootFilePath() + File.separator + contentFileUploadPath + File.separator
			+ saveBoardReqDTO.getTempId());
		File afterContentsDir = new File(fileUploadService.getRootFilePath() + File.separator + contentFileUploadPath + File.separator
			+ saveBoardEntity.getBoardId());
		beforeContentsDir.renameTo(afterContentsDir);

		return saveBoardEntity.getBoardId();
	}

	@Override
	@Transactional
	public void updateBoardById(Long boardId, BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO, List<MultipartFile> attachedFiles) {
		BoardEntity findBoardEntity = boardRepository.findById(boardId)
			.orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_BOARD));

		updateAttachedFiles(updateBoardReqDTO.getDeleteBoardAttachedFileIds(), findBoardEntity, attachedFiles);

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
	public BoardResDTO.FindBoards findBoards(SortType sortType, String searchText, Pageable pageable) {
		Page<BoardEntity> boards = boardRepository.findBoards(sortType, searchText, PageableUtils.normalizePageable(pageable));
		return BoardResDTO.FindBoards.from(boards.getContent(), boards.getTotalElements());
	}

	@Override
	@Transactional
	public BoardResDTO.FindBoard findBoardById(long boardId) {
		BoardEntity findBoardEntity = boardRepository.findById(boardId)
			.orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_BOARD));
		List<BoardAttachedFileEntity> findBoardAttachedFileEntities = boardAttachRepository.findByBoardEntity(findBoardEntity);
		findBoardEntity.countRead();

		return BoardResDTO.FindBoard.from(findBoardEntity, findBoardAttachedFileEntities);
	}

	@Override
	@Transactional
	public void deleteBoardById(long id) {
		if (!boardRepository.existsById(id)) {
			throw new RestApiException(BoardErrorCode.NOT_FOUND_BOARD);
		}
		boardRepository.deleteById(id);
	}

	@Override
	public String saveContentsFile(MultipartFile contentsFile, String id) {
		String saveFullPath = fileUploadService.saveMultipartFileToFile(contentFileUploadPath + File.separator + id, contentsFile);
		return saveFullPath.substring(saveFullPath.lastIndexOf("/") + 1);
	}

	@Override
	public byte[] getContentsFile(String saveFileName, String id) {
		return fileUploadService.getFileBytes(contentFileUploadPath + File.separator + id, saveFileName);
	}

	private void saveAttachedFiles(List<MultipartFile> attachedFiles, BoardEntity saveBoardEntity) {
		if (attachedFiles == null) {
			return ;
		}
		List<BoardAttachedFileEntity> attachedFileEntities = new ArrayList<>();
		for (MultipartFile attachFile : attachedFiles) {
			String saveFileFullPath = fileUploadService.saveMultipartFileToFile(
				contentFileUploadPath + File.separator + saveBoardEntity.getBoardId(),
				attachFile);
			String saveFilePath = saveFileFullPath.substring(0, saveFileFullPath.lastIndexOf("/"));
			String saveFileName = saveFileFullPath.substring(saveFileFullPath.lastIndexOf("/") + 1);

			BoardAttachedFileEntity boardAttachedFileEntity = BoardAttachedFileEntity.saveBoardAttach()
				.boardEntity(saveBoardEntity)
				.originFileName(attachFile.getOriginalFilename())
				.saveFileName(saveFileName)
				.savePath(saveFilePath)
				.dataSize(attachFile.getSize())
				.fileExtension(saveFileFullPath.substring(saveFileFullPath.lastIndexOf(".") + 1))
				.build();

			attachedFileEntities.add(boardAttachedFileEntity);
		}
		boardAttachRepository.saveAll(attachedFileEntities);
	}

	@Override
	public String getSaveBoardAttachedFileFullPath(long boardAttachedFileId) {
		BoardAttachedFileEntity findBoardAttachedFileEntity = boardAttachRepository.findById(boardAttachedFileId)
			.orElseThrow(() -> new RestApiException(BoardErrorCode.NOT_FOUND_ATTACHED_FILE));

		return findBoardAttachedFileEntity.getSavePath() + File.separator + findBoardAttachedFileEntity.getSaveFileName();
	}

	private BoardEntity saveBoardEntity(BoardReqDTO.Edit.SaveBoard saveBoardReqDTO) {
		BoardEntity saveBoardEntity = BoardEntity.saveBoard()
			.title(saveBoardReqDTO.getTitle())
			.contents(saveBoardReqDTO.getContents())
			.popUpYN(saveBoardReqDTO.getPopUpYN())
			.popupStartDTM(saveBoardReqDTO.getPopUpStartDTM())
			.popupEndDTM(saveBoardReqDTO.getPopUpEndDTM())
			.boardType(saveBoardReqDTO.getBoardType())
			.build();

		boardRepository.save(saveBoardEntity);
		return saveBoardEntity;
	}

	// 기존 첨부 파일을 삭제하고 새로운 파일을 추가하는 메서드
	private void updateAttachedFiles(List<Long> deleteBoardAttachedFileIds, BoardEntity boardEntity, List<MultipartFile> newAttachedFiles) {
		boardAttachRepository.deleteAllById(deleteBoardAttachedFileIds);

		// 새로운 파일 저장
		saveAttachedFiles(newAttachedFiles, boardEntity);
	}
}
