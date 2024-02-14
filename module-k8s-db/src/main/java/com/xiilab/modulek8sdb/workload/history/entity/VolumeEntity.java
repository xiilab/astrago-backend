package com.xiilab.modulek8sdb.workload.history.entity;

import java.util.Collections;
import java.util.List;

import org.springframework.util.CollectionUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_VOLUME")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class VolumeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VOLUME_ID")
	private Long id;
	@Column(name = "VOLUME_NAME")
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkloadEntity workload;

	public static List<VolumeEntity> createVolumeList(List<String> volumes, WorkloadEntity workload) {
		if (CollectionUtils.isEmpty(volumes)) {
			return Collections.emptyList();
		}
		return volumes.stream().map(volume -> VolumeEntity.builder()
			.name(volume)
			.workload(workload)
			.build()).toList();
	}
}
