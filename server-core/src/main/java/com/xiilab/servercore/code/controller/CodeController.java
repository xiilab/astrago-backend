package com.xiilab.servercore.code.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8sdb.code.dto.CodeSearchCondition;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;
import com.xiilab.servercore.code.service.CodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/code")
@RequiredArgsConstructor
public class CodeController {
	private final CodeService codeService;

	@PostMapping("")
	@Operation(summary = "소스 코드 등록 API")
	public ResponseEntity<CodeResDTO> saveCode(@RequestBody CodeReqDTO codeReqDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		return new ResponseEntity<>(codeService.saveCode(codeReqDTO, userInfoDTO), HttpStatus.OK);
	}

	@GetMapping("")
	@Operation(summary = "소스 코드 목록 API")
	public ResponseEntity<Page<CodeResDTO>> getCodeList(
		@RequestParam(value = "workspacename", required = false) String workspaceName,
		CodeSearchCondition codeSearchCondition,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		Pageable pageable,
		@RequestParam(value = "pageMode") PageMode pageMode) {
		return new ResponseEntity<>(
			codeService.getCodeList(workspaceName, userInfoDTO, pageable, codeSearchCondition, pageMode),
			HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "소스 정보 수정 API")
	public ResponseEntity<HttpStatus> modifyCode(@PathVariable(name = "id") Long codeId,
		@RequestBody ModifyCodeReqDTO modifyCodeReqDTO) {
		codeService.modifyCode(codeId, modifyCodeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "소스 코드 상세 조회 API")
	public ResponseEntity<CodeResDTO> getCodeById(@PathVariable(name = "id") long id) {
		return new ResponseEntity<>(codeService.getCodeById(id), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "소스 코드 삭제 API")
	public ResponseEntity<HttpStatus> deleteCodeById(@PathVariable(name = "id") long id) {
		codeService.deleteCodeById(id);
		return ResponseEntity.ok().build();
	}

	// @GetMapping("/check")
	// @Operation(summary = "소스 코드URL 검증 API")
	// public ResponseEntity<HttpStatus> isCodeValid(@RequestParam(value = "codeURL") String codeURL,
	// 	@RequestParam(value = "credentialId") Long credentialId) {
	// 	codeService.isCodeURLValid(codeURL, credentialId);
	// 	return ResponseEntity.ok().build();
	// }
}
