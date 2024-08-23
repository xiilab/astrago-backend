package com.xiilab.modulek8sdb.workload.history.entity;

import java.time.LocalDateTime;
import java.util.Map;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "TB_WORKLOAD_DISTRIBUTED_JOB")
@SuperBuilder
@NoArgsConstructor
@Getter
public class DistributedJobEntity extends DevelopEntity {
	@Column(name = "LAUNCHER_CPU_REQUEST")
	private Float launcherCpuRequest;
	@Column(name = "LAUNCHER_MEM_REQUEST")
	private Float launcherMemRequest;
	@Column(name = "WORKER_CPU_REQUEST")
	private Float workerCpuRequest;
	@Column(name = "WORKER_MEM_REQUEST")
	private Float workerMemRequest;
	@Column(name = "WORKER_GPU_REQUEST")
	private Integer workerGpuRequest;
	@Column(name = "WORKER_COUNT")
	private Integer workerCount;

	@Builder(builderMethodName = "jobBuilder", builderClassName = "jobBuilder")
	DistributedJobEntity(String uid, String name, String description, String resourceName, String workspaceName,
		String workspaceResourceName, String nodeName, String gpuName, GPUType gpuType, Integer gpuOnePerMemory,
		Integer resourcePresetId, float launcherCpuRequest, float launcherMemRequest, float workerCpuRequest,
		float workerMemRequest, int workerGpuRequest, int workerCount,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creatorRealName, String creatorName, String creatorId,
		// Map<String, String> envs,
		//List<String> volumes,
		Map<String, Integer> ports,
		WorkloadType workloadType, String workloadCmd,
		ImageEntity image, DeleteYN deleteYN, String ide, String workingDir, Map<String, String> parameter,
		WorkloadStatus workloadStatus) {
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
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creatorId = creatorId;
		this.creatorRealName = creatorRealName;
		this.creatorName = creatorName;
		// this.envList = EnvEntity.generateEnvList(envs, this);
		// this.volumeList = VolumeEntity.generateVolumeList(volumes, this);
		this.portList = PortEntity.generatePortList(ports, this);
		this.workloadType = workloadType;
		this.workingDir = workingDir;
		this.workloadCMD = workloadCmd;
		// this.image = image;
		this.deleteYN = deleteYN;
		this.workloadStatus = workloadStatus;
		this.parameter = JsonConvertUtil.convertMapToJson(parameter);
		this.launcherCpuRequest = launcherCpuRequest;
		this.launcherMemRequest = launcherMemRequest;
		this.workerCpuRequest = workerCpuRequest;
		this.workerMemRequest = workerMemRequest;
		this.workerGpuRequest = workerGpuRequest;
		this.workerCount = workerCount;
	}
}
