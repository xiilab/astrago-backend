package com.xiilab.modulek8s.workload.svc.service;

import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;

public interface SvcService {
	/**
	 * 노드포트 서비스 생성
	 *
	 * @param createSvcReqDTO
	 */
	void createNodePortService(CreateSvcReqDTO createSvcReqDTO);

	void deleteService(String workSpaceName, String workloadName);

	void createClusterIPService(CreateClusterIPSvcReqDTO createClusterIPSvcReqDTO);
}
