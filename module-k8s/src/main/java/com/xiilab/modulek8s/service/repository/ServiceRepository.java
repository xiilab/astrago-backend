package com.xiilab.modulek8s.service.repository;

import com.xiilab.modulek8s.service.vo.ServiceVO;

public interface ServiceRepository {
	void createService(ServiceVO serviceVO);

	void deleteService(String workSpaceName, String workloadName);
}
