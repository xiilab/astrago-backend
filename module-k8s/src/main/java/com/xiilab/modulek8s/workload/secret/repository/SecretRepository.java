package com.xiilab.modulek8s.workload.secret.repository;

import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

public interface SecretRepository {
	String createSecret(CredentialVO credentialVO);

	String createIbmSecret(SecretDTO secretDTO);
}
