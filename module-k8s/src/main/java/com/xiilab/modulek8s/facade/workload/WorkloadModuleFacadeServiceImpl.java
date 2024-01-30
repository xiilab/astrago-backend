package com.xiilab.modulek8s.facade.workload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.service.VolumeService;
import com.xiilab.modulek8s.workload.dto.request.ModuleCreateWorkloadReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.VolumeSelectionType;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.log.service.LogService;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.service.SvcService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadModuleFacadeServiceImpl implements WorkloadModuleFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final VolumeService volumeService;
	private final SvcService svcService;
	private final LogService logService;

	@Override
	public ModuleBatchJobResDTO createBatchJobWorkload(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		// 볼륨 추가
		addNewVolume(moduleCreateWorkloadReqDTO);

		// 잡 생성
		ModuleBatchJobResDTO moduleBatchJobResDTO = workloadModuleService.createBatchJobWorkload(
			moduleCreateWorkloadReqDTO);

		CreateSvcReqDTO createSvcReqDTO = CreateSvcReqDTO.createWorkloadReqDTOToCreateServiceDto(
			moduleCreateWorkloadReqDTO, moduleBatchJobResDTO.getName());

		// 노드포트 연결
		svcService.createNodePortService(createSvcReqDTO);

		return moduleBatchJobResDTO;
	}

	@Override
	public ModuleInteractiveJobResDTO createInteractiveJobWorkload(
		ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		// 볼륨 추가
		addNewVolume(moduleCreateWorkloadReqDTO);

		// 디플로이먼트 생성
		ModuleInteractiveJobResDTO moduleInteractiveJobResDTO = workloadModuleService.createInteractiveJobWorkload(
			moduleCreateWorkloadReqDTO);

		CreateSvcReqDTO createSvcReqDTO = CreateSvcReqDTO.createWorkloadReqDTOToCreateServiceDto(
			moduleCreateWorkloadReqDTO, moduleInteractiveJobResDTO.getName());

		// 노드포트 연결
		svcService.createNodePortService(createSvcReqDTO);

		return moduleInteractiveJobResDTO;
	}

	@Override
	public ModuleBatchJobResDTO getBatchWorkload(String workSpaceName, String workloadName) {
		return workloadModuleService.getBatchJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public ModuleInteractiveJobResDTO getInteractiveWorkload(String workSpaceName, String workloadName) {
		return workloadModuleService.getInteractiveJobWorkload(workSpaceName, workloadName);
	}

	@Override
	public void deleteBatchHobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteBatchJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName) {
		workloadModuleService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
		svcService.deleteService(workSpaceName, workloadName);
	}

	@Override
	public List<ModuleWorkloadResDTO> getWorkloadList(String workSpaceName) {
		List<ModuleWorkloadResDTO> workloadList = new ArrayList<>();
		List<ModuleWorkloadResDTO> jobWorkloadList = workloadModuleService.getBatchJobWorkloadList(workSpaceName);
		List<ModuleWorkloadResDTO> workloadResList = workloadModuleService.getInteractiveJobWorkloadList(workSpaceName);

		if (!jobWorkloadList.isEmpty()) {
			workloadList.addAll(jobWorkloadList);
		}
		if (!workloadResList.isEmpty()) {
			workloadList.addAll(workloadResList);
		}

		return workloadList;
	}

	@Override
	public LogWatch watchLogByWorkload(String workspaceName, String podName) {
		return logService.watchLogByWorkload(workspaceName, podName);
	}
	@Override
	public ExecListenable connectWorkloadTerminal(String workloadName, String workspaceName, WorkloadType workloadType) {
		return workloadModuleService.connectWorkloadTerminal(workloadName, workspaceName, workloadType);

	}

	private void addNewVolume(ModuleCreateWorkloadReqDTO moduleCreateWorkloadReqDTO) {
		List<ModuleVolumeReqDTO> volumes = moduleCreateWorkloadReqDTO.getVolumes();
		for (ModuleVolumeReqDTO volume : volumes) {
			if (volume.getVolumeSelectionType().equals(VolumeSelectionType.NEW)) {
				CreateVolumeDTO createVolumeDTO = CreateVolumeDTO.builder()
					.name(volume.getName())
					.workspaceMetaDataName(moduleCreateWorkloadReqDTO.getWorkspace())
					.storageType(volume.getStorageType())
					.creator(moduleCreateWorkloadReqDTO.getCreator())
					.creatorName(moduleCreateWorkloadReqDTO.getCreatorName())
					.requestVolume(volume.getRequestVolume())
					.storageClassMetaName(volume.getStorageClassMetaName())
					.build();
				String volume1 = createVolume(createVolumeDTO);
				volume.setVolumeMetaDataName(volume1);
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

	@Override
	public Pod getJobPod(String workspaceName, String workloadName, WorkloadType workloadType) {
		return workloadModuleService.getJobPod(workspaceName, workloadName, workloadType);
	}
}
