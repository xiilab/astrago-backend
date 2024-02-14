package com.xiilab.servercore.image.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.dto.response.BuiltInImageResDTO;
import com.xiilab.servercore.image.service.BuiltInImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core/builtInImages")
public class BuiltInImageController {
	private final BuiltInImageService builtInImageService;

	@GetMapping("")
	public ResponseEntity<List<BuiltInImageResDTO>> getBuiltInImageList(@RequestParam(name = "type", required = false) WorkloadType workloadType) {
		return ResponseEntity.ok(builtInImageService.getBuiltInImageList(workloadType));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BuiltInImageResDTO> getBuiltInImageById(@PathVariable("id") Long id) {
		return ResponseEntity.ok(builtInImageService.getBuiltInImageById(id));
	}
}
