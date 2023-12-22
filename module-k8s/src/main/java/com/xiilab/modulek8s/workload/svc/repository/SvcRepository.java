package com.xiilab.modulek8s.workload.svc.repository;

import com.xiilab.modulek8s.workload.svc.vo.ServiceVO;

public interface SvcRepository {
	void createNodePortService(ServiceVO serviceVO);
}
