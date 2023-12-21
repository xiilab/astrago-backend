package com.xiilab.modulek8s.service.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.service.vo.ServiceVO;

public interface ServiceRepository {
	void createService(ServiceVO serviceVO);
}
