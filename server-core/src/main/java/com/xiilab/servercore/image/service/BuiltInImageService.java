package com.xiilab.servercore.image.service;

import java.util.List;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.image.dto.response.BuiltInImageResDTO;

public interface BuiltInImageService {
	BuiltInImageResDTO getBuiltInImageById(Long id);
	List<BuiltInImageResDTO> getBuiltInImageList(WorkloadType workloadType);
}
