package com.xiilab.servercore.board.dto;

import java.util.List;

import com.xiilab.modulecommon.enums.PopUpYN;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8sdb.board.entity.BoardEntity;
import com.xiilab.servercore.common.dto.ResDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BoardResDTO extends ResDTO {
	protected Long id;
	protected String title;
	protected String contents;
	protected Integer readCount;

	protected static BoardResDTO of(BoardEntity boardEntity) {
		return BoardResDTO.builder()
			.id(boardEntity.getBoardId())
			.title(boardEntity.getTitle())
			.contents(boardEntity.getContents())
			.readCount(boardEntity.getReadCount())
			.regUserId(boardEntity.getRegUser().getRegUserId())
			.regUserName(boardEntity.getRegUser().getRegUserName())
			.regUserRealName(boardEntity.getRegUser().getRegUserRealName())
			.regDate(DataConverterUtil.convertLDTToString(boardEntity.getRegDate(), "yyyy-MM-dd HH:mm:ss"))
			.modDate(DataConverterUtil.convertLDTToString(boardEntity.getModDate(), "yyyy-MM-dd HH:mm:ss"))
			.build();
	}

	@Getter
	@SuperBuilder
	public static class FindBoard extends BoardResDTO {
		private PopUpYN popUpYN;
		private String popupStartDTM;
		private String popupEndDTM;

		public static FindBoard from(BoardEntity boardEntity) {
			return FindBoard.builder()
				.id(boardEntity.getBoardId())
				.title(boardEntity.getTitle())
				.contents(boardEntity.getContents())
				.popUpYN(boardEntity.getPopUpYN())
				.popupStartDTM(DataConverterUtil.convertLDTToString(boardEntity.getPopupStartDTM(), "yyyy-MM-dd HH:mm:ss"))
				.popupEndDTM(DataConverterUtil.convertLDTToString(boardEntity.getPopupEndDTM(), "yyyy-MM-dd HH:mm:ss"))
				.readCount(boardEntity.getReadCount())
				.regUserId(boardEntity.getRegUser().getRegUserId())
				.regUserName(boardEntity.getRegUser().getRegUserName())
				.regUserRealName(boardEntity.getRegUser().getRegUserRealName())
				.regDate(DataConverterUtil.convertLDTToString(boardEntity.getRegDate(), "yyyy-MM-dd HH:mm:ss"))
				.modDate(DataConverterUtil.convertLDTToString(boardEntity.getModDate(), "yyyy-MM-dd HH:mm:ss"))
				.build();
		}
	}

	@Getter
	@SuperBuilder
	public static class FindBoards {
		private List<BoardResDTO> boards;
		private long totalCount;
		public static FindBoards from(List<BoardEntity> boardEntities, long totalCount) {
			return FindBoards.builder()
				.boards(boardEntities.stream().map(BoardResDTO::of).toList())
				.totalCount(totalCount)
				.build();

		}
	}
}
