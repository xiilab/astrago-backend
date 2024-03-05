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
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModulePortResDTO;
import com.xiilab.modulek8sdb.workload.history.dto.PortDTO;

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

		public static CreateWorkloadHistory from(ModuleJobResDTO moduleJobResDTO) {
			return CreateWorkloadHistory.builder()
				.imageId(moduleJobResDTO.getImageId())
				.name(moduleJobResDTO.getName())
				.description(moduleJobResDTO.getDescription())
				.resourceName(moduleJobResDTO.getResourceName())
				.workspaceName(moduleJobResDTO.getWorkspaceName())
				.workspaceResourceName(moduleJobResDTO.getWorkspaceResourceName())
				.envs(convertEnvDtoToMap(moduleJobResDTO.getEnvs()))
				//TODO 포트 추가필요
				.ports(convertPortDtoToMap(moduleJobResDTO.getPorts()))
				.gpuRequest(Integer.parseInt(moduleJobResDTO.getGpuRequest()))
				.cpuRequest(Float.parseFloat(moduleJobResDTO.getCpuRequest()))
				.memRequest(Float.parseFloat(moduleJobResDTO.getMemRequest()))
				.creatorName(moduleJobResDTO.getCreatorUserName())
				.creatorId(moduleJobResDTO.getCreatorId())
				.workloadType(moduleJobResDTO.getType())
				.cmd(moduleJobResDTO.getCommand())
				.createdAt(moduleJobResDTO.getCreatedAt())
				.datasetIds(moduleJobResDTO.getDatasetIds())
				.modelIds(moduleJobResDTO.getModelIds())
				.codeIds(moduleJobResDTO.getCodeIds())
				.codesInfoMap(moduleJobResDTO.getCodesInfoMap())
				.datasetInfoMap(moduleJobResDTO.getDatasetInfoMap())
				.modelInfoMap(moduleJobResDTO.getModelInfoMap())
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
