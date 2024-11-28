package com.xiilab.servercore.board.controller;

/*@Tag(name = "BoardController", description = "게시판 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/boards")*/
public class BoardController {
	/*private final BoardService boardService;

	@PostMapping("")
	@Operation(summary = "Board 등록")
	public ResponseEntity<Void> saveBoard(
		@RequestPart(value = "saveBoardReqDTO") @Valid BoardReqDTO.Edit.SaveBoard saveBoardReqDTO,
		@RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
		boardService.saveBoard(saveBoardReqDTO, attachedFiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Board 업데이트")
	public ResponseEntity<Void> updateBoardById(@PathVariable(name = "id") long boardId,
		@RequestPart(value = "updateBoardReqDTO") @Valid BoardReqDTO.Edit.UpdateBoard updateBoardReqDTO,
		@RequestPart(value = "attachedFiles", required = false) List<MultipartFile> attachedFiles) {
		boardService.updateBoardById(boardId, updateBoardReqDTO, attachedFiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Board 목록 조회")
	public ResponseEntity<BoardResDTO.FindBoards> findBoards(
		@ParameterObject BoardReqDTO.FindSearchCondition findSearchCondition,
		@ParameterObject Pageable pageable) {
		return new ResponseEntity<>(boardService.findBoards(findSearchCondition, pageable), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Board 단일 조회")
	public ResponseEntity<BoardResDTO.FindBoard> findBoard(@PathVariable(name = "id") long boardId) {
		return new ResponseEntity<>(boardService.findBoardById(boardId), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Board 삭제")
	public ResponseEntity<Void> deleteBoardById(@PathVariable(name = "id") long boardId) {
		boardService.deleteBoardById(boardId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping
	@Operation(summary = "Board 삭제")
	public ResponseEntity<Void> deleteBoardByIds(
		@NotNull(message = "삭제할 게시글을 선택해주세요.")
		@NotEmpty(message = "삭제할 게시글을 선택해주세요.")
		@RequestParam(name = "boardIds") List<Long> boardIds) {
		boardService.deleteBoardByIds(boardIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/contents/file")
	@Operation(summary = "Board Contents에 파일 추가")
	public ResponseEntity<String> saveContentsFile(@RequestPart(name = "contentsFile") MultipartFile contentsFile,
		@RequestPart(name = "id") String id) {
		return new ResponseEntity<>(boardService.saveContentsFile(contentsFile, id), HttpStatus.OK);
	}

	@GetMapping("/contents/file/{saveFileName}")
	@Operation(summary = "Board Contents 파일 조회")
	public ResponseEntity<byte[]> getContentsFile(@PathVariable(name = "saveFileName") String saveFileName,
		@RequestParam(name = "id") String id) {
		return new ResponseEntity<>(boardService.getContentsFile(saveFileName, id), HttpStatus.OK);
	}

	@GetMapping(value = "/download/{boardAttachFileId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Operation(summary = "Board 첨부 파일 다운로드")
	public void downloadAttachFile(@PathVariable("boardAttachFileId") long boardAttachFileId,
		HttpServletResponse response,
		@Parameter(description = "클라이언트에서 서버로 요청시, HTTP 헤더에 포함되어 있는 브라우저 및 OS 정보")
		@RequestHeader(value = "User-Agent", required = false) String agent) {
		String saveFullPath = boardService.getSaveBoardAttachedFileFullPath(boardAttachFileId);

		try (InputStream is = new BufferedInputStream(
			new FileInputStream(saveFullPath)); OutputStream out = response.getOutputStream();) {
			Path saveFilePath = Path.of(saveFullPath);
			String fileName = String.valueOf(saveFilePath.getFileName());
			String onlyFileName = fileName.substring(fileName.lastIndexOf("_") + 1);
			if (agent.contains("Trident"))//Internet Explore
				onlyFileName = URLEncoder.encode(onlyFileName, StandardCharsets.UTF_8).replaceAll("\\+", " ");
			else if (agent.contains("Edge")) //Micro Edge
				onlyFileName = URLEncoder.encode(onlyFileName, StandardCharsets.UTF_8);
			else //Chrome
				onlyFileName = new String(onlyFileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);

			response.setHeader("Content-Disposition", "attachment;filename=" + onlyFileName);
			IOUtils.copy(is, out);
		} catch (Exception e) {
			throw new RestApiException(BoardErrorCode.FAILED_DOWNLOAD_ATTACHED_FILE);
		}
	}*/

}
