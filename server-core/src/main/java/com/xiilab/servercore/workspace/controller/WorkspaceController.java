package com.xiilab.servercore.workspace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.servercore.facade.workspace.service.WorkspaceServiceFacade;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceServiceFacade workspaceServiceFacade;

	@GetMapping("/workspaces/{workspaceMetaName}/volumes/{volumeMetaName}/workloads")
	public ResponseEntity<VolumeWithWorkloadsResDTO> findVolumeWithWorkloadsByMetaName(
		@PathVariable("workspaceMetaName") String workspaceMetaName,
		@PathVariable("volumeMetaName") String volumeMetaName){

		VolumeWithWorkloadsResDTO result = workspaceServiceFacade.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}


}
