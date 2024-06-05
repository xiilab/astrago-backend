package com.xiilab.servercore.smtp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8sdb.smtp.dto.SmtpDTO;
import com.xiilab.servercore.smtp.service.SmtpFacadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/smtp")
public class SmtpController {
	private final SmtpFacadeService smtpFacadeService;

	@PostMapping()
	public ResponseEntity<HttpStatus> saveSmtp(@RequestBody SmtpDTO.RequestDTO requestDTO) {
		smtpFacadeService.saveSmtp(requestDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<List<SmtpDTO.ResponseDTO>> getSmtp() {
		return new ResponseEntity<>(smtpFacadeService.getSmtp(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteSmtpById(@PathVariable(name = "id") Long id) {
		smtpFacadeService.deleteSmtpById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
