package com.xiilab.servercore.modelrepo.controller;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.modelrepo.dto.ModelRepoDTO;
import com.xiilab.servercore.modelrepo.service.ModelRepoFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ModelRepoController", description = "모델 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/modelrepo")
@RestController()
public class ModelRepoController {
	private final ModelRepoFacadeService modelRepoFacadeService;

	@PostMapping()
	@Operation(summary = "신규 모델 등록하는 API")
	public ResponseEntity<ModelRepoDTO.ResponseDTO> createModelRepo(
		@RequestPart(name = "modelRepoReqDTO") ModelRepoDTO.RequestDTO modelRepoReqDTO) {
		return new ResponseEntity<>(modelRepoFacadeService.createModelRepo(modelRepoReqDTO), HttpStatus.OK);
	}

	@GetMapping("/{workspaceResourceName}")
	@Operation(summary = "해당 워크스페이스에 전체 모델 리스트 조회 API")
	public ResponseEntity<List<ModelRepoDTO.ResponseDTO>> getModelRepoList(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName) {
		return new ResponseEntity<>(modelRepoFacadeService.getModelRepoList(workspaceResourceName), HttpStatus.OK);
	}

	@GetMapping("/{workspaceResourceName}/{modelRepoId}")
	@Operation(summary = "해당 ID의 모델을 조회하는 API")
	public ResponseEntity<ModelRepoDTO.ResponseDTO> getModelRepoById(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "modelRepoId") Long modelRepoId) {
		return new ResponseEntity<>(modelRepoFacadeService.getModelRepoById(workspaceResourceName, modelRepoId),
			HttpStatus.OK);
	}

	@DeleteMapping("/{modelRepoId}")
	@Operation(summary = "해당 ID의 모델 삭제하는 API")
	public ResponseEntity<HttpStatus> deleteModelRepoById(
		@PathVariable(name = "modelRepoId") Long modelRepoId) {
		modelRepoFacadeService.deleteModelRepoById(modelRepoId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("/{modelRepoId}")
	@Operation(summary = "해당 ID의 모델 수정하는 API")
	public ResponseEntity<HttpStatus> modifyModelRepo(
		@PathVariable(name = "modelRepoId") Long modelRepoId,
		@RequestBody ModelRepoDTO.RequestDTO modelRepoReqDTO) {
		modelRepoFacadeService.modifyModelRepo(modelRepoId, modelRepoReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
