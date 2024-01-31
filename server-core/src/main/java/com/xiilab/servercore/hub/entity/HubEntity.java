package com.xiilab.servercore.hub.entity;

import com.xiilab.servercore.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_HUB")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HubEntity extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HUB_ID")
	private Long hubId;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "SAVE_PATH")
	private String savePath;

	@Column(name = "SAVE_FILENAME")
	private String saveFileName;

	@Column(name = "IMAGE")
	private String image;

	@Column(name = "SOURCE_CODE_URL")
	private String sourceCodeUrl;

	@Column(name = "SOURCE_CODE_BRANCH")
	private String sourceCodeBranch;


}
