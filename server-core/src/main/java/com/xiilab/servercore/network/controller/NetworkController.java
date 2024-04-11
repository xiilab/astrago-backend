package com.xiilab.servercore.network.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.servercore.network.dto.ModifyNetworkDTO;
import com.xiilab.servercore.network.service.NetworkService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "NetworkController", description = "네트워크 테스트용 API")
@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class NetworkController {

	private final NetworkService networkService;

	@Operation(summary = "네트워크 폐쇄망 유무 조회")
	@GetMapping("/network/network-status")
	public ResponseEntity<NetworkCloseYN> getNetworkStatus(){
		NetworkCloseYN closeYN = networkService.getNetworkStatus();
		return new ResponseEntity<>(closeYN, HttpStatus.OK);
	}

	@Operation(summary = "네트워크 폐쇄망 유무 수정")
	@PatchMapping("/network/network-status")
	public ResponseEntity<HttpStatus> modifyNetworkStatus(@RequestBody ModifyNetworkDTO modifyNetworkDTO){
		networkService.modifyNetworkStatus(modifyNetworkDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
