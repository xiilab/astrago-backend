package com.xiilab.servercore.dataset.entity;


import java.util.ArrayList;
import java.util.List;

import com.xiilab.servercore.common.entity.BaseEntity;
import com.xiilab.servercore.common.enums.DatasetDivision;

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
@Table(name = "TB_DATASET")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name="DIVISION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Dataset extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DATASET_ID")
	private Long datasetId;

	@Column(name = "DATASET_NAME")
	private String datasetName;

	@Column(name = "DIVISION", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	protected DatasetDivision division;

	@OneToMany(mappedBy = "dataset")
	private List<DatasetWorkSpaceMappingEntity> mappingEntities = new ArrayList<>();

	@Transient
	private boolean isAvailable = false;

	public Dataset(Long datasetId, String datasetName) {
		this.datasetId = datasetId;
		this.datasetName = datasetName;
	}
	public boolean isAvailable() {
		 return !this.getMappingEntities().isEmpty();
	}

	public abstract boolean isAstargoDataset();
	public abstract boolean isLocalDataset();
}
