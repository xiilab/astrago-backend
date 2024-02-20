package com.xiilab.servercore.workload.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulealert.dto.AlertDTO;
import com.xiilab.modulealert.enumeration.AlertMessage;
import com.xiilab.modulealert.enumeration.AlertType;
import com.xiilab.modulealert.service.AlertService;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.workload.dto.request.ModuleCodeReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleImageReqDTO;
import com.xiilab.modulek8s.workload.dto.request.ModuleVolumeReqDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.servercore.credential.dto.CredentialResDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.credential.service.CredentialService;
import com.xiilab.servercore.dataset.dto.DatasetDTO;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workload.dto.request.CreateWorkloadJobReqDTO;
import com.xiilab.servercore.workload.enumeration.WorkloadSortCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final PinService pinService;
	private final AlertService alertService;
	private final DatasetService datasetService;
	private final WorkloadHistoryService workloadHistoryService;
	private final CredentialService credentialService;

	public void createWorkload(CreateWorkloadJobReqDTO moduleCreateWorkloadReqDTO, UserInfoDTO userInfoDTO) {
		moduleCreateWorkloadReqDTO.setUserInfo(userInfoDTO.getId(), userInfoDTO.getUserName(),
			userInfoDTO.getUserFullName());

		// 이미지 credential 세팅
		if (!ObjectUtils.isEmpty(moduleCreateWorkloadReqDTO.getImage().getCredentialId()) &&  moduleCreateWorkloadReqDTO.getImage().getCredentialId() > 0) {
			setImageCredentialReqDTO(moduleCreateWorkloadReqDTO.getImage(), userInfoDTO);
		}

		// 코드 credential 세팅
		if (!CollectionUtils.isEmpty(moduleCreateWorkloadReqDTO.getCodes())) {
			setCodeCredentialReqDTO(moduleCreateWorkloadReqDTO.getCodes());
		}

		// 데이터셋 볼륨 추가
		if (!CollectionUtils.isEmpty(moduleCreateWorkloadReqDTO.getDatasets())) {
			setVolume(moduleCreateWorkloadReqDTO.getWorkspace(), moduleCreateWorkloadReqDTO.getDatasets());
		}

		// 모델 볼륨 추가
		if (!CollectionUtils.isEmpty(moduleCreateWorkloadReqDTO.getModels())) {
			setVolume(moduleCreateWorkloadReqDTO.getWorkspace(), moduleCreateWorkloadReqDTO.getModels());
		}

		workloadModuleFacadeService.createJobWorkload(moduleCreateWorkloadReqDTO.toModuleDTO());

		// 워크로드 생성 알림
		alertService.sendAlert(AlertDTO.builder()
			.recipientId(userInfoDTO.getId())
			.senderId("SYSTEM")
			.alertType(AlertType.WORKLOAD)
			.message(String.format(AlertMessage.CREATE_WORKLOAD.getMessage(), moduleCreateWorkloadReqDTO.getName()))
			.build());
	}

	private void setCodeCredentialReqDTO(List<ModuleCodeReqDTO> codes) {
		// 코드 목록에 있는 크레덴셜 ID만 추출
		List<Long> credentialIds = codes.stream()
			.map(ModuleCodeReqDTO::getCredentialId)
			.filter(credentialId -> credentialId != null && credentialId > 0)
			.toList();
		if (CollectionUtils.isEmpty(credentialIds)) {
			return ;
		}

		// 크레덴셜 목록 조회
		CredentialResDTO.CredentialInfos credentialInfos = credentialService.findCredentialByIdIn(credentialIds,
			PageRequest.of(1, 9999));
		Map<Long, CredentialResDTO.CredentialInfo> credentialInfoMap = converListToMap(
			!CollectionUtils.isEmpty(credentialInfos.getDatasets()) ? credentialInfos.getDatasets() :
				new ArrayList<>());

		codes.forEach(moduleCodeReqDTO -> {
			Long credentialId = moduleCodeReqDTO.getCredentialId();
			if (!ObjectUtils.isEmpty(credentialId) && credentialInfoMap.containsKey(credentialId)) {
				CredentialResDTO.CredentialInfo credentialInfo = credentialInfoMap.get(
					moduleCodeReqDTO.getCredentialId());
				moduleCodeReqDTO.setCredentialReqDTO(credentialInfo.toModuleCredentialReqDTO());
			}
		});
	}

	private Map<Long, CredentialResDTO.CredentialInfo> converListToMap(List<CredentialResDTO.CredentialInfo> datasets) {
		return datasets
			.stream()
			.collect(Collectors.toMap(
				CredentialResDTO::getId,
				credentialInfo -> credentialInfo
			));
	}

	private void setImageCredentialReqDTO(ModuleImageReqDTO moduleImageReqDTO, UserInfoDTO userInfoDTO) {
		// ModuleImageReqDTO imageReqDTO = moduleCreateWorkloadReqDTO.getImage();
		CredentialResDTO.CredentialInfo findCredential = credentialService.findCredentialById(
			moduleImageReqDTO.getCredentialId(),
			userInfoDTO);
		moduleImageReqDTO.setCredentialReqDTO(findCredential.toModuleCredentialReqDTO());
	}

	public ModuleWorkloadResDTO getWorkloadInfoByResourceName(String workspaceName, String resourceName,
		WorkloadType workloadType) {
		if (workloadType == WorkloadType.BATCH) {
			return workloadModuleFacadeService.getBatchWorkload(workspaceName, resourceName);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			return workloadModuleFacadeService.getInteractiveWorkload(workspaceName, resourceName);
		} else {
			return null;
		}
	}

	public void updateWorkload(String workloadName, WorkloadType workloadType, UserInfoDTO userInfoDTO) {
		if (workloadType == WorkloadType.BATCH) {

		} else if (workloadType == WorkloadType.INTERACTIVE) {

		}
	}

	public void stopWorkload(String workspaceName, String workloadName, WorkloadType workloadType,
		UserInfoDTO userInfoDTO
	) throws IOException {
		if (workloadType == WorkloadType.BATCH) {
			stopBatchHobWorkload(workspaceName, workloadName, userInfoDTO);
		} else if (workloadType == WorkloadType.INTERACTIVE) {
			stopInteractiveJobWorkload(workspaceName, workloadName, userInfoDTO);
		}
	}

	public void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO) {
		ModuleWorkloadResDTO workloadHistory = workloadHistoryService.getWorkloadHistoryById(id);
		workloadHistoryService.deleteWorkloadHistory(id, userInfoDTO);
		//해당 워크로드를 등록한 모든 Pin 삭제
		pinService.deletePin(workloadHistory.getResourceName(), PinType.WORKLOAD);
	}

	public PageDTO<ModuleWorkloadResDTO> getOverViewWorkloadList(WorkloadType workloadType, String workspaceName,
		String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, int pageNum,
		UserInfoDTO userInfoDTO) {
		//통합용 리스트 선언
		List<ModuleWorkloadResDTO> workloadResDTOList = new ArrayList<>();
		if (workloadType == WorkloadType.BATCH) {
			//k8s cluster에 생성되어있는 batchJob list
			List<ModuleBatchJobResDTO> batchJobListFromCluster = workloadModuleService.getBatchWorkloadListByCondition(
				workspaceName, userInfoDTO.getId());
			//종료된 batchJob list
			List<ModuleBatchJobResDTO> batchWorkloadHistoryList = workloadHistoryService.getBatchWorkloadHistoryList(
				workspaceName,
				null, userInfoDTO.getId());
			workloadResDTOList.addAll(batchJobListFromCluster);
			workloadResDTOList.addAll(batchWorkloadHistoryList);
		} else {
			//k8s cluster에서 생성되어있는 interactive job list 조회
			List<ModuleInteractiveJobResDTO> interactiveJobFromCluster = workloadModuleService.getInteractiveWorkloadListByCondition(
				workspaceName, userInfoDTO.getId());
			//종료된 interactive job list 조회
			List<ModuleInteractiveJobResDTO> interactiveWorkloadHistoryList = workloadHistoryService.getInteractiveWorkloadHistoryList(
				workspaceName, null, userInfoDTO.getId());
			workloadResDTOList.addAll(interactiveJobFromCluster);
			workloadResDTOList.addAll(interactiveWorkloadHistoryList);
		}
		//핀 워크로드 목록 필터링
		List<ModuleWorkloadResDTO> pinWorkloadList = filterAndMarkPinnedWorkloads(workloadResDTOList,
			userInfoDTO.getId());
		//일반 워크로드 목록 필터링
		List<ModuleWorkloadResDTO> normalWorkloadList = filterNormalWorkloads(workloadResDTOList,
			searchName, workloadStatus, workloadSortCondition, userInfoDTO.getId());
		return new PageDTO<>(pinWorkloadList, normalWorkloadList, pageNum, 10);
	}

	private List<ModuleWorkloadResDTO> filterNormalWorkloads(List<ModuleWorkloadResDTO> workloadList, String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition workloadSortCondition, String userId) {
		// 사용자가 추가한 PIN 목록을 가져옵니다.
		Set<String> userWorkloadPinList = getUserWorkloadPinList(userId);
		// PIN이 없는 워크로드를 필터링합니다.
		List<ModuleWorkloadResDTO> normalWorkloadList = filterPinnedWorkloads(workloadList, userWorkloadPinList,
			false);
		//필터링 및 정렬 적용
		return applyWorkloadListCondition(normalWorkloadList, searchName, workloadStatus, workloadSortCondition);
	}

	/**
	 * 워크로드 목록을 사용자가 추가한 PIN에 따라 필터링하고, PINYN을 업데이트한 후 반환합니다.
	 *
	 * @param workloadList 워크로드 목록
	 * @param userId       사용자 ID
	 * @return PIN에 따라 필터링된 작업량 목록
	 */
	private List<ModuleWorkloadResDTO> filterAndMarkPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList,
		String userId) {
		// 사용자가 추가한 PIN 목록을 가져옵니다.
		Set<String> userWorkloadPinList = getUserWorkloadPinList(userId);
		// PIN에 따라 작업량을 필터링합니다.
		List<ModuleWorkloadResDTO> pinWorkloadList = filterPinnedWorkloads(workloadList, userWorkloadPinList, true);
		// 필터링된 작업량의 PINYN을 업데이트합니다.
		markPinnedWorkloads(pinWorkloadList);
		return pinWorkloadList;
	}

	/**
	 * 사용자가 추가한 PIN 목록을 가져옵니다.
	 *
	 * @param userId 사용자 ID
	 * @return 사용자가 추가한 PIN 목록
	 */
	private Set<String> getUserWorkloadPinList(String userId) {
		return pinService.getUserWorkloadPinList(userId);
	}

	/**
	 * 작업량 목록을 사용자가 추가한 PIN에 따라 필터링하여 반환합니다.
	 *
	 * @param workloadList        작업량 목록
	 * @param userWorkloadPinList 사용자가 추가한 PIN 목록
	 * @param pinFilterCondition  pin여부에 따라 필터링 할지에 대한 flag 값
	 * @return PIN에 따라 필터링된 작업량 목록
	 */
	private List<ModuleWorkloadResDTO> filterPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList,
		Set<String> userWorkloadPinList, boolean pinFilterCondition) {
		return workloadList.stream()
			.filter(workload -> pinFilterCondition == userWorkloadPinList.contains(workload.getResourceName()))
			.toList();
	}

	/**
	 * 작업량 목록의 PINYN을 업데이트합니다.
	 *
	 * @param workloadList 작업량 목록
	 */
	private void markPinnedWorkloads(List<ModuleWorkloadResDTO> workloadList) {
		workloadList.forEach(workload -> workload.updatePinYN(true));
	}

	private List<ModuleWorkloadResDTO> applyWorkloadListCondition(List<ModuleWorkloadResDTO> workloadList,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition) {

		Stream<ModuleWorkloadResDTO> workloadStream = workloadList.stream()
			.filter(batch -> searchName == null || (batch.getName() != null && batch.getName().contains(searchName)))
			.filter(batch -> workloadStatus == null || batch.getStatus() == workloadStatus);

		if (sortCondition != null) {
			return switch (sortCondition) {
				case AGE_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt)).toList();
				case AGE_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt).reversed()).toList();
				case REMAIN_TIME_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getRemainTime)).toList();
				case REMAIN_TIME_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleWorkloadResDTO::getRemainTime).reversed())
						.toList();
			};
		} else {
			return workloadStream.toList();
		}
	}

	private void stopBatchHobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
			WorkloadType.BATCH);
		FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		workloadModuleFacadeService.deleteBatchHobWorkload(workSpaceName, workloadName);
		// 워크로드 삭제 알림
		alertService.sendAlert(AlertDTO.builder()
			.recipientId(userInfoDTO.getId())
			.senderId("SYSTEM")
			.alertType(AlertType.WORKLOAD)
			.message(String.format(AlertMessage.DELETE_WORKLOAD.getMessage(), workloadName))
			.build());
	}

	private void stopInteractiveJobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
			WorkloadType.INTERACTIVE);
		FileUtils.saveLogFile(log, workloadName, userInfoDTO.getId());
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
		// 워크로드 삭제 알림
		alertService.sendAlert(AlertDTO.builder()
			.recipientId(userInfoDTO.getId())
			.senderId("SYSTEM")
			.alertType(AlertType.WORKLOAD)
			.message(String.format(AlertMessage.DELETE_WORKLOAD.getMessage(), workloadName))
			.build());
	}

	private void setVolume(String workspaceName, List<ModuleVolumeReqDTO> list) {
		for (ModuleVolumeReqDTO reqDto : list) {
			setCreatePVAndPVC(workspaceName, reqDto);
		}
	}

	private void setCreatePVAndPVC(String workspaceName, ModuleVolumeReqDTO moduleVolumeReqDTO) {
		Dataset findDataset = datasetService.findById(moduleVolumeReqDTO.getId());
		DatasetDTO.ResDatasetWithStorage resDatasetWithStorage = DatasetDTO.ResDatasetWithStorage.toDto(
			findDataset);

		String pvcName = "astrago-storage-pvc-" + UUID.randomUUID().toString().substring(6);
		String pvName = "astrago-storage-pv-" + UUID.randomUUID().toString().substring(6);
		String ip = resDatasetWithStorage.getIp();
		String storagePath = resDatasetWithStorage.getStoragePath();
		StorageType storageType = resDatasetWithStorage.getStorageType();
		int requestVolume = 50;

		// PV 생성
		CreatePV createPV = CreatePV.builder()
			.pvcName(pvcName)
			.pvName(pvName)
			.ip(ip)
			.storagePath(storagePath)
			.namespace(workspaceName)
			.storageType(storageType)
			.requestVolume(requestVolume)
			.build();
		moduleVolumeReqDTO.setCreatePV(createPV);
		CreatePVC createPVC = CreatePVC.builder()
			.pvcName(pvcName)
			.namespace(workspaceName)
			.requestVolume(requestVolume)
			.build();
		moduleVolumeReqDTO.setCreatePVC(createPVC);
	}

}
