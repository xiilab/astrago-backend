package com.xiilab.modulek8s.workload.svc.service;

import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;

import io.fabric8.kubernetes.api.model.ServiceList;

public interface SvcService {
	/**
	 * 노드포트 서비스 생성
	 *
	 * @param createSvcReqDTO
	 */
	void createNodePortService(CreateSvcReqDTO createSvcReqDTO);

	void deleteService(String workSpaceName, String workloadName);

	void createClusterIPService(CreateClusterIPSvcReqDTO createClusterIPSvcReqDTO);

	void deleteServiceByResourceName(String svcName, String namespace);

	SvcResDTO.FindSvcs getServicesByResourceName(String workspaceResourceName, String workloadResourcedName);
}
