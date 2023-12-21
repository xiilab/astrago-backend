package com.xiilab.modulek8s.facade.workload;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.service.dto.request.CreateServiceDTO;
import com.xiilab.modulek8s.service.enums.ServiceType;
import com.xiilab.modulek8s.service.service.ServiceService;
import com.xiilab.modulek8s.storage.storageclass.service.StorageClassService;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.VolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.service.WorkloadService;

import io.fabric8.kubernetes.api.model.storage.StorageClass;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadModuleFacadeServiceImpl implements WorkloadModuleFacadeService {
	private final WorkloadService workloadService;
	private final VolumeService volumeService;
	private final StorageClassService storageClassService;
	private final ServiceService serviceService;

	@Override
	public JobResDTO createBatchJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO) {
		// 볼륨 추가
		addNewVolume(createWorkloadReqDTO);

		// 잡 생성
		JobResDTO jobResDTO = workloadService.createBatchJobWorkload(createWorkloadReqDTO);

		CreateServiceDTO createServiceDTO = CreateServiceDTO.CreateWorkloadReqDTOToCreateServiceDto(
			createWorkloadReqDTO, jobResDTO.getName());
		// 노드포트 연결
		serviceService.createService(createServiceDTO);

		return jobResDTO;
	}

	@Override
	public JobResDTO createInteractiveJobWorkload(CreateWorkloadReqDTO createWorkloadReqDTO) {
		return null;
	}

	private void addNewVolume(CreateWorkloadReqDTO createWorkloadReqDTO) {
		List<VolumeReqDTO> volumes = createWorkloadReqDTO.getVolumes();
		for (VolumeReqDTO volume : volumes) {
			if (volume.volumeSelectionType().equals(VolumeSelectionType.NEW)) {
				CreateVolumeDTO createVolumeDTO = CreateVolumeDTO.builder()
					.name(volume.name())
					.workspaceMetaDataName(createWorkloadReqDTO.getWorkspace())
					.storageType(volume.storageType())
					.creator(createWorkloadReqDTO.getCreator())
					.creatorName(createWorkloadReqDTO.getCreatorName())
					.requestVolume(volume.requestVolume())
					.build();
				createVolume(createVolumeDTO);
			}
		}
	}

	/**
	 * TODO 스토리지 파사드 수정될때마다 같이 수정돼야함 (문제해결필요)
	 * 워크스페이스(namespace)에 볼륨 생성
	 * @param createVolumeReqDTO
	 */
	private void createVolume(CreateVolumeDTO createVolumeReqDTO) {
		//sc type -> sc provisioner 조회
		StorageClass storageClass = storageClassService.findStorageClassByType(createVolumeReqDTO.getStorageType());
		String storageClassMetaName = storageClass.getMetadata().getName();

		//volume 생성
		com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO createVolumeDTO = com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO.storageReqDtoToCreateVolumeDto(
			createVolumeReqDTO);
		createVolumeDTO.setStorageClassMetaName(storageClassMetaName);
		volumeService.createVolume(createVolumeDTO);
	}
}
