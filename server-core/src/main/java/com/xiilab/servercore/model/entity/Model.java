package com.xiilab.servercore.model.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.servercore.common.enums.DatasetDivision;
import com.xiilab.servercore.dataset.entity.DatasetWorkSpaceMappingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TB_MODEL")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="DIVISION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Model extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_ID")
	private Long modelId;

	@Column(name = "MODEL_NAME")
	private String modelName;

	@Column(name = "DIVISION", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	protected DatasetDivision division;

	@OneToMany(mappedBy = "model")
	private List<ModelWorkSpaceMappingEntity> mappingEntities = new ArrayList<>();

	@Transient
	private boolean isAvailable = false;

	public Model(Long modelId, String modelName) {
		this.modelId = modelId;
		this.modelName = modelName;
	}

	public boolean isAvailable() {
		return !this.getMappingEntities().isEmpty();
	}

	public void modifyModelName(String modelName){
		this.modelName = modelName;
	}
	public abstract boolean isAstargoModel();
	public abstract boolean isLocalModel();
}
