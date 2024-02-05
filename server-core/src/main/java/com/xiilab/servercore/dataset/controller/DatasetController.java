package com.xiilab.servercore.dataset.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DirectoryDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.dto.NginxFilesDTO;
import com.xiilab.servercore.dataset.service.DatasetFacadeService;
import com.xiilab.servercore.dataset.service.DatasetService;

import io.swagger.v3.oas.annotations.Operation;
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
	@Operation(summary = "아스트라고 데이터 셋 생성")
	public ResponseEntity insertAstragoDataset(
		@RequestPart(name = "createDataset") DatasetDTO.CreateAstragoDataset createDatasetDTO,
		@RequestPart(name = "files") List<MultipartFile> files){

		datasetFacadeService.insertAstragoDataset(createDatasetDTO, files);

		return new ResponseEntity(HttpStatus.OK);
	}
	@PostMapping("/datasets/local")
	@Operation(summary = "로컬 데이터 셋 생성")
	public ResponseEntity insertLocalDataset(
		@RequestBody DatasetDTO.CreateLocalDataset createDatasetDTO){

		datasetFacadeService.insertLocalDataset(createDatasetDTO);

		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/datasets")
	@Operation(summary = "데이터 셋 전체 조회")
	public ResponseEntity getDatasets(@RequestParam(required = false, defaultValue = "0", value = "page") int pageNo,
		@RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize,
		UserInfoDTO userInfoDTO){
		DatasetDTO.ResDatasets datasets = datasetService.getDatasets(pageNo, pageSize, userInfoDTO);
		return new ResponseEntity(datasets, HttpStatus.OK);
	}

	@GetMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 단건 조회")
	public ResponseEntity getDataset(@PathVariable(name = "datasetId") Long datasetId){
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetFacadeService.getDataset(datasetId);
		return new ResponseEntity(datasetWithStorage, HttpStatus.OK);
	}

	@PutMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 수정")
	public ResponseEntity modifyDataset(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ModifyDatset modifyDataset,
		UserInfoDTO userInfoDTO){
		datasetFacadeService.modifyDataset(modifyDataset, datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 삭제")
	public ResponseEntity deleteDataset(@PathVariable(name = "datasetId") Long datasetId, UserInfoDTO userInfoDTO){
		datasetFacadeService.deleteDataset(datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/list")
	@Operation(summary = "astrago 데이터 셋 파일 조회")
	public ResponseEntity getAstragoDatasetFiles(@RequestBody DatasetDTO.ReqFilePathDTO reqFilePathDTO){
		DirectoryDTO datasetFiles = datasetService.getDatasetFiles(reqFilePathDTO);
		return new ResponseEntity(datasetFiles, HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/files/upload")
	@Operation(summary = "astrago 데이터 셋 파일 업로드")
	public ResponseEntity astragoDatasetUploadFile(
		@RequestPart(name = "path") String path,
		@RequestPart(name = "files") List<MultipartFile> files){
		datasetService.astragoDatasetUploadFile(path, files);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/files/delete")
	@Operation(summary = "astrago 데이터 셋 파일, 디렉토리 삭제")
	public ResponseEntity astragoDatasetDeleteFiles(@RequestBody DatasetDTO.ReqFilePathDTO reqFilePathDTO){
		datasetService.astragoDatasetDeleteFiles(reqFilePathDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/files/download")
	@Operation(summary = "astrago 데이터 셋 파일 다운로드")
	public ResponseEntity astragoDatasetDownloadFile(@RequestBody DatasetDTO.ReqFilePathDTO reqFilePathDTO){
		DownloadFileResDTO downloadFileResDTO = datasetService.astragoDatasetDownloadFile(reqFilePathDTO);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(downloadFileResDTO.getMediaType());
		return new ResponseEntity(downloadFileResDTO.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/datasets/local/{datasetId}/list")
	@Operation(summary = "local 데이터 셋 파일, 디렉토리 리스트 조회")
	public ResponseEntity getLocalDatasetFiles(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ReqFilePathDTO reqFilePathDTO){
		DirectoryDTO files = datasetFacadeService.getLocalDatasetFiles(datasetId,
			reqFilePathDTO);
		return new ResponseEntity(files, HttpStatus.OK);
	}
}