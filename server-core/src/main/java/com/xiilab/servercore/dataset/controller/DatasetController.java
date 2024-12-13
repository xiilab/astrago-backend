package com.xiilab.servercore.dataset.controller;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.PageMode;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.dataset.service.DatasetFacadeService;
import com.xiilab.servercore.dataset.service.DatasetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
	public ResponseEntity<HttpStatus> insertAstragoDataset(
		@RequestPart(name = "createDataset") DatasetDTO.CreateAstragoDataset createDatasetDTO,
		@RequestPart(name = "files", required = false) List<MultipartFile> files) {
		datasetFacadeService.insertAstragoDataset(createDatasetDTO, files);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/local")
	@Operation(summary = "로컬 데이터 셋 생성")
	public ResponseEntity<HttpStatus> insertLocalDataset(
		@RequestBody DatasetDTO.CreateLocalDataset createDatasetDTO) {
		datasetFacadeService.insertLocalDataset(createDatasetDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/datasets")
	@Operation(summary = "데이터 셋 전체 조회")
	public ResponseEntity<DatasetDTO.ResDatasets> getDatasets(
		PageInfo pageInfo,
		RepositorySearchCondition repositorySearchCondition,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		@RequestParam(value = "pageMode") PageMode pageMode) {
		DatasetDTO.ResDatasets datasets = datasetService.getDatasets(pageInfo, repositorySearchCondition, userInfoDTO,
			pageMode);
		return new ResponseEntity(datasets, HttpStatus.OK);
	}

	@GetMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 상세 조회")
	public ResponseEntity<DatasetDTO.ResDatasetWithStorage> getDataset(
		@PathVariable(name = "datasetId") Long datasetId) {
		DatasetDTO.ResDatasetWithStorage datasetWithStorage = datasetFacadeService.getDataset(datasetId);
		return new ResponseEntity(datasetWithStorage, HttpStatus.OK);
	}

	@GetMapping("/datasets/{datasetId}/workloads")
	@Operation(summary = "데이터 셋을 사용중인 워크로드 리스트 조회")
	public ResponseEntity<WorkloadResDTO.PageUsingDatasetDTO> getWorkloadsUsingDataset(
		PageInfo pageInfo,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO,
		@PathVariable(name = "datasetId") Long datasetId
	) {
		WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset = datasetFacadeService.getWorkloadsUsingDataset(
			pageInfo, datasetId, userInfoDTO);
		return new ResponseEntity(workloadsUsingDataset, HttpStatus.OK);
	}

	@PutMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 수정")
	public ResponseEntity<HttpStatus> modifyDataset(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ModifyDatset modifyDataset,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		datasetFacadeService.modifyDataset(modifyDataset, datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("/datasets/{datasetId}")
	@Operation(summary = "데이터 셋 삭제")
	public ResponseEntity<HttpStatus> deleteDataset(@PathVariable(name = "datasetId") Long datasetId,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		datasetFacadeService.deleteDataset(datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/datasets/astrago/{datasetId}/files")
	@Operation(summary = "astrago 데이터 셋 파일리스트 조회")
	public ResponseEntity<DirectoryDTO> getAstragoDatasetFiles(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DirectoryDTO datasetFiles = datasetService.getAstragoDatasetFiles(datasetId, filePath);
		return new ResponseEntity(datasetFiles, HttpStatus.OK);
	}

	@GetMapping("/datasets/astrago/{datasetId}/file")
	@Operation(summary = "astrago 데이터 셋 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getAstragoDatasetFileInfo(
		@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		FileInfoDTO fileInfo = datasetFacadeService.getAstragoDatasetFileInfo(datasetId, filePath);
		return new ResponseEntity(fileInfo, HttpStatus.OK);
	}

	@GetMapping("/datasets/astrago/{datasetId}/preview")
	@Operation(summary = "astrago 데이터 셋 파일 미리 보기")
	public ResponseEntity<Resource> getAstragoDatasetFile(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = datasetFacadeService.getAstragoDatasetFile(datasetId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/files/upload")
	@Operation(summary = "astrago 데이터 셋 파일 업로드")
	public ResponseEntity<HttpStatus> astragoDatasetUploadFile(
		@PathVariable(name = "datasetId") Long datasetId,
		@RequestPart(name = "path") String path,
		@RequestPart(name = "files") List<MultipartFile> files) {
		datasetService.astragoDatasetUploadFile(datasetId, path, files);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/directory")
	@Operation(summary = "astrago 데이터 셋 폴더 생성")
	public ResponseEntity<HttpStatus> astragoDatasetCreateDirectory(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ReqFilePathDTO reqFilePathDTO) {
		datasetService.astragoDatasetCreateDirectory(datasetId, reqFilePathDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/files/delete")
	@Operation(summary = "astrago 데이터 셋 파일, 디렉토리 삭제")
	public ResponseEntity<HttpStatus> astragoDatasetDeleteFiles(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ReqFilePathsDTO reqFilePathsDTO) {
		datasetService.astragoDatasetDeleteFiles(datasetId, reqFilePathsDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/datasets/astrago/{datasetId}/files/download")
	@Operation(summary = "astrago 데이터 셋 파일, 디렉토리 다운로드")
	public ResponseEntity<Resource> downloadAstragoDatasetFile(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO downloadFileResDTO = datasetService.downloadAstragoDatasetFile(datasetId, filePath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(downloadFileResDTO.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + downloadFileResDTO.getFileName());
		return new ResponseEntity<>(downloadFileResDTO.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/datasets/astrago/{datasetId}/compress")
	@Operation(summary = "astrago 데이터 셋 압축")
	public ResponseEntity<HttpStatus> compressAstragoDatasetFiles(@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody DatasetDTO.ReqCompressDTO reqCompressDTO) {

		datasetService.compressAstragoDatasetFiles(datasetId, reqCompressDTO);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/datasets/astrago/{datasetId}/decompress")
	@Operation(summary = "astrago 데이터 셋 압축해제")
	public ResponseEntity<HttpStatus> deCompressAstragoDatasetFile(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {

		datasetService.deCompressAstragoDatasetFile(datasetId, filePath);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/datasets/local/{datasetId}/files")
	@Operation(summary = "local 데이터 셋 파일, 디렉토리 리스트 조회")
	public ResponseEntity<DirectoryDTO> getLocalDatasetFiles(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DirectoryDTO files = datasetFacadeService.getLocalDatasetFiles(datasetId,
			filePath);
		return new ResponseEntity<>(files, HttpStatus.OK);
	}

	@GetMapping("/datasets/local/{datasetId}/files/download")
	@Operation(summary = "local 데이터 셋 파일 다운로드")
	public ResponseEntity<Resource> DownloadLocalDatasetFile(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = datasetFacadeService.DownloadLocalDatasetFile(datasetId,
			filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + file.getFileName());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@GetMapping("/datasets/local/{datasetId}/file")
	@Operation(summary = "local 데이터 셋 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getLocalDatasetFileInfo(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		return new ResponseEntity<>(datasetFacadeService.getLocalDatasetFileInfo(datasetId, filePath), HttpStatus.OK);
	}

	@GetMapping("/datasets/local/{datasetId}/preview")
	@Operation(summary = "local 데이터 셋 파일 미리 보기")
	public ResponseEntity<Resource> getLocalDatasetFile(@PathVariable(name = "datasetId") Long datasetId,
		@RequestParam(value = "filePath") String filePath) {
		DownloadFileResDTO file = datasetFacadeService.getLocalDatasetFile(datasetId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}
}
