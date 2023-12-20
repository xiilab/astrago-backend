package com.xiilab.servercore.storageclass.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
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

	/**
	 * 스토리지 클래스 생성
	 * @param createStorageClassReqDTO
	 * @param userInfoDTO
	 * @return
	 */
	@PostMapping("/storageClasses")
	@Operation(summary = "create StorageClass")
	public ResponseEntity<Object> createStorageClass(@RequestBody CreateStorageClassReqDTO createStorageClassReqDTO,
		UserInfoDTO userInfoDTO){
		storageClassFacadeService.createStorageClass(createStorageClassReqDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 스토리지 연결 테스트
	 * @param storageType
	 * @return
	 */
	@GetMapping("/storageClasses/connection-test")
	public ResponseEntity<Boolean> storageClassConnectionTest(@RequestParam("storageType") String storageType){
		boolean connectionCheck = storageClassFacadeService.storageClassConnectionTest(storageType);
		return new ResponseEntity<>(connectionCheck, HttpStatus.OK);
	}

	/**
	 * 스토리지 클래스 단건 조회
	 *
	 * @param storageClassMetaName
	 * @return
	 */
	@GetMapping("/storageClasses/{storageClassMetaName}")
	public ResponseEntity<StorageClassResDTO> findStorageClassByMetaName(@PathVariable("storageClassMetaName") String storageClassMetaName){
		StorageClassResDTO storageClass = storageClassFacadeService.findStorageClassByMetaName(
			storageClassMetaName);
		return new ResponseEntity<>(storageClass, HttpStatus.OK);
	}


}
