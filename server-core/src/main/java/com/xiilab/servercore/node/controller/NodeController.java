package com.xiilab.servercore.node.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.servercore.node.dto.ScheduleDTO;
import com.xiilab.servercore.node.service.NodeFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "NodeController", description = "노드 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/nodes")
public class NodeController {
	private final NodeFacadeService nodeFacadeService;

	@GetMapping("")
	@Operation(summary = "노드 전체 리스트 조회")
	public ResponseEntity<ResponseDTO.PageNodeDTO> getNodeList(
		@RequestParam(required = false, defaultValue = "1", value = "page") int pageNo,
		@RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize,
		@RequestParam(required = false, value = "searchText") String searchText) {
		return new ResponseEntity<>(nodeFacadeService.getNodeList(pageNo, pageSize, searchText), HttpStatus.OK);
	}

	@GetMapping("/{resourceName}")
	@Operation(summary = "노드 상세 조회")
	public ResponseEntity<ResponseDTO.NodeInfo> getNodeByResourceName(
		@PathVariable(name = "resourceName") String resourceName) {
		return new ResponseEntity<>(nodeFacadeService.getNodeByResourceName(resourceName), HttpStatus.OK);
	}

	@GetMapping("/{resourceName}/resources")
	@Operation(summary = "노드 리소스 상세 조회")
	public ResponseEntity<ResponseDTO.NodeResourceInfo> getNode(
		@PathVariable(name = "resourceName") String resourceName) {
		return new ResponseEntity<>(nodeFacadeService.getNodeResourceByResourceName(resourceName), HttpStatus.OK);
	}

	@PostMapping("/{resourceName}/schedule")
	@Operation(summary = "노드 스케줄 설정")
	public ResponseEntity<HttpStatus> setSchedule(@RequestBody ScheduleDTO scheduleDTO,
		@PathVariable String resourceName) {
		nodeFacadeService.setSchedule(resourceName, scheduleDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("{nodeName}/mig/status")
	@Operation(summary = "노드의 mig profile 및 status 조회")
	public ResponseEntity<MIGGpuDTO.MIGInfoStatus> getNodeMigStatus(@PathVariable(value = "nodeName") String nodeName) {
		return new ResponseEntity<>(nodeFacadeService.getNodeMigStatus(nodeName), HttpStatus.OK);
	}

	@PatchMapping("/mig")
	@Operation(summary = "node에 mig 설정")
	public ResponseEntity<HttpStatus> setMigConfig(@RequestBody MIGGpuDTO MIGGpuDTO
	) {
		nodeFacadeService.updateMIGProfile(MIGGpuDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PatchMapping("{nodeName}/mig/disable")
	@Operation(summary = "node mig 비활성화")
	public ResponseEntity<HttpStatus> setMigDisable(@PathVariable(value = "nodeName") String nodeName) {
		nodeFacadeService.disableMIG(nodeName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * 등록된 node의 mig 가능한 리스트를 리턴하는 API
	 *
	 * @param nodeName 조회하려고 하는 node Name
	 * @return
	 */
	@GetMapping("/{nodeName}/mig/list")
	public ResponseEntity<MIGProfileDTO> getNodeEnableMIGProfileList(
		@PathVariable(name = "nodeName") String nodeName,
		@RequestParam(name = "giCount") int giCount) {
		return new ResponseEntity<>(nodeFacadeService.getNodeMIGProfiles(nodeName, giCount), HttpStatus.OK);
	}
}
