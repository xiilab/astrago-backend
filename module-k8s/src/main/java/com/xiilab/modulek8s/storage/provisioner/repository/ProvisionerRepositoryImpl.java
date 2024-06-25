package com.xiilab.modulek8s.storage.provisioner.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerStatus;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.History;
import com.xiilab.modulek8s.storage.common.service.DellService;
import com.xiilab.modulek8s.storage.common.service.IbmService;
import com.xiilab.modulek8s.storage.common.service.WekaFsService;
import com.xiilab.modulek8s.storage.provisioner.dto.response.ProvisionerResDTO;
import com.xiilab.modulek8s.storage.provisioner.vo.ProvisionerVO;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
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
	private final IbmService ibmService;
	private final DellService dellService;
	private final WekaFsService wekaFsService;
	private static void checkInstallation(StorageType storageType, KubernetesClient client) {
		MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> nfsClient = client.resources(
			HelmRelease.class);
		String type = storageType.name();
		List<HelmRelease> items = nfsClient.inAnyNamespace()
			.withLabel(LabelField.STORAGE_TYPE.getField(), type)
			.list().getItems();
		if (items != null && !items.isEmpty()) {
			throw new RestApiException(CommonErrorCode.PLUGIN_ALREADY_INSTALLED);
		}
	}

	@Override
	public List<ProvisionerResDTO> findProvisioners() {
		//추 후 디비에서 관리하고 조회해와야함
		Map<String, String> nfsProvisioner = new HashMap<>();
		nfsProvisioner.put("name", "NFS_Provisioner");
		nfsProvisioner.put("type", "NFS");

		Map<String, String> pureProvisioner = new HashMap<>();
		pureProvisioner.put("name", "PURE_Provisioner");
		pureProvisioner.put("type", "PURE");

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

				if (helmReleases == null || helmReleases.isEmpty()) {
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
					ProvisionerStatus status = ProvisionerStatus.DISABLE;
					if (historyList != null && !historyList.isEmpty()) {
						String lastStatus = historyList.get(historyList.size() - 1).getStatus();
						status = "deployed".equalsIgnoreCase(lastStatus) ? ProvisionerStatus.ENABLE :
							ProvisionerStatus.DISABLE;
					}

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
			if (storageType == StorageType.NFS) {
				//이미 설치된 provisioner가 있는지 확인
				checkInstallation(storageType, client);
				MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
					HelmRelease.class);
				HelmRelease nfsResource = ProvisionerVO.createNFSResource();
				helmClient.inNamespace("csi").resource(nfsResource).create();
			} else if (storageType == StorageType.IBM) {
				// IBM Block 스토리지 설치
				ibmService.ibmInstall(client);
			} else if (storageType == StorageType.WEKA_FS) {
				// WEKA 스토리지 설치
				wekaFsService.wekaFsInstall(client);
			} else if(storageType == StorageType.DELL){
				// Dell CSI 스토리지 설치
				dellService.dellCrdInstall();
			}
		}
	}

	@Override
	public void unInstallProvisioner(StorageType storageType) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			if(storageType == StorageType.NFS){
				MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
					HelmRelease.class);
				helmClient.inNamespace("csi").withLabel(LabelField.STORAGE_TYPE.getField(), storageType.name())
					.delete();
			}else if(storageType == StorageType.IBM){
				ibmService.ibmDelete(client);
			}else if(storageType == StorageType.DELL){
				dellService.dellCsiUnInstall();
			} else if (storageType == StorageType.WEKA_FS) {
				wekaFsService.wekaFsUnInstall();
			}
		}
	}
}
