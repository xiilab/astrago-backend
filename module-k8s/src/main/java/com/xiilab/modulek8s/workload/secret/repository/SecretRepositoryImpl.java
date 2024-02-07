package com.xiilab.modulek8s.workload.secret.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.workload.enums.CredentialType;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;
import com.xiilab.modulek8s.workload.secret.vo.SecretVO;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.KubernetesResourceUtil;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SecretRepositoryImpl implements SecretRepository {
	private final K8sAdapter k8sAdapter;
	private static final String DOCKER_HUB_API_URL = "https://index.docker.io/v1/";

	@Override
	public String createSecret(CredentialVO credentialVO) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Secret createSecret = null;
			if (credentialVO.credentialType() == CredentialType.DOCKER_HUB) {
				createSecret = createDockerSecret(credentialVO);
			}
			Secret result = client.secrets()
				.inNamespace(credentialVO.workspaceName())
				.resource(createSecret)
				.serverSideApply();
			return result.getMetadata().getName();
		}
	}

	private Secret createDockerSecret(CredentialVO credentialVO) {
		return KubernetesResourceUtil.createDockerRegistrySecret(DOCKER_HUB_API_URL,
			credentialVO.credentialLoginId(), credentialVO.credentialLoginPw(), credentialVO.credentialName());
	}

	// private Secret createGitSecret(CredentialVO credentialVO) {
	// 	return
	// }
}
