package com.xiilab.modulek8s.facade.storage;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.FindVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyStorageClassDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.facade.dto.PageFindVolumeDTO;
import com.xiilab.modulek8s.storage.common.dto.PageResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassResDTO;
import com.xiilab.modulek8s.storage.storageclass.dto.response.StorageClassWithVolumesResDTO;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.StorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageModuleServiceImpl implements StorageModuleService{
	private final VolumeService volumeService;
	private final StorageClassService storageClassService;
	private final WorkloadModuleService workloadModuleService;

	/**
	 * 워크스페이스(namespace)에 볼륨 생성
	 *
	 * @param createVolumeDTO
	 * @return
	 */
	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO){
		//volume 생성
		volumeService.createVolume(createVolumeDTO);
	}

	/**
	 * 해당 워크스페이스에 스토리지 타입으로 볼륨 리스트 조회
	 * @param workspaceMetaName
	 * @param storageType
	 * @return
	 */
	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
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
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param modityName
	 */
	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO){
		volumeService.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	/**
	 * 워크스페이스 명과 볼륨 명으로 볼륨 삭제
	 * @param deleteVolumeDTO
	 *
	 */
	@Override
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO){
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
	 * @param volumeMetaName
	 * @return
	 */
	@Override
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName){
		return volumeService.findVolumeByMetaName(volumeMetaName);
	}

	/**
	 * 볼륨명으로 볼륨 삭제
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

	@Override
	public List<StorageClassWithVolumesResDTO> findStorageClassesWithVolumes() {
		return storageClassService.findStorageClassesWithVolumes();
	}

	@Override
	public StorageResDTO createStorage(CreateStorageReqDTO createStorageReqDTO) {
		String pvcName = "astrago-pvc-"+ UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-pv-"+ UUID.randomUUID().toString().substring(6);
		String connectTestDeploymentName = "astrago-deployment-"+ UUID.randomUUID().toString().substring(6);
		String volumeLabelSelectorName = "storage-volume-"+ UUID.randomUUID().toString().substring(6);
		String connectTestLabelName = "connect-test-"+ UUID.randomUUID().toString().substring(6);
		String hostPath = createStorageReqDTO.getHostPath();
		String namespace = createStorageReqDTO.getNamespace();
		String astragoDeploymentName = createStorageReqDTO.getAstragoDeploymentName();
		//pv 생성
		CreatePV createPV = CreatePV.builder()
			.pvName(pvName)
			.pvcName(pvcName)
			.ip(createStorageReqDTO.getIp())
			.storagePath(createStorageReqDTO.getStoragePath())
			.storageType(createStorageReqDTO.getStorageType())
			.requestVolume(createStorageReqDTO.getRequestVolume())
			.namespace(namespace)
			.build();
		volumeService.createPV(createPV);

		//pvc 생성
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(namespace)
			.requestVolume(createStorageReqDTO.getRequestVolume())
			.build();
		volumeService.createPVC(createPVC);

		//connect test pod 생성 후 pvc 연결 테스트
		ConnectTestDTO connectTestDTO = ConnectTestDTO.builder()
			.deploymentName(connectTestDeploymentName)
			.volumeLabelSelectorName(volumeLabelSelectorName)
			.pvcName(pvcName)
			.pvName(pvName)
			.namespace(namespace)
			.connectTestLabelName(connectTestLabelName)
			.hostPath(hostPath)
			.build();
		//connect test deployment 생성
		workloadModuleService.createConnectTestDeployment(connectTestDTO);

		//deployment 상태 조회
		boolean isAvailable = workloadModuleService.IsAvailableTestConnectPod(connectTestLabelName, namespace);

		//connection 실패
		if(!isAvailable){
			//pvc, pv, connect deployment 삭제
			workloadModuleService.deleteConnectTestDeployment(connectTestDeploymentName, namespace);
			volumeService.deletePVC(pvcName, namespace);
			volumeService.deletePV(pvName);
			//연결 실패 응답
			throw new RuntimeException("NFS 스토리지 연결 실패");
		}
		//connection 성공
		//connect deployment 삭제, astrago deployment mount edit
		workloadModuleService.deleteConnectTestDeployment(connectTestDeploymentName, namespace);
		EditAstragoDeployment editAstragoDeployment = EditAstragoDeployment.builder()
			.hostPath(hostPath)
			.pvcName(pvcName)
			.volumeLabelSelectorName(volumeLabelSelectorName)
			.namespace(namespace)
			.astragoDeploymentName(astragoDeploymentName)
			.connectTestLabelName(connectTestLabelName)
			.build();
		workloadModuleService.editAstragoDeployment(editAstragoDeployment);

		return StorageResDTO.builder()
			.storageName(createStorageReqDTO.getStorageName())
			.description(createStorageReqDTO.getDescription())
			.storageType(createStorageReqDTO.getStorageType())
			.ip(createStorageReqDTO.getIp())
			.storagePath(createStorageReqDTO.getStoragePath())
			.namespace(namespace)
			.astragoDeploymentName(astragoDeploymentName)
			.hostPath(hostPath)
			.volumeName(volumeLabelSelectorName)
			.pvName(pvName)
			.pvcName(pvcName)
			.requestVolume(createStorageReqDTO.getRequestVolume())
			.build();
	}

	@Override
	public void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		//astrago deployment에 볼륨 제거
		volumeService.deleteStorage(deleteStorageReqDTO);
		//PVC, PV 삭제
		volumeService.deletePVC(deleteStorageReqDTO.getPvcName(), deleteStorageReqDTO.getNamespace());
		volumeService.deletePV(deleteStorageReqDTO.getPvName());
	}
}
