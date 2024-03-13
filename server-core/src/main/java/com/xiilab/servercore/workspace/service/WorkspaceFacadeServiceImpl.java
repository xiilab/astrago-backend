package com.xiilab.servercore.workspace.service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.UserErrorCode;
import com.xiilab.modulek8s.cluster.service.ClusterService;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;
import com.xiilab.modulek8sdb.workspace.repository.ResourceQuotaCustomRepository;
import com.xiilab.modulek8sdb.workspace.repository.ResourceQuotaRepository;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertService;
import com.xiilab.servercore.alert.systemalert.service.SystemAlertSetService;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkspaceFacadeServiceImpl implements WorkspaceFacadeService {
	private final WorkspaceModuleFacadeService workspaceModuleFacadeService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ResourceQuotaRepository resourceQuotaRepository;
	private final ResourceQuotaCustomRepository resourceQuotaCustomRepository;
	private final PinService pinService;
	private final GroupService groupService;
	private final ClusterService clusterService;
	private final WorkspaceService workspaceService;
	private final SystemAlertSetService systemAlertSetService;
	private final SystemAlertService systemAlertService;

	@Override
	public void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO) {
		//워크스페이스 생성
		WorkspaceDTO.ResponseDTO workspace = workspaceModuleFacadeService.createWorkspace(CreateWorkspaceDTO.builder()
			.name(applicationForm.getName())
			.description(applicationForm.getDescription())
			.creatorId(userInfoDTO.getId())
			.creatorUserName(userInfoDTO.getUserName())
			.creatorFullName(userInfoDTO.getUserFullName())
			.reqCPU(applicationForm.getReqCPU())
			.reqMEM(applicationForm.getReqMEM())
			.reqGPU(applicationForm.getReqGPU())
			.build());
		//group 추가
		groupService.createWorkspaceGroup(
			GroupReqDTO.builder()
				.name(workspace.getResourceName())
				.createdBy(workspace.getCreatorUserName())
				.createdUserId(workspace.getCreatorId())
				.description(workspace.getDescription())
				.users(applicationForm.getUserIds())
				.build(), userInfoDTO);
		systemAlertSetService.saveAlertSet(workspace.getResourceName());
	}

	@Override
	public PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition,
		int pageNum, UserInfoDTO userInfoDTO) {
		Set<String> groupList = userInfoDTO.getWorkspaceList(isMyWorkspace);
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());
		//조건절 처리
		workspaceList = workspaceList.stream()
			.filter(workspace -> groupList.contains(workspace.getResourceName()))
			.filter(workspace -> searchCondition == null || workspace.getName().contains(searchCondition))
			.sorted(Comparator.comparing(WorkspaceDTO.ResponseDTO::getCreatedAt).reversed())
			.toList();
		//페이지네이션 진행
		PageDTO<WorkspaceDTO.ResponseDTO> pageDTO = new PageDTO<>(workspaceList, pageNum, 9);
		//pinYN 처리 및 최근 워크로드 불러오기 진행
		//최적화를 위해 pageNation 후에 최근워크로드 조회 작업을 진행
		List<WorkspaceDTO.TotalResponseDTO> resultList = pageDTO.getContent()
			.stream()
			.map(workspace -> new WorkspaceDTO.TotalResponseDTO(
				workspace.getId(),
				workspace.getName(),
				workspace.getResourceName(),
				workspace.getDescription(),
				userWorkspacePinList.contains(workspace.getResourceName()),
				workspace.getCreatedAt(),
				workloadModuleFacadeService.getUserRecentlyWorkload(workspace.getResourceName(),
					userInfoDTO.getUserName())))
			.toList();
		return new PageDTO<>(resultList, pageNum, 9);
	}

	@Override
	public void updateWorkspace(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO) {
		workspaceModuleFacadeService.updateWorkspaceInfoByName(workspaceName, updateDTO);
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName, UserInfoDTO userInfoDTO) {
		workspaceModuleFacadeService.deleteWorkspaceByName(workspaceName);
		//pin 삭제
		pinService.deletePin(workspaceName, PinType.WORKSPACE);
		groupService.deleteWorkspaceGroupByName(workspaceName);
		// 워크스페이스 알림 설정 삭제
		systemAlertSetService.deleteAlert(workspaceName);
	}

	@Override
	public List<WorkspaceDTO.TotalResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO) {
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());

		return workspaceList.stream()
			.filter(workspace -> userWorkspacePinList.contains(workspace.getResourceName()))
			.map(workspace ->
				new WorkspaceDTO.TotalResponseDTO(
					workspace.getId(),
					workspace.getName(),
					workspace.getResourceName(),
					workspace.getDescription(),
					userWorkspacePinList.contains(workspace.getResourceName()),
					workspace.getCreatedAt(),
					workloadModuleFacadeService.getUserRecentlyWorkload(workspace.getResourceName(),
						userInfoDTO.getUserName()))).toList();

	}

	@Override
	public WorkspaceResourceQuotaState getWorkspaceResourceQuotaState(String workspaceResourceName) {
		ClusterResourceDTO clusterResource = clusterService.getClusterResource();
		ResourceQuotaResDTO workspaceResourceQuota = workspaceModuleFacadeService.getWorkspaceResourceQuota(
			workspaceResourceName);
		return new WorkspaceResourceQuotaState(clusterResource, workspaceResourceQuota);
	}

	@Override
	public WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceResourceName) {
		return workspaceModuleFacadeService.getWorkspaceInfoByName(workspaceResourceName);
	}

	@Override
	@Transactional
	public void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO, UserInfoDTO userInfoDTO) {
		//관리자가 요청 했을 경우 승인 프로세스를 건너뛰고 바로 적용
		if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN) {
			workspaceModuleFacadeService.updateWorkspaceResourceQuota(
				workspaceResourceReqDTO.getWorkspace(),
				workspaceResourceReqDTO.getCpuReq(),
				workspaceResourceReqDTO.getMemReq(),
				workspaceResourceReqDTO.getGpuReq());
			//관리자 외의 유저의 경우는 승인 프로세스 진행
		} else {
			resourceQuotaRepository.save(new ResourceQuotaEntity(workspaceResourceReqDTO));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageDTO<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, int pageNum,
		UserInfoDTO userInfoDTO) {
		List<ResourceQuotaEntity> resourceQuotaReqList = resourceQuotaRepository.findByWorkspace(workspace);

		List<ResourceQuotaFormDTO> list = resourceQuotaReqList.stream()
			.map(resourceQuotaEntity ->
				ResourceQuotaFormDTO.builder()
					.id(resourceQuotaEntity.getId())
					.workspace(resourceQuotaEntity.getWorkspace())
					.requestReason(resourceQuotaEntity.getRequestReason())
					.rejectReason(resourceQuotaEntity.getRejectReason())
					.status(resourceQuotaEntity.getStatus())
					.modDate(resourceQuotaEntity.getModDate())
					.regDate(resourceQuotaEntity.getRegDate())
					.cpuReq(resourceQuotaEntity.getCpuReq())
					.gpuReq(resourceQuotaEntity.getGpuReq())
					.memReq(resourceQuotaEntity.getMemReq())
					.requester(resourceQuotaEntity.getRegUser().getRegUserRealName())
					.build())
			.toList();

		return new PageDTO<>(list, pageNum, 10);
	}

	@Override
	@Transactional
	public void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO) {
		ResourceQuotaEntity resourceQuotaEntity = resourceQuotaRepository.findById(id).orElseThrow();
		if (resourceQuotaApproveDTO.isApprovalYN()) {
			resourceQuotaEntity.approval();
			workspaceModuleFacadeService.updateWorkspaceResourceQuota(
				resourceQuotaEntity.getWorkspace(),
				resourceQuotaEntity.getCpuReq(),
				resourceQuotaEntity.getMemReq(),
				resourceQuotaEntity.getGpuReq()
			);

			SystemAlertSetDTO.ResponseDTO workspaceAlertSet = systemAlertSetService.getWorkspaceAlertSet(
				resourceQuotaEntity.getWorkspace());
			if (workspaceAlertSet.isResourceApprovalAlert()) {

				systemAlertService.sendAlert(SystemAlertDTO.builder()
					.recipientId(resourceQuotaEntity.getRegUser().getRegUserId())
					.systemAlertType(SystemAlertType.WORKLOAD)
					.message(SystemAlertMessage.RESOURCE_APPROVAL.getMessage())
					.senderId("SYSTEM")
					.build());
			}
		} else {
			resourceQuotaEntity.denied(resourceQuotaEntity.getRejectReason());
		}
	}

	@Override
	public void deleteResourceQuota(long id) {
		resourceQuotaRepository.deleteById(id);
	}

	@Override
	public List<WorkspaceDTO.WorkspaceResourceStatus> getUserWorkspaceResourceStatus(String workspaceName,
		UserInfoDTO userInfoDTO) {
		Set<String> workspaceList = userInfoDTO.getWorkspaceList(false);
		return workspaceList.stream()
			.map(workspaceService::getWorkspaceResourceStatus)
			.filter(workspace -> workspaceName == null || workspace.getName().contains(workspaceName))
			.toList();
	}

	@Override
	public SystemAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName) {
		return systemAlertSetService.getWorkspaceAlertSet(workspaceName);
	}

	@Override
	public SystemAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName,
		SystemAlertSetDTO systemAlertSetDTO) {
		return systemAlertSetService.updateWorkspaceAlertSet(workspaceName, systemAlertSetDTO);
	}

	@Override
	public boolean workspaceAccessAuthority(String workspaceResourceName, UserInfoDTO userInfoDTO) {
		return userInfoDTO.isAccessAuthorityWorkspace(workspaceResourceName);
	}

	@Override
	public PageDTO<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition,
		WorkspaceSortCondition sortCondition, int pageNum, int pageSize, UserInfoDTO userInfoDTO) {
		//권한 체크
		if (userInfoDTO.getAuth() != AuthType.ROLE_ADMIN) {
			throw new RestApiException(UserErrorCode.USER_AUTH_FAIL);
		}
		//검색 조건으로 전체 조회
		Stream<WorkspaceDTO.AdminResponseDTO> workspaceStream = workspaceModuleFacadeService.getAdminWorkspaceList(
			searchCondition).stream();
		if (sortCondition != null) {
			workspaceStream = switch (sortCondition) {
				case CPU_ASSIGN_ASC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCpu));
				case CPU_ASSIGN_DESC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCpu).reversed());
				case MEM_ASSIGN_ASC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getMem));
				case MEM_ASSIGN_DESC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getMem).reversed());
				case GPU_ASSIGN_ASC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getGpu));
				case GPU_ASSIGN_DESC ->
					workspaceStream.sorted(Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getGpu).reversed());
			};
		}

		return new PageDTO<>(workspaceStream.toList(), pageNum, pageSize);
	}

	@Override
	public PageDTO<ResourceQuotaFormDTO> getAdminResourceQuotaRequests(int pageNum, int pageSize, UserInfoDTO userInfoDTO) {
		List<ResourceQuotaEntity> resourceQuotaEntityList = resourceQuotaRepository.findAll();

		List<ResourceQuotaFormDTO> list = resourceQuotaEntityList.stream()
			.map(resourceQuotaEntity ->
				ResourceQuotaFormDTO.builder()
					.id(resourceQuotaEntity.getId())
					.workspace(resourceQuotaEntity.getWorkspace())
					.requestReason(resourceQuotaEntity.getRequestReason())
					.rejectReason(resourceQuotaEntity.getRejectReason())
					.status(resourceQuotaEntity.getStatus())
					.modDate(resourceQuotaEntity.getModDate())
					.regDate(resourceQuotaEntity.getRegDate())
					.cpuReq(resourceQuotaEntity.getCpuReq())
					.gpuReq(resourceQuotaEntity.getGpuReq())
					.memReq(resourceQuotaEntity.getMemReq())
					.requester(resourceQuotaEntity.getRegUser().getRegUserRealName())
					.build())
			.toList();

		return new PageDTO<>(list, pageNum, pageSize);
	}

	@Override
	public WorkspaceDTO.AdminInfoDTO getAdminWorkspaceInfo(String name) {
		WorkspaceDTO.ResponseDTO workspaceInfo = workspaceService.getWorkspaceByName(name);
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			name);
		ResourceQuotaEntity recentlyResourceRequest = resourceQuotaCustomRepository.findByWorkspaceRecently(name);
		ClusterResourceDTO clusterResource = clusterService.getClusterResource();
		return WorkspaceDTO.AdminInfoDTO
			.builder()
			.id(workspaceInfo.getId())
			.name(workspaceInfo.getName())
			.resourceName(workspaceInfo.getResourceName())
			.description(workspaceInfo.getDescription())
			.createdAt(workspaceInfo.getCreatedAt())
			.creator(workspaceInfo.getCreatorFullName())
			.reqCPU(recentlyResourceRequest == null ? 0 : recentlyResourceRequest.getCpuReq())
			.reqMEM(recentlyResourceRequest == null ? 0 : recentlyResourceRequest.getMemReq())
			.reqGPU(recentlyResourceRequest == null ? 0 : recentlyResourceRequest.getGpuReq())
			.useCPU(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getCpuUsed()))
			.useMEM(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getMemUsed()))
			.useGPU(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getGpuUsed()))
			.allocCPU(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getCpuLimit()))
			.allocMEM(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getMemLimit()))
			.allocGPU(Integer.parseInt(workspaceResourceStatus.getResourceStatus().getGpuLimit()))
			.totalCPU(clusterResource.getCpu())
			.totalMEM(clusterResource.getMem())
			.totalGPU(clusterResource.getGpu())
			.build();
	}

}
