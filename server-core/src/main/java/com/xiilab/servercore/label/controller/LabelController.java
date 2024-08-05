package com.xiilab.servercore.label.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.label.dto.LabelDTO;
import com.xiilab.servercore.label.service.LabelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "LabelController", description = "라벨 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/label")
@RestController()
public class LabelController {
	
	private final LabelService labelService;

	@PostMapping("/{workspaceResourceName}")
	@Operation(summary = "라벨 저장 API")
	public ResponseEntity<LabelDTO.ResponseDTO> saveLabel(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@RequestBody LabelDTO labelDTO) {
		return new ResponseEntity<>(labelService.addLabel(workspaceResourceName, labelDTO), HttpStatus.OK);
	}

	@GetMapping("/{workspaceResourceName}")
	@Operation(summary = "해당 워크스페이스에 등록된 라벨 리스트 조회 API")
	public ResponseEntity<List<LabelDTO.ResponseDTO>> getLabels(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName) {
		return new ResponseEntity<>(labelService.getLabels(workspaceResourceName), HttpStatus.OK);
	}

	@GetMapping("/{workspaceResourceName}/check")
	@Operation(summary = "해당 워크스페이스에 등록 가능한 라벨인지 확인하는 API")
	public ResponseEntity<Boolean> checkLabel(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@RequestParam(name = "labelName") String labelName) {
		return new ResponseEntity<>(labelService.checkLabel(workspaceResourceName, labelName), HttpStatus.OK);
	}

	@DeleteMapping("/{labelId}")
	@Operation(summary = "해당 ID의 라벨을 삭제하는 API")
	public ResponseEntity<HttpStatus> delete(
		@PathVariable(name = "labelId") Long labelId) {
		labelService.deleteLabelById(labelId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping()
	@Operation(summary = "라벨를 변경하는 API")
	public ResponseEntity<HttpStatus> modifyLabel(
		@RequestBody List<LabelDTO.UpdateDTO> updateLabelDTOs) {
		labelService.modifyLabels(updateLabelDTOs);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
