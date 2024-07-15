package com.xiilab.modulek8sdb.deploy.entity;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_DEPLOY")
@SuperBuilder
@NoArgsConstructor
@Getter
public class DeployEntity extends WorkloadEntity {
	@Column(name = "DEPLOY_TYPE")
	@Enumerated(EnumType.STRING)
	private DeployType deployType;
	@Column(name = "REPLICA")
	protected Integer replica;
	@Column(name = "REQ_CPU")
	private Float cpuRequest;
	@Column(name = "REQ_MEM")
	private Float memRequest;
	@Column(name = "REQ_GPU")
	private Integer gpuRequest;
	// private String modelId;
	// private String versionId;

}
