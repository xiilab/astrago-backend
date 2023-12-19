package com.xiilab.servercore.storageclass.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.storageclass.dto.CreateStorageClassReqDTO;
import com.xiilab.servercore.storageclass.service.StorageClassFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StorageClassController {
	private final StorageClassFacadeService storageClassFacadeService;

	@PostMapping("/storageClasses")
	@Operation(summary = "create StorageClass")
	public ResponseEntity<Object> createStorageClass(@RequestBody CreateStorageClassReqDTO createStorageClassReqDTO,
		UserInfoDTO userInfoDTO){
		storageClassFacadeService.createStorageClass(createStorageClassReqDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
