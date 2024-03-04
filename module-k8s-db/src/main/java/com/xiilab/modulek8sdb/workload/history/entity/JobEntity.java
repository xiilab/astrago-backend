package com.xiilab.modulek8sdb.workload.history.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.workload.history.dto.PortDTO;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "TB_WORKLOAD_JOB")
@SuperBuilder
@NoArgsConstructor
@Getter
public class JobEntity extends WorkloadEntity {

	@Builder(builderMethodName = "jobBuilder", builderClassName = "jobBuilder")
	JobEntity(String name, String description, String resourceName, String workspaceName, String workspaceResourceName, Float cpuReq, Integer gpuReq, Float memReq,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creatorName, String creatorId, Map<String, String> envs,
		List<String> volumes, Map<String, Integer> ports, WorkloadType workloadType, String workloadCmd, ImageEntity image) {
		this.name = name;
		this.description = description;
		this.resourceName = resourceName;
		this.workspaceName = workspaceName;
		this.workspaceResourceName = workspaceResourceName;
		this.cpuRequest = new BigDecimal(cpuReq);
		this.gpuRequest = gpuReq;
		this.memRequest = new BigDecimal(memReq);
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creatorId = creatorId;
		this.creatorName = creatorName;
		this.envList = EnvEntity.generateEnvList(envs, this);
		this.volumeList = VolumeEntity.generateVolumeList(volumes, this);
		this.portList = PortEntity.generatePortList(ports, this);
		this.workloadType = workloadType;
		this.workloadCMD = workloadCmd;
		this.image = image;
		// end::tagname[]
	}
}
