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
	@Getter
	public static class NodeResourceInfo{
		private String gpuType;
		private String gpuCount;
		private String gpuMem;
		private String gpuDriverVersion;
		private Capacity capacity;
		private Allocatable allocatable;
		private Requests requests;
		private Limits limits;

		public void setRequests(Requests requests){
			this.requests = requests;
		}
		public void setLimits(Limits limits){
			this.limits = limits;
		}
		@Builder
		@Getter
		public static class Capacity{
			private String capacityCpu;
			private String capacityEphemeralStorage;
			private String capacityHugepages1Gi;
			private String capacityHugepages2Mi;
			private String capacityMemory;
			private String capacityPods;
			private String capacityGpu;
		}
		@Builder
		@Getter
		public static class Allocatable{
			private String allocatableCpu;
			private String allocatableEphemeralStorage;
			private String allocatableHugepages1Gi;
			private String allocatableHugepages2Mi;
			private String allocatableMemory;
			private String allocatablePods;
			private String allocatableGpu;
		}
		@Getter
		@Builder
		public static class Requests{
			private long cpu;
			private long memory;
			private long gpu;
			private int cpuPercent;
			private int memoryPercent;
			private int gpuPercent;

			public void cpuPercentCalculation(double totalCPU){
				long cpuCore = this.cpu / 1000;
				this.cpuPercent = (int)(cpuCore / totalCPU * 100);
			}
			public void memoryPercentCalculation(double totalMEM){
				this.memoryPercent = (int)(this.memory / totalMEM * 100);
			}
			public void gpuPercentCalculation(double totalGPU){
				this.gpuPercent = (int)(this.gpu / totalGPU * 100);
			}
		}
		@Getter
		@Builder
		public static class Limits{
			private long cpu;
			private long memory;
			private long gpu;
			private int cpuPercent;
			private int memoryPercent;
			private int gpuPercent;

			public void cpuPercentCalculation(double totalCPU){
				long cpuCore = this.cpu / 1000;
				this.cpuPercent = (int)(cpuCore / totalCPU * 100);
			}
			public void memoryPercentCalculation(double totalMEM){
				this.memoryPercent = (int)(this.memory / totalMEM * 100);
			}
			public void gpuPercentCalculation(double totalGPU){
				this.gpuPercent = (int)(this.gpu / totalGPU * 100);
			}
		}
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
