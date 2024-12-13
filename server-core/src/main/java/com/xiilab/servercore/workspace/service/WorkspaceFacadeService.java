package com.xiilab.servercore.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workspace.dto.ClusterResourceCompareDTO;
import com.xiilab.servercore.workspace.dto.FindWorkspaceResDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceSettingDTO;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm, UserDTO.UserInfo userInfoDTO);

	PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition, int pageNum,
		UserDTO.UserInfo userInfoDTO);

	WorkspaceDTO.FindJoinedWorkspaces getJoinedWorkspaceList(String title, UserDTO.UserInfo userInfoDTO);

	void updateWorkspace(String workspaceName, WorkspaceDTO.UpdateDTO updateDTO);

	void deleteWorkspaceByName(String workspaceName, UserDTO.UserInfo userInfoDTO);

	List<WorkspaceDTO.TotalResponseDTO> getWorkspaceOverView(UserDTO.UserInfo userInfoDTO);

	WorkspaceResourceQuotaState getWorkspaceResourceQuotaState(String workspaceResourceName);

	WorkspaceResourceQuotaState getWorkspaceResourceUsage(String workspaceResourceName);

	WorkspaceTotalDTO getWorkspaceInfoByName(String workspaceResourceName);

	FindWorkspaceResDTO.JoinedWorkspaceDetail getJoinedWorkspaceInfoByName(String workspaceResourceName, String regUserId);

	void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO, UserDTO.UserInfo userInfoDTO);

	PageDTO<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, int pageNum, UserDTO.UserInfo userInfoDTO);

	void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO, UserDTO.UserInfo userInfoDTO);

	void deleteResourceQuota(long id);

	List<WorkspaceDTO.WorkspaceResourceStatus> getUserWorkspaceResourceStatus(String workspaceName,
		UserDTO.UserInfo userInfoDTO);

	WorkspaceAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);

	WorkspaceAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName,
		WorkspaceAlertSetDTO workspaceAlertSetDTO);

	boolean workspaceAccessAuthority(String workspaceResourceName, UserDTO.UserInfo userInfoDTO);

	PageDTO<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition,
		WorkspaceSortCondition sortCondition, int pageNum, int pageSize, UserDTO.UserInfo userInfoDTO);

	PageDTO<ResourceQuotaFormDTO> getAdminResourceQuotaRequests(int pageNum, int pageSize,
		UserDTO.UserInfo userInfoDTO);

	WorkspaceDTO.AdminInfoDTO getAdminWorkspaceInfo(String name);

	ClusterResourceCompareDTO requestResourceComparedClusterResource();

	WorkspaceResourceSettingDTO getWorkspaceResourceSetting();

	void updateWorkspaceResourceSetting(WorkspaceResourceSettingDTO workspaceResourceSettingDTO,
		UserDTO.UserInfo userInfoDTO);

	void validRedirectWorkspace(String workspaceResourceName);
}
