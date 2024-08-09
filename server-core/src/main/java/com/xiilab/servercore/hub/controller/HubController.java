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

import com.xiilab.servercore.hub.dto.request.HubReqDTO;
import com.xiilab.servercore.hub.dto.response.FindHubResDTO;
import com.xiilab.servercore.hub.service.HubService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/hubs")
public class HubController {

	private final HubService hubService;

	@GetMapping()
	@Operation(summary = "Hub 목록 조회")
	public ResponseEntity<FindHubResDTO.Hubs> getHubList(
		@RequestParam(value = "searchText", required = false) String searchText,
		@RequestParam(value = "categoryNames", required = false) String[] categoryNames,
		Pageable pageable) {
		return new ResponseEntity<>(hubService.getHubList(searchText, categoryNames, pageable), HttpStatus.OK);
	}

	@GetMapping("/{hubId}")
	@Operation(summary = "Hub 상세 조회")
	public ResponseEntity<FindHubResDTO.HubDetail> getHubResDtoByHubId(@PathVariable("hubId") Long hubId) {
		return new ResponseEntity<>(hubService.getHubByHubId(hubId), HttpStatus.OK);
	}

	@PostMapping()
	@Operation(summary = "Hub 저장")
	public ResponseEntity<Void> saveHub(@RequestBody HubReqDTO.SaveHub saveHubDTO) {
		hubService.saveHub(saveHubDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@GetMapping("/{hubId}/readme")
	@Operation(summary = "Hub readme 조회")
	public ResponseEntity<String> getHubReadMe(@PathVariable("hubId") Long hubId){
		return new ResponseEntity<>(hubService.getHubReadMe(hubId), HttpStatus.OK);
	}

}
