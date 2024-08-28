package com.xiilab.servercore.workload.service;

import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadSortCondition;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.workspace.dto.RecentlyWorkloadDTO;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.workload.history.entity.DistributedJobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepoCustom;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.user.service.UserFacadeService;
import com.xiilab.servercore.workload.dto.request.WorkloadUpdateDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.OverViewWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadSummaryDTO;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkloadHistoryServiceImpl implements WorkloadHistoryService {
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final WorkloadHistoryRepoCustom workloadHistoryRepoCustom;
	private final ApplicationEventPublisher publisher;
	private final UserFacadeService userFacadeService;

	@Override
	public WorkloadSummaryDTO getWorkloadHistoryById(long id) {
		WorkloadEntity workload = workloadHistoryRepo.findById(id).orElseThrow();
		return new WorkloadSummaryDTO(workload);
	}

	@Override
	public FindWorkloadResDTO getWorkloadInfoByResourceName(String workspaceName,
		String workloadResourceName, UserDTO.UserInfo userInfoDTO) {
		WorkloadEntity workloadEntity = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workspaceName, workloadResourceName)
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		// 삭제된 워크로드는 다른 에러메시지 처리
		if (workloadEntity.getDeleteYN() == DeleteYN.Y) {
			throw new RestApiException(WorkloadErrorCode.DELETED_WORKLOAD_INFO);
		}
		Set<String> workspaceList = userFacadeService.getWorkspaceList(userInfoDTO.getId(), true);
		workloadEntity.updateCanBeDeleted(userInfoDTO.getId(), workspaceList);

		if (workloadEntity.getWorkloadType() == WorkloadType.DISTRIBUTED) {
			return FindWorkloadResDTO.DistributedWorkloadDetail.from((DistributedJobEntity)workloadEntity);
		} else {
			return FindWorkloadResDTO.SingleWorkloadDetail.from((JobEntity)workloadEntity);
		}
	}
	@Override
	public FindWorkloadResDTO getAdminWorkloadInfoByResourceName(String workspaceName,
		String workloadResourceName, UserDTO.UserInfo userInfoDTO) {
		WorkloadEntity workloadEntity = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workspaceName, workloadResourceName)
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		// 삭제된 워크로드는 다른 에러메시지 처리
		if (workloadEntity.getDeleteYN() == DeleteYN.Y) {
			throw new RestApiException(WorkloadErrorCode.DELETED_WORKLOAD_INFO);
		}
		workloadEntity.updateCanBeDeleted(true);

		if (workloadEntity.getWorkloadType() == WorkloadType.DISTRIBUTED) {
			return FindWorkloadResDTO.DistributedWorkloadDetail.from((DistributedJobEntity)workloadEntity);
		} else {
			return FindWorkloadResDTO.SingleWorkloadDetail.from((JobEntity)workloadEntity);
		}
	}

	@Override
	public WorkloadEntity findById(Long id) {
		return workloadHistoryRepo.findById(id).orElseThrow(() -> new RestApiException(WorkloadErrorCode.NOT_FOUND_WORKLOAD));
	}

	@Override
	public void deleteWorkloadHistory(long id, UserDTO.UserInfo userInfoDTO) {
		WorkloadEntity jobEntity = workloadHistoryRepo.findById(id).orElseThrow();
		// owner 권한인 워크스페이스 목록 가져옴
		List<String> loginUserOwnerWorkspaceList = userInfoDTO.getWorkspaces()
			.stream()
			.filter(workspace -> workspace.contains("/owner"))
			.map(workspace -> workspace.split("/owner")[0])
			.toList();

		String workloadName = jobEntity.getName();
		WorkspaceUserAlertEvent workspaceUserAlertEvent = null;
		// 워크로드 생성자가 삭제
		if (jobEntity.getCreatorId().equals(userInfoDTO.getId())) {

			String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_DELETE_CREATOR.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_DELETE_CREATOR.getMessage(), workloadName);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
				userInfoDTO.getId(), jobEntity.getCreatorId(), emailTitle, title, message,
				jobEntity.getWorkspaceResourceName(), null, null);

		} else if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN || loginUserOwnerWorkspaceList.contains(
			jobEntity.getWorkspaceResourceName())) {    // 관리자 또는 워크스페이스 생성자가 삭제

			String emailTitle = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMailTitle(), workloadName);
			String title = AlertMessage.WORKLOAD_DELETE_ADMIN.getTitle();
			String message = String.format(AlertMessage.WORKLOAD_DELETE_ADMIN.getMessage(),
				userInfoDTO.getUserFullName(), userInfoDTO.getEmail(), workloadName);

			String receiverMail = userFacadeService.getUserInfoById(jobEntity.getCreatorId()).getEmail();
			MailDTO mailDTO = MailServiceUtils.deleteWorkloadMail(jobEntity.getName(), receiverMail);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
				userInfoDTO.getId(), jobEntity.getCreatorId(), emailTitle, title, message,
				jobEntity.getWorkspaceResourceName(), null, mailDTO);
		} else {
			throw new IllegalArgumentException("해당 유저는 워크스페이스 삭제 권한이 없습니다.");
		}

		workloadHistoryRepo.deleteById(id);
		publisher.publishEvent(workspaceUserAlertEvent);
	}

	@Override
	public void editWorkloadHistory(WorkloadUpdateDTO workloadUpdateDTO) {
		WorkloadEntity findWorkload = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
				workloadUpdateDTO.getWorkspaceResourceName(),
				workloadUpdateDTO.getWorkloadResourceName())
			.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		findWorkload.updateJob(workloadUpdateDTO.getName(), workloadUpdateDTO.getDescription());
		workloadHistoryRepo.save(findWorkload);

	}

	@Override
	public RecentlyWorkloadDTO findByWorkspaceAndRecently(String workspaceName, String username) {
		WorkloadEntity workload = workloadHistoryRepoCustom.findByWorkspaceNameRecently(workspaceName, username);
		if (workload == null) {
			return null;
		}
		return new RecentlyWorkloadDTO(
			workload.getName(),
			workload.getWorkloadStatus(),
			workload.getStartTime() != null ? new AgeDTO(workload.getStartTime()) : null);
	}

	@Override
	public List<WorkloadEntity> getWorkloadByResourceName(String workspaceResourceName) {
		return workloadHistoryRepo.findByWorkspaceResourceName(workspaceResourceName);
	}

	@Override
	public void deleteWorkload(String workspaceName) {
		List<WorkloadEntity> jobEntities = workloadHistoryRepo.findByWorkspaceResourceName(workspaceName);
		for (WorkloadEntity jobEntity : jobEntities) {
			workloadHistoryRepo.deleteById(jobEntity.getId());
		}
	}

	@Override
	public List<WorkloadEntity> getWorkloadByResourceNameAndStatus(String workspaceResourceName,
		WorkloadStatus workloadStatus) {
		return workloadHistoryRepo.getWorkloadByResourceNameAndStatus(workspaceResourceName, workloadStatus);
	}

	@Override
	public List<WorkloadSummaryDTO> getWorkloadHistoryInResourceNames(List<String> pinResourceNameList,
		WorkloadType workloadType, WorkloadSortCondition sortCondition) {
		List<WorkloadEntity> workloads = workloadHistoryRepoCustom.getWorkloadHistoryInResourceNames(
			pinResourceNameList,
			workloadType, sortCondition);
		return workloads.stream().map(WorkloadSummaryDTO::new).toList();
	}

	@Override
	public OverViewWorkloadResDTO<WorkloadSummaryDTO> getOverViewWorkloadList(String workspaceName,
		WorkloadType workloadType, String searchName,
		Boolean isCreatedByMe, String userId, List<String> pinResourceNameList, WorkloadStatus workloadStatus,
		WorkloadSortCondition workloadSortCondition, PageRequest pageRequest) {
		//overview 페이지에서 요청 or 워크로드 전체 조회 페이지에서 내가 생성한 워크로드 체크 해제 시
		Page<WorkloadEntity> workloadEntities;
		if (ValidUtils.isNullOrFalse(isCreatedByMe)) {
			workloadEntities = workloadHistoryRepoCustom.getOverViewWorkloadList(
				workspaceName, workloadType, searchName, StringUtils.hasText(workspaceName) ? null : userId,
				pinResourceNameList, workloadSortCondition, pageRequest, workloadStatus);
		} else {
			workloadEntities = workloadHistoryRepoCustom.getOverViewWorkloadList(
				workspaceName, workloadType, searchName, userId, pinResourceNameList, workloadSortCondition,
				pageRequest,
				workloadStatus);
		}
		List<WorkloadSummaryDTO> workloads = workloadEntities
			.map(WorkloadSummaryDTO::new)
			.toList();
		long totalCount = workloadEntities.getTotalElements();
		return new OverViewWorkloadResDTO<>(totalCount, workloads);
	}

	@Override
	public OverViewWorkloadResDTO<WorkloadSummaryDTO> getAdminWorkloadList(String workspaceName,
		WorkloadType workloadType, String searchName, Boolean isCreatedByMe, WorkloadStatus workloadStatus,
		WorkloadSortCondition workloadSortCondition, PageRequest pageRequest) {
		Page<WorkloadEntity> workloadEntities = workloadHistoryRepoCustom.getAdminWorkloadList(
			workspaceName, workloadType, searchName, workloadSortCondition,
			pageRequest,
			workloadStatus);
		List<WorkloadSummaryDTO> workloads = workloadEntities
			.map(WorkloadSummaryDTO::new)
			.toList();
		long totalCount = workloadEntities.getTotalElements();
		return new OverViewWorkloadResDTO<>(totalCount, workloads);
	}

	@Override
	public List<WorkloadEntity> getWorkloadHistoryByUsingDivisionGPU(String workspaceResourceName) {
		List<WorkloadStatus> statuses = List.of(WorkloadStatus.ERROR, WorkloadStatus.PENDING, WorkloadStatus.RUNNING);
		List<GPUType> types = List.of(GPUType.MIG, GPUType.MPS);
		return workloadHistoryRepo.getWorkloadHistoryByUsingDivisionGPU(workspaceResourceName, statuses, types);
	}
}
