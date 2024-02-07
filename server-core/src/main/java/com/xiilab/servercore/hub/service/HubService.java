package com.xiilab.servercore.hub.service;

import org.springframework.data.domain.Pageable;

import com.xiilab.servercore.hub.dto.HubDTO;

public interface HubService {
	// 허브 목록
	HubDTO.Response.HubsDto getHubList(String[] categoryNames, Pageable pageable);

	// 허브 상세 보기
	HubDTO.Response.HubDetailDto getHubByHubId(Long hubId);
}
