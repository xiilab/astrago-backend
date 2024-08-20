package com.xiilab.modulek8sdb.workload.history.entity;

import com.xiilab.modulek8sdb.label.entity.LabelEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_LABEL_EXPERIMENT_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class LabelExperimentMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXPERIMENT_LABEL_ID")
	private long id;
	@ManyToOne(fetch = FetchType.LAZY)
	private ExperimentEntity experiment;
	@ManyToOne(fetch = FetchType.LAZY)
	private LabelEntity label;
}
