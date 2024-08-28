package com.xiilab.modulek8s.workload.svc.repository;

import java.util.List;

import com.xiilab.modulek8s.workload.svc.vo.ClusterIPSvcVO;
import com.xiilab.modulek8s.workload.svc.vo.NodeSvcVO;

import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;

public interface SvcRepository {
	void createNodePortService(NodeSvcVO nodeSvcVO);

	void deleteService(String workSpaceName, String workloadName);


	void createClusterIPService(ClusterIPSvcVO serviceDtoToServiceVO);

	void deleteServiceByResourceName(String svcName, String namespace);
	ServiceList getServicesByResourceName(String workspaceResourceName, String workloadResourcedName);
	List<List<ServicePort>> getPortsByWorkloadResourceName(String namespace, String resourceName);

}
