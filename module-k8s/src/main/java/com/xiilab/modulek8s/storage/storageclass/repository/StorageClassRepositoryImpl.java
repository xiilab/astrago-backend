package com.xiilab.modulek8s.storage.storageclass.repository;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassRepository;
import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
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
}
