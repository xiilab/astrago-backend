package com.xiilab.modulek8sdb.common.enums;

import com.xiilab.modulecommon.enums.OutputVolumeYN;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class RepositorySearchCondition {
	private RepositoryDivision repositoryDivision;
	private String searchText;
	private RepositorySortType sort;
	private OutputVolumeYN outputVolumeYN;

	public RepositorySearchCondition(RepositoryDivision repositoryDivision, String searchText,
		RepositorySortType sort, OutputVolumeYN outputVolumeYN) {
		this.repositoryDivision = repositoryDivision;
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.sort = sort == null ? RepositorySortType.NAME : sort;
		this.outputVolumeYN = outputVolumeYN;
	}
}
