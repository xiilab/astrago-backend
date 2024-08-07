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
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceDatasetDTO;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceModelDTO;
import com.xiilab.modulek8sdb.workspace.dto.InsertWorkspaceVolumeDTO;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceDatasetDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceModelDTO;
import com.xiilab.modulek8sdb.workspace.dto.UpdateWorkspaceVolumeDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.model.dto.ModelDTO;
import com.xiilab.servercore.model.service.ModelService;
import com.xiilab.servercore.volume.dto.VolumeResDTO;
import com.xiilab.servercore.volume.service.VolumeService;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workspace.dto.ClusterResourceCompareDTO;
import com.xiilab.servercore.workspace.dto.FindWorkspaceResDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceSettingDTO;
import com.xiilab.servercore.workspace.service.WorkspaceFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceFacadeService workspaceService;
	private final DatasetService datasetService;
	private final ModelService modelService;
	private final VolumeService volumeService;

	@PostMapping("")
	@Operation(summary = "워크스페이스 생성")
	public ResponseEntity<HttpStatus> createWorkspace(@RequestBody WorkspaceApplicationForm workspaceApplicationForm,
		UserDTO.UserInfo userInfoDTO) {
		workspaceService.createWorkspace(workspaceApplicationForm, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{name}")
	@Operation(summary = "워크스페이스 정보 조회")
	public ResponseEntity<WorkspaceTotalDTO> getWorkspaceInfo(@PathVariable(name = "name") String name) {

		return ResponseEntity.ok(workspaceService.getWorkspaceInfoByName(name));
	}

	@GetMapping("/admin/{name}")
	@Operation(summary = "관리자용 워크스페이스 정보 조회")
	public ResponseEntity<WorkspaceDTO.AdminInfoDTO> getAdminWorkspaceInfo(@PathVariable(name = "name") String name) {
		return ResponseEntity.ok(workspaceService.getAdminWorkspaceInfo(name));
	}

	@GetMapping("/resourceStatus")
	@Operation(summary = "내가 속한 워크스페이스의 resource 현황 조회")
	public ResponseEntity<List<WorkspaceDTO.WorkspaceResourceStatus>> getWorkspaceResourceStatus(
		@RequestParam(value = "workspaceName", required = false) String workspaceName,
		UserDTO.UserInfo userInfoDTO) {
		return ResponseEntity.ok(workspaceService.getUserWorkspaceResourceStatus(workspaceName, userInfoDTO));
	}

	@GetMapping("/resource/usage")
	@Operation(summary = "워크스페이스 리소스 사용 현황 조회")
	public ResponseEntity<WorkspaceResourceQuotaState> getWorkspaceResourceUsage(
		@RequestParam(value = "workspaceName") String workspaceName) {
		return ResponseEntity.ok(workspaceService.getWorkspaceResourceUsage(workspaceName));
	}

	@GetMapping("")
	@Operation(summary = "워크스페이스 리스트 조회")
	public ResponseEntity<PageDTO<WorkspaceDTO.TotalResponseDTO>> getWorkspaceList(
		@RequestParam(value = "isMyWorkspace") boolean isMyWorkspace,
		@RequestParam(value = "searchCondition", required = false) String searchCondition,
		@RequestParam(value = "pageNum") int pageNum,
		UserDTO.UserInfo userInfoDTO) {
		return ResponseEntity.ok(
			workspaceService.getWorkspaceList(isMyWorkspace, searchCondition, pageNum, userInfoDTO));
	}

	@GetMapping("/joined")
	@Operation(summary = "내가 속한 워크스페이스 리스트 조회")
	public ResponseEntity<WorkspaceDTO.FindJoinedWorkspaces> getJoinedWorkspaceList(
		@RequestParam(value = "title", required = false) String title,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok(
			workspaceService.getJoinedWorkspaceList(title, userInfoDTO));
	}

	@GetMapping("/joined/{workspaceResourceName}")
	@Operation(summary = "내가 속한 워크스페이스 상세 조회")
	public ResponseEntity<FindWorkspaceResDTO.JoinedWorkspaceDetail> getJoinedWorkspaceInfo(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		return ResponseEntity.ok(workspaceService.getJoinedWorkspaceInfoByName(workspaceResourceName, userInfoDTO.getId()));
	}

	@GetMapping("/admin")
	@Operation(summary = "관리자용 워크스페이스 리스트 조회")
	public ResponseEntity<PageDTO<WorkspaceDTO.AdminResponseDTO>> getAdminWorkspaceList(
		@RequestParam(value = "searchCondition", required = false) String searchCondition,
		@RequestParam(value = "sortCondition", required = false) WorkspaceSortCondition sortCondition,
		@RequestParam(value = "pageNum") int pageNum,
		@RequestParam(value = "pageSize") int pageSize,
		UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok(
			workspaceService.getAdminWorkspaceList(searchCondition, sortCondition, pageNum, pageSize, userInfoDTO)
		);
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
		UserDTO.UserInfo userInfoDTO) {
		workspaceService.deleteWorkspaceByName(name, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/overview/pin")
	@Operation(summary = "워크스페이스 OverView 조회")
	public ResponseEntity<List<WorkspaceDTO.TotalResponseDTO>> getWorkspaceOverView(UserDTO.UserInfo userInfoDTO) {
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
		@RequestBody WorkspaceResourceReqDTO workspaceResourceReqDTO, UserDTO.UserInfo userInfoDTO) {
		workspaceService.requestWorkspaceResource(workspaceResourceReqDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/resource")
	@Operation(summary = "워크스페이스 resource 리스트 조회")
	public ResponseEntity<PageDTO<ResourceQuotaFormDTO>> getResourceQuotaList(
		@RequestParam(value = "workspace") String workspace,
		@RequestParam(value = "pageNum") int pageNum,
		UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok(workspaceService.getResourceQuotaRequests(workspace, pageNum, userInfoDTO));
	}

	@GetMapping("/admin/resource")
	@Operation(summary = "관리자용 워크스페이스 resource 신청 리스트 조회")
	public ResponseEntity<PageDTO<ResourceQuotaFormDTO>> getAdminResourceQuotaList(
		@RequestParam(value = "pageNum") int pageNum,
		@RequestParam(value = "pageSize") int pageSize,
		UserDTO.UserInfo userInfoDTO
	) {
		return ResponseEntity.ok(workspaceService.getAdminResourceQuotaRequests(pageNum, pageSize, userInfoDTO));
	}

	@GetMapping("/clusterResourceUsage")
	@Operation(summary = "클러스터 자원량 대비 리소스 요청량")
	public ResponseEntity<ClusterResourceCompareDTO> requestResourceComparedClusterResource() {
		return ResponseEntity.ok(workspaceService.requestResourceComparedClusterResource());
	}

	@PatchMapping("/resource/{id}")
	@Operation(summary = "워크스페이스 resource 요청 승인/반려")
	public ResponseEntity<HttpStatus> updateResourceQuota(
		@PathVariable(value = "id") long id,
		@RequestBody ResourceQuotaApproveDTO resourceQuotaApproveDTO,
		UserDTO.UserInfo userInfoDTO
	) {
		workspaceService.updateResourceQuota(id, resourceQuotaApproveDTO, userInfoDTO);
		return ResponseEntity.ok().build();
	}


	// TODO 삭제 예정
	@PostMapping("{workspaceResourceName}/datasets")
	@Operation(summary = "워크스페이스 데이터 셋 추가")
	public ResponseEntity insertWorkspaceDataset(@RequestBody InsertWorkspaceDatasetDTO insertWorkspaceDatasetDTO) {
		datasetService.insertWorkspaceDataset(insertWorkspaceDatasetDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("{workspaceResourceName}/datasets")
	@Operation(summary = "워크스페이스 데이터 셋 전체 조회")
	public ResponseEntity<DatasetDTO.DatasetsInWorkspace> getDatasets(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName) {
		DatasetDTO.DatasetsInWorkspace datasetsByRepositoryType = datasetService.getDatasetsByWorkspaceResourceName(
			workspaceResourceName);
		return new ResponseEntity<>(datasetsByRepositoryType, HttpStatus.OK);
	}

	@PatchMapping("{workspaceResourceName}/datasets/{datasetId}")
	@Operation(summary = "워크스페이스 데이터 셋 defaultPath 수정")
	public ResponseEntity updateWorkspaceDataset(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "datasetId") Long datasetId,
		@RequestBody UpdateWorkspaceDatasetDTO updateWorkspaceDatasetDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) {
		datasetService.updateWorkspaceDataset(updateWorkspaceDatasetDTO, workspaceResourceName, datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("{workspaceResourceName}/datasets/{datasetId}")
	@Operation(summary = "워크스페이스 데이터 셋 삭제")
	public ResponseEntity deleteWorkspaceDataset(
		@PathVariable(value = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(value = "datasetId") Long datasetId, UserDTO.UserInfo userInfoDTO) {
		datasetService.deleteWorkspaceDataset(workspaceResourceName, datasetId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@PostMapping("{workspaceResourceName}/models")
	@Operation(summary = "워크스페이스 model 추가")
	public ResponseEntity insertWorkspaceModel(@RequestBody InsertWorkspaceModelDTO insertWorkspaceModelDTO) {
		modelService.insertWorkspaceModel(insertWorkspaceModelDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("{workspaceResourceName}/models")
	@Operation(summary = "워크스페이스 model 전체 조회")
	public ResponseEntity<ModelDTO.ModelsInWorkspace> getModels(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName) {
		ModelDTO.ModelsInWorkspace datasetsByRepositoryType = modelService.getModelsByWorkspaceResourceName(
			workspaceResourceName);

		return new ResponseEntity<>(datasetsByRepositoryType, HttpStatus.OK);
	}

	@PatchMapping("{workspaceResourceName}/models/{modelId}")
	@Operation(summary = "워크스페이스 model defaultPath 수정")
	public ResponseEntity updateWorkspaceModel(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "modelId") Long modelId,
		@RequestBody UpdateWorkspaceModelDTO updateWorkspaceModelDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) {
		modelService.updateWorkspaceModel(updateWorkspaceModelDTO, workspaceResourceName, modelId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("{workspaceResourceName}/models/{modelId}")
	@Operation(summary = "워크스페이스 model 삭제")
	public ResponseEntity deleteWorkspaceModel(
		@PathVariable(value = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(value = "modelId") Long modelId, UserDTO.UserInfo userInfoDTO) {
		modelService.deleteWorkspaceModel(workspaceResourceName, modelId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	//
	@PostMapping("{workspaceResourceName}/volumes")
	@Operation(summary = "워크스페이스 볼륨 추가")
	public ResponseEntity insertWorkspaceModel(@RequestBody InsertWorkspaceVolumeDTO insertWorkspaceVolumeDTO) {
		volumeService.insertWorkspaceVolume(insertWorkspaceVolumeDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("{workspaceResourceName}/volumes")
	@Operation(summary = "워크스페이스 volume 전체 조회")
	public ResponseEntity<VolumeResDTO.VolumesInWorkspace> getVolumesByWorkspaceResourceName(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName) {
		// ModelDTO.ModelsInWorkspace datasetsByRepositoryType = modelService.getModelsByWorkspaceResourceName(
		// 	workspaceResourceName);
		VolumeResDTO.VolumesInWorkspace volumesByRepositoryType = volumeService.getVolumesByWorkspaceResourceName(
			workspaceResourceName);

		return new ResponseEntity<>(volumesByRepositoryType, HttpStatus.OK);
	}

	@PatchMapping("{workspaceResourceName}/volumes/{volumeId}")
	@Operation(summary = "워크스페이스 volume defaultPath 수정")
	public ResponseEntity updateWorkspaceVolume(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(name = "volumeId") Long volumeId,
		@RequestBody UpdateWorkspaceVolumeDTO updateWorkspaceVolumeDTO,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO
	) {
		volumeService.updateWorkspaceVolume(updateWorkspaceVolumeDTO, workspaceResourceName, volumeId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@DeleteMapping("{workspaceResourceName}/models/{volumeId}")
	@Operation(summary = "워크스페이스 volume 삭제")
	public ResponseEntity deleteWorkspaceVolume(
		@PathVariable(value = "workspaceResourceName") String workspaceResourceName,
		@PathVariable(value = "volumeId") Long volumeId, UserDTO.UserInfo userInfoDTO) {
		volumeService.deleteWorkspaceVolume(workspaceResourceName, volumeId, userInfoDTO);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/alert/{workspaceName}")
	@Operation(summary = "워크스페이스 Alert Setting 조회 메소드")
	public ResponseEntity<WorkspaceAlertSetDTO.ResponseDTO> getWorkspaceAlertSet(
		@PathVariable(name = "workspaceName") String workspaceName) {
		return new ResponseEntity<>(workspaceService.getWorkspaceAlertSet(workspaceName), HttpStatus.OK);
	}

	@PatchMapping("/alert/{workspaceName}")
	@Operation(summary = "워크스페이스 Alert Setting 수정 메소드")
	public ResponseEntity<WorkspaceAlertSetDTO.ResponseDTO> updateWorkspaceAlertSet(
		@PathVariable(name = "workspaceName") String workspaceName,
		@RequestBody WorkspaceAlertSetDTO updateDTO) {
		return new ResponseEntity<>(workspaceService.updateWorkspaceAlertSet(workspaceName, updateDTO), HttpStatus.OK);
	}

	@GetMapping("/{workspaceResourceName}/accessAuthority")
	@Operation(summary = "워크스페이스 접근 권한 체크")
	public ResponseEntity<Boolean> workspaceAccessAuthority(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName,
		@Parameter(hidden = true) UserDTO.UserInfo userInfoDTO) {
		boolean accessAuthority = workspaceService.workspaceAccessAuthority(workspaceResourceName, userInfoDTO);
		return new ResponseEntity<>(accessAuthority, HttpStatus.OK);
	}

	@GetMapping("/resource/setting")
	@Operation(summary = "워크스페이스 리소스 디폴트 세팅 값 조회")
	public ResponseEntity<WorkspaceResourceSettingDTO> getWorkspaceResourceSetting() {
		return new ResponseEntity<>(workspaceService.getWorkspaceResourceSetting(), HttpStatus.OK);
	}

	@PatchMapping("/resource/setting")
	@Operation(summary = "워크스페이스 리소스 세팅 값 수정")
	public ResponseEntity<HttpStatus> updateWorkspaceResourceSetting(
		@RequestBody WorkspaceResourceSettingDTO workspaceResourceSettingDTO,
		UserDTO.UserInfo userInfoDTO) {
		workspaceService.updateWorkspaceResourceSetting(workspaceResourceSettingDTO, userInfoDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/redirect/valid/{workspaceResourceName}")
	@Operation(summary = "워크스페이스 리소스 세팅 값 수정")
	public ResponseEntity<HttpStatus> validRedirectWorkspace(
		@PathVariable(name = "workspaceResourceName") String workspaceResourceName
	) {
		workspaceService.validRedirectWorkspace(workspaceResourceName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
