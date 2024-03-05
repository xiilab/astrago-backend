package com.xiilab.modulek8sdb.common.enums;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class RepositorySearchCondition {
	private RepositoryDivision repositoryDivision;
	private String searchText;
	private RepositorySortType sort;

	public RepositorySearchCondition(RepositoryDivision repositoryDivision, String searchText,
		RepositorySortType sort) {
		this.repositoryDivision = repositoryDivision == null ? null : repositoryDivision;
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.sort = sort == null ? RepositorySortType.NAME : sort;
	}
}
