package com.xiilab.modulek8s.workload.secret.service;

import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;

public interface SecretService {
	String createSecret(CreateWorkloadReqDTO moduleCreateWorkloadReqDTO);

	String createIbmSecret(SecretDTO secretDTO);

	void deleteIbmSecret(String secretName);
}
