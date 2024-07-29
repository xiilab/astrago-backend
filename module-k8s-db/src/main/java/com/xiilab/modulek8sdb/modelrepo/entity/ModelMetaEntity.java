package com.xiilab.modulek8sdb.modelrepo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MODEL_REPO_META")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ModelMetaEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_REPO_META_ID")
	private long id;
	@Column(name = "MODEL_FILE_NAME")
	private String modelFileName;
	@Column(name = "MODEL_FILE_SIZE")
	private Long modelFileSize;
	@ManyToOne
	@JoinColumn(name = "MODEL_REPO_VERSION_ID")
	private ModelVersionEntity modelVersionEntity;
}
