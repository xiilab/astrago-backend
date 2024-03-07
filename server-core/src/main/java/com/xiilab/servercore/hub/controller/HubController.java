package com.xiilab.servercore.hub.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.hub.dto.HubReqDTO;
import com.xiilab.servercore.hub.dto.HubResDTO;
import com.xiilab.servercore.hub.service.HubService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/hubs")
public class HubController {

	private final HubService hubService;

	@GetMapping()
	public ResponseEntity<HubResDTO.FindHubs> getHubList(@RequestParam(required = false) String[] categoryNames,
		Pageable pageable) {
		return new ResponseEntity<>(hubService.getHubList(categoryNames, pageable), HttpStatus.OK);
	}

	@GetMapping("/{hubId}")
	public ResponseEntity<HubResDTO.FindHub> getHubResDtoByHubId(@PathVariable("hubId") Long hubId) {
		return new ResponseEntity<>(hubService.getHubByHubId(hubId), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<Void> saveHub(@RequestBody HubReqDTO.SaveHub saveHubDTO) {
		hubService.saveHub(saveHubDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
