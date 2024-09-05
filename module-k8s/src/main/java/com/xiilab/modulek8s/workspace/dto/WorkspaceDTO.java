package com.xiilab.modulek8s.workspace.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.facade.dto.ResMIGDTO;
import com.xiilab.modulek8s.facade.dto.ResMPSDTO;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
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
		private RecentlyWorkloadDTO recentlyWorkload;

		public TotalResponseDTO(String id, String name, String resourceName, String description, boolean isPinYN,
			LocalDateTime createdTime,
			RecentlyWorkloadDTO recentlyWorkload) {
			this.id = id;
			this.name = name;
			this.resourceName = resourceName;
			this.description = description;
			this.isPinYN = isPinYN;
			this.age = createdTime != null ? new AgeDTO(createdTime) : null;
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
		private int allocCPU;
		private int allocMEM;
		private int allocGPU;
		private float useCPU;
		private float useMEM;
		private int useGPU;

		public AdminResponseDTO(WorkspaceDTO.ResponseDTO workspace, ResourceQuotaResDTO resourceQuotaResDTO) {
			this.id = workspace.getId();
			this.name = workspace.getName();
			this.resourceName = workspace.getResourceName();
			this.description = workspace.getDescription();
			this.creator = workspace.getCreatorFullName();
			this.createdAt = workspace.getCreatedAt();
			this.allocCPU = resourceQuotaResDTO.getReqCPU();
			this.allocMEM = resourceQuotaResDTO.getReqMEM();
			this.allocGPU = resourceQuotaResDTO.getReqGPU();
			this.useCPU = resourceQuotaResDTO.getUseCPU();
			this.useMEM = resourceQuotaResDTO.getUseMEM();
			this.useGPU = resourceQuotaResDTO.getUseGPU();
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
		private float useCPU;
		private float useMEM;
		private float useGPU;
		private float allocCPU;
		private float allocMEM;
		private float allocGPU;
		private int totalCPU;
		private int totalMEM;
		private int totalGPU;
		private List<ResMIGDTO> migInfo;
		private int migTotalCount;
		private List<ResMPSDTO> mpsInfo;
		private int mpsTotalCount;

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
		private String creatorFullName;
		private String creatorId;
		private String creatorUserName;

		public WorkspaceResourceStatus(WorkspaceVO.ResponseVO workspaceResVO, ResourceQuotaStatus resourceStatus) {
			this.id = workspaceResVO.getUid();
			this.name = workspaceResVO.getName();
			this.resourceName = workspaceResVO.getResourceName();
			this.description = workspaceResVO.getDescription();
			this.creatorFullName = workspaceResVO.getCreatorFullName();
			this.creatorId = workspaceResVO.getCreatorId();
			this.creatorUserName = workspaceResVO.getCreatorUserName();
			this.resourceStatus = new ResourceStatus(resourceStatus);
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceStatus {
		private float cpuLimit;
		private float memLimit;
		private float gpuLimit;
		private float cpuUsed;
		private float memUsed;
		private float gpuUsed;

		public ResourceStatus(ResourceQuotaStatus resourceQuota) {
			this.cpuLimit = K8sInfoPicker.convertQuantity(resourceQuota.getHard().get("requests.cpu"));
			this.cpuUsed = K8sInfoPicker.convertQuantity(resourceQuota.getUsed().get("requests.cpu"));
			this.gpuLimit = Float.parseFloat(resourceQuota.getHard().get("requests.nvidia.com/gpu").getAmount());
			this.gpuUsed = Float.parseFloat(resourceQuota.getUsed().get("requests.nvidia.com/gpu").getAmount());
			this.memLimit = K8sInfoPicker.convertQuantity(resourceQuota.getHard().get("requests.memory"));
			this.memUsed = K8sInfoPicker.convertQuantity(resourceQuota.getUsed().get("requests.memory"));
		}
	}
}
