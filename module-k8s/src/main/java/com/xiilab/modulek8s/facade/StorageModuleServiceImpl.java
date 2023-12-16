package com.xiilab.modulek8s.facade;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
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
	 * @param createVolumeDTO
	 */
	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO){
		//sc type -> sc provisioner 조회
		StorageClass storageClass = storageClassService.findStorageClassByType(createVolumeDTO.getStorageType());
		String storageClassMetaName = storageClass.getMetadata().getName();

		//volume 생성
		CreateDTO createDTO = CreateDTO.createVolumeDtoToCreateDto(createVolumeDTO);
		createDTO.setStorageClassMetaName(storageClassMetaName);
		volumeService.createVolume(createDTO);
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

	/**
	 * 볼륨의 이름 변경
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param modityName
	 */
	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO){
		volumeService.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	/**
	 * 볼륨 삭제
	 * @param deleteVolumeDTO
	 */
	@Override
	public void deleteVolumeByMetaName(DeleteVolumeDTO deleteVolumeDTO){
		volumeService.deleteVolumeByMetaName(deleteVolumeDTO);
	}
}
