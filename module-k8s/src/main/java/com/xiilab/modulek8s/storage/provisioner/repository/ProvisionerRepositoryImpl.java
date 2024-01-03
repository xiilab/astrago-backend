package com.xiilab.modulek8s.storage.provisioner.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerStatus;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Chart;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmReleaseSpec;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Install;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.SourceRef;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Spec;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.History;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ProvisionerRepositoryImpl implements ProvisionerRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public List<ProvisionerResDTO> findProvisioners() {
		//추 후 디비에서 관리하고 조회해와야함
		HashMap<String, String> nfsProvisioner = new HashMap<>();
		nfsProvisioner.put("name", "NFS_Provisioner");
		nfsProvisioner.put("type", "NFS");

		HashMap<String, String> pureProvisioner = new HashMap<>();
		nfsProvisioner.put("name", "PURE_Provisioner");
		nfsProvisioner.put("type", "PURE");

		List<Map<String, String>> provisioners = new ArrayList<>();
		provisioners.add(nfsProvisioner);
		provisioners.add(pureProvisioner);

		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<ProvisionerResDTO> provisionerResDTOS = new ArrayList<>();
			MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> nfsClient = client.resources(
				HelmRelease.class);
			for (Map<String, String> provisioner : provisioners) {
				String provisionerName = provisioner.get("name");
				String storageType = provisioner.get("type");
				//설치된 리스트 조회
				List<HelmRelease> helmReleases = nfsClient.inAnyNamespace()
					.withLabel(LabelField.STORAGE_TYPE.getField(), storageType)
					.list().getItems();

				if (helmReleases.isEmpty()) {
					ProvisionerResDTO provisionerResDTO = ProvisionerResDTO.builder()
						.provisionerName(provisionerName)
						.status(ProvisionerStatus.NONE)
						.storageCnt(0)
						.build();
					provisionerResDTOS.add(provisionerResDTO);
					continue;
				}
				for (HelmRelease helmRelease : helmReleases) {
					//상태 조회
					List<History> historyList = helmRelease.getStatus().getHistory();
					String lastStatus = historyList.get(historyList.size() - 1).getStatus();
					ProvisionerStatus status =
						lastStatus.equalsIgnoreCase("deployed") ? ProvisionerStatus.ENABLE : ProvisionerStatus.DISABLE;

					//연결된 sc 개수 조회
					int storageSize = client.storage()
						.v1()
						.storageClasses()
						.withLabel(LabelField.STORAGE_TYPE.getField(), storageType)
						.list()
						.getItems()
						.size();

					ProvisionerResDTO provisionerResDTO = ProvisionerResDTO.builder()
						.provisionerMetaName(helmRelease.getMetadata().getName())
						.provisionerName(
							helmRelease.getMetadata().getAnnotations().get(AnnotationField.NAME.getField()))
						.status(status)
						.storageCnt(storageSize)
						.build();
					provisionerResDTOS.add(provisionerResDTO);
				}
			}
			return provisionerResDTOS;
		}
	}

	@Override
	public void installProvisioner(StorageType storageType) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			if (storageType.equals(StorageType.NFS)) {
				//이미 설치된 provisioner가 있는지 확인
				MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> nfsClient = client.resources(
					HelmRelease.class);
				String type = storageType.name();
				List<HelmRelease> items = nfsClient.inAnyNamespace()
					.withLabel(LabelField.STORAGE_TYPE.getField(), type)
					.list().getItems();
				if(items != null && !items.isEmpty()){
					throw new RuntimeException("이미 설치된 플러그인입니다.");
				}
				ObjectMeta objectMeta = new ObjectMetaBuilder()
					.withName("csi-" + UUID.randomUUID()) //pr-uuid
					.addToAnnotations(AnnotationField.NAME.getField(), "NFS 플러그인")
					.addToLabels(LabelField.STORAGE_TYPE.getField(), type)
					.build();
				HelmRelease helmRelease = new HelmRelease();
				helmRelease.setMetadata(objectMeta);

				SourceRef sourceRef = SourceRef.builder()
					.kind("HelmRepository")
					.name("nfs-helmrepository")
					.build();

				Spec spec = Spec.builder()
					.chart("csi-driver-nfs")
					.sourceRef(sourceRef)
					.build();

				Chart chart = Chart.builder()
					.spec(spec)
					.build();

				Install install = Install.builder()
					.createNamespace(true)
					.build();

				HelmReleaseSpec helmReleaseSpec = HelmReleaseSpec.builder()
					.chart(chart)
					.interval("1m0s")
					.install(install)
					.releaseName("csi")
					.storageNamespace("csi")
					.targetNamespace("csi")
					.build();

				helmRelease.setSpec(helmReleaseSpec);
				MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
					HelmRelease.class);

				helmClient.inNamespace("csi").resource(helmRelease).create();
			}
		}
	}
}
