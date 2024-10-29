package com.xiilab.servercore.board.dto;

import java.util.List;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.board.entity.BoardAttachedFileEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BoardAttachedFileResDTO extends ResDTO {
	private Long boardAttachedFileId;
	private String originFileName;
	private String saveFileName;
	private String savePath;
	private Long dataSize;
	private String fileExtension;

	protected static BoardAttachedFileResDTO of(BoardAttachedFileEntity boardAttachedFileEntity) {
		return BoardAttachedFileResDTO.builder()
			.boardAttachedFileId(boardAttachedFileEntity.getBoardAttachedFileId())
			.originFileName(boardAttachedFileEntity.getOriginFileName())
			.saveFileName(boardAttachedFileEntity.getSaveFileName())
			.savePath(boardAttachedFileEntity.getSavePath())
			.dataSize(boardAttachedFileEntity.getDataSize())
			.fileExtension(boardAttachedFileEntity.getFileExtension())
			.regUserId(boardAttachedFileEntity.getRegUser().getRegUserId())
			.regUserName(boardAttachedFileEntity.getRegUser().getRegUserName())
			.regUserRealName(boardAttachedFileEntity.getRegUser().getRegUserRealName())
			.regDate(DataConverterUtil.convertLDTToString(boardAttachedFileEntity.getRegDate(), "yyyy-MM-dd HH:mm:ss"))
			.modDate(DataConverterUtil.convertLDTToString(boardAttachedFileEntity.getModDate(), "yyyy-MM-dd HH:mm:ss"))
			.build();
	}

	@Getter
	@SuperBuilder
	public static class FindBoardAttachedFiles {
		private List<BoardAttachedFileResDTO> boardAttachedFiles;
		private long totalCount;

		public static FindBoardAttachedFiles from(List<BoardAttachedFileEntity> boardAttachEntities) {
			return FindBoardAttachedFiles.builder()
				.boardAttachedFiles(boardAttachEntities.stream().map(BoardAttachedFileResDTO::of).toList())
				.totalCount(boardAttachEntities.size())
				.build();
		}
	}
}
