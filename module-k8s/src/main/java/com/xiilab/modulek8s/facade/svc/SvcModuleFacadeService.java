package com.xiilab.modulek8s.facade.svc;

import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;

public interface SvcModuleFacadeService {
	// 서비스 조회
	SvcResDTO.FindSvcs getServicesByResourceName(String workspaceResourceName, String workloadResourcedName);
}
