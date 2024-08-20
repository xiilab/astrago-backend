package com.xiilab.modulek8sdb.experiment.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;
import com.xiilab.modulek8sdb.experiment.dto.ExperimentColumnDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_EXPERIMENT_COLUMN")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExperimentColumnEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	@Column(name = "NAME")
	private String name;
	@Column(name = "ORDER")
	private int order;
	@Column(name = "WORKSPACE")
	private String workspace;

	public ExperimentColumnEntity(ExperimentColumnDTO.Req req) {
		this.name = req.getName();
		this.order = req.getOrder();
		this.workspace = req.getWorkspace();
	}

	public void updateOrder(int order) {
		this.order = order;
	}
}
