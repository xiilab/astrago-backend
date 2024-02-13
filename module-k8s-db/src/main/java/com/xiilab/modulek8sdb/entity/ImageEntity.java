package com.xiilab.modulek8sdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "TB_IMAGE")
@Getter
public class ImageEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_ID")
	private Long id;
	@Column(name = "IMAGE_NAME")
	private String name;
	@Column(name = "IMAGE_BRANCH")
	private String branch;
	@Column(name = "IMAGE_URL")
	private String url;
}
