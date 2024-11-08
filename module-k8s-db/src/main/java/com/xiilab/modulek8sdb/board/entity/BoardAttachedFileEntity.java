package com.xiilab.modulek8sdb.board.entity;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "TB_BOARD_ATTACHED_FILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_BOARD_ATTACHED_FILE tb SET tb.DELETE_YN = 'Y' WHERE tb.BOARD_ATTACHED_FILE_ID in (?)")
public class BoardAttachedFileEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "BOARD_ATTACHED_FILE_ID")
	private Long boardAttachedFileId;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "DELETE_YN", nullable = false)
	private DeleteYN deleteYN = DeleteYN.N;

	@Builder(builderClassName = "SaveBoardAttachBuilder", builderMethodName = "saveBoardAttach")
	public BoardAttachedFileEntity(BoardEntity boardEntity, String originFileName, String saveFileName,
		String savePath, Long dataSize, String fileExtension) {
		this.boardEntity = boardEntity;
		this.originFileName = originFileName;
		this.saveFileName = saveFileName;
		this.savePath = savePath;
		this.dataSize = dataSize;
		this.fileExtension = fileExtension;
	}
}
