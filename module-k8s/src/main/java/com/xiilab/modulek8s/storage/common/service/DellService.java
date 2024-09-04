package com.xiilab.modulek8s.storage.common.service;

import static com.xiilab.modulecommon.exception.errorcode.StorageErrorCode.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmRepository;
import com.xiilab.modulek8s.storage.common.utils.StorageUtils;
import com.xiilab.modulek8s.storage.provisioner.vo.ProvisionerVO;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DellService extends StorageUtils {
	private final K8sAdapter k8sAdapter;
	private final String namespace = "unity";

	public void installDellProvisioner(String arrayId, String userName, String password, String endPoint) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			// Dell CSI 설치
			// 1. Namespace 생성
			createNamespace(client);
			// 2. Secret 생성
			createConfigMap(arrayId, userName, password, endPoint, client);
			createSecret(client);
			// 3. helm repository 생성
			MixedOperation<HelmRepository, KubernetesResourceList<HelmRepository>, Resource<HelmRepository>> helmRepo = client.resources(
				HelmRepository.class);
			HelmRepository repository = ProvisionerVO.createRepository();
			helmRepo.inNamespace(namespace).resource(repository).create();
			// 4. dell csi 설치
			MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
				HelmRelease.class);
			HelmRelease nfsResource = ProvisionerVO.createDellProvisioner();
			helmClient.inNamespace(namespace).resource(nfsResource).create();
			// 5. 설치 여부 검증
			validateDellProvisioner(client);
		} catch (KubernetesClientException e) {
			throw new RestApiException(STORAGE_ALREADY_INSTALLED_DELL);
		}
	}

	public void uninstallDellProvisioner() {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			// CSI 삭제
			MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
				HelmRelease.class);
			helmClient.inNamespace(namespace)
				.withLabel(LabelField.STORAGE_TYPE.getField(), StorageType.DELL_UNITY.name())
				.delete();
			// helm repository 삭제
			MixedOperation<HelmRepository, KubernetesResourceList<HelmRepository>, Resource<HelmRepository>> helmRepo = client.resources(
				HelmRepository.class);
			helmRepo.inNamespace(namespace).withName("unity-helmrepository").delete();
			checkDeleteDellPlugin(client);
			// helm secret 삭제
			deleteSecret(client);
			// namespace 삭제
			deleteNamespace(client);
		}
	}

	public void addProvisionerNodeLabel(String arrayId) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			// 추가할 라벨의 키 생성
			String key = "csi-unity.dellemc.com/" + arrayId + "-nfs";

			// 클러스터 내의 모든 노드에 라벨 추가
			client.nodes()
				.list()
				.getItems()
				.stream()
				.filter(node -> !node.getMetadata().getLabels().containsKey(key))
				.forEach(node -> {
					// 노드의 메타데이터에서 라벨 추가
					Map<String, String> labels = node.getMetadata().getLabels();
					if (labels == null) {
						labels = new HashMap<>();
					}
					labels.put(key, "true");
					node.getMetadata().setLabels(labels);

					// 변경 사항을 클러스터에 적용
					client.nodes().withName(node.getMetadata().getName()).patch(node);

					// 결과 출력
					System.out.println(node.getMetadata().getName() + " : " + key);
				});
		}
	}

	private void createNamespace(KubernetesClient client) {
		Namespace createNamespace = new NamespaceBuilder()
			.withNewMetadata()
			.withName(namespace)
			.endMetadata()
			.build();
		if (client.namespaces().withName(namespace).get() == null) {
			client.namespaces().resource(createNamespace).create();
		}
	}

	private void createConfigMap(String arrayId, String userName, String password, String endPoint,
		KubernetesClient client) {
		// Content of secret.yaml encoded in Base64
		String secretYamlContent = "storageArrayList:\n" +
			"  - arrayId: \"" + arrayId + "\"\n" +
			"    username: \"" + userName + "\"\n" +
			"    password: \"" + password + "\"\n" +
			"    endpoint: \"" + endPoint + "\"\n" +
			"    skipCertificateValidation: true\n" +
			"    isDefault: true";

		// Base64 encode the content
		String encodedContent = Base64.getEncoder().encodeToString(secretYamlContent.getBytes());

		// Create the Secret object
		Secret secret = new SecretBuilder()
			.withApiVersion("v1")
			.withKind("Secret")
			.withNewMetadata()
			.withName("csi-unity-creds")
			.withNamespace(namespace)
			.endMetadata()
			.withType("Opaque")
			.addToData("config", encodedContent)
			.build();
		if (client.secrets().inNamespace(namespace).withName("csi-unity-creds").get() == null) {
			client.secrets()
				.inNamespace(namespace)
				.create(secret);
		}
	}

	private void createSecret(KubernetesClient client) {
		Secret secret = new SecretBuilder()
			.withNewMetadata()
			.withName("csi-unity-certs-0")
			.withNamespace(namespace)
			.endMetadata()
			.withType("Opaque")
			.addToData("cert-0", "")
			.build();
		if (client.secrets().inNamespace(namespace).withName("csi-unity-certs-0").get() == null) {
			client.secrets()
				.inNamespace(namespace)
				.resource(secret)
				.create();
		}
	}

	private void deleteSecret(KubernetesClient client){
		client.secrets().inNamespace(namespace).delete();
	}

	private void deleteNamespace(KubernetesClient client) {
		client.namespaces().withName(namespace).delete();
	}

	private void validateDellProvisioner(KubernetesClient client) {
		int count = 0;
		while (count < 10) {
			try {
				Thread.sleep(5000);
				boolean b = client.pods().inNamespace(namespace).list().getItems().stream()
					.filter(pod -> !pod.getStatus().getPhase().equals("Running") && pod.getMetadata()
						.getName()
						.contains("csi-unity")).toList().size() == 0;
				if (b) {
					break;
				}
			} catch (InterruptedException e) {
				uninstallDellProvisioner();
				throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
			}
		}
	}

	private void checkDeleteDellPlugin(KubernetesClient client) {
		int count = 0;
		while (count < 5){
			try {
				Thread.sleep(5000);
			}catch (InterruptedException e) {
				throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
			}
			boolean b = client.pods().inNamespace("unity").list().getItems().size() == 0;
			if(b){
				break;
			}
		}
	}
}
