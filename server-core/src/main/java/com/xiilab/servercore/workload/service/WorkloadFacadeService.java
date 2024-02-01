package com.xiilab.servercore.workload.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8s.workload.service.WorkloadModuleService;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workload.enumeration.WorkloadSortCondition;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadFacadeService {
	private final WorkloadModuleService workloadModuleService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final WorkspaceService workspaceService;
	private final PinService pinService;

	public PageDTO getWorkloadListByCondition(WorkloadType workloadType,
		String workspaceName,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition, int pageNum,
		UserInfoDTO userInfoDTO) {
		if (workloadType == WorkloadType.BATCH) {
			return getBatchWorkloadByCondition(workspaceName, searchName, workloadStatus, sortCondition, pageNum,
				userInfoDTO);
		} else {
			return getInteractiveWorkloadByCondition(workspaceName, searchName, workloadStatus, sortCondition, pageNum,
				userInfoDTO);
		}
	}

	public PageDTO<ModuleBatchJobResDTO> getBatchWorkloadByCondition(String workspaceName, String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition, int pageNum, UserInfoDTO userInfoDTO) {
		WorkspaceDTO.ResponseDTO workspace = workspaceService.getWorkspaceByName(workspaceName);
		//통합용 리스트 선언
		List<ModuleBatchJobResDTO> totalJobList = new ArrayList<>();
		//DB에 저장되어있는 유저가 추가한 PIN 목록
		Set<String> userWorkloadPinList = pinService.getUserWorkloadPinList(userInfoDTO.getId(), workspaceName);
		//특정 워크스페이스의 워크로드
		List<ModuleBatchJobResDTO> batchJobWorkloadList = workloadModuleService.getBatchJobWorkloadList(workspaceName);

		// pin list filtering
		List<ModuleBatchJobResDTO> pinList = batchJobWorkloadList.stream()
			.filter(batch -> userWorkloadPinList.contains(batch.getResourceName())).toList();
		//pin list에 대한 filtering sorting 검색
		pinList = applyBatchWorkloadListCondition(pinList, searchName,
			workloadStatus, sortCondition);

		// 워크로드 일반 리스트를 가져옴
		List<ModuleBatchJobResDTO> normalList = batchJobWorkloadList.stream()
			.filter(batch -> !userWorkloadPinList.contains(batch.getUid()))
			.toList();
		//일반 리스트 대한 filtering sorting 검색
		normalList = applyBatchWorkloadListCondition(normalList, searchName, workloadStatus, sortCondition);
		//총 목록 - pin 리스트 개수
		int normalListPageSize = getNormalListPageSize(pinList.size());
		//총 페이지 개수
		int totalPageSize = (int)Math.ceil(normalList.size() / (double)normalListPageSize);
		//실질적인 페이지네이션 후의 리스트
		List<ModuleBatchJobResDTO> normalPagingList = getPaginatedList(normalList, pageNum, normalListPageSize);
		//TODO 종료된 workload list 조회 및 추가

		if (totalPageSize == 0) {
			return new PageDTO(0, 0, 0, null);
		}

		if (totalPageSize < pageNum) {
			//사용자가 더 많은 페이지 인덱스를 입력했을 경우
			throw new IllegalArgumentException("total page size보다 입력한 pageNum이 더 큽니다.");
		}

		totalJobList.addAll(pinList);
		totalJobList.addAll(normalPagingList);

		PageDTO<ModuleBatchJobResDTO> pageDTO = new PageDTO<>(totalJobList, pageNum, 10);

		List<ModuleBatchJobResDTO> resultList = pageDTO.getContent()
			.stream()
			.map(workload ->
				ModuleBatchJobResDTO.builder()
					.uid(workload.getUid())
					.name(workload.getName())
					.resourceName(workload.getResourceName())
					.description(workload.getDescription())
					.creator(workload.getCreator())
					.workspaceName(workspace.getName())
					.workspaceResourceName(workspace.getResourceName())
					.type(workload.getType())
					.image(workload.getImage())
					.gpuRequest(workload.getGpuRequest())
					.cpuRequest(workload.getCpuRequest())
					.memRequest(workload.getMemRequest())
					.createdAt(workload.getCreatedAt())
					.schedulingType(workload.getSchedulingType())
					.envs(workload.getEnvs())
					.ports(workload.getPorts())
					.command(workload.getCommand())
					.status(workload.getStatus())
					.age(workload.getAge())
					.isPinYN(userWorkloadPinList.contains(workload))
					.build())
			.collect(Collectors.toList());
		return new PageDTO<>(resultList, pageNum, 10);
	}

	public PageDTO<ModuleInteractiveJobResDTO> getInteractiveWorkloadByCondition(String workspaceName,
		String searchName,
		WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition, int pageNum, UserInfoDTO userInfoDTO) {
		List<ModuleInteractiveJobResDTO> totalJobList = new ArrayList<>();
		WorkspaceDTO.ResponseDTO workspace = workspaceService.getWorkspaceByName(workspaceName);
		Set<String> userWorkloadPinList = pinService.getUserWorkloadPinList(userInfoDTO.getId(), workspaceName);
		List<ModuleInteractiveJobResDTO> interactiveJobWorkloadList = workloadModuleService.getInteractiveJobWorkloadList(
			workspaceName);

		// pin list filtering
		List<ModuleInteractiveJobResDTO> pinList = interactiveJobWorkloadList.stream()
			.filter(batch -> userWorkloadPinList.contains(batch.getUid())).toList();

		pinList = applyInteractiveWorkloadListCondition(pinList, searchName,
			workloadStatus, sortCondition);

		// PIN 등록하지 않은 전체 Job List
		List<ModuleInteractiveJobResDTO> normalList = interactiveJobWorkloadList.stream()
			.filter(batch -> !userWorkloadPinList.contains(batch.getUid()))
			.toList();
		// 전체 JOB List 검색 조건 filter
		normalList = applyInteractiveWorkloadListCondition(normalList, searchName, workloadStatus,
			sortCondition);
		int normalListPageSize = getNormalListPageSize(pinList.size());

		int totalPageSize = (int)Math.ceil(normalList.size() / (double)normalListPageSize);
		List<ModuleInteractiveJobResDTO> normalPagingList = getPaginatedList(normalList, pageNum, normalListPageSize);
		//TODO 종료된 workload list 조회 및 추가

		if (totalPageSize == 0) {
			return new PageDTO(0, 0, 0, null);
		}

		if (totalPageSize < pageNum) {
			throw new IllegalArgumentException("total page size보다 입력한 pageNum이 더 큽니다.");
		}

		totalJobList.addAll(pinList);
		totalJobList.addAll(normalPagingList);

		PageDTO<ModuleInteractiveJobResDTO> pageDTO = new PageDTO<>(totalJobList, pageNum, 10);

		List<ModuleInteractiveJobResDTO> resultList = pageDTO.getContent()
			.stream()
			.map(workload ->
				ModuleInteractiveJobResDTO.builder()
					.uid(workload.getUid())
					.name(workload.getName())
					.resourceName(workload.getResourceName())
					.description(workload.getDescription())
					.creator(workload.getCreator())
					.workspaceName(workspace.getName())
					.workspaceResourceName(workspace.getResourceName())
					.type(workload.getType())
					.image(workload.getImage())
					.gpuRequest(workload.getGpuRequest())
					.cpuRequest(workload.getCpuRequest())
					.memRequest(workload.getMemRequest())
					.createdAt(workload.getCreatedAt())
					.schedulingType(workload.getSchedulingType())
					.envs(workload.getEnvs())
					.ports(workload.getPorts())
					.command(workload.getCommand())
					.status(workload.getStatus())
					.age(workload.getAge())
					.isPinYN(userWorkloadPinList.contains(workload))
					.build())
			.collect(Collectors.toList());
		return new PageDTO<>(resultList, pageNum, 10);
	}

	public List<ModuleBatchJobResDTO> applyBatchWorkloadListCondition(List<ModuleBatchJobResDTO> workloadList,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition) {

		Stream<ModuleBatchJobResDTO> workloadStream = workloadList.stream()
			.filter(batch -> searchName == null || batch.getResourceName().contains(searchName))
			.filter(batch -> workloadStatus == null || batch.getStatus() == workloadStatus);

		if (sortCondition != null) {
			return switch (sortCondition) {

				case AGE_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleBatchJobResDTO::getCreatedAt)).toList();
				case AGE_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleBatchJobResDTO::getCreatedAt).reversed()).toList();
				case REMAIN_TIME_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleBatchJobResDTO::getRemainTime)).toList();
				case REMAIN_TIME_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleBatchJobResDTO::getRemainTime).reversed())
						.toList();
			};
		} else {
			return workloadStream.toList();
		}
	}

	public List<ModuleInteractiveJobResDTO> applyInteractiveWorkloadListCondition(
		List<ModuleInteractiveJobResDTO> workloadList,
		String searchName, WorkloadStatus workloadStatus, WorkloadSortCondition sortCondition) {

		Stream<ModuleInteractiveJobResDTO> workloadStream = workloadList.stream()
			.filter(batch -> searchName == null || batch.getResourceName().contains(searchName))
			.filter(batch -> workloadStatus == null || batch.getStatus() == workloadStatus);

		if (sortCondition != null) {
			return switch (sortCondition) {
				case AGE_ASC ->
					workloadStream.sorted(Comparator.comparing(ModuleInteractiveJobResDTO::getCreatedAt)).toList();
				case AGE_DESC ->
					workloadStream.sorted(Comparator.comparing(ModuleInteractiveJobResDTO::getCreatedAt).reversed())
						.toList();
				case REMAIN_TIME_ASC, REMAIN_TIME_DESC ->
					throw new IllegalArgumentException("interactive job은 remainTime을 계산할 수 없습니다.");
			};
		} else {
			return workloadStream.toList();
		}
	}

	public void deleteBatchHobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
			WorkloadType.BATCH);
		FileUtils.saveLogFile(log, workloadName, userInfoDTO.getUserName());
		workloadModuleFacadeService.deleteBatchHobWorkload(workSpaceName, workloadName);
	}

	public void deleteInteractiveJobWorkload(String workSpaceName, String workloadName, UserInfoDTO userInfoDTO) throws
		IOException {
		String log = workloadModuleFacadeService.getWorkloadLogByWorkloadName(workSpaceName, workloadName,
			WorkloadType.INTERACTIVE);
		FileUtils.saveLogFile(log, workloadName, userInfoDTO.getUserName());
		workloadModuleFacadeService.deleteInteractiveJobWorkload(workSpaceName, workloadName);
	}

	private <T extends ModuleWorkloadResDTO> List<T> getPaginatedList(List<T> workloadList,
		int pageNum, int pageSize) {
		int startIndex = (pageNum - 1) * pageSize;
		int endIndex = Math.min(pageNum * pageSize, workloadList.size());
		return workloadList.subList(startIndex, endIndex);
	}

	private int getNormalListPageSize(int pinListSize) {
		return 10 - pinListSize;
	}

}
