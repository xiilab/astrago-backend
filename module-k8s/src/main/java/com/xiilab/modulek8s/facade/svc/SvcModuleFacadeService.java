package com.xiilab.modulek8s.facade.svc;

import java.util.List;

import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;

import io.fabric8.kubernetes.api.model.ServicePort;

public interface SvcModuleFacadeService {
	// 서비스 조회
	SvcResDTO.FindSvcs getServicesByResourceName(String workspaceResourceName, String workloadResourcedName);
	List<List<ServicePort>> getPortsByWorkloadResourceName(String namespace, String resourceName);

}
