package com.xiilab.modulek8sdb.dataset.entity;


import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "TB_DATASET_WORKLOAD_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_DATASET_WORKLOAD_MAPPING tdwm SET tdwm.DELETE_YN = 'Y' WHERE tdwm.DATASET_WORKLOAD_MAPPING_ID = ?")
@Getter
public class DatasetWorkLoadMappingEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DATASET_WORKLOAD_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DATASET_ID")
	@JsonIgnore
	private Dataset dataset;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKLOAD_ID")
	private WorkloadEntity workload;

	@Column(name = "MOUNT_PATH")
	private String mountPath;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYN = DeleteYN.N;

	@Builder
	public DatasetWorkLoadMappingEntity(Dataset dataset, WorkloadEntity workload, String mountPath) {
		this.dataset = dataset;
		this.workload = workload;
		this.mountPath = mountPath;
		//연관관계 편의 메서드
		// dataset.getDatasetWorkloadMappingList().add(this);
		// workload.getDatasetWorkloadMappingList().add(this);
	}



}
