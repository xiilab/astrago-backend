package com.xiilab.servercore.node.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NodeFacadeService {
	private final NodeRepository nodeRepository;
	private final PrometheusService prometheusService;

	public List<ResponseDTO.NodeDTO> getNodeList() {
		List<ResponseDTO.NodeDTO> nodeList = nodeRepository.getNodeList();
		RequestDTO requestDTO = new RequestDTO();

		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalCpuMap = getMetricMap(requestDTO,
			Promql.TOTAL_NODE_CPU_CORE);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageCpuMap = getMetricMap(requestDTO,
			Promql.USAGE_NODE_CPU_CORE);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalGpuMap = getMetricMap(requestDTO,
			Promql.TOTAL_NODE_GPU_COUNT);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageGpuMap = getMetricMap(requestDTO,
			Promql.USAGE_NODE_GPU_COUNT);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalMemMap = getMetricMap(requestDTO,
			Promql.TOTAL_NODE_MEMORY_SIZE);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageMemMap = getMetricMap(requestDTO,
			Promql.USAGE_NODE_MEMORY_SIZE);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalDiskMap = getMetricMap(requestDTO,
			Promql.NODE_ROOT_DISK_SIZE);
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageDiskMap = getMetricMap(requestDTO,
			Promql.NODE_ROOT_DISK_USAGE_SIZE);

		for (ResponseDTO.NodeDTO nodeDTO : nodeList) {
			String nodeName = nodeDTO.getNodeName();
			nodeDTO.setTotalCPU(getValueFromMap(totalCpuMap, nodeName, Promql.TOTAL_NODE_CPU_CORE));
			nodeDTO.setRequestCPU(getValueFromMap(usageCpuMap, nodeName, Promql.USAGE_NODE_CPU_CORE));
			nodeDTO.setTotalGPU(getValueFromMap(totalGpuMap, nodeName, Promql.TOTAL_NODE_GPU_COUNT));
			nodeDTO.setRequestGPU(getValueFromMap(usageGpuMap, nodeName, Promql.USAGE_NODE_GPU_COUNT));
			nodeDTO.setTotalMEM(getValueFromMap(totalMemMap, nodeName, Promql.TOTAL_NODE_MEMORY_SIZE));
			nodeDTO.setRequestMEM(getValueFromMap(usageMemMap, nodeName, Promql.USAGE_NODE_MEMORY_SIZE));
			nodeDTO.setTotalDISK(getValueFromMap(totalDiskMap, nodeName, Promql.NODE_ROOT_DISK_SIZE));
			nodeDTO.setRequestDISK(getValueFromMap(usageDiskMap, nodeName, Promql.NODE_ROOT_DISK_USAGE_SIZE));
		}

		return nodeList;
	}
	private String getValueFromMap(Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> metricMap, String nodeName, Promql promql) {
		com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO realTimeDTO = metricMap.get(nodeName);
		if (realTimeDTO == null) {
			return "0";
		}
		String value = realTimeDTO.value();
		switch (promql) {
			case TOTAL_NODE_CPU_CORE:
			case USAGE_NODE_CPU_CORE:
				return DataConverterUtil.convertToCPU(value) + " CORE";
			case TOTAL_NODE_GPU_COUNT:
			case USAGE_NODE_GPU_COUNT:
				return DataConverterUtil.convertToGPU(value) + " 개";
			case TOTAL_NODE_MEMORY_SIZE:
			case USAGE_NODE_MEMORY_SIZE:
				return DataConverterUtil.convertToMemorySize(value);
			case NODE_ROOT_DISK_SIZE:
			case NODE_ROOT_DISK_USAGE_SIZE:
				return DataConverterUtil.formatDiskSize(value);
			default:
				return "0";
		}
	}
	// MetricType에 따라 요청을 처리하는 메서드 추가
	private Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> getMetricMap(
		RequestDTO requestDTO, Promql promql) {
		requestDTO.changeMetricName(promql.name());
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> metricList = prometheusService.getRealTimeMetric(requestDTO);
		return listToMap(metricList);
	}
	private Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> listToMap(
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> list) {
		return list.stream()
			.collect(Collectors.toMap(com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO::nodeName, dto -> dto));
	}

	/**
	 * nodeName로 가능한 mig profile list를 조회하는 메소드
	 *
	 * @param nodeName 노드의 Name
	 * @return
	 */
	public ResponseDTO.MIGProfile getNodeMIGProfiles(String nodeName) {
		return nodeRepository.getNodeMIGProfiles(nodeName);
	}

	/**
	 * mig profile을 update 하는 메소드
	 *
	 * @param nodeName 노드 Name
	 * @param option mig 요청 profile
	 */
	public void updateMIGAllProfile(String nodeName, String option) {
		nodeRepository.updateMIGAllProfile(nodeName, option);
	}

	public ResponseDTO.NodeInfo getNodeByResourceName(String resourceName) {
		return nodeRepository.getNodeByResourceName(resourceName);

	}

	public ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName) {
		ResponseDTO.NodeResourceInfo nodeResourceInfo = nodeRepository.getNodeResourceByResourceName(
			resourceName);
		RequestDTO requestDTO = RequestDTO.builder()
			.metricName(Promql.TOTAL_NODE_REQUEST_RESOURCE.name())
			.nodeName(resourceName).build();
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> requestResource = prometheusService.getRealTimeMetric(
			requestDTO);
		requestDTO.changeMetricName(Promql.TOTAL_NODE_LIMIT_RESOURCE.name());
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> limitResource = prometheusService.getRealTimeMetric(
			requestDTO);

		double totalCPUResource = getTotalResource(Promql.TOTAL_NODE_CPU_CORE.name());
		double totalGPUResource = getTotalResource(Promql.TOTAL_NODE_GPU_COUNT.name());
		double totalMEMResource = getTotalResource(Promql.TOTAL_NODE_MEMORY_SIZE.name()) / 1024;

		ResponseDTO.NodeResourceInfo.Requests requests = buildRequests(requestResource, totalCPUResource, totalGPUResource, totalMEMResource);
		ResponseDTO.NodeResourceInfo.Limits limits = buildLimits(limitResource, totalCPUResource, totalGPUResource, totalMEMResource);

		nodeResourceInfo.setRequests(requests);
		nodeResourceInfo.setLimits(limits);

		return nodeResourceInfo;
	}
	private double getTotalResource(String metricName) {
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.changeMetricName(metricName);
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalResource = prometheusService.getRealTimeMetric(requestDTO);
		return Double.parseDouble(totalResource.get(0).value());
	}
	private ResponseDTO.NodeResourceInfo.Requests buildRequests(List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> requestResource,
		double totalCPUResource, double totalGPUResource, double totalMEMResource) {
		long cpu = 0;
		long memory = 0;
		long gpu = 0;
		for (com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO request : requestResource) {
			if (request.resource().equalsIgnoreCase("memory")) {
				memory = (long) Double.parseDouble(request.value()) / 1024;
			} else if (request.resource().equalsIgnoreCase("cpu")) {
				cpu = (long) (Double.parseDouble(request.value()) * 1000);
			} else if (request.resource().equalsIgnoreCase("nvidia_com_gpu")) {
				gpu = (long) Float.parseFloat(request.value());
			}
		}
		ResponseDTO.NodeResourceInfo.Requests requests = ResponseDTO.NodeResourceInfo.Requests.builder()
			.cpu(cpu)
			.memory(memory)
			.gpu(gpu)
			.build();
		requests.cpuPercentCalculation(totalCPUResource);
		requests.gpuPercentCalculation(totalGPUResource);
		requests.memoryPercentCalculation(totalMEMResource);
		return requests;
	}
	private ResponseDTO.NodeResourceInfo.Limits buildLimits(List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> limitResource,
		double totalCPUResource, double totalGPUResource, double totalMEMResource) {
		long cpu = 0;
		long memory = 0;
		long gpu = 0;
		for (com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO limits : limitResource) {
			if (limits.resource().equalsIgnoreCase("memory")) {
				memory = (long) Double.parseDouble(limits.value()) / 1024;
			} else if (limits.resource().equalsIgnoreCase("cpu")) {
				cpu = (long) (Double.parseDouble(limits.value()) * 1000);
			} else if (limits.resource().equalsIgnoreCase("nvidia_com_gpu")) {
				gpu = (long) Float.parseFloat(limits.value());
			}
		}
		ResponseDTO.NodeResourceInfo.Limits limitsObj = ResponseDTO.NodeResourceInfo.Limits.builder()
			.gpu(gpu)
			.memory(memory)
			.cpu(cpu)
			.build();
		limitsObj.cpuPercentCalculation(totalCPUResource);
		limitsObj.gpuPercentCalculation(totalGPUResource);
		limitsObj.memoryPercentCalculation(totalMEMResource);
		return limitsObj;
	}
}
