package com.xiilab.servercore.code.controller;

import java.util.List;

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

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.code.dto.CodeReqDTO;
import com.xiilab.servercore.code.dto.CodeResDTO;
import com.xiilab.servercore.code.dto.ModifyCodeReqDTO;
import com.xiilab.servercore.code.service.CodeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/code")
@RequiredArgsConstructor
public class CodeController {
	private final CodeService codeService;

	@PostMapping("")
	@Operation(summary = "소스 코드 등록 API")
	public ResponseEntity<CodeResDTO> saveCode(@RequestBody CodeReqDTO codeReqDTO){
		return new ResponseEntity<>(codeService.saveCode(codeReqDTO), HttpStatus.OK);
	}

	@GetMapping("")
	@Operation(summary = "소스 코드 목록 API")
	public ResponseEntity<List<CodeResDTO>> getCodeList(
		@RequestParam(value = "workspacename", required = false) String workspaceName,
		UserInfoDTO userInfoDTO){
		return new ResponseEntity<>(codeService.getCodeList(workspaceName, userInfoDTO), HttpStatus.OK);
	}


	@PatchMapping("/{id}")
	@Operation(summary = "소스 정보 수정 API")
	public ResponseEntity<HttpStatus> modifyCode(@PathVariable(name = "id") Long codeId,
		@RequestBody ModifyCodeReqDTO modifyCodeReqDTO){
		codeService.modifyCode(codeId, modifyCodeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "소스 코드 상세 조회 API")
	public ResponseEntity<CodeResDTO> getCodeById(@PathVariable(name = "id") long id){
		return new ResponseEntity<>(codeService.getCodeById(id), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "소스 코드 삭제 API")
	public ResponseEntity<HttpStatus> deleteCodeById(@PathVariable(name = "id") long id){
		codeService.deleteCodeById(id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/check")
	@Operation(summary = "소스 코드URL 검증 API")
	public ResponseEntity<HttpStatus> isCodeValid(@RequestParam(value = "codeURL") String codeURL, @RequestParam(value = "credentialId") Long credentialId){
		codeService.isCodeURLValid(codeURL, credentialId);
		return ResponseEntity.ok().build();
	}

}
