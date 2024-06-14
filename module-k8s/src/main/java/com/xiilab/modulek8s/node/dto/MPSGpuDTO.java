package com.xiilab.modulek8s.node.dto;

import com.xiilab.modulecommon.enums.MPSStatus;

import lombok.Builder;
import lombok.Getter;
@Getter
public class MPSGpuDTO {

	@Getter
	@Builder
	public static class MPSInfoDTO {
		private String nodeName;
		// gpu 종류
		private String gpuName;
		// gpu 개수
		private int gpuCnt;
		// mps 설정 유무
		private boolean mpsCapable;
		// mps 설정 개수
		private int mpsReplicas;
		// gpu 메모리 양
		private int totalMemory;
		// mps 최대 설정 개수
		private int mpsMaxReplicas;
		private MPSStatus mpsStatus;
	}

	@Getter
	public static class SetMPSDTO {
		private String nodeName;
		// mps 설정 유무
		private boolean mpsCapable;
		// mps 설정 개수
		private int mpsReplicas;
		public void setNodeName(String nodeName){
			this.nodeName = nodeName;
		}
	}
}
