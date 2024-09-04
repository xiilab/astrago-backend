package com.xiilab.servercore.tus.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
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
	public ResponseEntity<String> uploadData(HttpServletRequest request,
		HttpServletResponse response) {
		String result = tusService.tusUpload(request, response);
		return ResponseEntity.ok()
			.contentType(MediaType.APPLICATION_JSON)
			.body(result);
	}
}
