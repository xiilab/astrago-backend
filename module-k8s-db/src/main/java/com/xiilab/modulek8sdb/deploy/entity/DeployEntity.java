package com.xiilab.modulek8sdb.deploy.entity;

import java.time.LocalDateTime;
import java.util.Map;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.workload.history.entity.EnvEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "TB_DEPLOY")
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODEL_REPO_ID")
	private ModelRepoEntity modelRepoEntity;

	@Builder
	public DeployEntity(String uid, String name, String description, String resourceName, String workspaceName,
		String workspaceResourceName, String nodeName, String gpuName, GPUType gpuType, Integer gpuOnePerMemory, Integer resourcePresetId, Float cpuReq, Integer gpuReq, Float memReq,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creatorRealName, String creatorName, String creatorId,
		Map<String, String> envs,
		Map<String, Integer> ports, WorkloadType workloadType, String workloadCmd,
		DeleteYN deleteYN, String ide, String workingDir,
		WorkloadStatus workloadStatus, int replica, DeployType deployType, ModelRepoEntity modelRepoEntity) {
		this.uid = uid;
		this.name = name;
		this.description = description;
		this.resourceName = resourceName;
		this.workspaceName = workspaceName;
		this.workspaceResourceName = workspaceResourceName;
		super.nodeName = nodeName;
		super.gpuName = gpuName;
		super.gpuType = gpuType;
		super.gpuOnePerMemory = gpuOnePerMemory;
		super.resourcePresetId = resourcePresetId;
		this.cpuRequest = cpuReq;
		this.gpuRequest = gpuReq;
		this.memRequest = memReq;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creatorId = creatorId;
		this.creatorRealName = creatorRealName;
		this.creatorName = creatorName;
		this.envList = EnvEntity.generateEnvList(envs, this);
		this.portList = PortEntity.generatePortList(ports, this);
		this.workloadType = workloadType;
		this.workingDir = workingDir;
		this.workloadCMD = workloadCmd;
		this.deleteYN = deleteYN;
		this.workloadStatus = workloadStatus;
		this.replica = replica;
		this.deployType = deployType;
		this.modelRepoEntity = modelRepoEntity;
	}
}
