package com.xiilab.servercore.node.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.NodeType;
import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.dto.MIGProfileDTO;
import com.xiilab.modulek8s.node.dto.MPSGpuDTO;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.enumeration.Promql;
import com.xiilab.modulemonitor.service.PrometheusService;
import com.xiilab.servercore.node.dto.NodeResDTO;
import com.xiilab.servercore.node.dto.ScheduleDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class NodeFacadeService {
	private final NodeRepository nodeRepository;
	private final PrometheusService prometheusService;

	public ResponseDTO.PageNodeDTO getNodeList(int pageNo, int pageSize, String searchText) {
		ResponseDTO.PageNodeDTO nodeList = nodeRepository.getNodeList(pageNo, pageSize, searchText);
		RequestDTO requestDTO = new RequestDTO();

		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalCpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.TOTAL_NODE_CPU_CORE));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageCpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.USAGE_NODE_CPU_CORE));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalGpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.TOTAL_NODE_GPU_COUNT));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalMpsGpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.TOTAL_NODE_MPS_GPU_COUNT));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageGpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.USAGE_NODE_GPU_COUNT));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageMpsGpuMap = listToMap(
			getMetricMap(requestDTO,
				Promql.USAGE_NODE_MPS_GPU_COUNT));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalMemMap = listToMap(
			getMetricMap(requestDTO,
				Promql.TOTAL_NODE_MEMORY_SIZE));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageMemMap = listToMap(
			getMetricMap(requestDTO,
				Promql.USAGE_NODE_MEMORY_SIZE));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalDiskMap = listToMap(
			getMetricMap(requestDTO,
				Promql.NODE_ROOT_DISK_SIZE));
		Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> usageDiskMap = listToMap(
			getMetricMap(requestDTO,
				Promql.NODE_ROOT_DISK_USAGE_SIZE));
		if (nodeList.getNodes() != null) {
			for (ResponseDTO.NodeDTO nodeDTO : nodeList.getNodes()) {
				String nodeName = nodeDTO.getNodeName();
				nodeDTO.setTotalCPU(getValueFromMap(totalCpuMap, nodeName, Promql.TOTAL_NODE_CPU_CORE));
				nodeDTO.setRequestCPU(getValueFromMap(usageCpuMap, nodeName, Promql.USAGE_NODE_CPU_CORE));
				nodeDTO.setTotalGPU(getValueFromMap(totalGpuMap, nodeName, Promql.TOTAL_NODE_GPU_COUNT));
				nodeDTO.setRequestGPU(getValueFromMap(usageGpuMap, nodeName, Promql.USAGE_NODE_GPU_COUNT));
				nodeDTO.setTotalMpsGPU(getValueFromMap(totalMpsGpuMap, nodeName, Promql.TOTAL_NODE_MPS_GPU_COUNT));
				nodeDTO.setRequestMpsGPU(getValueFromMap(usageMpsGpuMap, nodeName, Promql.USAGE_NODE_MPS_GPU_COUNT));
				nodeDTO.setTotalMEM(getValueFromMap(totalMemMap, nodeName, Promql.TOTAL_NODE_MEMORY_SIZE));
				nodeDTO.setRequestMEM(getValueFromMap(usageMemMap, nodeName, Promql.USAGE_NODE_MEMORY_SIZE));
				nodeDTO.setTotalDISK(getValueFromMap(totalDiskMap, nodeName, Promql.NODE_ROOT_DISK_SIZE));
				nodeDTO.setRequestDISK(getValueFromMap(usageDiskMap, nodeName, Promql.NODE_ROOT_DISK_USAGE_SIZE));
				nodeDTO.percentCalculation();
			}
		}
		return nodeList;
	}

	private double getValueFromMap(Map<String, com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> metricMap,
		String nodeName, Promql promql) {
		com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO realTimeDTO = metricMap.get(nodeName);
		if (realTimeDTO == null) {
			return 0.0;
		}
		String value = realTimeDTO.value();
		switch (promql) {
			case TOTAL_NODE_CPU_CORE:
			case USAGE_NODE_CPU_CORE:
				return DataConverterUtil.convertToCPU(value);
			case TOTAL_NODE_GPU_COUNT:
			case TOTAL_NODE_MPS_GPU_COUNT:
			case USAGE_NODE_GPU_COUNT:
			case USAGE_NODE_MPS_GPU_COUNT:
				return (DataConverterUtil.convertToGPU(value));
			case TOTAL_NODE_MEMORY_SIZE:
			case USAGE_NODE_MEMORY_SIZE:
				return (DataConverterUtil.convertToGBMemorySize(value));
			case NODE_ROOT_DISK_SIZE:
			case NODE_ROOT_DISK_USAGE_SIZE:
				return (DataConverterUtil.formatGBDiskSize(value));
			default:
				return 0.0;
		}
	}

	// MetricType에 따라 요청을 처리하는 메서드 추가
	private List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> getMetricMap(
		RequestDTO requestDTO, Promql promql) {
		requestDTO.changeMetricName(promql.name());
		return prometheusService.getRealTimeMetric(requestDTO);
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
	public MIGProfileDTO getNodeMIGProfiles(String nodeName, int giCount) {
		return nodeRepository.getNodeMIGProfiles(nodeName, giCount);
	}

	public ResponseDTO.NodeInfo getNodeByResourceName(String resourceName) {
		return nodeRepository.getNodeByResourceName(resourceName);

	}

	public ResponseDTO.NodeResourceInfo getNodeResourceByResourceName(String resourceName) {
		ResponseDTO.NodeResourceInfo nodeResourceInfo = nodeRepository.getNodeResourceByResourceName(
			resourceName);
		RequestDTO requestDTO = RequestDTO.builder()
			.nodeName(resourceName).build();
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> requestResource = getMetricMap(requestDTO,
			Promql.TOTAL_NODE_REQUEST_RESOURCE);
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> limitResource = getMetricMap(requestDTO,
			Promql.TOTAL_NODE_LIMIT_RESOURCE);

		double totalCPUResource = getTotalResource(Promql.TOTAL_NODE_CPU_CORE.name());
		double totalMPSGPUResource = getTotalResource(Promql.TOTAL_NODE_MPS_GPU_COUNT.name());
		double totalGPUResource =
			totalMPSGPUResource != 0 ? totalMPSGPUResource : getTotalResource(Promql.TOTAL_NODE_GPU_COUNT.name());
		double totalMEMResource = getTotalResource(Promql.TOTAL_NODE_MEMORY_SIZE.name()) / 1024;

		ResponseDTO.NodeResourceInfo.Requests requests = buildRequests(requestResource, totalCPUResource,
			totalGPUResource, totalMEMResource);
		ResponseDTO.NodeResourceInfo.Limits limits = buildLimits(limitResource, totalCPUResource, totalGPUResource,
			totalMEMResource);

		nodeResourceInfo.setRequests(requests);
		nodeResourceInfo.setLimits(limits);

		return nodeResourceInfo;
	}

	private double getTotalResource(String metricName) {
		try {
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.changeMetricName(metricName);
			List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> totalResource = prometheusService.getRealTimeMetric(
				requestDTO);
			return Double.parseDouble(totalResource.get(0).value());
		} catch (Exception e) {
			return 0;
		}
	}

	private ResponseDTO.NodeResourceInfo.Requests buildRequests(
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> requestResource,
		double totalCPUResource, double totalGPUResource, double totalMEMResource) {
		long cpu = 0;
		long memory = 0;
		long gpu = 0;
		for (com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO request : requestResource) {
			if (request.resource().equalsIgnoreCase("memory")) {
				memory = (long)Double.parseDouble(request.value()) / 1024;
			} else if (request.resource().equalsIgnoreCase("cpu")) {
				cpu = (long)(Double.parseDouble(request.value()) * 1000);
			} else if (request.resource().equalsIgnoreCase("nvidia_com_gpu")) {
				gpu = (long)Float.parseFloat(request.value());
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

	private ResponseDTO.NodeResourceInfo.Limits buildLimits(
		List<com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO> limitResource,
		double totalCPUResource, double totalGPUResource, double totalMEMResource) {
		long cpu = 0;
		long memory = 0;
		long gpu = 0;
		for (com.xiilab.modulemonitor.dto.ResponseDTO.RealTimeDTO limits : limitResource) {
			if (limits.resource().equalsIgnoreCase("memory")) {
				memory = (long)Double.parseDouble(limits.value()) / 1024;
			} else if (limits.resource().equalsIgnoreCase("cpu")) {
				cpu = (long)(Double.parseDouble(limits.value()) * 1000);
			} else if (limits.resource().equalsIgnoreCase("nvidia_com_gpu")) {
				gpu = (long)Float.parseFloat(limits.value());
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

	public void setSchedule(String resourceName, ScheduleDTO scheduleDTO) {
		nodeRepository.setSchedule(resourceName, scheduleDTO.getScheduleType());
	}

	public void updateMIGProfile(MIGGpuDTO migGpuDTO) {
		nodeRepository.saveGpuProductTOLabel(migGpuDTO.getNodeName());
		//mig parted configmap에 해당 노드의 프로파일 추가
		nodeRepository.updateMigProfile(migGpuDTO);
		//변경된 configmap이 적용될 수 있도록 mig manager를 restart한다.
		nodeRepository.restartMIGManager();
		//node의 라벨값 변경
		nodeRepository.updateMIGProfile(migGpuDTO.getNodeName(), migGpuDTO.getMigKey());
	}

	public void disableMIG(String nodeName) {
		nodeRepository.updateMIGProfile(nodeName, "all-disabled");
	}

	public MIGGpuDTO.MIGInfoStatus getNodeMigStatus(String nodeName) {
		return nodeRepository.getNodeMigStatus(nodeName);
	}

	public MPSGpuDTO.MPSInfoDTO getMpsConfig(String nodeName) {
		return nodeRepository.getMpsConfig(nodeName);
	}

	public void setMpsConfig(String nodeName, MPSGpuDTO.SetMPSDTO setMPSDTO) {
		setMPSDTO.setNodeName(nodeName);
		nodeRepository.setMpsConfig(setMPSDTO);
	}

	public NodeResDTO.FindGpuResources getNodeGpus(NodeType nodeType) {

		ResponseDTO.NodeGPUs nodeGPUs = nodeRepository.getNodeGPUs(nodeType);
		Map<String, List<NodeResDTO.GPUInfo>> normalGpuMap = getGpuInfos(nodeGPUs.getNormalGPU(), GPUType.NORMAL);
		Map<String, List<NodeResDTO.GPUInfo>> migGpuMap = getGpuInfos(nodeGPUs.getMigGPU(), GPUType.MIG);
		Map<String, List<NodeResDTO.GPUInfo>> mpsGpuMap = getGpuInfos(nodeGPUs.getMpsGPU(), GPUType.MPS);

		return NodeResDTO.FindGpuResources.builder()
			.normalGpuMap(normalGpuMap)
			.migGpuMap(migGpuMap)
			.mpsGpuMap(mpsGpuMap)
			.build();
	}

	private Map<String, List<NodeResDTO.GPUInfo>> getGpuInfos(Map<String, List<ResponseDTO.NodeGPUs.GPUInfo>> gpuList,
		GPUType gpuType) {
		return gpuList.entrySet().stream()
			.collect(Collectors.toMap(
				Map.Entry::getKey,
				entry -> {
					ResponseDTO.NodeGPUs.GPUInfo firstGpuInfo = entry.getValue().get(0);

					if (gpuType == GPUType.MPS) {
						// Group by onePerMemory value
						Map<Integer, List<ResponseDTO.NodeGPUs.GPUInfo>> groupedByMemory = entry.getValue().stream()
							.collect(Collectors.groupingBy(ResponseDTO.NodeGPUs.GPUInfo::getGpuOnePerMemory));

						return groupedByMemory.entrySet().stream()
							.map(memoryEntry -> NodeResDTO.GPUInfo.builder()
								.nodeName(memoryEntry.getValue().stream()
									.map(ResponseDTO.NodeGPUs.GPUInfo::getNodeName)
									.collect(Collectors.joining(",")))
								.gpuOnePerMemory(memoryEntry.getKey())
								.maximumGpuCount(1)
								.useAllGPUStatus(memoryEntry.getValue().get(0).isUseAllGPUStatus())
								.build())
							.collect(Collectors.toList());
					} else if (gpuType == GPUType.NORMAL) {
						return List.of(NodeResDTO.GPUInfo.builder()
							.gpuOnePerMemory(firstGpuInfo.getGpuOnePerMemory())
							.maximumGpuCount(getMaximumGPUCount(entry.getValue()))
							.useAllGPUStatus(firstGpuInfo.isUseAllGPUStatus())
							.build());
					} else {
						return List.of(NodeResDTO.GPUInfo.builder()
							.gpuOnePerMemory(firstGpuInfo.getGpuOnePerMemory())
							.maximumGpuCount(1)
							.useAllGPUStatus(firstGpuInfo.isUseAllGPUStatus())
							.build());
					}
				}
			));
	}

	private String getNodeName(String nodeName, GPUType gpuType) {
		return gpuType == GPUType.MPS ? nodeName : null;
	}

	private Integer getMaximumGPUCount(List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos) {
		return gpuInfos.stream()
			.map(ResponseDTO.NodeGPUs.GPUInfo::getCount)
			.max(Integer::compareTo)
			.orElseGet(() -> 0);
	}

	private Integer getTotalGPUCount(List<ResponseDTO.NodeGPUs.GPUInfo> gpuInfos) {
		return gpuInfos.stream()
			.mapToInt(ResponseDTO.NodeGPUs.GPUInfo::getCount)
			.sum();
	}
}
