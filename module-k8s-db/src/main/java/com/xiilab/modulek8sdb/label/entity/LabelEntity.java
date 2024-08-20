package com.xiilab.modulek8sdb.label.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelLabelEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_LABEL")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class LabelEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "LABEL_ID")
	private long id;
	@Column(name = "LABEL_NAME")
	private String name;
	@Column(name = "WORKSPACE_RESOURCE_NAME")
	private String workspaceResourceName;
	@Column(name = "LABEL_COLER_CODE")
	private String colorCode;
	@Column(name = "LABEL_COLER_NAME")
	private String colorName;
	@Column(name = "LABEL_ORDER")
	private int order;
	@OneToMany(mappedBy = "labelEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ModelLabelEntity> modelLabelEntityList = new ArrayList<>();

	public void updateLabel(String name, String colorCode, String colorCodeName, int order) {
		this.name = name;
		this.colorCode = colorCode;
		this.colorName = colorCodeName;
		this.order = order;
	}
}
