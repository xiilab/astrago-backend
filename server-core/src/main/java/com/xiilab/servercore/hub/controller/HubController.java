package com.xiilab.servercore.hub.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.hub.dto.HubResDTO;
import com.xiilab.servercore.hub.service.HubServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/hubs")
public class HubController {

	private final HubServiceImpl hubServiceImpl;

	@GetMapping()
	public ResponseEntity<Page<HubResDTO>> getHubResDto(String[] categoryNames, Pageable pageable) {
		return new ResponseEntity<>(hubServiceImpl.getHubList(categoryNames, pageable), HttpStatus.OK);
	}

	@GetMapping("/{hubId}")
	public ResponseEntity<HubResDTO> getHubResDtoByHubId(@PathVariable("hubId") Long hubId) {
		return new ResponseEntity<>(hubServiceImpl.getHubByHubId(hubId), HttpStatus.OK);
	}
}
