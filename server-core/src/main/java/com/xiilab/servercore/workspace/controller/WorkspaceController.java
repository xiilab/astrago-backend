package com.xiilab.servercore.workspace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.facade.workspace.service.WorkspaceServiceFacade;
import com.xiilab.servercore.workspace.dto.ModifyVolumeReqDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceServiceFacade workspaceServiceFacade;

	/**
	 * 워크스페이스에 등록된 볼륨 상세 조회(해당 볼륨을 사용중인 원크로드 포함)
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @return
	 */
	@GetMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}/workloads")
	public ResponseEntity<VolumeWithWorkloadsResDTO> findVolumeWithWorkloadsByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName){

		VolumeWithWorkloadsResDTO result = workspaceServiceFacade.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PutMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}")
	public ResponseEntity<Object> volumeModifyByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName,
		@RequestBody ModifyVolumeReqDTO modifyVolumeReqDTO
		){
		modifyVolumeReqDTO.setMetaNames(workspaceMetaName, volumeMetaName);
		workspaceServiceFacade.volumeModifyByMetaName(modifyVolumeReqDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}



}
