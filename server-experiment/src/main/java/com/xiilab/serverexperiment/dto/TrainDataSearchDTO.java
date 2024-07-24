package com.xiilab.serverexperiment.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class TrainDataSearchDTO {
	private String workloadName;
	private List<Value> value;

	@Getter
	public static class Value {
		private int step;
		private int epoch;
		private Map<String, Object> metrics;
	}
}
