package com.xiilab.servercore.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.facade.dto.WorkspaceTotalDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.modulek8sdb.alert.systemalert.dto.WorkspaceAlertSetDTO;
import com.xiilab.modulek8sdb.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.modulek8sdb.workspace.dto.WorkspaceResourceReqDTO;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.workload.enumeration.WorkspaceSortCondition;
import com.xiilab.servercore.workspace.dto.ClusterResourceCompareDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceQuotaState;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceSettingDTO;

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

	void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO, UserInfoDTO userInfoDTO);

	void deleteResourceQuota(long id);

	List<WorkspaceDTO.WorkspaceResourceStatus> getUserWorkspaceResourceStatus(String workspaceName, UserInfoDTO userInfoDTO);

	WorkspaceAlertSetDTO.ResponseDTO getWorkspaceAlertSet(String workspaceName);

	WorkspaceAlertSetDTO.ResponseDTO updateWorkspaceAlertSet(String workspaceName, WorkspaceAlertSetDTO workspaceAlertSetDTO);

	boolean workspaceAccessAuthority(String workspaceResourceName, UserInfoDTO userInfoDTO);

	PageDTO<WorkspaceDTO.AdminResponseDTO> getAdminWorkspaceList(String searchCondition,
		WorkspaceSortCondition sortCondition, int pageNum, int pageSize, UserInfoDTO userInfoDTO);

	PageDTO<ResourceQuotaFormDTO> getAdminResourceQuotaRequests(int pageNum, int pageSize, UserInfoDTO userInfoDTO);

	WorkspaceDTO.AdminInfoDTO getAdminWorkspaceInfo(String name);
	ClusterResourceCompareDTO requestResourceComparedClusterResource();
	WorkspaceResourceSettingDTO getWorkspaceResourceSetting();
	void updateWorkspaceResourceSetting(WorkspaceResourceSettingDTO workspaceResourceSettingDTO, UserInfoDTO userInfoDTO);
}
