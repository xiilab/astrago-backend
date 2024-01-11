package com.xiilab.servercore.workspace.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.DeleteWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyWorkspaceVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceTotalDTO;
import com.xiilab.servercore.workspace.service.WorkspaceFacadeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceFacadeService workspaceService;

	@PostMapping("")
	public ResponseEntity<HttpStatus> createWorkspace(@RequestBody WorkspaceApplicationForm workspaceApplicationForm) {
		workspaceService.createWorkspace(workspaceApplicationForm);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{name}")
	public ResponseEntity<WorkspaceTotalDTO> getWorkspaceInfo(@PathVariable(name = "name") String name) {
		return ResponseEntity.ok(workspaceService.getWorkspaceInfoByName(name));
	}

	@GetMapping("")
	public ResponseEntity<List<WorkspaceDTO.ResponseDTO>> getWorkspaceList(UserInfoDTO userInfoDTO) {
		return ResponseEntity.ok(workspaceService.getWorkspaceList(userInfoDTO));
	}

	@DeleteMapping("/{name}")
	public ResponseEntity<HttpStatus> deleteWorkspaceByName(@PathVariable(name = "name") String name) {
		workspaceService.deleteWorkspaceByName(name);
		return ResponseEntity.ok().build();
	}

	/**
	 * 해당 워크스페이스 & 스토리지 타입에 맞는 볼륨 리스트 조회
	 * @param workspaceMetaName
	 * @param storageMetaName
	 * @return
	 */
	@GetMapping("/{workspaceMetaName}/volumes/storages/{storageMetaName}")
	public ResponseEntity<List<VolumeResDTO>> findVolumesByWorkspaceMetaNameAndStorageMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("storageMetaName") String storageMetaName
	) {
		List<VolumeResDTO> volumesByStorageType = workspaceService.findVolumesByWorkspaceMetaNameAndStorageMetaName(
			workspaceMetaName,
			storageMetaName);

		return new ResponseEntity<>(volumesByStorageType, HttpStatus.OK);
	}

	/**
	 * 워크스페이스에 등록된 볼륨 상세 조회(해당 볼륨을 사용중인 원크로드 포함)
	 *
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @return
	 */
	@GetMapping("/{workspaceMetaName}/volumes/{volumeMetaName}/workloads")
	public ResponseEntity<VolumeWithWorkloadsResDTO> findVolumeWithWorkloadsByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName) {
		VolumeWithWorkloadsResDTO result = workspaceService.findVolumeWithWorkloadsByMetaName(workspaceMetaName,
			volumeMetaName);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 볼륨 리스트 조회 - 검색, 페이징 포함
	 *
	 * @param workspaceMetaName
	 * @param pageable
	 * @param searchCondition
	 * @return
	 */
	@GetMapping("/{workspaceMetaName}/volumes")
	public ResponseEntity<PageResDTO> findVolumesWithPagination(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		Pageable pageable,
		@ModelAttribute SearchCondition searchCondition) {
		PageResDTO result = workspaceService.findVolumesWithPagination(workspaceMetaName, pageable,
			searchCondition);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 볼륨 수정
	 *
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param modifyWorkspaceVolumeReqDTO
	 * @param userInfoDTO
	 * @return
	 */
	@PutMapping("/{workspaceMetaName}/volumes/{volumeMetaName}")
	public ResponseEntity<Object> modifyVolumeByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName,
		@RequestBody ModifyWorkspaceVolumeReqDTO modifyWorkspaceVolumeReqDTO,
		UserInfoDTO userInfoDTO
	) {
		modifyWorkspaceVolumeReqDTO.setMetaNames(workspaceMetaName, volumeMetaName);
		modifyWorkspaceVolumeReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workspaceService.modifyVolumeByMetaName(modifyWorkspaceVolumeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 볼륨 삭제
	 *
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param userInfoDTO
	 * @return
	 */
	@DeleteMapping("/{workspaceMetaName}/volumes/{volumeMetaName}")
	public ResponseEntity<Object> deleteVolumeByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName,
		UserInfoDTO userInfoDTO
	) {
		DeleteWorkspaceVolumeReqDTO deleteWorkspaceVolumeReqDTO = DeleteWorkspaceVolumeReqDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creator(userInfoDTO.getUserName())
			.creatorName(userInfoDTO.getUserRealName())
			.build();
		workspaceService.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(deleteWorkspaceVolumeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
