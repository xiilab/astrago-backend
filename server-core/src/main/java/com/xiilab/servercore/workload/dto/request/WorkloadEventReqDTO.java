package com.xiilab.servercore.workload.dto.request;

import com.xiilab.modulecommon.enums.K8sContainerReason;
import com.xiilab.servercore.workload.enumeration.WorkloadEventAgeSortCondition;
import com.xiilab.servercore.workload.enumeration.WorkloadEventTypeSortCondition;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadEventReqDTO {
	private String workload;
	private String workspace;
	private WorkloadEventTypeSortCondition typeSortCondition;
	private WorkloadEventAgeSortCondition ageSortCondition;
	private K8sContainerReason k8SReasonType;
	private String searchCondition;
	private int pageNum;
	private int pageSize;
}
