package com.xiilab.servercore.workspace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.workspace.dto.InsertWorkspaceDatasetDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.servercore.workspace.service.WorkspaceFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceFacadeService workspaceService;
	private final WorkspaceModuleFacadeService workspaceModuleFacadeService;
	private final DatasetService datasetService;

	@PostMapping("")
	@Operation(summary = "워크스페이스 생성")
	public ResponseEntity<HttpStatus> createWorkspace(@RequestBody WorkspaceApplicationForm workspaceApplicationForm,
		UserInfoDTO userInfoDTO) {
		workspaceService.createWorkspace(workspaceApplicationForm, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{name}")
	@Operation(summary = "워크스페이스 정보 조회")
	public ResponseEntity<WorkspaceTotalDTO> getWorkspaceInfo(@PathVariable(name = "name") String name) {
		return ResponseEntity.ok(workspaceService.getWorkspaceInfoByName(name));
	}

	@GetMapping("")
	@Operation(summary = "워크스페이스 리스트 조회")
	public ResponseEntity<PageDTO<WorkspaceDTO.TotalResponseDTO>> getWorkspaceList(
		@RequestParam(value = "isMyWorkspace") boolean isMyWorkspace,
		@RequestParam(value = "searchCondition", required = false) String searchCondition,
		@RequestParam(value = "pageNum") int pageNum,
		UserInfoDTO userInfoDTO) {
		return ResponseEntity.ok(
			workspaceService.getWorkspaceList(isMyWorkspace, searchCondition, pageNum, userInfoDTO));
	}

	@PatchMapping("/{workspaceName}")
	@Operation(summary = "워크스페이스 수정")
	public ResponseEntity<HttpStatus> updateWorkspaceInfo(
		@PathVariable("workspaceName") String workspaceName,
		@RequestBody WorkspaceDTO.UpdateDTO updateDTO
	) {
		workspaceService.updateWorkspace(workspaceName, updateDTO);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{name}")
	@Operation(summary = "워크스페이스 삭제")
	public ResponseEntity<HttpStatus> deleteWorkspaceByName(@PathVariable(name = "name") String name,
		UserInfoDTO userInfoDTO) {
		workspaceService.deleteWorkspaceByName(name, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/overview/pin")
	@Operation(summary = "워크스페이스 OverView 조회")
	public ResponseEntity<List<WorkspaceDTO.TotalResponseDTO>> getWorkspaceOverView(UserInfoDTO userInfoDTO) {
		return ResponseEntity.ok(workspaceService.getWorkspaceOverView(userInfoDTO));
	}

	@GetMapping("/resource/state")
	@Operation(summary = "워크스페이스 resource 현황 조회")
	public ResponseEntity<WorkspaceResourceQuotaState> getWorkspaceResourceState(
		@RequestParam("workspaceName") String workspaceName) {
		return ResponseEntity.ok(workspaceService.getWorkspaceResourceQuotaState(workspaceName));
	}

	@DeleteMapping("/resource/{id}")
	@Operation(summary = "워크스페이스 resource 요청 취소")
	public ResponseEntity<HttpStatus> deleteWorkspaceResource(
		@PathVariable("id") long id) {
		workspaceService.deleteResourceQuota(id);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/resource")
	@Operation(summary = "워크스페이스 resource 요청")
	public ResponseEntity<HttpStatus> requestWorkspaceResource(
		@RequestBody WorkspaceResourceReqDTO workspaceResourceReqDTO, UserInfoDTO userInfoDTO) {
		workspaceService.requestWorkspaceResource(workspaceResourceReqDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/resource")
	@Operation(summary = "워크스페이스 resource 리스트 조회")
	public ResponseEntity<List<ResourceQuotaFormDTO>> getResourceQuotaList(
		@RequestParam(value = "workspace") String workspace,
		UserInfoDTO userInfoDTO
	) {
		return ResponseEntity.ok(workspaceService.getResourceQuotaRequests(workspace, userInfoDTO));
	}

	@PatchMapping("/resource/{id}")
	@Operation(summary = "워크스페이스 resource 요청 승인/반려")
	public ResponseEntity<HttpStatus> updateResourceQuota(
		@PathVariable(value = "id") long id,
		@RequestBody ResourceQuotaApproveDTO resourceQuotaApproveDTO
	) {
		workspaceService.updateResourceQuota(id, resourceQuotaApproveDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{name}/quota")
	@Operation(summary = "워크스페이스 리소스쿼터 조회")
	public ResponseEntity<ResourceQuotaResDTO> getWorkspaceResourceQuota(@PathVariable(name = "name") String name) {
		return ResponseEntity.ok(workspaceModuleFacadeService.getWorkspaceResourceQuota(name));
	}

	@PostMapping("{workspaceResourceName}/datasets")
	@Operation(summary = "워크스페이스 데이터 셋 추가")
	public ResponseEntity insertWorkspaceDataset(@RequestBody InsertWorkspaceDatasetDTO insertWorkspaceDatasetDTO){
		datasetService.insertWorkspaceDataset(insertWorkspaceDatasetDTO);
		return new ResponseEntity(HttpStatus.OK);
	}
	@DeleteMapping("{workspaceResourceName}/datasets/{datasetId}")
	@Operation(summary = "워크스페이스 데이터 셋 삭제")
	public ResponseEntity deleteWorkspaceDataset(@PathVariable(value = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(value = "datasetId") Long datasetId){
		datasetService.deleteWorkspaceDataset(workspaceResourceName, datasetId);
		return new ResponseEntity(HttpStatus.OK);
	}
}
