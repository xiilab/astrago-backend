package com.xiilab.modulek8sdb.workload.history.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.WorkloadType;
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
public class JobEntity extends WorkloadEntity {
	@Column(name = "WORKLOAD_IDE")
	private String ide;
	@Builder(builderMethodName = "jobBuilder", builderClassName = "jobBuilder")
	JobEntity(String uid, String name, String description, String resourceName, String workspaceName, String workspaceResourceName, Float cpuReq, Integer gpuReq, Float memReq,
		LocalDateTime createdAt, LocalDateTime deletedAt, String creatorRealName, String creatorName, String creatorId, Map<String, String> envs,
		List<String> volumes, Map<String, Integer> ports, WorkloadType workloadType, String workloadCmd, ImageEntity image, DeleteYN deleteYN, String ide,
		String workingDir, Map<String,String> argMap) {
		this.uid = uid;
		this.name = name;
		this.description = description;
		this.resourceName = resourceName;
		this.workspaceName = workspaceName;
		this.workspaceResourceName = workspaceResourceName;
		this.cpuRequest = BigDecimal.valueOf(cpuReq);
		this.gpuRequest = gpuReq;
		this.memRequest = BigDecimal.valueOf(memReq);
		this.createdAt = createdAt;
		this.deletedAt = deletedAt;
		this.creatorId = creatorId;
		this.creatorRealName = creatorRealName;
		this.creatorName = creatorName;
		this.envList = EnvEntity.generateEnvList(envs, this);
		this.volumeList = VolumeEntity.generateVolumeList(volumes, this);
		this.portList = PortEntity.generatePortList(ports, this);
		this.workloadType = workloadType;
		this.workingDir = workingDir;
		this.workloadCMD = workloadCmd;
		this.image = image;
		this.ide = ide;
		this.deleteYN = deleteYN;

		if (!CollectionUtils.isEmpty(argMap)) {
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				this.workloadArgs = objectMapper.writeValueAsString(argMap);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void updateImage(ImageEntity image) {
		this.image = image;
	}

	public void updateJob(String name, String description) {
		super.name = name;
		super.description = description;
	}

}
