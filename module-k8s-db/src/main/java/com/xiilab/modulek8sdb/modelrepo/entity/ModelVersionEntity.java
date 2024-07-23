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

@Entity(name = "TB_MODEL_REPO_VERSION")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ModelVersionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_REPO_VERSION_ID")
	private long id;
	@Column(name = "VERSION")
	private String version;
	@Column(name = "MODEL_FILE_NAME")
	private String modelFileName;
	@Column(name = "LABEL_FILE_NAME")
	private String labelFileName;
	@ManyToOne
	@JoinColumn(name = "MODEL_REPO_ID")
	private ModelRepoEntity modelRepoEntity;
}
