package com.xiilab.servercore.registry.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.registry.dto.CommitImageReqDTO;
import com.xiilab.servercore.registry.dto.RegistryImageDTO;
import com.xiilab.servercore.registry.dto.RegistryTagDTO;
import com.xiilab.servercore.registry.service.RegistryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/registry")
@RequiredArgsConstructor
public class RegistryController {
	private final RegistryService registryService;

	@PostMapping()
	public ResponseEntity<HttpStatus> commitImage(@RequestBody CommitImageReqDTO imageReqDTO,
		UserDTO.UserInfo userInfo) {
		registryService.commitImage(imageReqDTO, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/image/list")
	public ResponseEntity<List<RegistryImageDTO>> getImageList(
		@RequestParam(value = "searchCondition", defaultValue = "", required = false) String searchCondition,
		@RequestParam(value = "page") int page,
		@RequestParam(value = "pageSize") int pageSize,
		UserDTO.UserInfo userInfo
	) {
		return new ResponseEntity<>(registryService.getImageList(searchCondition, page, pageSize, userInfo),
			HttpStatus.OK);
	}

	@GetMapping("/image/{imageName}/info")
	public ResponseEntity<RegistryImageDTO> getImageInfo(
		@PathVariable(value = "imageName") String imageName,
		UserDTO.UserInfo userInfo
	) {
		return new ResponseEntity<>(registryService.getImageInfo(imageName, userInfo), HttpStatus.OK);
	}

	@GetMapping("/image/{imageName}/tag")
	public ResponseEntity<List<RegistryTagDTO>> getImageTags(
		@PathVariable(value = "imageName") String imageName,
		UserDTO.UserInfo userInfo) {
		return new ResponseEntity<>(registryService.getImageTagList(imageName, userInfo), HttpStatus.OK);
	}

	@DeleteMapping("/image")
	public ResponseEntity<HttpStatus> deleteImage(
		@RequestParam(value = "imageName") String imageName,
		UserDTO.UserInfo userInfo
	) {
		registryService.deleteImage(imageName, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/image/{imageName}/tag/{tagName}")
	public ResponseEntity<HttpStatus> deleteImageTag(
		@PathVariable(value = "imageName") String imageName,
		@PathVariable(value = "tagName") String tagName,
		UserDTO.UserInfo userInfo
	) {
		registryService.deleteImageTag(imageName, tagName, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
