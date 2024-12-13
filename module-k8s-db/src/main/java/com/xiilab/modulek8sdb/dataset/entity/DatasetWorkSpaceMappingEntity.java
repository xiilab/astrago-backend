package com.xiilab.modulek8sdb.dataset.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_DATASET_WORKSPACE_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DatasetWorkSpaceMappingEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DATASET_WORKSPACE_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DATASET_ID")
	@JsonIgnore
	private Dataset dataset;

	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "DATASET_DEFAULT_MOUNT_PATH")
	private String datasetDefaultMountPath;

	@Builder
	public DatasetWorkSpaceMappingEntity(Dataset dataset, String workspaceResourceName, String datasetDefaultMountPath) {
		this.dataset = dataset;
		this.workspaceResourceName = workspaceResourceName;
		this.datasetDefaultMountPath = datasetDefaultMountPath;
		//연관관계 편의 메서드
		dataset.getWorkspaceMappingList().add(this);
	}
	public void modifyDefaultPath(String datasetDefaultMountPath){
		this.datasetDefaultMountPath = datasetDefaultMountPath;
	}
}
