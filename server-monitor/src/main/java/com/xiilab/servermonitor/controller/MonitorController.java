package com.xiilab.servermonitor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;
import com.xiilab.modulemonitor.service.MonitorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
public class MonitorController {

	private final MonitorService monitorService;

	/**
	 * 실시간 모니터링 조회 API
	 * @param requestDTO
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping()
	public ResponseEntity<List<ResponseDTO.RealTimeDTO>> getPrometheusRealTimeMetric(@RequestBody RequestDTO requestDTO){
		return new ResponseEntity<>(monitorService.getRealTimeMetricByQuery(requestDTO), HttpStatus.OK);
	}
	/**
	 * 과거 모니터링 조회 API
	 * @param requestDTO
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping("/history")
	public ResponseEntity<List<ResponseDTO.HistoryDTO>> getPrometheusHistoryMetric(@RequestBody RequestDTO requestDTO){
		return new ResponseEntity<>(monitorService.getHistoryMetric(requestDTO), HttpStatus.OK);
	}
	/**
	 * 실시간 모니터링 조회 API
	 * @return 조회된 Monitor Metric
	 */
	@GetMapping("/event")
	public ResponseEntity<List<ResponseDTO.EventDTO>> getEventList(@RequestParam(name = "namespace", required = false) String namespace,
		@RequestParam(name = "podName",required = false) String podName){
		return new ResponseEntity<>(monitorService.getEventList(namespace, podName), HttpStatus.OK);
	}
	/**
	 * 등록된 Promql List 조회하는 API
	 * @return 등록된 Promql List
	 */
	@GetMapping("/promql")
	public ResponseEntity<List<ResponseDTO.PromqlDTO>> getPromqlList(){
		return new ResponseEntity<>(monitorService.getPromqlList(), HttpStatus.OK);
	}
}
