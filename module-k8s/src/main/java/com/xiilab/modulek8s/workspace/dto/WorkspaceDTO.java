package com.xiilab.modulek8s.workspace.dto;

import java.time.LocalDateTime;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workspace.vo.WorkspaceVO;

import io.fabric8.kubernetes.api.model.ResourceQuotaStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkspaceDTO {
	@Getter
	@AllArgsConstructor
	public static class RequestDTO {
		private String name;
		private String description;
		private LocalDateTime createAt;
		private String creatorId;
		private String creatorUserName;
		private String creatorFullName;

		public WorkspaceVO.RequestVO convertToVO() {
			return WorkspaceVO.RequestVO.builder()
				.name(this.name)
				.description(this.description)
				.createdAt(createAt)
				.creatorId(creatorId)
				.creatorUserName(creatorUserName)
				.creatorFullName(creatorFullName)
				.build();
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateDTO {
		private String name;
		private String description;
	}

	@Getter
	@AllArgsConstructor
	public static class ResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private String creatorId;
		private String creatorUserName;
		private String creatorFullName;
		private LocalDateTime createdAt;
	}

	@Getter
	public static class TotalResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private boolean isPinYN;
		private AgeDTO age;
		private ModuleWorkloadResDTO recentlyWorkload;

		public TotalResponseDTO(String id, String name, String resourceName, String description, boolean isPinYN,
			LocalDateTime createdTime,
			ModuleWorkloadResDTO recentlyWorkload) {
			this.id = id;
			this.name = name;
			this.resourceName = resourceName;
			this.description = description;
			this.isPinYN = isPinYN;
			this.age = new AgeDTO(createdTime);
			this.recentlyWorkload = recentlyWorkload;
		}
	}

	@Getter
	public static class AdminResponseDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private String creator;
		private LocalDateTime createdAt;
		private int cpu;
		private int mem;
		private int gpu;

		public AdminResponseDTO(WorkspaceDTO.ResponseDTO workspace, ResourceQuotaResDTO resourceQuotaResDTO) {
			this.id = workspace.getId();
			this.name = workspace.getDescription();
			this.resourceName = workspace.getResourceName();
			this.description = workspace.getDescription();
			this.creator = workspace.getCreatorFullName();
			this.createdAt = workspace.getCreatedAt();
			this.cpu = resourceQuotaResDTO.getReqCPU();
			this.mem = resourceQuotaResDTO.getReqMEM();
			this.gpu = resourceQuotaResDTO.getReqGPU();
		}

		public AdminResponseDTO(WorkspaceDTO.ResponseDTO workspace) {
			this.id = workspace.getId();
			this.name = workspace.getDescription();
			this.resourceName = workspace.getResourceName();
			this.description = workspace.getDescription();
			this.creator = workspace.getCreatorFullName();
			this.createdAt = workspace.getCreatedAt();
		}
	}

	@Getter
	@Builder
	public static class AdminInfoDTO {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private String creator;
		private LocalDateTime createdAt;
		private int reqCPU;
		private int reqMEM;
		private int reqGPU;
		private int useCPU;
		private int useMEM;
		private int useGPU;
		private int allocCPU;
		private int allocMEM;
		private int allocGPU;
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class WorkspaceResourceStatus {
		private String id;
		private String name;
		private String resourceName;
		private String description;
		private ResourceStatus resourceStatus;

		public WorkspaceResourceStatus(WorkspaceVO.ResponseVO workspaceResVO, ResourceQuotaStatus resourceStatus) {
			this.id = workspaceResVO.getUid();
			this.name = workspaceResVO.getName();
			this.resourceName = workspaceResVO.getResourceName();
			this.description = workspaceResVO.getDescription();
			this.resourceStatus = new ResourceStatus(resourceStatus);
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceStatus {
		private String cpuLimit;
		private String cpuUsed;
		private String gpuLimit;
		private String gpuUsed;
		private String memLimit;
		private String memUsed;

		public ResourceStatus(ResourceQuotaStatus resourceQuota) {
			this.cpuLimit = resourceQuota.getHard().get("requests.cpu").getAmount();
			this.cpuUsed = resourceQuota.getUsed().get("requests.cpu").getAmount();
			this.gpuLimit = resourceQuota.getHard().get("requests.nvidia.com/gpu").getAmount();
			this.gpuUsed = resourceQuota.getUsed().get("requests.nvidia.com/gpu").getAmount();
			this.memLimit = resourceQuota.getHard().get("requests.memory").getAmount();
			this.memUsed = resourceQuota.getUsed().get("requests.memory").getAmount();
		}
	}
}
