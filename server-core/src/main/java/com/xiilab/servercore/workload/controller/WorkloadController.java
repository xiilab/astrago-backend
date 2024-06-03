package com.xiilab.servercore.workload.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xiilab.modulecommon.dto.DirectoryDTO;
import com.xiilab.modulecommon.dto.FileInfoDTO;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadEventDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.common.dto.FileUploadResultDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.hub.dto.response.FindHubInWorkloadResDTO;
import com.xiilab.servercore.hub.service.HubService;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.servercore.model.service.ModelService;
import com.xiilab.servercore.workload.dto.request.CreateDistributedWorkloadJobReqDTO;
import com.xiilab.servercore.workload.dto.request.CreateSingleWorkloadJobReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadEventReqDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadSummaryDTO;
import com.xiilab.servercore.workload.service.WorkloadFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core")
@RequiredArgsConstructor
public class WorkloadController {
	private final WorkloadFacadeService workloadFacadeService;
	private final DatasetService datasetService;
	private final ModelService modelService;
	private final HubService hubService;


	@PostMapping("/workloads")
	@Operation(summary = "워크로드 생성")
	public ResponseEntity<HttpStatus> createWorkload(
		@RequestBody CreateSingleWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserDTO.UserInfo userInfoDTO) {
		workloadFacadeService.createWorkload(createWorkloadJobReqDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/workloads/distributed")
	@Operation(summary = "분산 학습용 워크로드 생성")
	public ResponseEntity<HttpStatus> createWorkloadDistributed(
		@RequestBody CreateDistributedWorkloadJobReqDTO createWorkloadJobReqDTO,
		UserDTO.UserInfo userInfoDTO
	) {
		workloadFacadeService.createWorkload(createWorkloadJobReqDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/workloads/{workloadType}")
	@Operation(summary = "워크로드 수정")
	public ResponseEntity<HttpStatus> updateWorkload(
		@PathVariable("workloadType") WorkloadType workloadType,
		@RequestBody WorkloadUpdateDTO workloadUpdateDTO) {
		workloadFacadeService.editWorkload(workloadType, workloadUpdateDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/workloads/{type}")
	@Operation(summary = "워크로드 상세 조회")
	public ResponseEntity<FindWorkloadResDTO> getWorkloadInfo(
		@PathVariable("type") WorkloadType workloadType,
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("workloadResourceName") String workloadResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		return new ResponseEntity<>(
			workloadFacadeService.getWorkloadInfoByResourceName(workloadType, workspaceResourceName,
				workloadResourceName, userInfoDTO),
			HttpStatus.OK);
	}

	@GetMapping("/workloads/ports/{type}")
	@Operation(summary = "워크로드에 접근 할 수 있는 포트 조회")
	public ResponseEntity<List<FindWorkloadResDTO.Port>> getWorkloadsPorts(
		@PathVariable("type") WorkloadType workloadType,
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("workloadResourceName") String workloadResourceName
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getWorkloadPortInfo(workloadType, workspaceResourceName, workloadResourceName),
			HttpStatus.OK
		);
	}

	@PostMapping("/workloads/{type}/event")
	@Operation(summary = "워크로드 이벤트 리스트 조회")
	public ResponseEntity<PageDTO<WorkloadEventDTO>> getWorkloadEventList(
		@PathVariable("type") WorkloadType workloadType,
		@RequestBody WorkloadEventReqDTO workloadEventDTO
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getWorkloadEvent(workloadType, workloadEventDTO),
			HttpStatus.OK
		);
	}

	@GetMapping("/workloads/jobList")
	@Operation(summary = "워크로드 리스트 조회")
	public ResponseEntity<PageDTO<WorkloadSummaryDTO>> getWorkloadList(
		@RequestParam(value = "workloadType", required = false) WorkloadType workloadType,
		@RequestParam(value = "workspaceName", required = false) String workspaceName,
		@RequestParam(value = "searchName", required = false) String searchName,
		@RequestParam(value = "workloadStatus", required = false) WorkloadStatus workloadStatus,
		@RequestParam(value = "workloadSortCondition", required = false) WorkloadSortCondition workloadSortCondition,
		@RequestParam(value = "pageNum") int pageNum,
		@RequestParam(value = "isCreatedByMe", required = false) Boolean isCreatedByMe,
		UserDTO.UserInfo userInfoDTO
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getOverViewWorkloadList(workloadType, workspaceName, searchName, workloadStatus,
				workloadSortCondition, pageNum, isCreatedByMe, userInfoDTO), HttpStatus.OK);
	}

	@DeleteMapping("/workloads/{type}")
	@Operation(summary = "워크로드 종료 api")
	public ResponseEntity<HttpStatus> stopWorkload(
		@PathVariable(value = "type") WorkloadType workloadType,
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("resourceName") String resourceName,
		UserDTO.UserInfo userInfoDTO
	) throws IOException {
		workloadFacadeService.stopWorkload(workspaceResourceName, resourceName, workloadType, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/workloads/history/{id}")
	@Operation(summary = "워크로드 삭제 api")
	public ResponseEntity<HttpStatus> deleteWorkloadHistory(
		@PathVariable(value = "id") long id,
		UserDTO.UserInfo userInfoDTO
	) {
		workloadFacadeService.deleteWorkloadHistory(id, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/workloads/datasets")
	@Operation(summary = "워크로드 생성 시 데이터 셋 전체 조회")
	public ResponseEntity<DatasetDTO.DatasetsInWorkspace> getDatasets(
		@RequestParam(name = "workspaceResourceName") String workspaceResourceName,
		@RequestParam(name = "repositoryType") RepositoryType repositoryType,
		UserDTO.UserInfo userInfoDTO) {
		DatasetDTO.DatasetsInWorkspace datasetsByRepositoryType = datasetService.getDatasetsByRepositoryType(
			workspaceResourceName, repositoryType, userInfoDTO);

		return new ResponseEntity<>(datasetsByRepositoryType, HttpStatus.OK);
	}

	@GetMapping("/workloads/models")
	@Operation(summary = "워크로드 생성 시 model 전체 조회")
	public ResponseEntity<ModelDTO.ModelsInWorkspace> getModels(
		@RequestParam(name = "workspaceResourceName") String workspaceResourceName,
		@RequestParam(name = "repositoryType") RepositoryType repositoryType,
		UserDTO.UserInfo userInfoDTO) {
		ModelDTO.ModelsInWorkspace datasetsByRepositoryType = modelService.getModelsByRepositoryType(
			workspaceResourceName, repositoryType, userInfoDTO);

		return new ResponseEntity<>(datasetsByRepositoryType, HttpStatus.OK);
	}

	@GetMapping("/workloads/hubs")
	@Operation(summary = "워크로드 생성 시 hub 전체 조회")
	public ResponseEntity<FindHubInWorkloadResDTO.Hubs> getHubs(
		@RequestParam("workloadType") WorkloadType workloadType) {
		return new ResponseEntity<>(hubService.getHubListInWorkload(workloadType), HttpStatus.OK);
	}

	@GetMapping("/workloads/{workloadName}/files/list")
	@Operation(summary = "workload 파일리스트 조회")
	public ResponseEntity<DirectoryDTO> getFileListInWorkloadContainer(
		@PathVariable("workloadName") String workloadName,
		@RequestParam("workspaceName") String workspaceName,
		@RequestParam("workloadType") WorkloadType workloadType,
		@RequestParam("path") String path
	) throws IOException {
		return new ResponseEntity<>(
			workloadFacadeService.getFileListInWorkloadContainer(workloadName, workspaceName, workloadType, path),
			HttpStatus.OK);
	}

	@GetMapping("/workloads/{workloadName}/files/download")
	@Operation(summary = "workload 파일 다운로드")
	public ResponseEntity<Resource> downloadWorkloadFile(
		@PathVariable(value = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(value = "path") String path
	) throws IOException {
		MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(path);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + CoreFileUtils.splitFileName(path))
			.body(workloadFacadeService.downloadFileFromWorkload(workloadName, workspaceName, workloadType, path));
	}

	@DeleteMapping("/workloads/{workloadName}/files")
	@Operation(summary = "workload 파일 삭제")
	public ResponseEntity<HttpStatus> deleteFileFromWorkload(
		@PathVariable(value = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(value = "paths") List<String> path
	) {
		workloadFacadeService.deleteFileFromWorkload(workloadName, workspaceName, workloadType, path);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/workloads/{workloadName}/files/upload")
	@Operation(summary = "workload 파일 업로드")
	public ResponseEntity<FileUploadResultDTO> workloadFileUpload(
		@PathVariable(name = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestPart(name = "path") String path,
		@RequestPart(name = "files") List<MultipartFile> files) {
		return ResponseEntity.ok(
			workloadFacadeService.workloadFileUpload(workloadName, workspaceName, workloadType, path, files));
	}

	@PostMapping("/workloads/{workloadName}/folder")
	@Operation(summary = "workload 폴더 생성")
	public ResponseEntity<Boolean> workloadMkdir(
		@PathVariable(name = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(name = "path") String path
	) {
		return ResponseEntity.ok(workloadFacadeService.workloadMkdir(workloadName, workspaceName, workloadType, path));
	}

	@GetMapping("/workloads/{workloadName}/files/info")
	@Operation(summary = "workload 파일 상세 조회")
	public ResponseEntity<FileInfoDTO> getWorkloadFileInfo(
		@PathVariable(name = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(name = "path") String path) throws IOException {
		return ResponseEntity.ok()
			.body(workloadFacadeService.getWorkloadFileInfo(workloadName, workspaceName, workloadType, path));
	}

	@GetMapping("/workloads/{workloadName}/files/preview")
	@Operation(summary = "workload 파일 미리보기")
	public ResponseEntity<byte[]> getWorkloadFilePreview(
		@PathVariable(name = "workloadName") String workloadName,
		@RequestParam(value = "workspaceName") String workspaceName,
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(name = "path") String path
	) throws IOException {
		MediaType mediaType = CoreFileUtils.getMediaTypeForFileName(path);
		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
			.body(workloadFacadeService.getWorkloadFilePreview(workloadName, workspaceName, workloadType, path));
	}

	@GetMapping("/workloads/{workloadName}/history/log")
	@Operation(summary = "종료된 워크로드의 로그 조회하기")
	public ResponseEntity<byte[]> getEndWorkloadHistoryLog(
		@PathVariable(name = "workloadName") String workloadName,
		UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok()
			.body(workloadFacadeService.getWorkloadLogFile(workloadName, userInfoDTO));
	}

	//관리자 api
	@GetMapping("/admin/workloads/jobList")
	@Operation(summary = "관리자 워크로드 리스트 조회")
	public ResponseEntity<PageDTO<WorkloadSummaryDTO>> getAdminWorkloadList(
		@RequestParam(value = "workloadType") WorkloadType workloadType,
		@RequestParam(value = "workspaceName", required = false) String workspaceName,
		@RequestParam(value = "searchName", required = false) String searchName,
		@RequestParam(value = "workloadStatus", required = false) WorkloadStatus workloadStatus,
		@RequestParam(value = "workloadSortCondition", required = false) WorkloadSortCondition workloadSortCondition,
		@RequestParam(value = "pageNum") int pageNum,
		@RequestParam(value = "isCreatedByMe", required = false) Boolean isCreatedByMe,
		UserDTO.UserInfo userInfoDTO
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getAdminOverViewWorkloadList(workloadType, workspaceName, searchName, workloadStatus,
				workloadSortCondition, pageNum, isCreatedByMe, userInfoDTO), HttpStatus.OK);
	}

	@GetMapping("/admin/workloads/{type}")
	@Operation(summary = "관리자 워크로드 상세 조회")
	public ResponseEntity<FindWorkloadResDTO> getAdminWorkloadInfo(
		@PathVariable("type") WorkloadType workloadType,
		@RequestParam("workspaceResourceName") String workspaceResourceName,
		@RequestParam("workloadResourceName") String workloadResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		return new ResponseEntity<>(
			workloadFacadeService.getAdminWorkloadInfoByResourceName(workloadType, workspaceResourceName,
				workloadResourceName, userInfoDTO),
			HttpStatus.OK);
	}

	@GetMapping("/admin/workloads/{workloadName}/history/log")
	@Operation(summary = "관리자 종료된 워크로드의 로그 조회하기")
	public ResponseEntity<byte[]> getAdminEndWorkloadHistoryLog(
		@PathVariable(name = "workloadName") String workloadName,
		UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok()
			.body(workloadFacadeService.getWorkloadLogFile(workloadName, userInfoDTO));
	}

	@PostMapping("/admin/workloads/{type}/event")
	@Operation(summary = "워크로드 이벤트 리스트 조회")
	public ResponseEntity<PageDTO<WorkloadEventDTO>> getAdminWorkloadEventList(
		@PathVariable("type") WorkloadType workloadType,
		@RequestBody WorkloadEventReqDTO workloadEventDTO
	) {
		return new ResponseEntity<>(
			workloadFacadeService.getWorkloadEvent(workloadType, workloadEventDTO),
			HttpStatus.OK
		);
	}
}
