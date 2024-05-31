package com.xiilab.modulek8s.workload.dto.response.abst;

import com.xiilab.modulek8s.common.dto.DistributedResourceDTO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class AbstractDistributedWorkloadResDTO extends AbstractModuleWorkloadResDTO {
	protected DistributedResourceDTO.LauncherInfo launcherInfo;
	protected DistributedResourceDTO.WorkerInfo workerInfo;

	protected AbstractDistributedWorkloadResDTO(HasMetadata hasMetadata) {
		super(hasMetadata);
	}
}
