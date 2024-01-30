package com.xiilab.servercore.dataset.entity;


import com.xiilab.servercore.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
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

	public Dataset(Long datasetId, String datasetName) {
		this.datasetId = datasetId;
		this.datasetName = datasetName;
	}
}
