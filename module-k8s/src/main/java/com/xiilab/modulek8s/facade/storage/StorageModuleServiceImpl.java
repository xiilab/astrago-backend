package com.xiilab.modulek8s.facade.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
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
import com.xiilab.modulek8s.storage.volume.service.K8sVolumeService;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.secret.service.SecretService;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageModuleServiceImpl implements StorageModuleService{
	private final K8sVolumeService k8sVolumeService;
	private final StorageClassService storageClassService;
	private final WorkloadModuleService workloadModuleService;
	private final SecretService secretService;
	private final String unityNamespace = "unity";

	/**
	 * 워크스페이스(namespace)에 볼륨 생성
	 *
	 * @param createVolumeDTO
	 * @return
	 */
	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO){
		//volume 생성
		k8sVolumeService.createVolume(createVolumeDTO);
	}

	/**
	 * 해당 워크스페이스에 스토리지 타입으로 볼륨 리스트 조회
	 * @param workspaceMetaName
	 * @param storageMetaName
	 * @return
	 */
	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
		return k8sVolumeService.findVolumesByWorkspaceMetaNameAndStorageMetaName(workspaceMetaName, storageMetaName);
	}

	/**
	 * 볼륨 단건 조회(해당 볼륨을 사용중인 워크로드 리스트 포함)
	 *
	 * @param volumeMetaName
	 * @return
	 */
	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName){
		return k8sVolumeService.findVolumeWithWorkloadsByMetaName(workspaceMetaName, volumeMetaName);
	}

	/**
	 * 볼륨의 이름 변경
	 * @param modifyVolumeDTO
	 */
	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO){
		k8sVolumeService.modifyVolumeByMetaName(modifyVolumeDTO);
	}

	/**
	 * 워크스페이스 명과 볼륨 명으로 볼륨 삭제
	 * @param deleteVolumeDTO
	 *
	 */
	@Override
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO){
		k8sVolumeService.deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(deleteVolumeDTO);
	}

	/**
	 * 특정 워크스페이스 내 볼륨 리스트 조회 (검색, 페이징 포함)
	 *
	 * @param pageFindVolumeDTO
	 * @return
	 */
	@Override
	public PageResDTO findVolumesWithPagination(PageFindVolumeDTO pageFindVolumeDTO) {
		return k8sVolumeService.findVolumesWithPagination(pageFindVolumeDTO);
	}

	/**
	 * 전체 볼륨 리스트 조회(검색조건 포함)
	 *
	 * @param findVolumeDTO
	 * @return
	 */
	@Override
	public List<PageVolumeResDTO> findVolumes(FindVolumeDTO findVolumeDTO) {
		return k8sVolumeService.findVolumes(findVolumeDTO);
	}

	/**
	 * 볼륨 상세 조회
	 * @param volumeMetaName
	 * @return
	 */
	@Override
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName){
		return k8sVolumeService.findVolumeByMetaName(volumeMetaName);
	}

	/**
	 * 볼륨명으로 볼륨 삭제
	 * @param volumeMetaName
	 */
	@Override
	public void deleteVolumeByMetaName(String volumeMetaName) {
		k8sVolumeService.deleteVolumeByMetaName(volumeMetaName);
	}

	/**
	 * 볼룸 수정
	 *
	 * @param modifyVolumeDTO
	 */
	@Override
	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		k8sVolumeService.modifyVolume(modifyVolumeDTO);
	}

	@Override
	public void createStorageClass(CreateStorageClassDTO createStorageClassDTO) {
		storageClassService.createStorageClass(createStorageClassDTO);
	}

	@Override
	public StorageResDTO createDELLStorage(CreateStorageReqDTO createStorageReqDTO) {
		// install check
		storageClassService.dellPluginInstallCheck();
		// storageClass 생성
		String storageName = "dell-storage-"+ UUID.randomUUID().toString().substring(6);
		// pv 생성
		String pvName = "dell-unity-pv-"+ UUID.randomUUID().toString().substring(6);
		String pvcName = "dell-unity-pvc-"+ UUID.randomUUID().toString().substring(6);
		k8sVolumeService.createDellPV(pvName, pvcName, createStorageReqDTO.getArrayId().toLowerCase(), createStorageReqDTO.getDellVolumeId());
		// pvc 생성
		k8sVolumeService.createDellPVC(pvcName, storageName);
		// deployment에 연결 테스트
		String connectTestDeploymentName = "astrago-storage-deployment-"+ UUID.randomUUID().toString().substring(6);
		String connectTestLabelName = "connect-test-"+ UUID.randomUUID().toString().substring(6);
		workloadModuleService.createConnectTestDeployment(connectTestDeploymentName, connectTestLabelName, pvcName);
		connectTestDeployment(createStorageReqDTO, connectTestLabelName, connectTestDeploymentName, storageName, pvcName);
		// 성공하면 deployment 삭제
		workloadModuleService.deleteConnectTestDeployment(connectTestDeploymentName, createStorageReqDTO.getNamespace());
		// deployment 삭제 대기
		try {
			Thread.sleep(7000);
		}catch (InterruptedException e) {
			throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
		}
		// core 연결
		StorageResDTO deployment = workloadModuleService.editAstragoDeployment(createStorageReqDTO, pvcName);

		return StorageResDTO.builder()
			.storageName(createStorageReqDTO.getStorageName())
			.description(createStorageReqDTO.getDescription())
			.storageType(createStorageReqDTO.getStorageType())
			.ip(createStorageReqDTO.getIp())
			.storagePath(createStorageReqDTO.getStoragePath())
			.namespace(createStorageReqDTO.getNamespace())
			.astragoDeploymentName(deployment.getAstragoDeploymentName())
			.pvcName(pvcName)
			.requestVolume(createStorageReqDTO.getRequestVolume())
			.volumeName(deployment.getVolumeName())
			.storageClassName(storageName)
			.hostPath(createStorageReqDTO.getHostPath())
			.build();
	}

	private void connectTestDeployment(CreateStorageReqDTO createStorageReqDTO, String connectTestLabelName,
		String connectTestDeploymentName, String storageName, String pvcName) {
		//deployment 상태 조회 - 컨테이너 실행 시간 대기
		int failCount = 0;
		boolean isAvailable = workloadModuleService.isAvailableTestConnectPod(connectTestLabelName, createStorageReqDTO.getNamespace());
		//connection 실패
		if(!isAvailable){
			while(failCount < 15){
				try {
					Thread.sleep(5000);
					failCount++;
					isAvailable = workloadModuleService.isAvailableTestConnectPod(connectTestLabelName, createStorageReqDTO.getNamespace());
					if(isAvailable){
						break;
					}
				} catch (InterruptedException e) {
					throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
				}
			}if(!isAvailable){
				//pvc, pv, connect deployment 삭제
				workloadModuleService.deleteConnectTestDeployment(connectTestDeploymentName, createStorageReqDTO.getNamespace());
				storageClassService.deleteStorageClass(storageName);
				k8sVolumeService.deletePVC(pvcName, createStorageReqDTO.getNamespace());
				//연결 실패 응답
				throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
			}
		}
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
		String pvcName = "astrago-storage-pvc-"+ UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-storage-pv-"+ UUID.randomUUID().toString().substring(6);
		String connectTestDeploymentName = "astrago-storage-deployment-"+ UUID.randomUUID().toString().substring(6);
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
		k8sVolumeService.createPV(createPV);

		//pvc 생성
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(namespace)
			.requestVolume(createStorageReqDTO.getRequestVolume())
			.build();
		k8sVolumeService.createPVC(createPVC);

		//connect test pod 생성 후 pvc 연결 테스트
		ConnectTestDTO connectTestDTO = ConnectTestDTO.builder()
			.deploymentName(connectTestDeploymentName)
			.volumeLabelSelectorName(volumeLabelSelectorName)
			.pvcName(pvcName)
			.pvName(pvName)
			.namespace(namespace)
			.connectTestLabelName(connectTestLabelName)
			.hostPath(hostPath)
			// .dockerImage("xiilab/astrago-dataset-nginx")
			.dockerImage(createStorageReqDTO.getConnectionTestImageUrl())
			.build();
		//connect test deployment 생성
		workloadModuleService.createConnectTestDeployment(connectTestDTO);
		try {
			Thread.sleep(5000);
		}catch (InterruptedException e) {
			throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
		}

		//deployment 상태 조회 - 컨테이너 실행 시간 대기
		int failCount = 0;

		boolean isAvailable = workloadModuleService.isAvailableTestConnectPod(connectTestLabelName, namespace);
		//connection 실패
		if(!isAvailable){
			while(failCount < 5){
				try {
					Thread.sleep(2000);
					failCount++;
					isAvailable = workloadModuleService.isAvailableTestConnectPod(connectTestLabelName, namespace);
					if(isAvailable){
						break;
					}
				} catch (InterruptedException e) {
					throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
				}
			}if(!isAvailable){
				//pvc, pv, connect deployment 삭제
				workloadModuleService.deleteConnectTestDeployment(connectTestDeploymentName, namespace);
				k8sVolumeService.deletePVC(pvcName, namespace);
				k8sVolumeService.deletePV(pvName);
				//연결 실패 응답
				throw new K8sException(StorageErrorCode.STORAGE_CONNECTION_FAILED);
			}
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
		if(deleteStorageReqDTO.getStorageType() == StorageType.IBM){
			// pvc 삭제
			k8sVolumeService.deleteIbmPvc(deleteStorageReqDTO.getPvcName());
			// storage 삭제
			storageClassService.deleteIbmStorage(deleteStorageReqDTO.getStorageName());
			// secret 삭제
			secretService.deleteIbmSecret(deleteStorageReqDTO.getSecretName());
		} else if (deleteStorageReqDTO.getStorageType() == StorageType.DELL_UNITY) {
			k8sVolumeService.deleteDellStorage(deleteStorageReqDTO);
			//PVC 삭제
			k8sVolumeService.deletePVC(deleteStorageReqDTO.getPvcName(), deleteStorageReqDTO.getNamespace());
			// Storage Class 삭제
			k8sVolumeService.deleteStorageClass(deleteStorageReqDTO);
		} else{
			//astrago deployment에 볼륨 제거
			k8sVolumeService.deleteStorage(deleteStorageReqDTO);
			//PVC, PV 삭제
			k8sVolumeService.deletePVC(deleteStorageReqDTO.getPvcName(), deleteStorageReqDTO.getNamespace());
			k8sVolumeService.deletePV(deleteStorageReqDTO.getPvName());
		}
	}

	@Override
	public void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> mounts) {
		List<String> deploymentVolumeNames = k8sVolumeService.getAstragoVolumes();
		List<AstragoDeploymentConnectPVC> missingPVCs = getMissingPVCs(mounts, deploymentVolumeNames);
		k8sVolumeService.astragoCoreDeploymentConnectPVC(missingPVCs);
	}

	public static List<AstragoDeploymentConnectPVC> getMissingPVCs(List<AstragoDeploymentConnectPVC> astragoDeploymentPVCs, List<String> deploymentVolumeNames) {
		// deploymentVolumeNames에 있는 이름들을 Set으로 변환
		List<String> deploymentVolumeNamesCopy = new ArrayList<>(deploymentVolumeNames);
		List<AstragoDeploymentConnectPVC> missingPVCs = new ArrayList<>();

		// storageEntities를 iteration하며 deploymentVolumeNames에 있는 이름을 제거
		for (AstragoDeploymentConnectPVC storageEntity : astragoDeploymentPVCs) {
			if (deploymentVolumeNamesCopy.contains(storageEntity.getVolumeName())) {
				deploymentVolumeNamesCopy.remove(storageEntity.getVolumeName());
			} else {
				// deploymentVolumeNames에 없는 이름을 가진 storageEntity를 missingEntities에 추가
				missingPVCs.add(storageEntity);
			}
		}
		return missingPVCs;
	}

	@Override
	public StorageClass createIbmStorage(String secretName){
		return storageClassService.createIbmStorage(secretName);
	}

	@Override
	public PersistentVolumeClaim createIbmPvc(String storageName){
		return k8sVolumeService.createIbmPvc(storageName);
	}
}
