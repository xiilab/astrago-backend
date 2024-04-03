package com.xiilab.modulek8sdb.model.entity;

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
@Table(name = "TB_MODEL_WORKSPACE_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ModelWorkSpaceMappingEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MODEL_WORKSPACE_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_ID")
	@JsonIgnore
	private Model model;

	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "MODEL_DEFAULT_MOUNT_PATH")
	private String modelDefaultMountPath;
	@Builder
	public ModelWorkSpaceMappingEntity(Model model, String workspaceResourceName, String modelDefaultMountPath) {
		this.model = model;
		this.workspaceResourceName = workspaceResourceName;
		this.modelDefaultMountPath = modelDefaultMountPath;
		//연관관계 편의 메서드
		model.getModelWorkSpaceMappingList().add(this);
	}

	public void modifyDefaultPath(String modelDefaultMountPath) {
		this.modelDefaultMountPath = modelDefaultMountPath;
	}
}
