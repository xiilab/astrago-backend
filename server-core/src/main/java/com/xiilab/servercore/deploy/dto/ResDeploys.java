package com.xiilab.servercore.deploy.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResDeploys {
	private List<ResDeploy> deploys;
	private long totalCount;

	public static ResDeploys entitiesToDtos(List<DeployEntity> deployEntities, long totalCount){
		return ResDeploys.builder()
			.totalCount(totalCount)
			.deploys(deployEntities.stream().map(ResDeploy::toDto).toList())
			.build();
	}

	@Getter
	@Builder
	public static class ResDeploy{
		private Long id;
		private String name;
		private String deployResourceName;
		private String description;
		private WorkloadStatus workloadStatus;
		private String modelName;
		private String modelVersion;
		private String creatorId;                    // 생성자 ID
		private String creatorUserName;              // 생성자 username(unique)
		private String creatorFullName;              // 생성자 fullName(unique)
		private LocalDateTime createdAt;             // 워크로드 생성일시

		public static ResDeploys.ResDeploy toDto(DeployEntity deploy){
			return ResDeploy.builder()
				.id(deploy.getId())
				.name(deploy.getName())
				.deployResourceName(deploy.getResourceName())
				.description(deploy.getDescription())
				.workloadStatus(deploy.getWorkloadStatus())
				.creatorId(deploy.getCreatorId())
				.creatorUserName(deploy.getCreatorName())
				.creatorFullName(deploy.getCreatorRealName())
				.createdAt(deploy.getCreatedAt())
				.modelName(deploy.getModelRepoEntity() != null ? deploy.getModelRepoEntity().getModelName() : null)
				.modelVersion(deploy.getModelVersion())
				.build();
		}
	}
	@Getter
	@Builder
	public static class DeployInfo{
		private Long id;
		private String workloadName;
		private String description;
		private String workloadResourceName;
		private String workspaceName;
		private String workSpaceResourceName;
		private WorkloadType workloadType;
		private DeployType deployType;
		private FindWorkloadResDTO.Image image;
		private List<FindWorkloadResDTO.Port> ports;
		private List<FindWorkloadResDTO.Env> envs;
		private List<FindWorkloadResDTO.Volume> volumes;
		private String workingDir;
		private String command;
		private WorkloadStatus status;
		private String nodeName;
		private GPUType gpuType;
		private String gpuName;
		private Integer gpuOnePerMemory;
		private Float cpuRequest;
		private Integer gpuRequest;
		private Float memRequest;
		private String regUserId;
		private String regUserName;
		private String regUserRealName;
		private String regDate;
		private boolean canBeDeleted;
		private String modelName;
		private String modelVersion;
		private String modelPath;
		private String storageName;

		public static DeployInfo from(DeployEntity deploy){
			return DeployInfo.builder()
				.id(deploy.getId())
				.workloadName(deploy.getName())
				.workloadResourceName(deploy.getResourceName())
				.workspaceName(deploy.getWorkspaceName())
				.workSpaceResourceName(deploy.getWorkspaceResourceName())
				.description(deploy.getDescription())
				.deployType(deploy.getDeployType())
				.workloadType(deploy.getWorkloadType())
				.image(new FindWorkloadResDTO.Image(deploy.getImageWorkloadMappingEntity().getImage()))
				.ports(deploy.getPortList().stream().map(FindWorkloadResDTO.Port::new).toList())
				.envs(deploy.getEnvList().stream().map(FindWorkloadResDTO.Env::new).toList())
				.volumes(deploy.getVolumeWorkloadMappingList().stream().map(FindWorkloadResDTO.Volume::new).toList())
				.command(deploy.getWorkloadCMD())
				.cpuRequest(deploy.getCpuRequest())
				.gpuRequest(deploy.getGpuRequest() == null ? 0 : deploy.getGpuRequest())
				.memRequest(deploy.getMemRequest())
				.regUserId(deploy.getCreatorId())
				.regUserName(deploy.getCreatorName())
				.regUserRealName(deploy.getCreatorRealName())
				.regDate(deploy.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.workingDir(deploy.getWorkingDir())
				.status(deploy.getWorkloadStatus())
				.canBeDeleted(deploy.isCanBeDeleted())
				.gpuName(deploy.getGpuName())
				.gpuType(deploy.getGpuType())
				.gpuOnePerMemory(deploy.getGpuOnePerMemory())
				.nodeName(deploy.getNodeName())
				.modelName(deploy.getModelRepoEntity() != null ? deploy.getModelRepoEntity().getModelName() : null)
				.modelVersion(deploy.getModelVersion())
				.modelPath(deploy.getModelPath())
				.storageName(deploy.getStorageEntity() != null ? deploy.getStorageEntity().getStorageName() : null)
				.build();
		}
	}
}
