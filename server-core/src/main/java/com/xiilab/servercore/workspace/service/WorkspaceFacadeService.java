package com.xiilab.servercore.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.SystemAlertSetDTO;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO);

	PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition, int pageNum,
		UserInfoDTO userInfoDTO);

	void updateWorkspace(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO);

	void deleteWorkspaceByName(String workspaceName, UserInfoDTO userInfoDTO);

	List<WorkspaceDTO.TotalResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO);

	WorkspaceResourceQuotaState getWorkspaceResourceQuotaState(String workspaceResourceName);

	WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceResourceName);

	void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO, UserInfoDTO userInfoDTO);

	PageDTO<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, int pageNum, UserInfoDTO userInfoDTO);

	void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO);

	void deleteResourceQuota(long id);

	List<WorkspaceDTO.WorkspaceResourceStatus> getUserWorkspaceResourceStatus(String workspaceName,
		UserInfoDTO userInfoDTO);

	SystemAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);

	SystemAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, SystemAlertSetDTO systemAlertSetDTO);

	boolean workspaceAccessAuthority(String workspaceResourceName, UserInfoDTO userInfoDTO);

	PageDTO<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition,
		WorkspaceSortCondition sortCondition, int pageNum, int pageSize, UserInfoDTO userInfoDTO);

}
