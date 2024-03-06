package com.xiilab.servercore.image.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.servercore.image.dto.ImageReqDTO;
import com.xiilab.servercore.image.dto.ImageResDTO;
import com.xiilab.servercore.image.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/images")
public class ImageController {
	private final ImageService imageService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// GETMapping 파라미터 setter없이 DTO로 바인딩
		binder.initDirectFieldAccess();
	}

	@PostMapping("/{imageType}")
	@Operation(summary = "이미지 저장 API")
	public ResponseEntity<HttpStatus> saveImage(@RequestBody ImageReqDTO.SaveImage saveImageDTO) {
		imageService.saveImage(saveImageDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/{imageId}")
	@Operation(summary = "이미지 조회 API")
	public ResponseEntity<ImageResDTO.FindImage> findImageById(@PathVariable(name = "imageId") Long id) {
		return new ResponseEntity<>(imageService.findImageById(id), HttpStatus.OK);
	}

	@GetMapping("")
	@Operation(summary = "이미지 목록 조회 API")
	public ResponseEntity<ImageResDTO.FindImages> findImages(ImageReqDTO.FindSearchCondition findSearchCondition) {
		return new ResponseEntity<>(imageService.findImages(findSearchCondition), HttpStatus.OK);
	}

	@DeleteMapping("/{imageId}")
	@Operation(summary = "이미지 삭제 API")
	public ResponseEntity<HttpStatus> deleteImageById(@PathVariable(name = "imageId") long id){
		imageService.deleteImageById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}


