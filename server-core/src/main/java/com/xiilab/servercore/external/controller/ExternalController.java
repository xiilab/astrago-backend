package com.xiilab.servercore.external.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.servercore.external.service.ExternalService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core/externals")
public class ExternalController {
	private final ExternalService externalService;

	@GetMapping("/github/branches")
	public ResponseEntity<List<String>> getGitHubRepoBranchList(@RequestParam(value="credentialId", required = false) Long credentialId,
		@RequestParam(value="repoName", required = false) String repoName,
		@RequestParam(value="codeType", required = false) CodeType codeType) {
		return ResponseEntity.ok(externalService.getGitHubRepoBranchList(credentialId, repoName, codeType));
	}
}
