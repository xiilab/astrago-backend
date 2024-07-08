package com.xiilab.modulek8sdb.modelrepo.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_MODEL_REPO")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ModelRepoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "MODEL_NAME")
	private String modelName;
	@Column(name = "MODEL_DESCRIPTION")
	private String description;
	@Column(name = "MODEL_VERSION")
	private String version;
	@Column(name = "MODEL_STORAGE_PATH")
	private String storagePath;
	@OneToMany(mappedBy = "modelRepoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ModelLabelEntity> modelLabelEntities = new ArrayList<>();

}
