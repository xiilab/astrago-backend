package com.xiilab.servercore.node.controller;

import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.servercore.node.service.NodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/core/node")
public class NodeController {
	private final NodeService nodeService;

	@GetMapping("")
	public ResponseEntity<List<ResponseDTO.NodeDTO>> getNodeList(){
		return new ResponseEntity<>(nodeService.getNodeList(), HttpStatus.OK);
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
