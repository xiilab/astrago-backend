package com.xiilab.servercore.hub.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.xiilab.servercore.hub.dto.HubResDTO;

public interface HubService {
	// 허브 목록
	Page<HubResDTO> getHubList(String[] categoryNames, Pageable pageable);

	// 허브 상세 보기
	HubResDTO getHubByHubId(Long hubId);
}
