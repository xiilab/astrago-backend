package com.xiilab.servercore.board.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.enums.SortType;
import com.xiilab.servercore.board.dto.BoardReqDTO;
import com.xiilab.servercore.board.dto.BoardResDTO;
import com.xiilab.servercore.board.service.BoardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "BoardController", description = "게시판 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/boards")
public class BoardController {
	private final BoardService boardService;

	@PostMapping("")
	@Operation(summary = "Board 등록")
	public ResponseEntity<Void> saveBoard(
		@RequestPart(value = "saveBoardReqDTO") BoardReqDTO.Edit.SaveBoard saveBoardReqDTO,
		@RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
		boardService.saveBoard(saveBoardReqDTO, attachedFiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Board 업데이트")
	public ResponseEntity<Void> updateBoardById(@PathVariable(name = "id") long boardId,
		@RequestBody @Valid BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO) {
		boardService.updateBoardById(boardId, updateBoardReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Board 목록 조회")
	public ResponseEntity<BoardResDTO.FindBoards> findBoards(SortType sortType, String searchText, Pageable pageable) {
		return new ResponseEntity<>(boardService.findBoards(sortType, searchText, pageable), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Board 단일 조회")
	public ResponseEntity<BoardResDTO.FindBoard> findBoard(@PathVariable(name = "id") long boardId) {
		return new ResponseEntity<>(boardService.findBoardById(boardId), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Board 삭제")
	public ResponseEntity<Void> deleteBoard(@PathVariable(name = "id") long boardId) {
		boardService.deleteBoardById(boardId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/contents/file")
	@Operation(summary = "Board Contents에 파일 추가")
	public ResponseEntity<String> saveContentsFile(
		@RequestPart(name = "contentsFile") MultipartFile contentsFile,
		@RequestPart(name = "id") String id
	) {
		return new ResponseEntity<>(boardService.saveContentsFile(contentsFile, id), HttpStatus.OK);
	}

	@GetMapping("/contents/file/{saveFileName}")
	@Operation(summary = "Board Contents 파일 조회")
	public ResponseEntity<byte[]> getContentsFile(
		@PathVariable(name = "saveFileName") String saveFileName,
		@RequestParam(name = "id") String id
	) {
		return new ResponseEntity<>(boardService.getContentsFile(saveFileName, id), HttpStatus.OK);
	}

}
