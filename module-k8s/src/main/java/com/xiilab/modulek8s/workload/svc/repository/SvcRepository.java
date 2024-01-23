package com.xiilab.modulek8s.workload.svc.repository;

import com.xiilab.modulek8s.workload.svc.vo.NodeSvcVO;

public interface SvcRepository {
	void createNodePortService(NodeSvcVO nodeSvcVO);

	void deleteService(String workSpaceName, String workloadName);
}
