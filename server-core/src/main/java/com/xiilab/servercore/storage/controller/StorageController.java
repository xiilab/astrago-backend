package com.xiilab.servercore.storage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.storage.dto.CreateStorageReqDTO;
import com.xiilab.servercore.storage.service.StorageService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "StorageController", description = "스토리지 테스트용 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core/storages")
public class StorageController {
	private final StorageService storageService;

	@PostMapping("/")
	public ResponseEntity<Void> insertStorage(@RequestBody CreateStorageReqDTO createStorageReqDTO){
		storageService.insertStorage(createStorageReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}


}
