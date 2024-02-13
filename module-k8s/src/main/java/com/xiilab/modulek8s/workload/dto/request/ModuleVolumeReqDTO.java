package com.xiilab.modulek8s.workload.dto.request;

import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.workload.vo.JobVolumeVO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ModuleVolumeReqDTO {
	private Long id;
	private String mountPath;
	@Setter
	private CreatePV createPV;
	@Setter
	private CreatePVC createPVC;

	public JobVolumeVO toJobVolumeVO() {
		return new JobVolumeVO(id, this.mountPath, createPV.getPvName(), createPV.getPvcName());
	}
}
