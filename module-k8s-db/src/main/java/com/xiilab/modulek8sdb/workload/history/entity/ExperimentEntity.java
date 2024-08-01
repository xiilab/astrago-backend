package com.xiilab.modulek8sdb.workload.history.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkloadEntity workload;
}
