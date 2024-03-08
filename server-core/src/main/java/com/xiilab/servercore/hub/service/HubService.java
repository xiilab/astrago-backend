package com.xiilab.servercore.hub.service;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.servercore.hub.dto.HubReqDTO;
import com.xiilab.servercore.hub.dto.response.FindHubInWorkloadResDTO;
import com.xiilab.servercore.hub.dto.response.FindHubResDTO;

public interface HubService {
	// 허브 목록
	FindHubResDTO.Hubs getHubList(String[] categoryNames, Pageable pageable);

	// 허브 상세 보기
	FindHubResDTO.HubDetail getHubByHubId(Long hubId);

	// 허브 목록
	FindHubInWorkloadResDTO.Hubs getHubListInWorkload(WorkloadType workloadType);

	// 허브 저장
	void saveHub(HubReqDTO.SaveHub saveHubDTO);
}
