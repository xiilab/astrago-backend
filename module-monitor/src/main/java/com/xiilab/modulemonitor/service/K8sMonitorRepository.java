package com.xiilab.modulemonitor.service;

import java.util.List;

import com.xiilab.modulemonitor.dto.RequestDTO;
import com.xiilab.modulemonitor.dto.ResponseDTO;

import io.fabric8.kubernetes.api.model.Event;

public interface K8sMonitorRepository {
	List<ResponseDTO.RealTimeDTO> getK8sMetricsByQuery(RequestDTO requestDTO);
	List<Event> getEventList();
	List<Event> getEventList(String namespace);
	List<Event> getEventList(String namespace, String podName);
}
