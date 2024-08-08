package com.xiilab.modulek8sdb.common.enums;

import com.xiilab.modulecommon.enums.OutputVolumeYN;
import com.xiilab.modulecommon.util.ValidUtils;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class RepositorySearchCondition {
	private RepositoryDivision repositoryDivision;
	private String searchText;
	private RepositorySortType sort;
	private OutputVolumeYN outputVolumeYN;
	private Integer pageNo;
	private Integer pageSize;
	private String workspaceResourceName;

	public RepositorySearchCondition(RepositoryDivision repositoryDivision, String searchText,
		RepositorySortType sort, OutputVolumeYN outputVolumeYN, Integer pageNo, Integer pageSize, String workspaceResourceName) {
		this.repositoryDivision = repositoryDivision;
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.sort = sort == null ? RepositorySortType.NAME : sort;
		this.outputVolumeYN = outputVolumeYN;
		this.pageNo = !ValidUtils.isNullOrZero(pageNo)? pageNo - 1 : null;
		this.pageSize = pageSize;
		this.workspaceResourceName = workspaceResourceName;
	}
}
