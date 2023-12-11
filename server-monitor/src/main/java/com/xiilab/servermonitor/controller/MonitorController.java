package com.xiilab.servermonitor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.service.MonitorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/v1/monitor")
@RequiredArgsConstructor
public class MonitorController {

	private final MonitorService monitorService;

	@GetMapping()
	public ResponseEntity<List<ResponseDTO.RealTimeDTO>> getPrometheusRealTimeMetric(@RequestBody RequestDTO requestDTO){
		return new ResponseEntity<>(monitorService.getRealTimeMetricByQuery(requestDTO), HttpStatus.OK);
	}
}
