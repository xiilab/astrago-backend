package com.xiilab.servercore.board.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;
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
		protected String title;
		@NotBlank(message = "내용은 필수값입니다.")
		protected String contents;
		protected PopUpYN popUpYN;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		protected LocalDateTime popUpStartDTM;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
		protected LocalDateTime popUpEndDTM;
		protected BoardType boardType;

		@Getter
		public static class SaveBoard extends Edit {
			private String tempId;
		}

		@Getter
		public static class UpdateBoard extends Edit {
			private List<Long> deleteBoardAttachedFileIds;
		}
	}

	@Getter
	public static class SaveContentsFile {
		private MultipartFile contentsFile;
		private String id;
	}


	@Getter
	public static class FindSearchCondition {
		private SortType boardSortType;
		private String searchText;
	}

}
