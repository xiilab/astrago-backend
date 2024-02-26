package com.xiilab.modulek8s.node.dto;

import java.util.List;

import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.node.enumeration.MIGProduct;

import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import lombok.Builder;
import lombok.Getter;

public class ResponseDTO {

	@Builder
	@Getter
	public static class NodeDTO {
		private String nodeName;
		private String ip;
		private String totalGPU;
		private String totalMEM;
		private String totalCPU;
		private String totalDISK;
		private String requestGPU;
		private String requestMEM;
		private String requestCPU;
		private String requestDISK;
		private AgeDTO age;
		private int gpuCount;
		private boolean status;
		private boolean schedulable;

		public void setTotalGPU(String totalGPU){
			this.totalGPU = totalGPU;
		}
		public void setRequestGPU(String requestGPU){
			this.requestGPU = requestGPU;
		}public void setTotalMEM(String totalMEM){
			this.totalMEM = totalMEM;
		}
		public void setRequestMEM(String requestMEM){
			this.requestMEM = requestMEM;
		}public void setTotalCPU(String totalCPU){
			this.totalCPU = totalCPU;
		}
		public void setRequestCPU(String requestCPU){
			this.requestCPU = requestCPU;
		}
		public void setTotalDISK(String totalDISK){
			this.totalDISK = totalDISK;
		}
		public void setRequestDISK(String requestDISK){
			this.requestDISK = requestDISK;
		}
	}
	@Builder
	@Getter
	public static class NodeInfo{
		private String nodeName;
		private String ip;
		private String hostName;
		private String role;
		private String creationTimestamp;
		private List<NodeCondition>  nodeCondition;
		private NodeSystemInfo nodeSystemInfo;

	}
	@Builder
	public record MIGProfile(MIGProduct migProduct,
							 List<MIGInfo> migInfos){
	}

	@Builder
	public record MIGInfo(String migProfile,
						  int count){
	}
	public record MIGProfileList(List<MIGProfile> migProfiles){
	}
}
