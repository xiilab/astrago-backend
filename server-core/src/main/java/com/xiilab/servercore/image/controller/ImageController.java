package com.xiilab.servercore.image.controller;

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

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.image.dto.ImageDTO;
import com.xiilab.servercore.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/image")
public class ImageController {
	private final ImageService imageService;

	@PostMapping()
	public ResponseEntity<HttpStatus> saveImage(@RequestBody ImageDTO.ReqDTO imageDTO,
		UserInfoDTO userInfoDTO) {
		imageService.saveImage(imageDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ImageDTO.ResDTO> getImageById(@PathVariable("id") long id) {
		return new ResponseEntity<>(imageService.getImageById(id), HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<List<ImageDTO.ResDTO>> getImageList() {
		return new ResponseEntity<>(imageService.getImageList(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteImageById(@PathVariable("id") long id) {
		imageService.deleteImageById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}


