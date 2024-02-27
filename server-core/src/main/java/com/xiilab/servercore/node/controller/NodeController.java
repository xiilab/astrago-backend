package com.xiilab.servercore.node.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.servercore.node.service.NodeFacadeService;
import com.xiilab.servercore.node.service.NodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "NodeController", description = "노드 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/nodes")
public class NodeController {
	private final NodeService nodeService;
	private final NodeFacadeService nodeFacadeService;

	@GetMapping("")
	@Operation(summary = "노드 전체 리스트 조회")
	public ResponseEntity<ResponseDTO.PageNodeDTO> getNodeList(@RequestParam(required = false, defaultValue = "1", value = "page") int pageNo,
		@RequestParam(required = false, defaultValue = "10", value = "pageSize") int pageSize){
		return new ResponseEntity<>(nodeFacadeService.getNodeList(pageNo, pageSize), HttpStatus.OK);
	}
	@GetMapping("/{resourceName}")
	@Operation(summary = "노드 상세 조회")
	public ResponseEntity<ResponseDTO.NodeInfo> getNodeByResourceName(@PathVariable(name = "resourceName") String resourceName){
		return new ResponseEntity<>(nodeFacadeService.getNodeByResourceName(resourceName), HttpStatus.OK);
	}
	@GetMapping("/{resourceName}/resources")
	@Operation(summary = "노드 리소스 상세 조회")
	public ResponseEntity<ResponseDTO.NodeResourceInfo> getNode(@PathVariable(name = "resourceName") String resourceName){
		return new ResponseEntity<>(nodeFacadeService.getNodeResourceByResourceName(resourceName), HttpStatus.OK);
	}

	/**
	 * 등록된 node의 mig 가능한 리스트를 리턴하는 API
	 *
	 * @param nodeName 조회하려고 하는 node Name
	 * @return
	 */
	@GetMapping("/{nodeName}/mig/list")
	public ResponseEntity<ResponseDTO.MIGProfile> getNodeEnableMIGProfileList(@PathVariable(name = "nodeName") String nodeName){
		return new ResponseEntity<>(nodeService.getNodeMIGProfiles(nodeName), HttpStatus.OK);
	}
	/**
	 * 등록된 node의 mig 기능을 상태를 update하는 api
	 *
	 * @param nodeName 조회하려고 하는 node Name
	 * @param option all, custom 두가지 요청 가능함.
	 * @return
	 */
	@GetMapping("/{nodeName}/mig/all")
	public ResponseEntity<HttpStatus> updateMIGProfile(@PathVariable(name = "nodeName") String nodeName,
		@RequestParam String option) {
		nodeService.updateMIGAllProfile(nodeName, option);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
