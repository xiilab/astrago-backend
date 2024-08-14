package com.xiilab.modulek8sdb.deploy.dto;

import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.util.ValidUtils;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public class DeploySearchCondition {
	private String searchText;
	private Integer pageNo;
	private Integer pageSize;
	private WorkloadStatus workloadStatus;

	public DeploySearchCondition(String searchText, Integer pageNo, Integer pageSize, WorkloadStatus workloadStatus) {
		this.searchText = StringUtils.isBlank(searchText) ? null : searchText.replace(" ", "");
		this.workloadStatus = workloadStatus;
		this.pageNo = !ValidUtils.isNullOrZero(pageNo)? pageNo - 1 : 0;
		this.pageSize = !ValidUtils.isNullOrZero(pageSize)? pageSize : 10;
	}
}
