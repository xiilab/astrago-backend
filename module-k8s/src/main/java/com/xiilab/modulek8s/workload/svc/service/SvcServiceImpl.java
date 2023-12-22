package com.xiilab.modulek8s.workload.svc.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;
import com.xiilab.modulek8s.workload.svc.vo.ServiceVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SvcServiceImpl implements SvcService {
	private final SvcRepository svcRepository;

	@Override
	public void createNodePortService(CreateSvcReqDTO createSvcReqDTO) {
		svcRepository.createNodePortService(ServiceVO.createServiceDtoToServiceVO(createSvcReqDTO));
	}
}
