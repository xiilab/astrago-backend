package com.xiilab.modulek8s.workload.secret.service;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;
import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;

public interface SecretService {
	String createSecret(K8SResourceReqDTO moduleCreateWorkloadReqDTO);

	String createIbmSecret(SecretDTO secretDTO);

	void deleteIbmSecret(String secretName);
}
