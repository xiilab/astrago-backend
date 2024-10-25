package com.xiilab.modulek8sdb.board.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulecommon.enums.BoardType;
import com.xiilab.modulecommon.enums.PopUpYN;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "TB_BOARD")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_BOARD tb SET tb.DELETE_YN = 'Y' WHERE tb.BOARD_ID = ?")
public class BoardEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BOARD_ID")
	private Long boardId;

	@Column(name = "BOARD_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	private BoardType boardType;

	@Column(name = "TITLE", nullable = false)
	private String title;

	@Column(name = "CONTENTS")
	private String contents;

	@Column(name = "READ_COUNT", nullable = false)
	private Integer readCount = 0; // 기본값 설정

	@Column(name = "POPUP_YN")
	@Enumerated(EnumType.STRING)
	private PopUpYN popUpYN;

	@Column(name = "POPUP_START_DTM")
	private LocalDateTime popupStartDTM;

	@Column(name = "POPUP_END_DTM")
	private LocalDateTime popupEndDTM;

	@Enumerated(EnumType.STRING)
	@Column(name = "DELETE_YN", nullable = false)
	private DeleteYN deleteYN = DeleteYN.N;

	@Builder(builderClassName = "SaveBoardBuilder", builderMethodName = "saveBoard")
	public BoardEntity(BoardType boardType, String title,
		String contents, PopUpYN popUpYN,
		LocalDateTime popupStartDTM, LocalDateTime popupEndDTM) {
		this.boardType = boardType;
		this.title = title;
		this.contents = contents;
		this.readCount = 0;
		this.popUpYN = popUpYN == null ? PopUpYN.N : popUpYN;
		this.popupStartDTM = popupStartDTM;
		this.popupEndDTM = popupEndDTM;
	}

	public void updateBoard(String title, String contents, BoardType boardType, PopUpYN popUpYN,
		LocalDateTime popupStartDTM, LocalDateTime popupEndDTM) {
		this.title = title;
		this.contents = contents;
		this.boardType = boardType;
		this.popUpYN = popUpYN;
		this.popupStartDTM = popupStartDTM;
		this.popupEndDTM = popupEndDTM;
	}

	// readCount 1증가
	public void countRead() {
		this.readCount++;
	}

	public void updateContents(String contents) {
		this.contents = contents;
	}
}

