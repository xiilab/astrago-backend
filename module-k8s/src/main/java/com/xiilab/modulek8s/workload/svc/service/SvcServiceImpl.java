package com.xiilab.modulek8s.workload.svc.service;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.response.SvcResDTO;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;
import com.xiilab.modulek8s.workload.svc.vo.ClusterIPSvcVO;
import com.xiilab.modulek8s.workload.svc.vo.NodeSvcVO;

import io.fabric8.kubernetes.api.model.ServiceList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SvcServiceImpl implements SvcService {
	private final SvcRepository svcRepository;

	@Override
	public void createNodePortService(CreateSvcReqDTO createSvcReqDTO) {
		svcRepository.createNodePortService(NodeSvcVO.createServiceDtoToServiceVO(createSvcReqDTO));
	}

	@Override
	public void deleteService(String workSpaceName, String workloadName) {
		svcRepository.deleteService(workSpaceName, workloadName);
	}

	@Override
	public void createClusterIPService(CreateClusterIPSvcReqDTO createClusterIPSvcReqDTO) {
		svcRepository.createClusterIPService(ClusterIPSvcVO.createServiceDtoToServiceVO(createClusterIPSvcReqDTO));
	}

	@Override
	public void deleteServiceByResourceName(String svcName, String namespace) {
		svcRepository.deleteServiceByResourceName(svcName, namespace);
	}

	@Override
	public SvcResDTO.FindSvcs getServicesByResourceName(String workspaceResourceName, String workloadResourcedName) {
		ServiceList findServiceList = svcRepository.getServicesByResourceName(workspaceResourceName,
			workloadResourcedName);
		return SvcResDTO.FindSvcs.from(findServiceList, findServiceList.getItems().size());
	}

}
