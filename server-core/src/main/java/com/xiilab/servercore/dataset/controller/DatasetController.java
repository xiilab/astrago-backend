package com.xiilab.servercore.dataset.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.service.DatasetFacadeService;
import com.xiilab.servercore.dataset.service.DatasetService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "DatasetController", description = "데이터셋 테스트용 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core")
public class DatasetController {
	private final DatasetService datasetService;
	private final DatasetFacadeService datasetFacadeService;

	@PostMapping("/astrago/datasets")
	public ResponseEntity insertAstragoDataset(
		@RequestPart(name = "createDataset") DatasetDTO.CreateAstragoDataset createDatasetDTO,
		@RequestPart(name = "files") List<MultipartFile> files){

		datasetFacadeService.insertAstragoDataset(createDatasetDTO, files);

		return new ResponseEntity(HttpStatus.OK);
	}

}
