package com.xiilab.modulek8s.storage.facade;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.facade.dto.StorageReqDTO;
import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {
	private final ProvisionerService provisionerService;
	private final VolumeService volumeService;
	private final StorageClassService storageClassService;

	public void createVolume(StorageReqDTO requestDTO){
		//sc type -> sc provisioner 조회
		StorageClass storageClass = storageClassService.findStorageClassByType(requestDTO.getStorageType());
		String storageClassMetaName = storageClass.getMetadata().getName();

		//volume 생성
		CreateVolumeDTO createVolumeDTO = CreateVolumeDTO.storageReqDtoToCreateVolumeDto(requestDTO);
		createVolumeDTO.setStorageClassMetaName(storageClassMetaName);
		volumeService.createVolume(createVolumeDTO);
	}


}
