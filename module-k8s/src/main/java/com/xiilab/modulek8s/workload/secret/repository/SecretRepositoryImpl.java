package com.xiilab.modulek8s.workload.secret.repository;

import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.CredentialType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.workload.secret.vo.CredentialVO;

import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
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
			if (credentialVO.credentialType() == CredentialType.DOCKER) {
				createSecret = createDockerSecret(credentialVO);
			}
			Secret result = client.secrets()
				.inNamespace(credentialVO.workspaceName())
				.resource(createSecret)
				.serverSideApply();
			return result.getMetadata().getName();
		}
	}

	@Override
	public String createIbmSecret(SecretDTO secretDTO) {
		try (KubernetesClient client = k8sAdapter.configServer()) {
			Secret secret = new SecretBuilder()
				.withNewMetadata()
				.withName("ibm-sc-" + UUID.randomUUID())
				.withNamespace("ibm")
				.endMetadata()
				.withType("Opaque")
				.addToData("username", Base64.getEncoder()
					.encodeToString(secretDTO.getUserName().getBytes()))
				.addToData("password", Base64.getEncoder()
					.encodeToString(secretDTO.getPassword().getBytes()))
				.build();
			client.secrets()
				.inNamespace("ibm")
				.resource(secret)
				.create();

			return secret.getMetadata().getName();
		}
	}

	private Secret createDockerSecret(CredentialVO credentialVO) {
		return KubernetesResourceUtil.createDockerRegistrySecret(DOCKER_HUB_API_URL,
			credentialVO.credentialLoginId(), credentialVO.credentialLoginPw(), "sc-" + UUID.randomUUID());
	}

	@Override
	public void deleteIbmSecret(String secretName){
		try (KubernetesClient client = k8sAdapter.configServer()) {
			client.secrets()
				.inNamespace("ibm")
				.withName(secretName)
				.delete();
		}
	}

}
