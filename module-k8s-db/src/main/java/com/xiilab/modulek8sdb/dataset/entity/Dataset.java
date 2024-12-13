package com.xiilab.modulek8sdb.dataset.entity;


import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

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
@SQLDelete(sql = "UPDATE TB_DATASET td SET td.DELETE_YN = 'Y' WHERE td.DATASET_ID = ?")
public abstract class Dataset extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DATASET_ID")
	private Long datasetId;

	@Column(name = "DATASET_NAME")
	private String datasetName;

	@Column(name = "DATASET_SIZE")
	private Long datasetSize;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYn = DeleteYN.N;

	@Column(name = "DIVISION", insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	protected RepositoryDivision division;

	@Column(name = "DATASET_DEFAULT_MOUNT_PATH")
	private String datasetDefaultMountPath;

	@OneToMany(mappedBy = "dataset")
	private List<DatasetWorkSpaceMappingEntity> workspaceMappingList = new ArrayList<>();
	@OneToMany(mappedBy = "dataset")
	private List<DatasetWorkLoadMappingEntity> datasetWorkloadMappingList = new ArrayList<>();

	@Transient
	private boolean isAvailable = false;

	public Dataset(Long datasetId, String datasetName, String datasetDefaultMountPath) {
		this.datasetId = datasetId;
		this.datasetName = datasetName;
		this.datasetDefaultMountPath = datasetDefaultMountPath;
	}
	public boolean isAvailable() {
		 return !this.getWorkspaceMappingList().isEmpty();
	}
	public void setDatasetSize(long size){
		this.datasetSize = size;
	}
	public void modifyDatasetName(String datasetName){
		this.datasetName = datasetName;
	}
	public void modifyDatasetDefaultPath(String datasetDefaultMountPath){
		this.datasetDefaultMountPath = datasetDefaultMountPath;
	}
	public abstract boolean isAstragoDataset();
	public abstract boolean isLocalDataset();
}
