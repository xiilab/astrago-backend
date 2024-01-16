package com.xiilab.modulek8sdb.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.xiilab.modulek8sdb.dto.PortDTO;

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
	JobEntity(String name, String description, String resourceName, int cpuReq, int gpuReq, int memReq,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creator, String creatorId, Map<String, String> envs,
		List<String> volumes, List<PortDTO> ports, WorkloadType workloadType) {
		this.name = name;
		this.description = description;
		this.resourceName = resourceName;
		this.cpuRequest = cpuReq;
		this.gpuRequest = gpuReq;
		this.memRequest = memReq;
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creator = creator;
		this.creatorId = creatorId;
		this.envList = EnvEntity.createEnvList(envs, this);
		this.volumeList = VolumeEntity.createVolumeList(volumes, this);
		this.portList = PortEntity.createPortList(ports, this);
		this.workloadType = workloadType;
	}
}
