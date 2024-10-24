package com.xiilab.servercore.board.dto;

import java.time.LocalDateTime;

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
			.regDate(formatDate(boardEntity.getRegDate()))
			.modDate(formatDate(boardEntity.getModDate()))
			.build();
	}

	protected static String formatDate(LocalDateTime dateTime) {
		return dateTime != null ? DataConverterUtil.convertLDTToString(dateTime, "yyyy-MM-dd HH:mm:ss") : null;
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
				.popupStartDTM(formatDate(boardEntity.getPopupStartDTM()))
				.popupEndDTM(formatDate(boardEntity.getPopupEndDTM()))
				.readCount(boardEntity.getReadCount())
				.regUserId(boardEntity.getRegUser().getRegUserId())
				.regUserName(boardEntity.getRegUser().getRegUserName())
				.regUserRealName(boardEntity.getRegUser().getRegUserRealName())
				.regDate(formatDate(boardEntity.getRegDate()))
				.modDate(formatDate(boardEntity.getModDate()))
				.build();
		}
	}
}
