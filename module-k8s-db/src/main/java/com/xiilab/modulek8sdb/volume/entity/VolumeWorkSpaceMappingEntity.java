package com.xiilab.modulek8sdb.volume.entity;

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
@Table(name = "TB_VOLUME_WORKSPACE_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VolumeWorkSpaceMappingEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VOLUME_WORKSPACE_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VOLUME_ID")
	@JsonIgnore
	private Volume volume;

	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "MODEL_DEFAULT_MOUNT_PATH")
	private String modelDefaultMountPath;
	@Builder
	public VolumeWorkSpaceMappingEntity(Volume volume, String workspaceResourceName, String volumeDefaultMountPath) {
		this.volume = volume;
		this.workspaceResourceName = workspaceResourceName;
		this.modelDefaultMountPath = volumeDefaultMountPath;
		//연관관계 편의 메서드
		volume.getVolumeWorkSpaceMappingList().add(this);
	}

	public void modifyDefaultPath(String modelDefaultMountPath) {
		this.modelDefaultMountPath = modelDefaultMountPath;
	}
}
