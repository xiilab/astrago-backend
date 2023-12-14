package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

public interface K8sMonitorRepository {
	List<ResponseDTO.RealTimeDTO> getK8sMetricsByQuery(RequestDTO requestDTO);
}
