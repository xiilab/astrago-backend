package com.xiilab.modulek8s.workload.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ModuleVolumeReqDTO {
	private Long id;
	private String mountPath;
	@Setter
	@JsonIgnore
	private CreatePV createPV;
	@Setter
	@JsonIgnore
	private CreatePVC createPVC;

	@Builder
	public ModuleVolumeReqDTO(Long id, String mountPath, CreatePV createPV, CreatePVC createPVC) {
		this.id = id;
		this.mountPath = mountPath;
		this.createPV = createPV;
		this.createPVC = createPVC;
	}

	public ModuleVolumeReqDTO(Long id, String mountPath) {
		this.id = id;
		this.mountPath = mountPath;
	}

	public JobVolumeVO toJobVolumeVO() {
		return new JobVolumeVO(id, this.mountPath, createPV.getPvName(), createPV.getPvcName());
	}
}
