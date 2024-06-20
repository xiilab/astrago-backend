package com.xiilab.modulek8s.node.service;

import com.xiilab.modulek8s.node.dto.GpuInfoDTO;

public interface NodeService {
	GpuInfoDTO getGpuInfoByNodeName(String gpuName, String nodeName);
}
