package com.xiilab.servercore.dataset.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.common.enums.DatasetDivision;
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

	@PostMapping("/datasets/astrago")
	public ResponseEntity insertAstragoDataset(
		@RequestPart(name = "createDataset") DatasetDTO.CreateAstragoDataset createDatasetDTO,
		@RequestPart(name = "files") List<MultipartFile> files){

		datasetFacadeService.insertAstragoDataset(createDatasetDTO, files);

		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/datasets/local")
	public ResponseEntity insertLocalDataset(
		@RequestBody DatasetDTO.CreateLocalDataset createDatasetDTO){

		datasetFacadeService.insertLocalDataset(createDatasetDTO);

		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/datasets")
	public ResponseEntity getDatasets(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
		@RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize,
		UserInfoDTO userInfoDTO){
		DatasetDTO.ResDatasets datasets = datasetService.getDatasets(pageNo, pageSize, userInfoDTO);
		return new ResponseEntity(datasets, HttpStatus.OK);
	}

	@GetMapping("/datasets/{datasetId}")
	public ResponseEntity getDataset(@PathVariable(name = "datasetId") Long datasetId){
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetFacadeService.getDataset(datasetId);
		return new ResponseEntity(datasetWithStorage, HttpStatus.OK);
	}

	@PutMapping("/datasets/{datasetId}")
	public ResponseEntity modifyDataset(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ModifyDatset modifyDataset,
		UserInfoDTO userInfoDTO){
		datasetFacadeService.modifyDataset(modifyDataset, datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}
}