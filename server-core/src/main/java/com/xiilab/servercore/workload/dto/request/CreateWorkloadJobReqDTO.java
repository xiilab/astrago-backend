package com.xiilab.servercore.workload.dto.request;

import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.APIBaseReqDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleEnvReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModulePortReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8sdb.version.enums.FrameWorkType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class CreateWorkloadJobReqDTO extends APIBaseReqDTO {
	protected String workspace;    // 워크스페이스명
	protected WorkloadType workloadType;
	protected ModuleImageReqDTO image;
	protected List<ModulePortReqDTO> ports;
	protected List<ModuleEnvReqDTO> envs;
	protected List<ModuleVolumeReqDTO> datasets;
	protected List<ModuleVolumeReqDTO> models;
	protected List<ModuleCodeReqDTO> codes;
	protected String workingDir;
	protected String command;
	protected Map<String, String> parameter;
	protected String creatorId;
	protected String creatorUserName;
	protected String creatorFullName;
	@Setter
	protected String nodeName;
	protected GPUType gpuType;
	protected String gpuName;
	protected Integer gpuOnePerMemory;
	protected Integer resourcePresetId;
	@Setter
	protected FrameWorkType ide;
	protected String outputMountPath;	// output 마운트 경로

	public abstract CreateWorkloadReqDTO toModuleDTO(String initContainerUrl);

	public void setUserInfo(String creatorId, String creatorName, String creatorFullName) {
		this.creatorId = creatorId;
		this.creatorUserName = creatorName;
		this.creatorFullName = creatorFullName;
	}

	public abstract float getTotalCpuRequest();

	public abstract float getTotalMemoryRequest();

	public abstract int getTotalGpuRequest();
}
