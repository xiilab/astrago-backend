package com.xiilab.servercore.workspace.service;

import java.util.List;

import com.xiilab.modulek8s.common.dto.PageDTO;
import com.xiilab.modulek8s.workspace.dto.WorkspaceDTO;
import com.xiilab.servercore.common.dto.UserInfoDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaApproveDTO;
import com.xiilab.servercore.workspace.dto.ResourceQuotaFormDTO;
import com.xiilab.servercore.workspace.dto.WorkspaceApplicationForm;
import com.xiilab.servercore.workspace.dto.WorkspaceResourceReqDTO;

public interface WorkspaceFacadeService {
	void createWorkspace(WorkspaceApplicationForm applicationForm, UserInfoDTO userInfoDTO);

	PageDTO<WorkspaceDTO.TotalResponseDTO> getWorkspaceList(boolean isMyWorkspace, String searchCondition, int pageNum,
		UserInfoDTO userInfoDTO);

	void deleteWorkspaceByName(String workspaceName);

	List<WorkspaceDTO.TotalResponseDTO> getWorkspaceOverView(UserInfoDTO userInfoDTO);

	void requestWorkspaceResource(WorkspaceResourceReqDTO workspaceResourceReqDTO, UserInfoDTO userInfoDTO);

	List<ResourceQuotaFormDTO> getResourceQuotaRequests(String workspace, UserInfoDTO userInfoDTO);

	void updateResourceQuota(long id, ResourceQuotaApproveDTO resourceQuotaApproveDTO);
}
