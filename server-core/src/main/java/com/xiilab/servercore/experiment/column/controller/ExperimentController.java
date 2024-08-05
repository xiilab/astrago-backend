package com.xiilab.servercore.experiment.column.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.experiment.column.dto.ExperimentColumnDTO;
import com.xiilab.servercore.experiment.column.service.ExperimentColumnService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/experiment/column")
@RequiredArgsConstructor
public class ExperimentController {
	private final ExperimentColumnService experimentColumnService;

	@GetMapping("/{workspace}")
	@Operation(summary = "유저의 비교실험 컬럼 데이터를 조회하는 API")
	public ResponseEntity<List<ExperimentColumnDTO.Res>> getExperimentColumns(
		@PathVariable("workspace") String workspace,
		UserDTO.UserInfo userInfo) {
		return new ResponseEntity<>(experimentColumnService.getColumns(workspace, userInfo.getId()), HttpStatus.OK);
	}

	@PostMapping("/{workspace}")
	@Operation(summary = "비교실험 컬럼 데이터를 추가하는 API")
	public ResponseEntity<HttpStatus> addExperimentColumn(
		@PathVariable("workspace") String workspace,
		@RequestBody List<ExperimentColumnDTO.Req> req,
		UserDTO.UserInfo userInfo) {
		experimentColumnService.updateColumn(req, workspace, userInfo.getId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
}
