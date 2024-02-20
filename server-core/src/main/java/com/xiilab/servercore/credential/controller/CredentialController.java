package com.xiilab.servercore.credential.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.modulek8sdb.credential.dto.CredentialReqDTO;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.servercore.credential.service.CredentialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/credential")
@RequiredArgsConstructor
public class CredentialController {
	private final CredentialService credentialService;

	@GetMapping()
	public ResponseEntity<Page<CredentialResDTO>> getCredentialList(Pageable pageable, UserInfoDTO userInfoDTO) {
		return new ResponseEntity<>(credentialService.getCredentialList(pageable, userInfoDTO), HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CredentialResDTO.CredentialInfo> findCredentialInfoById(@PathVariable(name = "id") long id,
		UserInfoDTO userInfoDTO) {
		return new ResponseEntity<>(credentialService.findCredentialById(id, userInfoDTO), HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<CredentialResDTO> createCredential(@RequestBody CredentialReqDTO credentialReqDTO,
		UserInfoDTO userInfoDTO) {
		return new ResponseEntity<>(credentialService.createCredential(credentialReqDTO, userInfoDTO), HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<HttpStatus> updateCredentialInfo(@PathVariable("id") long id,
		@RequestBody CredentialReqDTO.UpdateDTO updateDTO,
		UserInfoDTO userInfoDTO) {
		credentialService.updateCredentialById(id, updateDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteCredentialById(@PathVariable(name = "id") long id,
		UserInfoDTO userInfoDTO) {
		credentialService.deleteCredentialById(id, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
