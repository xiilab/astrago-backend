package com.xiilab.modulek8s.workload.svc.dto.request;

import java.util.List;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.svc.enums.SvcType;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class CreateClusterIPSvcReqDTO{
	private String namespace;
	private SvcType svcType;
	private String deploymentName;
	private String svcName;

}
