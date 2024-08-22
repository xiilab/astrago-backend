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

@Entity(name = "TB_WORKLOAD_JOB")
@SuperBuilder
@NoArgsConstructor
@Getter
public class JobEntity extends DevelopEntity {
	@Column(name = "WORKLOAD_IDE")
	private String ide;
	@Column(name = "WORKLOAD_REQ_CPU")
	private Float cpuRequest;
	@Column(name = "WORKLOAD_REQ_MEM")
	private Float memRequest;
	@Column(name = "WORKLOAD_REQ_GPU")
	private Integer gpuRequest;

	@Builder(builderMethodName = "jobBuilder", builderClassName = "jobBuilder")
	JobEntity(String uid, String name, String description, String resourceName, String workspaceName,
		String workspaceResourceName, String nodeName, String gpuName, GPUType gpuType, Integer gpuOnePerMemory, Integer resourcePresetId, Float cpuReq, Integer gpuReq, Float memReq,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creatorRealName, String creatorName, String creatorId,
		// Map<String, String> envs, // List<String> volumes,
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
		this.cpuRequest = cpuReq;
		this.gpuRequest = gpuReq;
		this.memRequest = memReq;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creatorId = creatorId;
		this.creatorRealName = creatorRealName;
		this.creatorName = creatorName;
		// this.envList = EnvEntity.generateEnvList(envs, this);
		// this.volumeList = VolumeEntity.generateVolumeList(volumes, this);
		// this.portList = PortEntity.generatePortList(ports, this);
		this.workloadType = workloadType;
		this.workingDir = workingDir;
		this.workloadCMD = workloadCmd;
		// this.image = image;
		this.ide = ide;
		this.deleteYN = deleteYN;
		this.workloadStatus = workloadStatus;
		this.parameter = JsonConvertUtil.convertMapToJson(parameter);
	}
}
