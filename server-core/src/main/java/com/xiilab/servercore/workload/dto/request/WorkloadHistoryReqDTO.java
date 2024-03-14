package com.xiilab.servercore.workload.dto.request;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workload.dto.response.ModuleEnvResDTO;
import com.xiilab.modulek8s.workload.dto.response.CreateJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModulePortResDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WorkloadHistoryReqDTO {
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CreateWorkloadHistory {
		private String name;
		private String description;
		private String resourceName;
		private String workspaceName;
		private String workspaceResourceName;
		private Integer gpuRequest;
		private Float cpuRequest;
		private Float memRequest;
		private String creatorRealName;
		private String creatorName;
		private String creatorId;
		private LocalDateTime createdAt;
		private Map<String, String> envs;
		private List<String> volumes;
		private Map<String, Integer> ports;
		private WorkloadType workloadType;
		private String cmd;
		private String imageId;
		@JsonIgnore
		private String datasetIds;
		@JsonIgnore
		private String modelIds;
		@JsonIgnore
		private String codeIds;
		@JsonIgnore
		private Map<Long, Map<String, String>> codesInfoMap;
		@JsonIgnore
		private Map<Long, Map<String, String>> datasetInfoMap;
		@JsonIgnore
		private Map<Long, Map<String, String>> modelInfoMap;

		public static CreateWorkloadHistory from(CreateJobResDTO createJobResDTO) {
			return CreateWorkloadHistory.builder()
				.imageId(createJobResDTO.getImageId())
				.name(createJobResDTO.getName())
				.description(createJobResDTO.getDescription())
				.resourceName(createJobResDTO.getResourceName())
				.workspaceName(createJobResDTO.getWorkspaceName())
				.workspaceResourceName(createJobResDTO.getWorkspaceResourceName())
				.envs(convertEnvDtoToMap(createJobResDTO.getEnvs()))
				.ports(convertPortDtoToMap(createJobResDTO.getPorts()))
				.gpuRequest(Integer.parseInt(createJobResDTO.getGpuRequest()))
				.cpuRequest(Float.parseFloat(createJobResDTO.getCpuRequest()))
				.memRequest(Float.parseFloat(createJobResDTO.getMemRequest()))
				.creatorRealName(createJobResDTO.getCreatorFullName())
				.creatorName(createJobResDTO.getCreatorUserName())
				.creatorId(createJobResDTO.getCreatorId())
				.workloadType(createJobResDTO.getType())
				.cmd(createJobResDTO.getCommand())
				.createdAt(createJobResDTO.getCreatedAt())
				.datasetIds(createJobResDTO.getDatasetIds())
				.modelIds(createJobResDTO.getModelIds())
				.codeIds(createJobResDTO.getCodeIds())
				.codesInfoMap(createJobResDTO.getCodesInfoMap())
				.datasetInfoMap(createJobResDTO.getDatasetInfoMap())
				.modelInfoMap(createJobResDTO.getModelInfoMap())
				.build();
		}

		private static Map<String, String> convertEnvDtoToMap(List<ModuleEnvResDTO> envs) {
			return Optional.ofNullable(envs)
				.orElse(Collections.emptyList())
				.stream()
				.collect(Collectors.toMap(ModuleEnvResDTO::variable, ModuleEnvResDTO::value));
		}

		private static Map<String, Integer> convertPortDtoToMap(List<ModulePortResDTO> ports) {
			return Optional.ofNullable(ports)
				.orElse(Collections.emptyList())
				.stream()
				.collect(Collectors.toMap(ModulePortResDTO::name, ModulePortResDTO::originPort));
		}

	}
}
