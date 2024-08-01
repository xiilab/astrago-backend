package com.xiilab.servercore.modelrepo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

	@PatchMapping()
	@Operation(summary = "워크로드 모델 등록 및 기존 모델 등록 API")
	public ResponseEntity<HttpStatus> registerOrVersionUpModelRepo(
		@RequestPart(name = "files", required = false) List<MultipartFile> files,
		@RequestBody ModelRepoDTO.wlModelRepoDTO modelRepoReqDTO) {
		modelRepoFacadeService.registerOrVersionUpModelRepo(files, modelRepoReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/version/{versionId}")
	@Operation(summary = "해당 ID의 모델의 버전을 삭제하는 API")
	public ResponseEntity<HttpStatus> deleteModelRepoVersion(
		@PathVariable(name = "versionId") Long versionId
	) {
		modelRepoFacadeService.deleteModelRepoVersion(versionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
