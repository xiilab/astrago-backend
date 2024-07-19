package com.xiilab.modulek8s.facade.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResMIGDTO {
	private String gpuName;
	private List<MigInfo> migInfoList;

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class MigInfo {
		private String migType;
		private int count;
	}
}
