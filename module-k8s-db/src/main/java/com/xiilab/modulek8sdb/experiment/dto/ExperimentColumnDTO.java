package com.xiilab.modulek8sdb.experiment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExperimentColumnDTO {

	@Getter
	public static class Req {
		private String name;
		private int order;
		private String workspace;
		private String userId;
	}

	@Getter
	@Builder
	public static class Res {
		private Long id;
		private String name;
		private int order;
	}
}
