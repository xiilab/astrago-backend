package com.xiilab.modulek8s.storage.storageclass.repository;

import static com.xiilab.modulecommon.exception.errorcode.StorageErrorCode.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerStatus;
import com.xiilab.modulek8s.common.enumeration.ProvisionerType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.History;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.VolumeDTO;
import com.xiilab.modulek8s.storage.storageclass.vo.StorageClassVO;

import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.storage.CSIDriver;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StorageClassRepositoryImpl implements StorageClassRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public List<StorageClass> findStorageClassByType(StorageType storageType) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<StorageClass> storageClasses = client.storage()
				.v1()
				.storageClasses()
				.withLabel(LabelField.STORAGE_TYPE.getField(), storageType.name())
				.withLabel(LabelField.CONTROL_BY.getField(), "astra")
				.list()
				.getItems();
			if (storageClasses.size() == 0) {
				throw new K8sException(StorageErrorCode.STORAGE_NOT_FOUND);
			}
			return storageClasses;
		}
	}

	@Override
	public void createStorageClass(CreateStorageClassDTO createStorageClassDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			StorageClassVO storageClassVO = StorageClassVO.dtoToVo(createStorageClassDTO);
			HashMap<String, String> parameters = new HashMap<>();
			parameters.put("server", createStorageClassDTO.getIp());
			parameters.put("share", createStorageClassDTO.getStorageSavePath());
			storageClassVO.setParameters(parameters);

			StorageClass resource = (StorageClass)storageClassVO.createResource();
			client.storage().v1().storageClasses().resource(resource).create();
		}
	}
	@Override
	public boolean storageClassConnectionTest(String storageType) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			ProvisionerType provisionerType = ProvisionerType.valueOf(storageType);
			CSIDriver csiDriver = client.storage()
				.v1()
				.csiDrivers()
				.withName(provisionerType.getProvisionerName())
				.get();
			if (csiDriver != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			StorageClass storageClass = client.storage()
				.v1()
				.storageClasses()
				.withName(storageClassMetaName).get();
			if (storageClass == null || !isControlledByAstra(storageClass.getMetadata().getLabels())) {
				throw new K8sException(StorageErrorCode.STORAGE_NOT_FOUND);
			}
			return StorageClassResDTO.toDTO(storageClass);
		}
	}

	@Override
	public void modifyStorageClassDTO(ModifyStorageClassDTO modifyStorageClassDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			Resource<StorageClass> storageClassResource = client.storage()
				.v1()
				.storageClasses()
				.withName(modifyStorageClassDTO.getStorageClassMetaName());

			if (storageClassResource.get() == null || !isControlledByAstra(
				storageClassResource.get().getMetadata().getLabels())) {
				throw new K8sException(StorageErrorCode.STORAGE_NOT_FOUND);
			}
			storageClassResource.edit(
				s -> new StorageClassBuilder(s).editMetadata()
					.addToAnnotations(AnnotationField.NAME.getField(), modifyStorageClassDTO.getName())
					.addToAnnotations(AnnotationField.DESCRIPTION.getField(), modifyStorageClassDTO.getDescription())
					.endMetadata()
					.build());
		}
	}

	@Override
	public void deleteStorageClass(String storageClassMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.storage()
				.v1()
				.storageClasses()
				.withName(storageClassMetaName)
				.delete();
		}
	}

	@Override
	public List<StorageClassResDTO> findStorageClasses() {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<StorageClass> storageClasses = client.storage()
				.v1()
				.storageClasses()
				.withLabel(LabelField.CONTROL_BY.getField(), "astra")
				.list()
				.getItems();
			return storageClasses.stream().map(StorageClassResDTO::toDTO).collect(Collectors.toList());
		}
	}

	@Override
	public List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes() {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<StorageClassWithVolumesResDTO> storages = client.storage()
				.v1()
				.storageClasses()
				.withLabel(LabelField.CONTROL_BY.getField(), "astra")
				.list()
				.getItems()
				.stream()
				.map(storageClass -> {
					String storageClassMetaName = storageClass.getMetadata().getName();
					List<VolumeDTO> volumeDTOS = client.persistentVolumeClaims()
						.inAnyNamespace()
						.withLabel(LabelField.STORAGE_NAME.getField(), storageClassMetaName)
						.list()
						.getItems()
						.stream()
						.map(VolumeDTO::toDTO)
						.collect(Collectors.toList());

					String storageType = storageClass.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField());
					MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> nfsClient = client.resources(
						HelmRelease.class);

					List<History> crdHistory = nfsClient.inAnyNamespace()
						.withLabel(LabelField.STORAGE_TYPE.getField(), storageType)
						.list().getItems()
						.stream()
						.findFirst()
						.map(helmRelease -> helmRelease.getStatus().getHistory())
						.orElse(Collections.emptyList());

					ProvisionerStatus status = crdHistory.stream()
						.allMatch(history -> history.getStatus().equals("deployed"))
						? ProvisionerStatus.ENABLE
						: ProvisionerStatus.DISABLE;

					return StorageClassWithVolumesResDTO.toDTO(storageClass, volumeDTOS, status);
				}).toList();
			return storages;
		}
	}

	private boolean isControlledByAstra(Map<String, String> map) {
		return map != null && "astra".equals(map.get("control-by"));
	}

	@Override
	public StorageClass createIbmStorage(String secretName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			StorageClass ibmStorageClass = createIbmStorageClass(secretName);
			client.storage()
				.v1()
				.storageClasses()
				.resource(ibmStorageClass)
				.create();
			return ibmStorageClass;
		}
	}

	private StorageClass createIbmStorageClass(String secretName) {
		return new StorageClassBuilder()
			.withNewMetadata()
			.withName("ibm-block-" + UUID.randomUUID())
			.endMetadata()
			.withProvisioner("block.csi.ibm.com")
			.addToParameters("pool", "demo-pool")
			.addToParameters("SpaceEfficiency", "thin")
			.addToParameters("virt_snap_func", "false")
			.addToParameters("csi.storage.k8s.io/fstype", "xfs")
			.addToParameters("csi.storage.k8s.io/secret-name", secretName)
			.addToParameters("csi.storage.k8s.io/secret-namespace", "default")
			.withAllowVolumeExpansion(true)
			.build();

	}

	@Override
	public void deleteIbmStorage(String storageName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.storage()
				.v1()
				.storageClasses()
				.withName(storageName)
				.delete();
		}
	}

	@Override
	public void createServiceAccount(String accountName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			ServiceAccount serviceAccount = new ServiceAccount().toBuilder()
				.withNewMetadata()
				.withName(accountName)
				.withNamespace("astrago")
				.endMetadata()
				.build();
			client.serviceAccounts().inNamespace("astrago").resource(serviceAccount).create();
		}
	}

	@Override
	public void dellPluginInstallCheck() {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			boolean size = client.pods().inNamespace("unity").list().getItems().stream()
				.filter(pod -> !pod.getStatus().getPhase().equals("Running") && pod.getMetadata().getName().contains("csi-unity")).toList().size() > 0;
			if(size){
				throw new RestApiException(STORAGE_INSTALL_WAIT);
			}
		}
	}
}
