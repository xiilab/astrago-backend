package com.xiilab.modulek8sdb.board.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "TB_BOARD_ATTACH_FILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardAttachEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BOARD_ATTACH_ID")
	private Long boardAttachId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BOARD_ID")
	private BoardEntity boardEntity;

	@Column(name = "ORIGIN_FILENAME", nullable = false)
	private String originFileName;

	@Column(name = "SAVE_FILENAME")
	private String saveFileName;

	@Column(name = "SAVE_PATH")
	private String savePath;

	@Column(name = "DATA_SIZE")
	private Long dataSize;

	@Column(name = "FILE_EXTENSION")
	private String fileExtension;

	@Builder(builderClassName = "SaveBoardAttachBuilder", builderMethodName = "saveBoardAttach")
	public BoardAttachEntity(BoardEntity boardEntity, String originFileName, String saveFileName,
		String savePath, Long dataSize, String fileExtension) {
		this.boardEntity = boardEntity;
		this.originFileName = originFileName;
		this.saveFileName = saveFileName;
		this.savePath = savePath;
		this.dataSize = dataSize;
		this.fileExtension = fileExtension;
	}
}

