package com.xiilab.modulek8sdb.dataset.entity;


import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
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
@Table(name = "TB_MODEL_WORKLOAD_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE TB_MODEL_WORKLOAD_MAPPING tmwm SET tmwm.DELETE_YN = 'Y' WHERE tmwm.MODEL_WORKLOAD_MAPPING_ID = ?")
@Getter
public class ModelWorkLoadMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_WORKLOAD_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@JsonIgnore
	private Model model;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKLOAD_ID")
	private WorkloadEntity workload;

	@Column(name = "MOUNT_PATH")
	private String mountPath;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYN = DeleteYN.N;

	@Builder
	public ModelWorkLoadMappingEntity(Model model, WorkloadEntity workload, String mountPath) {
		this.model = model;
		this.workload = workload;
		this.mountPath = mountPath;
		//연관관계 편의 메서드
		// model.getModelWorkLoadMappingList().add(this);
		// workload.getModelWorkloadMappingList().add(this);
	}
}
