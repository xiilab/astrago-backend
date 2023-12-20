package com.xiilab.modulek8s.storage.storageclass.repository;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerType;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.vo.StorageClassVO;

import io.fabric8.kubernetes.api.model.storage.CSIDriver;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StorageClassRepositoryImpl implements StorageClassRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public StorageClass findStorageClassByType(StorageType storageType) {
		try(final KubernetesClient client = k8sAdapter.configServer()){
			return client.storage().v1().storageClasses().withLabel(LabelField.STORAGE_TYPE.getField(), storageType.name()).list().getItems().get(0);
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
		try(final KubernetesClient client = k8sAdapter.configServer()){
			ProvisionerType provisionerType = ProvisionerType.valueOf(storageType);
			CSIDriver csiDriver = client.storage()
				.v1()
				.csiDrivers()
				.withName(provisionerType.getProvisionerName())
				.get();
			if(csiDriver != null){
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
			return StorageClassResDTO.toDTO(storageClass);
		}
	}

	@Override
	public void modifyStorageClassDTO(ModifyStorageClassDTO modifyStorageClassDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.storage()
				.v1()
				.storageClasses()
				.withName(modifyStorageClassDTO.getStorageClassMetaName())
				.edit(
					s -> new StorageClassBuilder(s).editMetadata()
						.addToAnnotations(AnnotationField.NAME.getField(), modifyStorageClassDTO.getName())
						.addToAnnotations(AnnotationField.DESCRIPTION.getField(), modifyStorageClassDTO.getDescription())
						.endMetadata()
						.build());
		}
	}

}
