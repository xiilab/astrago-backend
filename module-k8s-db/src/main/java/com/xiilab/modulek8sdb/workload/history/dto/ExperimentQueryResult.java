package com.xiilab.modulek8sdb.workload.history.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.WorkloadStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentQueryResult {
	private String id;
	private String workloadName;
	private String workloadResourceName;
	private String workspaceName;
	private WorkloadStatus status;
	private String username;
	private boolean isView;
	private List<LabelDTO> labels = new ArrayList<>();

	public void setLabels(List<LabelDTO> labels) {
		if (!CollectionUtils.isEmpty(labels)) {
			this.labels = labels;
		}
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Experiment {
		private String id;
		private String workloadName;
		private String workloadResourceName;
		private String workspaceName;
		private WorkloadStatus status;
		private String username;
		private boolean isView;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LabelDTO {
		private Long labelId;
		private String labelName;
		private String colorCode;
		private String colorName;
	}
}
