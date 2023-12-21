package com.xiilab.modulek8s.facade.workload;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.service.dto.request.CreateServiceDTO;
import com.xiilab.modulek8s.service.service.ServiceService;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.CreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.VolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.JobResDTO;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkloadModuleFacadeServiceImpl implements WorkloadModuleFacadeService {
	private final WorkloadService workloadService;
	private final VolumeService volumeService;
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
			if (volume.getVolumeSelectionType().equals(VolumeSelectionType.NEW)) {
				CreateVolumeDTO createVolumeDTO = CreateVolumeDTO.builder()
						.name(volume.getName())
					.workspaceMetaDataName(createWorkloadReqDTO.getWorkspace())
						.storageType(volume.getStorageType())
					.creator(createWorkloadReqDTO.getCreator())
					.creatorName(createWorkloadReqDTO.getCreatorName())
						.requestVolume(volume.getRequestVolume())
						.storageClassMetaName(volume.getStorageClassMetaName())
					.build();
				volume.setVolumeMetaDataName(createVolume(createVolumeDTO));
			}
		}
	}

	/**
	 * TODO 스토리지 파사드 수정될때마다 같이 수정돼야함 (문제해결필요)
	 * 워크스페이스(namespace)에 볼륨 생성
	 * @param createVolumeDTO
	 */
	private String createVolume(CreateVolumeDTO createVolumeDTO) {
		return volumeService.createVolume(createVolumeDTO);
	}
}
