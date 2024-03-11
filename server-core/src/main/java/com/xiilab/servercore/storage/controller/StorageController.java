package com.xiilab.servercore.storage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.storage.dto.StorageDTO;
import com.xiilab.servercore.storage.service.StorageFacadeService;
import com.xiilab.servercore.storage.service.StorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "StorageController", description = "스토리지 테스트용 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core/storages")
public class StorageController {
	private final StorageService storageService;
	private final StorageFacadeService storageFacadeService;

	@PostMapping()
	@Operation(summary = "스토리지 생성")
	public ResponseEntity insertStorage(@RequestBody StorageDTO storageDTO){
		storageFacadeService.insertStorage(storageDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("")
	@Operation(summary = "스토리지 목록 조회")
	public ResponseEntity<StorageDTO.ResStorages> getStorages(@RequestParam(required = false, defaultValue = "1", value = "page") int pageNo,
		@RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize){
		StorageDTO.ResStorages storages = storageService.getStorages(pageNo, pageSize);
		return new ResponseEntity<>(storages, HttpStatus.OK);
	}

	@GetMapping("/{storageId}")
	@Operation(summary = "스토리지 상세 조회")
	public ResponseEntity<StorageDTO.ResStorage> getStorages(@PathVariable(name = "storageId") Long storageId){
		StorageDTO.ResStorage storage = storageService.getStorage(storageId);
		return new ResponseEntity<>(storage, HttpStatus.OK);
	}
	@PatchMapping("/{storageId}")
	@Operation(summary = "스토리지 수정")
	public ResponseEntity<HttpStatus> modifyStorage(@PathVariable(name = "storageId") Long storageId,
		@RequestBody StorageDTO.ModifyStorage modifyStorage){
		storageFacadeService.modifyStorage(storageId, modifyStorage);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{storageId}")
	@Operation(summary = "스토리지 삭제")
	public ResponseEntity deleteStorage(@PathVariable(name = "storageId") Long storageId){
		storageFacadeService.deleteStorage(storageId);
		return new ResponseEntity(HttpStatus.OK);
	}
}
