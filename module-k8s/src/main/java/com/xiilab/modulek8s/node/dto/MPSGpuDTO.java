package com.xiilab.modulek8s.node.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.enums.MigStatus;
import com.xiilab.modulek8s.node.enumeration.MPSCapable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
public class MPSGpuDTO {

	@Getter
	@Builder
	public static class MPSInfoDTO {
		private String nodeName;
		// gpu 종류
		private String gpuName;
		// gpu 개수
		private String gpuCnt;
		// mps 설정 유무
		private MPSCapable mpsCapable;
		// mps 설정 개수
		private String mpsReplicas;
		// mps 최대 설정 개수
		private int mpsMaxReplicas;
	}

	@Getter
	public static class SetMPSDTO {
		private String nodeName;
		// mps 설정 유무
		private MPSCapable mpsCapable;
		// mps 설정 개수
		private String mpsReplicas;
		public void setNodeName(String nodeName){
			this.nodeName = nodeName;
		}
	}
}
