package com.xiilab.servercore.model.controller;

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
import com.xiilab.modulecommon.enums.CompressFileType;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.PageInfo;
import com.xiilab.modulek8sdb.common.enums.RepositorySearchCondition;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DownloadFileResDTO;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.servercore.model.service.ModelFacadeService;
import com.xiilab.servercore.model.service.ModelService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ModelController", description = "모델 테스트 용 api")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/core")
public class ModelController {
	private final ModelService modelService;
	private final ModelFacadeService modelFacadeService;

	@PostMapping("/models/astrago")
	@Operation(summary = "아스트라고 model 생성")
	public ResponseEntity<HttpStatus> insertAstragoModel(
		@RequestPart(name = "createModel") ModelDTO.CreateAstragoModel createModelDTO,
		@RequestPart(name = "files", required = false) List<MultipartFile> files) {
		modelFacadeService.insertAstragoModel(createModelDTO, files);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/models/local")
	@Operation(summary = "로컬 model 생성")
	public ResponseEntity<HttpStatus> insertLocalModel(@RequestBody ModelDTO.CreateLocalModel createLocalModel) {
		modelFacadeService.insertLocalModel(createLocalModel);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/models")
	@Operation(summary = "model 전체 조회")
	public ResponseEntity<ModelDTO.ResModels> getModels(PageInfo pageInfo,
		RepositorySearchCondition repositorySearchCondition,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO){
		ModelDTO.ResModels models = modelService.getModels(pageInfo, repositorySearchCondition, userInfoDTO);

		return new ResponseEntity<>(models, HttpStatus.OK);
	}

	@GetMapping("/models/{modelId}")
	@Operation(summary = "model 단건 조회")
	public ResponseEntity<ModelDTO.ResModelWithStorage> getModel(@PathVariable(name = "modelId") Long modelId){
		ModelDTO.ResModelWithStorage model = modelFacadeService.getModel(modelId);
		return new ResponseEntity<>(model, HttpStatus.OK);
	}
	@GetMapping("/models/{modelId}/workloads")
	@Operation(summary = "모델을 사용중인 워크로드 리스트 조회")
	public ResponseEntity<WorkloadResDTO.PageUsingModelDTO> getWorkloadsUsingModel(
		PageInfo pageInfo,
		@PathVariable(name = "modelId") Long modelId
	) {
		WorkloadResDTO.PageUsingModelDTO workloadsUsingModel = modelFacadeService.getWorkloadsUsingModel(
			pageInfo, modelId);
		return new ResponseEntity(workloadsUsingModel, HttpStatus.OK);
	}
	@PutMapping("/models/{modelId}")
	@Operation(summary = "model 수정")
	public ResponseEntity<HttpStatus> modifyModel(@PathVariable(name = "modelId") Long modelId,
		@RequestBody ModelDTO.ModifyModel modifyModel,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO){
		modelFacadeService.modifyModel(modifyModel, modelId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/models/{modelId}")
	@Operation(summary = "model 삭제")
	public ResponseEntity<HttpStatus> deleteModel(@PathVariable(name = "modelId") Long modelId,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO){
		modelFacadeService.deleteModel(modelId, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/files")
	@Operation(summary = "astrago model 파일리스트 조회")
	public ResponseEntity<DirectoryDTO> getAstragoModelFiles(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DirectoryDTO files = modelService.getAstragoModelFiles(modelId, filePath);
		return new ResponseEntity<>(files, HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/file")
	@Operation(summary = "astrago model 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getAstragoModelFileInfo(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		FileInfoDTO fileInfo = modelFacadeService.getAstragoModelFileInfo(modelId, filePath);
		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/preview")
	@Operation(summary = "astrago model 파일 미리 보기")
	public ResponseEntity<Resource> getAstragoModelFile(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DownloadFileResDTO file = modelFacadeService.getAstragoModelFile(modelId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@PostMapping("/models/astrago/{modelId}/files/upload")
	@Operation(summary = "astrago model 파일 업로드")
	public ResponseEntity<HttpStatus> astragoModelUploadFile(
		@PathVariable(name = "modelId") Long modelId,
		@RequestPart(name = "path") String path,
		@RequestPart(name = "files") List<MultipartFile> files){
		modelService.astragoModelUploadFile(modelId, path, files);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@PostMapping("/models/astrago/{modelId}/directory")
	@Operation(summary = "astrago model 폴더 생성")
	public ResponseEntity<HttpStatus> astragoModelCreateDirectory(@PathVariable(name = "modelId") Long modelId,
		@RequestBody ModelDTO.ReqFilePathDTO reqFilePathDTO){
		modelService.astragoModelCreateDirectory(modelId, reqFilePathDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	@PostMapping("/models/astrago/{modelId}/files/delete")
	@Operation(summary = "astrago model 파일, 디렉토리 삭제")
	public ResponseEntity<HttpStatus> astragoModelDeleteFiles(@PathVariable(name = "modelId") Long modelId,
		@RequestBody ModelDTO.ReqFilePathsDTO reqFilePathsDTO){
		modelService.astragoModelDeleteFiles(modelId, reqFilePathsDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/files/download")
	@Operation(summary = "astrago model 파일, 디렉토리 다운로드")
	public ResponseEntity<Resource> downloadAstragoModelFile(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DownloadFileResDTO downloadFileResDTO = modelService.downloadAstragoModelFile(modelId, filePath);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(downloadFileResDTO.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + downloadFileResDTO.getFileName());
		return new ResponseEntity<>(downloadFileResDTO.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/compress")
	@Operation(summary = "astrago 데이터 셋 압축")
	public ResponseEntity<HttpStatus> compressAstragoDatasetFiles(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") List<String> filePaths,
		@RequestParam(value = "compressFileType") CompressFileType compressFileType) {

		modelService.compressAstragoModelFiles(modelId, filePaths, compressFileType);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/models/astrago/{modelId}/decompress")
	@Operation(summary = "astrago 데이터 셋 압축해제")
	public ResponseEntity<HttpStatus> compressAstragoDatasetFiles(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath) {

		modelService.deCompressAstragoModelFile(modelId, filePath);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/models/local/{modelId}/files")
	@Operation(summary = "local model 파일, 디렉토리 리스트 조회")
	public ResponseEntity<DirectoryDTO> getLocalModelFiles(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DirectoryDTO files = modelFacadeService.getLocalModelFiles(modelId,
			filePath);
		return new ResponseEntity<>(files, HttpStatus.OK);
	}

	@GetMapping("/models/local/{modelId}/files/download")
	@Operation(summary = "local model 파일 다운로드")
	public ResponseEntity<Resource> DownloadLocalModelFile(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DownloadFileResDTO file = modelFacadeService.DownloadLocalModelFile(modelId,
			filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		headers.add("Content-Disposition", "attachment; filename=" + file.getFileName());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}

	@GetMapping("/models/local/{modelId}/file")
	@Operation(summary = "local model 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getLocalModelFileInfo(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		FileInfoDTO fileInfo = modelFacadeService.getLocalModelFileInfo(modelId,
			filePath);
		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}
	@GetMapping("/models/local/{modelId}/preview")
	@Operation(summary = "local model 파일 미리 보기")
	public ResponseEntity<Resource> getLocalModelFile(@PathVariable(name = "modelId") Long modelId,
		@RequestParam(value = "filePath") String filePath){
		DownloadFileResDTO file = modelFacadeService.getLocalModelFile(modelId, filePath);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(file.getMediaType());
		return new ResponseEntity<>(file.getByteArrayResource(), headers, HttpStatus.OK);
	}
}
