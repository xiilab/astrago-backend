package com.xiilab.servercore.hub.service;

import org.springframework.data.domain.Pageable;

import com.xiilab.modulek8sdb.hub.dto.HubResDTO;

public interface HubService {
	// 허브 목록
	HubResDTO.FindHubs getHubList(String[] categoryNames, Pageable pageable);

	// 허브 상세 보기
	HubResDTO.FindHub getHubByHubId(Long hubId);
}
