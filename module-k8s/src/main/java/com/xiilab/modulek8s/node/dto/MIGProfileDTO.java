package com.xiilab.modulek8s.node.dto;

import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.MigStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MIGProfileDTO {
	@Setter
	private String device;
	private List<Map<String, Integer>> profile;
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Status {
		private String device;
		private Map<String,Integer> profile;
		private MigStatus status;
	}
}
