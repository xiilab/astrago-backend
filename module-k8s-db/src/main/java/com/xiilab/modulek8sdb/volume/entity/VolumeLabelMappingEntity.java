package com.xiilab.modulek8sdb.volume.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_VOLUME_LABEL_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class VolumeLabelMappingEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VOLUME_LABEL_MAPPING")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VOLUME_ID")
	@JsonIgnore
	private Volume volume;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LABEL_ID")
	private LabelEntity label;

}
