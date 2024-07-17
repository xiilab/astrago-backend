package com.xiilab.servercore.tus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.tus.service.TusService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/tus")
public class TusController {
	private final TusService tusService;

	@RequestMapping("/**")
	public ResponseEntity<HttpStatus> uploadData(HttpServletRequest request,
		HttpServletResponse response) {
		tusService.tusUpload(request, response);
		return new ResponseEntity<>(HttpStatus.OK);

	}
}
