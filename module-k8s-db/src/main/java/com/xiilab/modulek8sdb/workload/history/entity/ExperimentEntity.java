package com.xiilab.modulek8sdb.workload.history.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8sdb.label.entity.LabelEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_EXPERIMENT")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EXPERIMENT_ID")
	private Long id;
	@Column(name = "EXPERIMENT_UUID")
	private String uuid;
	@CreatedDate
	@Column(name = "EXPERIMENT_CREATED_DATE")
	private LocalDateTime createdTime;
	@Column(name = "IS_VIEW_YN")
	private boolean isViewYN;
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkloadEntity workload;
	@Builder.Default
	@OneToMany(mappedBy = "experiment", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<LabelExperimentMappingEntity> labels = new ArrayList<>();

	public void updateIsViewYN(boolean isViewYN) {
		this.isViewYN = isViewYN;
	}

	public void addLabels(List<LabelEntity> labels) {
		if (!CollectionUtils.isEmpty(labels)) {
			this.labels.clear();
			List<LabelExperimentMappingEntity> labelEntities = labels.stream()
				.map(label ->
					LabelExperimentMappingEntity.builder()
						.label(label)
						.experiment(this)
						.build())
				.toList();
			this.labels.addAll(labelEntities);
		} else {
			this.labels.clear();
		}
	}
}
