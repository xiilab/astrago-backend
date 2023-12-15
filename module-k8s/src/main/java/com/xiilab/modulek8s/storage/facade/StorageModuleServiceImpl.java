package com.xiilab.modulek8s.storage.facade;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.facade.dto.CreateVolumeReqDTO;
import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageModuleServiceImpl implements StorageModuleService{
	private final ProvisionerService provisionerService;
	private final VolumeService volumeService;
	private final StorageClassService storageClassService;

	/**
	 * 워크스페이스(namespace)에 볼륨 생성
	 * @param requestDTO
	 */
	@Override
	public void createVolume(CreateVolumeReqDTO requestDTO){
		//sc type -> sc provisioner 조회
		StorageClass storageClass = storageClassService.findStorageClassByType(requestDTO.getStorageType());
		String storageClassMetaName = storageClass.getMetadata().getName();

		//volume 생성
		CreateVolumeDTO createVolumeDTO = CreateVolumeDTO.storageReqDtoToCreateVolumeDto(requestDTO);
		createVolumeDTO.setStorageClassMetaName(storageClassMetaName);
		volumeService.createVolume(createVolumeDTO);
	}

	/**
	 * 볼륨 단건 조회(해당 볼륨을 사용중인 워크로드 리스트 포함)
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName){
		return volumeService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

}
