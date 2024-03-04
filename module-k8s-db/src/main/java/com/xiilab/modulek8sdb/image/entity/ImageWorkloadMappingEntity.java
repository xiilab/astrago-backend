package com.xiilab.modulek8sdb.image.entity;

import java.awt.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

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
@Table(name = "TB_IMAGE_WORKLOAD_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ImageWorkloadMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMAGE_WORKLOAD_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "IMAGE_ID")
	@JsonIgnore
	private ImageEntity image;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKLOAD_ID")
	private WorkloadEntity workload;

	@Builder
	public ImageWorkloadMappingEntity(ImageEntity image, WorkloadEntity workload) {
		this.image = image;
		this.workload = workload;
	}
}
