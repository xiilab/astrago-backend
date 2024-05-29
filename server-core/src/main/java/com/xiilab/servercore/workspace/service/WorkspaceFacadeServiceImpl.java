package com.xiilab.servercore.workspace.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.UserErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.cluster.service.ClusterService;
import com.xiilab.modulek8s.common.dto.ClusterResourceDTO;
import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.CreateWorkspaceDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.facade.workspace.WorkspaceModuleFacadeService;
import com.xiilab.modulek8s.resource_quota.dto.ResourceQuotaResDTO;
import com.xiilab.modulek8s.resource_quota.dto.TotalResourceQuotaDTO;
import com.xiilab.modulek8s.resource_quota.service.ResourceQuotaService;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8s.workspace.service.WorkspaceService;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.alert.systemalert.service.WorkspaceAlertService;
import com.xiilab.modulek8sdb.pin.enumeration.PinType;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.modulek8sdb.workspace.entity.ResourceQuotaEntity;
import com.xiilab.modulek8sdb.workspace.repository.ResourceQuotaCustomRepository;
import com.xiilab.modulek8sdb.workspace.repository.ResourceQuotaHistoryRepository;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.service.GroupService;
import com.xiilab.servercore.alert.systemalert.service.WorkspaceAlertSetService;
import com.xiilab.servercore.code.service.CodeService;
import com.xiilab.servercore.dataset.service.DatasetService;
import com.xiilab.servercore.image.service.ImageService;
import com.xiilab.servercore.model.service.ModelService;
import com.xiilab.servercore.pin.service.PinService;
import com.xiilab.servercore.user.service.UserFacadeService;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workload.service.WorkloadHistoryService;
import com.xiilab.servercore.workspace.dto.ClusterResourceCompareDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceSettingDTO;
import com.xiilab.servercore.workspace.entity.WorkspaceSettingEntity;
import com.xiilab.servercore.workspace.repository.WorkspaceSettingRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class WorkspaceFacadeServiceImpl implements WorkspaceFacadeService {
	private final WorkspaceModuleFacadeService workspaceModuleFacadeService;
	private final WorkloadHistoryService workloadHistoryService;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final ResourceQuotaService resourceQuotaService;
	private final ResourceQuotaHistoryRepository resourceQuotaHistoryRepository;
	private final ResourceQuotaCustomRepository resourceQuotaCustomRepository;
	private final PinService pinService;
	private final GroupService groupService;
	private final ClusterService clusterService;
	private final WorkspaceService workspaceService;
	private final WorkspaceAlertSetService workspaceAlertSetService;
	private final WorkspaceSettingRepo workspaceSettingRepo;
	private final WorkspaceAlertService workspaceAlertService;
	private final ApplicationEventPublisher eventPublisher;
	private final DatasetService datasetService;
	private final ModelService modelService;
	private final CodeService codeService;
	private final ImageService imageService;
	private final MailService mailService;
	private final UserFacadeService userService;

	private static ByteArrayResource getByteArrayResource(String reportFile) {
		Path targetPath = Paths.get(reportFile);
		if (Files.exists(targetPath)) {
			try {
				byte[] bytes = Files.readAllBytes(targetPath);
				return new ByteArrayResource(bytes);
			} catch (IOException e) {
				log.error("엑셀 파일 다운로드 실패");
				throw new RestApiException(WorkspaceErrorCode.FAILED_DOWNLOAD_EXCEL_FILE);
			}
		} else {
			log.error("다운로드할 엑셀 파일이 존재하지않습니다.");
			throw new RestApiException(WorkspaceErrorCode.NOT_FOUND_EXCEL_FILE);
		}
	}

	@Override
	public void deleteWorkspaceByName(String workspaceName, UserDTO.UserInfo userInfoDTO) {
		//생성된 워크로드가 있는지 확인
		List<ModuleWorkloadResDTO> workloadList = workloadModuleFacadeService.getWorkloadList(workspaceName);
		if (workloadList != null && workloadList.size() > 0) {
			throw new RestApiException(WorkspaceErrorCode.WORKSPACE_DELETE_FAILED);
		}

		//워크스페이스 삭제
		workspaceModuleFacadeService.deleteWorkspaceByName(workspaceName);
		//리소스 요청 목록 삭제
		int deleteResult = resourceQuotaHistoryRepository.deleteByWorkspaceResourceName(workspaceName);
		log.info("리소스 요청 목록 {}건 삭제", deleteResult);
		//pin 삭제
		pinService.deletePin(workspaceName, PinType.WORKSPACE);
		groupService.deleteWorkspaceGroupByName(workspaceName);

		//워크로드 매핑 데이터셋, 모델, code, image 삭제 (deleteYN = Y)
		List<JobEntity> workloads = workloadHistoryService.getWorkloadByResourceName(workspaceName);
		for (JobEntity workload : workloads) {
			datasetService.deleteDatasetWorkloadMapping(workload.getId());
			modelService.deleteModelWorkloadMapping(workload.getId());
			codeService.deleteCodeWorkloadMapping(workload.getId());
			imageService.deleteImageWorkloadMapping(workload.getId());
		}
		//워크로드 삭제(deleteYN = Y)
		workloadHistoryService.deleteWorkload(workspaceName);

		//워크스페이스 삭제 알림 전송
		AuthType auth = userInfoDTO.getAuth();
		WorkspaceDTO.ResponseDTO workspace = workspaceService.getWorkspaceByName(workspaceName);
		String workspaceNm = workspace.getName();
		WorkspaceUserAlertEvent workspaceUserAlertEvent = null;

		if (auth == AuthType.ROLE_ADMIN) {
			//관리자가 삭제할 때
			AlertMessage workspaceDeleteAdmin = AlertMessage.WORKSPACE_DELETE_ADMIN;
			String emailTitle = String.format(workspaceDeleteAdmin.getMailTitle(), workspaceNm);
			String title = workspaceDeleteAdmin.getTitle();
			String message = String.format(workspaceDeleteAdmin.getMessage(), userInfoDTO.getUserFullName(),
				workspaceNm);

			String creatorMail = userService.getUserById(workspace.getCreatorId()).getEmail();
			MailDTO mailDTO = MailServiceUtils.deleteWorkspaceMail(userInfoDTO.getUserFullName(),
				userInfoDTO.getEmail(), workspace.getName(), creatorMail);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKSPACE_DELETE,
				userInfoDTO.getId(), workspace.getCreatorId(),
				emailTitle, title, message, workspaceName, null, mailDTO);
		} else {
			//사용자가 삭제할 때
			String emailTitle = String.format(AlertMessage.WORKSPACE_DELETE_OWNER.getMailTitle(), workspaceNm);
			String title = AlertMessage.WORKSPACE_DELETE_OWNER.getTitle();
			String message = String.format(AlertMessage.WORKSPACE_DELETE_OWNER.getMessage(), workspaceNm);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKSPACE_DELETE,
				userInfoDTO.getId(), workspace.getCreatorId(),
				emailTitle, title, message, workspaceName, null, null);
		}

		eventPublisher.publishEvent(workspaceUserAlertEvent);

	}

	@Override
	public void createWorkspace(WorkspaceApplicationForm applicationForm, UserDTO.UserInfo userInfoDTO) {
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
		groupService.createWorkspaceGroup(GroupReqDTO.builder()
			.name(workspace.getResourceName())
			// .createdBy(workspace.getCreatorUserName())
			.createdBy(userInfoDTO.getUserName())
			.createdUserId(userInfoDTO.getId())
			.description(workspace.getDescription())
			.users(applicationForm.getUserIds())
			.build(), userInfoDTO);
		workspaceAlertSetService.saveAlertSet(workspace.getResourceName());

		//오너, 초대받은 유저들의 알림 초기 세팅
		String ownerId = userInfoDTO.getId();
		workspaceAlertService.initWorkspaceAlertMapping(AlertRole.OWNER, ownerId, workspace.getResourceName());
		List<String> invitedUserIds = applicationForm.getUserIds();
		if (applicationForm.getUserIds() != null) {
			for (String invitedUserId : invitedUserIds) {
				workspaceAlertService.initWorkspaceAlertMapping(AlertRole.USER, invitedUserId,
					workspace.getResourceName());
			}
		}

		// 롤 생성
		workspaceService.editWorkspaceRole(workspace.getResourceName());
		// 롤 바인딩
		workspaceService.createPodAnnotationsRoleBinding(workspace.getResourceName());

		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(workspace.getResourceName())
			.build();

		// 관리자에게 워크스페이스 생성 알림 메시지 발송
		AlertMessage workspaceCreateAdmin = AlertMessage.WORKSPACE_CREATE_ADMIN;
		String workspaceName = workspace.getName();
		String workspaceResourceName = workspace.getResourceName();
		String mailTitle = String.format(workspaceCreateAdmin.getMailTitle(), applicationForm.getName());
		String title = workspaceCreateAdmin.getTitle();
		String message = String.format(workspaceCreateAdmin.getMessage(), userInfoDTO.getUserFullName(),
			userInfoDTO.getEmail(), workspaceName);

		MailDTO mailDTO = MailServiceUtils.createWorkspaceMail(applicationForm.getReqGPU(),
			applicationForm.getReqCPU(), applicationForm.getReqMEM(), workspace.getName(),
			userInfoDTO.getUserFullName(), userInfoDTO.getEmail());

		eventPublisher.publishEvent(
			new AdminAlertEvent(AlertName.ADMIN_WORKSPACE_CREATE, userInfoDTO.getId(), mailTitle, title, message,
				pageNaviParam, mailDTO));

		// 워크스페이스 생성자에게 알림 메시지 발송
		AlertMessage workspaceCreateOwner = AlertMessage.WORKSPACE_CREATE_OWNER;
		String emailTitle = String.format(workspaceCreateOwner.getMailTitle(), applicationForm.getName());
		String createOwnerTitle = workspaceCreateOwner.getTitle();
		String createOwnerMessage = String.format(workspaceCreateOwner.getMessage(), applicationForm.getName());
		if (!userInfoDTO.getAuth().equals(AuthType.ROLE_ADMIN)) {
			eventPublisher.publishEvent(
				new WorkspaceUserAlertEvent(AlertRole.OWNER, AlertName.OWNER_WORKSPACE_CREATE, userInfoDTO.getId(),
					userInfoDTO.getId(), emailTitle, createOwnerTitle,
					createOwnerMessage, workspaceResourceName, pageNaviParam, null));
		}
	}

	@Override
	public PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition,
		int pageNum, UserDTO.UserInfo userInfoDTO) {
		Set<String> userWorkspaceList = userInfoDTO.getWorkspaceList(isMyWorkspace);
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());
		//조건절 처리
		workspaceList = workspaceList.stream()
			.filter(workspace -> userWorkspaceList.contains(workspace.getResourceName()))
			.filter(workspace -> searchCondition == null || workspace.getName().contains(searchCondition))
			.sorted(Comparator.comparing(WorkspaceDTO.ResponseDTO::getCreatedAt).reversed())
			.toList();
		//페이지네이션 진행
		PageDTO<WorkspaceDTO.ResponseDTO> pageDTO = new PageDTO<>(workspaceList, pageNum, 9);
		//pinYN 처리 및 최근 워크로드 불러오기 진행
		//최적화를 위해 pageNation 후에 최근워크로드 조회 작업을 진행
		List<WorkspaceDTO.TotalResponseDTO> resultList = pageDTO.getContent()
			.stream()
			.map(workspace -> new WorkspaceDTO.TotalResponseDTO(workspace.getId(), workspace.getName(),
				workspace.getResourceName(), workspace.getDescription(),
				userWorkspacePinList.contains(workspace.getResourceName()), workspace.getCreatedAt(),
				getUserRecentlyWorkload(workspace.getResourceName(), userInfoDTO.getUserName())))
			.toList();
		return new PageDTO<>(pageDTO.getTotalSize(), pageDTO.getTotalPageNum(), pageDTO.getCurrentPage(), resultList);
	}

	private ModuleWorkloadResDTO getUserRecentlyWorkload(String workspaceName, String username) {
		List<ModuleWorkloadResDTO> serverWorkloadList = workloadModuleFacadeService.getWorkloadList(workspaceName);
		Optional<ModuleWorkloadResDTO> moduleWorkloadResDTO =
			CollectionUtils.isEmpty(serverWorkloadList) ? Optional.empty() : serverWorkloadList.stream()
				.filter(workload -> workload.getCreatorUserName().equals(username))
				.max(Comparator.comparing(ModuleWorkloadResDTO::getCreatedAt));

		return moduleWorkloadResDTO.orElseGet(
			() -> workloadHistoryService.findByWorkspaceAndRecently(workspaceName, username));
	}

	@Override
	public void updateWorkspace(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO) {
		workspaceModuleFacadeService.updateWorkspaceInfoByName(workspaceName, updateDTO);
	}

	@Override
	public List<WorkspaceDTO.TotalResponseDTO> getWorkspaceOverView(UserDTO.UserInfo userInfoDTO) {
		//전체 workspace 리스트 조회
		List<WorkspaceDTO.ResponseDTO> workspaceList = workspaceModuleFacadeService.getWorkspaceList();
		//user의 pin 리스트 조회
		Set<String> userWorkspacePinList = pinService.getUserWorkspacePinList(userInfoDTO.getId());

		return workspaceList.stream()
			.filter(workspace -> userWorkspacePinList.contains(workspace.getResourceName()))
			.map(workspace -> new WorkspaceDTO.TotalResponseDTO(workspace.getId(), workspace.getName(),
				workspace.getResourceName(), workspace.getDescription(),
				userWorkspacePinList.contains(workspace.getResourceName()), workspace.getCreatedAt(),
				getUserRecentlyWorkload(workspace.getResourceName(), userInfoDTO.getUserName())))
			.toList();

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
		WorkspaceTotalDTO workspaceInfoByName = workspaceModuleFacadeService.getWorkspaceInfoByName(
			workspaceResourceName);
		//종료된 워크로드 개수 추가
		List<JobEntity> endStatusWorkloads = workloadHistoryService.getWorkloadByResourceNameAndStatus(
			workspaceResourceName, WorkloadStatus.END);
		workspaceInfoByName.addEndStatusWorkloadCnt(endStatusWorkloads.size());
		return workspaceInfoByName;
	}

	@Override
	@Transactional
	public void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO,
		UserDTO.UserInfo userInfoDTO) {
		WorkspaceDTO.ResponseDTO workspaceInfo = workspaceService.getWorkspaceByName(
			workspaceResourceReqDTO.getWorkspace());

		//관리자가 요청 했을 경우 승인 프로세스를 건너뛰고 바로 적용
		if (userInfoDTO.getAuth() == AuthType.ROLE_ADMIN) {
			workspaceModuleFacadeService.updateWorkspaceResourceQuota(workspaceResourceReqDTO.getWorkspace(),
				workspaceResourceReqDTO.getCpuReq(), workspaceResourceReqDTO.getMemReq(),
				workspaceResourceReqDTO.getGpuReq());
			//관리자 외의 유저의 경우는 승인 프로세스 진행
		} else {
			resourceQuotaHistoryRepository.save(
				new ResourceQuotaEntity(workspaceResourceReqDTO, workspaceInfo.getName()));

			// 관리자한테 워크스페이스 리소스 요청 알림 메시지 발송
			PageNaviParam pageNaviParam = PageNaviParam.builder()
				.workspaceResourceName(workspaceInfo.getResourceName())
				.build();
			AlertMessage workspaceResourceRequestAdmin = AlertMessage.WORKSPACE_RESOURCE_REQUEST_ADMIN;
			String mailTitle = String.format(workspaceResourceRequestAdmin.getTitle(),
				workspaceInfo.getName());
			String title = workspaceResourceRequestAdmin.getTitle();
			String message = String.format(workspaceResourceRequestAdmin.getMessage(), userInfoDTO.getUserFullName(),
				userInfoDTO.getUserName(),
				workspaceInfo.getName());

			MailDTO mailDTO = MailServiceUtils.resourceRequestMail(
				workspaceResourceReqDTO.getGpuReq(), workspaceResourceReqDTO.getCpuReq(),
				workspaceResourceReqDTO.getMemReq(),
				workspaceInfo.getName(), userInfoDTO.getUserFullName(), userInfoDTO.getEmail());

			eventPublisher.publishEvent(
				new AdminAlertEvent(AlertName.ADMIN_USER_RESOURCE_REQUEST, userInfoDTO.getId(), mailTitle, title, message,
					pageNaviParam, mailDTO));

			// 워크스페이스 리소스 요청한 사용자에게 알림 발송
			AlertMessage workspaceCreateOwner = AlertMessage.WORKSPACE_RESOURCE_REQUEST_OWNER;
			String emailTitle = String.format(workspaceCreateOwner.getMailTitle(), workspaceInfo.getName());
			String createOwnerTitle = workspaceCreateOwner.getTitle();
			String createOwnerMessage = String.format(workspaceCreateOwner.getMessage(),
				workspaceInfo.getName());
			eventPublisher.publishEvent(
				new WorkspaceUserAlertEvent(AlertRole.OWNER, AlertName.OWNER_RESOURCE_REQUEST, userInfoDTO.getId(),
					workspaceInfo.getCreatorId(), emailTitle, createOwnerTitle,
					createOwnerMessage, workspaceResourceReqDTO.getWorkspace(), pageNaviParam, null));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PageDTO<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, int pageNum,
		UserDTO.UserInfo userInfoDTO) {
		List<ResourceQuotaEntity> resourceQuotaReqList = resourceQuotaHistoryRepository.findByWorkspaceResourceName(
			workspace);

		List<ResourceQuotaFormDTO> list = resourceQuotaReqList.stream()
			.map(resourceQuotaEntity -> ResourceQuotaFormDTO.builder()
				.id(resourceQuotaEntity.getId())
				.workspaceName(resourceQuotaEntity.getWorkspaceName())
				.workspaceResourceName(resourceQuotaEntity.getWorkspaceResourceName())
				.requestReason(resourceQuotaEntity.getRequestReason())
				.rejectReason(resourceQuotaEntity.getRejectReason())
				.status(resourceQuotaEntity.getStatus())
				.modDate(resourceQuotaEntity.getModDate())
				.regDate(resourceQuotaEntity.getRegDate())
				.cpuReq(resourceQuotaEntity.getCpuReq())
				.gpuReq(resourceQuotaEntity.getGpuReq())
				.memReq(resourceQuotaEntity.getMemReq())
				.build())
			.toList();

		return new PageDTO<>(list, pageNum, 10);
	}

	@Override
	@Transactional
	public void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO,
		UserDTO.UserInfo userInfoDTO) {
		if (userInfoDTO.getAuth() != AuthType.ROLE_ADMIN) {
			throw new RestApiException(UserErrorCode.USER_AUTH_FAIL);
		}

		ResourceQuotaEntity resourceQuotaEntity = resourceQuotaHistoryRepository.findById(id).orElseThrow();
		int cpu = 0;
		int mem = 0;
		int gpu = 0;
		String workspaceNm = resourceQuotaEntity.getWorkspaceName();
		WorkspaceUserAlertEvent workspaceUserAlertEvent = null;
		String receiverEmail = userService.getUserInfoById(resourceQuotaEntity.getRegUser().getRegUserId()).getEmail();
		if (resourceQuotaApproveDTO.isApprovalYN()) {
			resourceQuotaEntity.approval();
			cpu = resourceQuotaApproveDTO.getCpu() != null ? resourceQuotaApproveDTO.getCpu() :
				resourceQuotaEntity.getCpuReq();
			mem = resourceQuotaApproveDTO.getMem() != null ? resourceQuotaApproveDTO.getMem() :
				resourceQuotaEntity.getMemReq();
			gpu = resourceQuotaApproveDTO.getGpu() != null ? resourceQuotaApproveDTO.getGpu() :
				resourceQuotaEntity.getGpuReq();
			workspaceModuleFacadeService.updateWorkspaceResourceQuota(resourceQuotaEntity.getWorkspaceResourceName(),
				cpu, mem, gpu);
			//리소스 승인 알림 발송
			String emailTitle = String.format(AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getMailTitle(),
				workspaceNm);
			String title = AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getTitle();
			String message = String.format(AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getMessage(),
				userInfoDTO.getUserFullName(), workspaceNm, "승인");

			MailDTO mailDTO = MailServiceUtils.resourceApproveMail(gpu, cpu, mem, resourceQuotaEntity.getWorkspaceName(),
				userInfoDTO.getUserFullName(), userInfoDTO.getEmail(), receiverEmail);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.OWNER,
				AlertName.OWNER_RESOURCE_REQUEST_RESULT, userInfoDTO.getId(),
				resourceQuotaEntity.getRegUser().getRegUserId(), emailTitle,
				title, message, resourceQuotaEntity.getWorkspaceResourceName(), null, mailDTO);
		} else {
			resourceQuotaEntity.denied(resourceQuotaApproveDTO.getRejectReason());
			//리소스 반려 알림 발송
			String emailTitle = String.format(AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getMailTitle(),
				workspaceNm);
			String title = AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getTitle();
			String message = String.format(AlertMessage.WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER.getMessage(),
				userInfoDTO.getUserFullName(), workspaceNm, "반려");

			MailDTO mailDTO = MailServiceUtils.resourceRefuseMail(resourceQuotaEntity.getWorkspaceName(),
				resourceQuotaApproveDTO.getRejectReason(),
				userInfoDTO.getUserFullName(), userInfoDTO.getEmail(), receiverEmail);

			workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.OWNER,
				AlertName.OWNER_RESOURCE_REQUEST_RESULT, userInfoDTO.getId(),
				resourceQuotaEntity.getRegUser().getRegUserId(), emailTitle, title, message,
				resourceQuotaEntity.getWorkspaceResourceName(), null, mailDTO);
		}
		eventPublisher.publishEvent(workspaceUserAlertEvent);

	}

	@Override
	public void deleteResourceQuota(long id) {
		resourceQuotaHistoryRepository.deleteById(id);
	}

	@Override
	public List<WorkspaceDTO.WorkspaceResourceStatus> getUserWorkspaceResourceStatus(String workspaceName,
		UserDTO.UserInfo userInfoDTO) {
		Set<String> workspaceList = userInfoDTO.getWorkspaceList(false);
		return workspaceList.stream()
			.map(this::safeGetWorkspaceResourceStatus)
			.filter(Optional::isPresent) // Optional이 존재하는 경우만 필터링
			.map(Optional::get) // Optional에서 값을 추출
			.filter(workspace -> workspaceName == null || workspace.getName().contains(workspaceName))
			.toList();
	}

	@Override
	public WorkspaceAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName) {
		return workspaceAlertSetService.getWorkspaceAlertSet(workspaceName);
	}

	@Override
	public WorkspaceAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName,
		WorkspaceAlertSetDTO workspaceAlertSetDTO) {
		return workspaceAlertSetService.updateWorkspaceAlertSet(workspaceName, workspaceAlertSetDTO);
	}

	@Override
	public boolean workspaceAccessAuthority(String workspaceResourceName, UserDTO.UserInfo userInfoDTO) {
		return userInfoDTO.isAccessAuthorityWorkspace(workspaceResourceName);
	}

	@Override
	public PageDTO<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition,
		WorkspaceSortCondition sortCondition, int pageNum, int pageSize, UserDTO.UserInfo userInfoDTO) {
		//권한 체크
		if (userInfoDTO.getAuth() != AuthType.ROLE_ADMIN) {
			throw new RestApiException(UserErrorCode.USER_AUTH_FAIL);
		}
		//검색 조건으로 전체 조회
		List<WorkspaceDTO.AdminResponseDTO> workspaceList = workspaceModuleFacadeService.getAdminWorkspaceList(
			searchCondition);
		if (sortCondition != null) {
			Comparator<WorkspaceDTO.AdminResponseDTO> comparator = switch (sortCondition) {
				case CPU_ASSIGN_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCpu);
				case CPU_ASSIGN_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCpu).reversed();
				case MEM_ASSIGN_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getMem);
				case MEM_ASSIGN_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getMem).reversed();
				case GPU_ASSIGN_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getGpu);
				case GPU_ASSIGN_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getGpu).reversed();
				case CREATOR_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCreator);
				case CREATOR_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCreator).reversed();
				case CREATED_AT_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCreatedAt);
				case CREATED_AT_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getCreatedAt).reversed();
				case WORKSPACE_NAME_ASC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getName);
				case WORKSPACE_NAME_DESC -> Comparator.comparing(WorkspaceDTO.AdminResponseDTO::getName).reversed();
			};
			workspaceList = workspaceList.stream().sorted(comparator).toList();
		}
		return new PageDTO<>(workspaceList, pageNum, pageSize);
	}

	@Override
	public PageDTO<ResourceQuotaFormDTO> getAdminResourceQuotaRequests(int pageNum, int pageSize,
		UserDTO.UserInfo userInfoDTO) {
		List<ResourceQuotaEntity> resourceQuotaEntityList = resourceQuotaHistoryRepository.findAll();

		List<ResourceQuotaFormDTO> list = resourceQuotaEntityList.stream()
			.map(resourceQuotaEntity -> ResourceQuotaFormDTO.builder()
				.id(resourceQuotaEntity.getId())
				.workspaceName(resourceQuotaEntity.getWorkspaceName())
				.workspaceResourceName(resourceQuotaEntity.getWorkspaceResourceName())
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
			.sorted(Comparator.comparing(ResourceQuotaFormDTO::getRegDate).reversed()).toList();

		return new PageDTO<>(list, pageNum, pageSize);
	}

	@Override
	public WorkspaceDTO.AdminInfoDTO getAdminWorkspaceInfo(String name) {
		WorkspaceDTO.ResponseDTO workspaceInfo = workspaceService.getWorkspaceByName(name);
		WorkspaceDTO.WorkspaceResourceStatus workspaceResourceStatus = workspaceService.getWorkspaceResourceStatus(
			name);
		ResourceQuotaEntity recentlyResourceRequest = resourceQuotaCustomRepository.findByWorkspaceRecently(name);
		ClusterResourceDTO clusterResource = clusterService.getClusterResource();
		return WorkspaceDTO.AdminInfoDTO.builder()
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

	@Override
	public ClusterResourceCompareDTO requestResourceComparedClusterResource() {
		//cluster의 총 리소스 조회
		ClusterResourceDTO clusterResource = clusterService.getClusterResource();
		//리소스 할당량 조회
		TotalResourceQuotaDTO totalResourceQuota = resourceQuotaService.getTotalResourceQuota();
		return new ClusterResourceCompareDTO(clusterResource.getCpu(), clusterResource.getMem(),
			clusterResource.getGpu(), totalResourceQuota.getCpu(), totalResourceQuota.getMem(),
			totalResourceQuota.getGpu());
	}

	@Override
	@Transactional(readOnly = true)
	public WorkspaceResourceSettingDTO getWorkspaceResourceSetting() {
		WorkspaceSettingEntity workspaceSettingEntity = workspaceSettingRepo.findAll().get(0);
		return new WorkspaceResourceSettingDTO(workspaceSettingEntity.getCpu(), workspaceSettingEntity.getMem(),
			workspaceSettingEntity.getGpu());
	}

	@Override
	@Transactional
	public void updateWorkspaceResourceSetting(WorkspaceResourceSettingDTO workspaceResourceSettingDTO,
		UserDTO.UserInfo userInfoDTO) {
		if (userInfoDTO.getAuth() != AuthType.ROLE_ADMIN) {
			throw new RestApiException(UserErrorCode.USER_AUTH_FAIL);
		}
		WorkspaceSettingEntity workspaceSettingEntity = workspaceSettingRepo.findAll().get(0);
		workspaceSettingEntity.updateResource(workspaceResourceSettingDTO.getCpu(),
			workspaceResourceSettingDTO.getMem(), workspaceResourceSettingDTO.getGpu());
	}

	// 예외를 처리하는 별도의 메소드
	private Optional<WorkspaceDTO.WorkspaceResourceStatus> safeGetWorkspaceResourceStatus(String workspaceId) {
		try {
			// 성공적으로 값을 가져올 경우, Optional로 감싸 반환
			return Optional.ofNullable(workspaceService.getWorkspaceResourceStatus(workspaceId));
		} catch (Exception e) {
			// 예외 발생 시, 빈 Optional 반환
			return Optional.empty();
		}
	}

	@Override
	public void validRedirectWorkspace(String workspaceResourceName) {
		try {
			workspaceService.getWorkspaceByName(workspaceResourceName);
		} catch (K8sException e) {
			e.printStackTrace();
		}
	}
}
