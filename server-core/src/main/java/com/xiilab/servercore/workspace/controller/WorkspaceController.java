package com.xiilab.servercore.workspace.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.common.dto.SearchCondition;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.DeleteVolumeReqDTO;
import com.xiilab.servercore.workspace.dto.ModifyVolumeReqDTO;
import com.xiilab.servercore.workspace.service.WorkspaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceService workspaceService;

	/**
	 *
	 * @param workspaceMetaName
	 * @param storageType
	 * @return
	 */
	@GetMapping("/workspaces/{workspaceMetaName}/volumes/storages/{storageType}")
	public ResponseEntity<List<VolumeResDTO>> findVolumesByWorkspaceMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("storageType") StorageType storageType
	){
		List<VolumeResDTO> volumesByStorageType = workspaceService.findVolumesByWorkspaceMetaNameAndStorageType(workspaceMetaName,
			storageType);

		return new ResponseEntity<>(volumesByStorageType, HttpStatus.OK);
	}

	/**
	 * 워크스페이스에 등록된 볼륨 상세 조회(해당 볼륨을 사용중인 원크로드 포함)
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @return
	 */
	@GetMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}/workloads")
	public ResponseEntity<VolumeWithWorkloadsResDTO> findVolumeWithWorkloadsByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName) {
		VolumeWithWorkloadsResDTO result = workspaceService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
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
	@GetMapping("/workspaces/{workspaceMetaName}/volumes")
	public ResponseEntity<PageResDTO> findVolumesWithPagination(@PathVariable("workspaceMetaName") String workspaceMetaName,
		Pageable pageable,
		@ModelAttribute SearchCondition searchCondition){
		PageResDTO result = workspaceService.findVolumesWithPagination(workspaceMetaName, pageable,
			searchCondition);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * 볼륨 수정
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param modifyVolumeReqDTO
	 * @param userInfoDTO
	 * @return
	 */
	@PutMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}")
	public ResponseEntity<Object> modifyVolumeByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName,
		@RequestBody ModifyVolumeReqDTO modifyVolumeReqDTO,
		UserInfoDTO userInfoDTO
	) {
		modifyVolumeReqDTO.setMetaNames(workspaceMetaName, volumeMetaName);
		modifyVolumeReqDTO.setUserInfo(userInfoDTO.getUserName(), userInfoDTO.getUserRealName());
		workspaceService.modifyVolumeByMetaName(modifyVolumeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 볼륨 삭제
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param userInfoDTO
	 * @return
	 */
	@DeleteMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}")
	public ResponseEntity<Object> deleteVolumeByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName,
		UserInfoDTO userInfoDTO
	) {
		DeleteVolumeReqDTO deleteVolumeReqDTO = DeleteVolumeReqDTO.builder()
			.workspaceMetaName(workspaceMetaName)
			.volumeMetaName(volumeMetaName)
			.creator(userInfoDTO.getUserName())
			.creatorName(userInfoDTO.getUserRealName())
			.build();
		workspaceService.deleteVolumeByMetaName(deleteVolumeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
