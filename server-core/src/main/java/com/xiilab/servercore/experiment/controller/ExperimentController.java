package com.xiilab.servercore.experiment.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xiilab.modulek8sdb.experiment.dto.ChartDTO;
import com.xiilab.modulek8sdb.experiment.dto.ExperimentColumnDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.experiment.service.ExperimentService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/core/experiment")
@RequiredArgsConstructor
public class ExperimentController {
	private final ExperimentService experimentService;

	@GetMapping("/panel/list")
	@Operation(summary = "chart panel을 조회하는 api")
	public ResponseEntity<Page<ChartDTO.Panel>> getChartPanel(
		@RequestParam(name = "workspace") String workspace,
		UserDTO.UserInfo userInfo,
		Pageable pageable) {
		return new ResponseEntity<>(experimentService.getChartPartByUserId(workspace, pageable, userInfo),
			HttpStatus.OK);
	}

	@GetMapping("/panel/{id}/charts")
	@Operation(summary = "panel에 등록된 차트 리스트를 조회하는 api")
	public ResponseEntity<Page<ChartDTO.Res>> getChartsByPanelId(@PathVariable Long id, Pageable pageable) {
		return new ResponseEntity<>(experimentService.getChartsByPanelId(id, pageable), HttpStatus.OK);
	}

	@PostMapping("/panel")
	@Operation(summary = "panel을 추가하는 api")
	public ResponseEntity<HttpStatus> saveChartPanel(
		@RequestParam(name = "workspace") String workspace,
		@RequestParam(name = "title") String title) {
		experimentService.saveChartPanel(workspace, title);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/panel/{id}")
	@Operation(summary = "panel을 update하는 api")
	public ResponseEntity<HttpStatus> updateChartPanel(@PathVariable("id") Long id,
		@RequestParam(value = "title") String title, UserDTO.UserInfo userInfo) {
		experimentService.updateChatPartInfo(id, title, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/panel/{id}")
	@Operation(summary = "panel을 삭제하는 api")
	public ResponseEntity<HttpStatus> deleteChartPanel(@PathVariable("id") Long id, UserDTO.UserInfo userInfo) {
		experimentService.deletePanelById(id, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/panel/{id}")
	@Operation(summary = "panel에 chart를 등록하는 api")
	public ResponseEntity<HttpStatus> saveChart(@PathVariable("id") Long id, @RequestBody @Valid ChartDTO.Req req,
		UserDTO.UserInfo userInfo) {
		experimentService.addChart(id, req, userInfo);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@GetMapping("/chart/{id}")
	@Operation(summary = "chart를 조회하는 api")
	public ResponseEntity<ChartDTO.Res> getChartInfoById(@PathVariable("id") Long id, UserDTO.UserInfo userInfo) {
		return new ResponseEntity<>(experimentService.getChartInfoById(id, userInfo), HttpStatus.OK);
	}

	@PatchMapping("/chart/{id}")
	@Operation(summary = "chart를 수정하는 api")
	public ResponseEntity<HttpStatus> updateChartInfo(@PathVariable("id") Long chartId,
		@RequestBody @Valid ChartDTO.Req req,
		UserDTO.UserInfo userInfo) {
		experimentService.updateChartInfo(chartId, req, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/chart/{chartId}")
	@Operation(summary = "chart를 삭제하는 api")
	public ResponseEntity<HttpStatus> deleteChart(@PathVariable("chartId") Long chartId, UserDTO.UserInfo userInfo) {
		experimentService.deleteChart(chartId, userInfo);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/table")
	@Operation(summary = "유저의 비교실험 컬럼 데이터를 조회하는 API")
	public ResponseEntity<List<ExperimentColumnDTO.Res>> getExperimentColumns(
		@RequestParam(name = "workspace") String workspace,
		UserDTO.UserInfo userInfo) {
		return new ResponseEntity<>(experimentService.getColumns(workspace, userInfo.getId()), HttpStatus.OK);
	}

	@PostMapping("/table")
	@Operation(summary = "비교실험 컬럼 데이터를 추가하는 API")
	public ResponseEntity<HttpStatus> addExperimentColumn(
		@RequestParam(name = "workspace") String workspace,
		@RequestBody List<ExperimentColumnDTO.Req> req,
		UserDTO.UserInfo userInfo) {
		experimentService.updateColumn(req, workspace, userInfo.getId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping("/{uuid}/label")
	@Operation(summary = "비교 실험의 label을 추가하고 삭제하는 API")
	public ResponseEntity<HttpStatus> putExperimentLabels(
		@PathVariable("uuid") String experimentUUID,
		@RequestParam(name = "labelIds") List<Long> labelIds
	) {
		experimentService.updateExperimentLabel(experimentUUID, labelIds);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
