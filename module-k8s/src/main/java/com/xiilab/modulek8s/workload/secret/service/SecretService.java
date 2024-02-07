package com.xiilab.modulek8s.workload.secret.service;

import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;

public interface SecretService {
	String createSecret(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO);
}
