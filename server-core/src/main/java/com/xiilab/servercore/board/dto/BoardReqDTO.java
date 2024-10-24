package com.xiilab.servercore.board.dto;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;

import com.xiilab.modulecommon.enums.BoardType;
import com.xiilab.modulecommon.enums.PopUpYN;
import com.xiilab.modulecommon.enums.SortType;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public abstract class BoardReqDTO {
	@Getter
	public abstract static class Edit {
		@NotBlank(message = "제목은 필수값입니다.")
		private String title;
		@NotBlank(message = "내용은 필수값입니다.")
		private String contents;
		private PopUpYN popUpYN;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime popUpStartDTM;
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime popUpEndDTM;
		private BoardType boardType;

		@Getter
		public static class SaveBoard extends Edit {
		}

		@Getter
		public static class UpdateBoard extends Edit {
		}
	}

	@Getter
	public static class FindSearchCondition {
		// private Pageable pageable;
		private SortType boardSortType;
		private String searchText;

		/*public FindSearchCondition(Integer page, Integer size, SortType boardSortType, String searchText) {
			// this.page = !ValidUtils.isNullOrZero(page)? page - 1 : 0;
			// this.size = !ValidUtils.isNullOrZero(size)? size : 10;
			this.boardSortType = boardSortType;
			this.searchText = searchText;
		}*/

		public FindSearchCondition(Pageable pageable, SortType boardSortType, String searchText) {
			// this.page = !ValidUtils.isNullOrZero(page)? page - 1 : 0;
			// this.size = !ValidUtils.isNullOrZero(size)? size : 10;
			// this.pageable = pageable;
			this.boardSortType = boardSortType;
			this.searchText = searchText;
		}
	}

}
