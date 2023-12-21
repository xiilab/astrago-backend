package com.xiilab.modulek8s.facade;

import com.xiilab.modulek8s.facade.dto.*;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.provisioner.service.ProvisionerService;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageModuleServiceImpl implements StorageModuleService {
	private final ProvisionerService provisionerService;
	private final VolumeService volumeService;
	private final StorageClassService storageClassService;

	/**
	 * 워크스페이스(namespace)에 볼륨 생성
	 *
	 * @param createVolumeDTO
	 * @return
	 */
	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO) {
		//volume 생성
		volumeService.createVolume(createVolumeDTO);
	}

	/**
	 * 해당 워크스페이스에 스토리지 타입으로 볼륨 리스트 조회
	 *
	 * @param workspaceMetaName
	 * @param storageMetaName
	 * @return
	 */
	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName, String storageMetaName) {
		return volumeService.findVolumesByWorkspaceMetaNameAndStorageMetaName(workspaceMetaName, storageMetaName);
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
	 * @param modifyVolumeDTO
	 */
	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		volumeService.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	/**
	 * 워크스페이스 명과 볼륨 명으로 볼륨 삭제
	 *
	 * @param deleteVolumeDTO
	 */
	@Override
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO) {
		volumeService.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(deleteVolumeDTO);
	}

	/**
	 * 특정 워크스페이스 내 볼륨 리스트 조회 (검색, 페이징 포함)
	 *
	 * @param pageFindVolumeDTO
	 * @return
	 */
	@Override
	public PageResDTO findVolumesWithPagination(PageFindVolumeDTO pageFindVolumeDTO) {
		return volumeService.findVolumesWithPagination(pageFindVolumeDTO);
	}

	/**
	 * 전체 볼륨 리스트 조회(검색조건 포함)
	 *
	 * @param findVolumeDTO
	 * @return
	 */
	@Override
	public List<PageVolumeResDTO> findVolumes(FindVolumeDTO findVolumeDTO) {
		return volumeService.findVolumes(findVolumeDTO);
	}

	/**
	 * 볼륨 상세 조회
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@Override
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName) {
		return volumeService.findVolumeByMetaName(volumeMetaName);
	}

	/**
	 * 볼륨명으로 볼륨 삭제
	 *
	 * @param volumeMetaName
	 */
	@Override
	public void deleteVolumeByMetaName(String volumeMetaName) {
		volumeService.deleteVolumeByMetaName(volumeMetaName);
	}

	/**
	 * 볼룸 수정
	 *
	 * @param modifyVolumeDTO
	 */
	@Override
	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		volumeService.modifyVolume(modifyVolumeDTO);
	}

	@Override
	public void createStorageClass(CreateStorageClassDTO createStorageClassDTO) {
		storageClassService.createStorageClass(createStorageClassDTO);
	}

	@Override
	public boolean storageClassConnectionTest(String storageType) {
		return storageClassService.storageClassConnectionTest(storageType);
	}

	@Override
	public StorageClassResDTO findStorageClassByMetaName(String storageClassMetaName) {
		return storageClassService.findStorageClassByMetaName(storageClassMetaName);
	}

	@Override
	public void modifyStorageClass(ModifyStorageClassDTO modifyStorageClassDTO) {
		storageClassService.modifyStorageClass(modifyStorageClassDTO);
	}

	@Override
	public void deleteStorageClass(String storageClassMetaName) {
		storageClassService.deleteStorageClass(storageClassMetaName);
	}

	@Override
	public List<StorageClassResDTO> findStorageClasses() {
		return storageClassService.findStorageClasses();
	}
}
