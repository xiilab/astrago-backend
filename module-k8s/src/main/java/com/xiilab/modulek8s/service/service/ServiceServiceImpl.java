package com.xiilab.modulek8s.service.service;

import com.xiilab.modulek8s.service.dto.request.CreateServiceDTO;
import com.xiilab.modulek8s.service.repository.ServiceRepository;
import com.xiilab.modulek8s.service.vo.ServiceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceServiceImpl implements ServiceService {
	private final ServiceRepository serviceRepository;

	@Override
	public void createService(CreateServiceDTO createServiceDTO) {
		serviceRepository.createService(ServiceVO.CreateServiceDtoTOServiceVO(createServiceDTO));
	}

    @Override
    public void deleteService(String workSpaceName, String workloadName) {
        serviceRepository.deleteService(workSpaceName, workloadName);
    }
}
