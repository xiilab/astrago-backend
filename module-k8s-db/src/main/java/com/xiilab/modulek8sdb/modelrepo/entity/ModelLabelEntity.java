package com.xiilab.modulek8sdb.modelrepo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "TB_MODEL_REPO_LABEL")
public class ModelLabelEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_LABEL_ID")
	private long id;
	@Column(name = "MODEL_LABEL_NAME")
	private String name;
	@ManyToOne
	private ModelRepoEntity modelRepoEntity;
}
