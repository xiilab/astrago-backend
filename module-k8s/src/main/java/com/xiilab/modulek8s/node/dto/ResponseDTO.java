package com.xiilab.modulek8s.node.dto;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.xiilab.modulecommon.util.DataConverterUtil;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.node.enumeration.MIGProduct;

import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSystemInfo;
import lombok.Builder;
import lombok.Getter;

public class ResponseDTO {

	@Builder
	@Getter
	public static class PageNodeDTO {
		private List<NodeDTO> nodes;
		private long totalCount;
		private long totalPageCount;
	}

	@Builder
	@Getter
	public static class NodeDTO {
		private String nodeName;
		private String ip;
		private double totalGPU;
		private double totalMpsGPU;
		private double totalMEM;
		private double totalCPU;
		private double totalDISK;
		private double requestGPU;
		private double requestMpsGPU;
		private double requestMEM;
		private double requestCPU;
		private double requestDISK;
		private double gpuPercent;
		private double cpuPercent;
		private double memPercent;
		private double diskPercent;

		private AgeDTO age;
		private int gpuCount;
		private boolean status;
		private boolean schedulable;
		//mig 사용가능 유무
		private boolean migCapable;
		//mig 적용 유무
		private boolean isActiveMIG;
		private boolean isActiveMPS;
		//mps 사용가능 유무
		private boolean mpsCapable;
		private boolean migStatus;

		public void setTotalGPU(double totalGPU) {
			this.totalGPU = roundToFirstDecimalPlace(totalGPU);
		}

		public void setTotalMpsGPU(double totalMpsGPU) {
			this.totalMpsGPU = roundToFirstDecimalPlace(totalMpsGPU);
		}

		public void setRequestGPU(double requestGPU) {
			this.requestGPU = roundToFirstDecimalPlace(requestGPU);
		}

		public void setRequestMpsGPU(double requestMpsGPU) {
			this.requestMpsGPU = roundToFirstDecimalPlace(requestMpsGPU);
		}

		public void setTotalMEM(double totalMEM) {
			this.totalMEM = roundToFirstDecimalPlace(totalMEM);
		}

		public void setRequestMEM(double requestMEM) {
			this.requestMEM = roundToFirstDecimalPlace(requestMEM);
		}

		public void setTotalCPU(double totalCPU) {
			this.totalCPU = roundToFirstDecimalPlace(totalCPU);
		}

		public void setRequestCPU(double requestCPU) {
			this.requestCPU = roundToFirstDecimalPlace(requestCPU);
		}

		public void setTotalDISK(double totalDISK) {
			this.totalDISK = roundToFirstDecimalPlace(totalDISK);
		}

		public void setRequestDISK(double requestDISK) {
			this.requestDISK = roundToFirstDecimalPlace(requestDISK);
		}

		public void percentCalculation() {
			this.gpuPercent = calculatePercentage(this.requestGPU, this.totalGPU);
			this.cpuPercent = calculatePercentage(this.requestCPU, this.totalCPU);
			this.memPercent = calculatePercentage(this.requestMEM, this.totalMEM);
			this.diskPercent = calculatePercentage(this.requestDISK, this.totalDISK);
		}

		private int calculatePercentage(double request, double total) {
			if (total == 0) {
				return 0;
			}
			return (int)Math.ceil(request / total * 100);
		}

		public static double roundToFirstDecimalPlace(double number) {
			DecimalFormat df = new DecimalFormat("#.#");
			return Double.parseDouble(df.format(number));
		}
	}

	@Builder
	@Getter
	public static class NodeInfo {
		private String nodeName;
		private String ip;
		private String hostName;
		private String role;
		private String creationTimestamp;
		private List<NodeCondition> nodeCondition;
		private NodeSystemInfo nodeSystemInfo;
	}

	@Builder
	@Getter
	public static class WorkerNodeDriverInfo {
		private String driverMajor;
		private String driverMinor;
		private String driverRev;
		private String computeMajor;
		private String computeMinor;
	}

	@Builder
	@Getter
	public static class NodeResourceInfo {
		private String gpuType;
		private String gpuCount;
		private String gpuMem;
		private String gpuDriverVersion;
		private Capacity capacity;
		private Allocatable allocatable;
		private Requests requests;
		private Limits limits;

		public void setRequests(Requests requests) {
			this.requests = requests;
		}

		public void setLimits(Limits limits) {
			this.limits = limits;
		}

		@Builder
		@Getter
		public static class Capacity {
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
		public static class Allocatable {
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
		public static class Requests {
			private long cpu;
			private long memory;
			private long gpu;
			private int cpuPercent;
			private int memoryPercent;
			private int gpuPercent;

			public void cpuPercentCalculation(double totalCPU) {
				double cpuCore = this.cpu / 1000.0;
				this.cpuPercent = roundToFirstDecimalPlace((cpuCore / totalCPU * 100));
			}

			public void memoryPercentCalculation(double totalMEM) {
				this.memoryPercent = roundToFirstDecimalPlace((this.memory / totalMEM * 100));
			}

			public void gpuPercentCalculation(double totalGPU) {
				this.gpuPercent = roundToFirstDecimalPlace((this.gpu / totalGPU * 100));
			}

			public static int roundToFirstDecimalPlace(double number) {
				return (int)(Math.ceil(number));
			}
		}

		@Getter
		@Builder
		public static class Limits {
			private long cpu;
			private long memory;
			private long gpu;
			private int cpuPercent;
			private int memoryPercent;
			private int gpuPercent;

			public void cpuPercentCalculation(double totalCPU) {
				double cpuCore = this.cpu / 1000.0;
				this.cpuPercent = roundToFirstDecimalPlace((cpuCore / totalCPU * 100));
			}

			public void memoryPercentCalculation(double totalMEM) {
				this.memoryPercent = roundToFirstDecimalPlace((this.memory / totalMEM * 100));
			}

			public void gpuPercentCalculation(double totalGPU) {
				this.gpuPercent = roundToFirstDecimalPlace((this.gpu / totalGPU * 100));
			}

			public static int roundToFirstDecimalPlace(double number) {
				return (int)(Math.ceil(number));
			}
		}
	}

	@Getter
	@Builder
	public static class NodeGPUs {
		private Map<String, List<GPUInfo>> normalGPU;
		private Map<String, List<GPUInfo>> migGPU;
		private Map<String, List<GPUInfo>> mpsGPU;

		@Getter
		public static class GPUInfo {
			private String nodeName;
			private String onePerMemory;
			private Integer count;

			public GPUInfo(String nodeName, Integer onePerMemory, Integer count) {
				this.nodeName = nodeName;
				this.onePerMemory = DataConverterUtil.convertMbToGb(onePerMemory) + "GB";
				this.count = count;
			}
		}
	}


	@Builder
	public record MIGProfile(MIGProduct migProduct,
							 List<MIGInfo> migInfos) {
	}

	@Builder
	public record MIGInfo(String migProfile,
						  int count) {
	}

	public record MIGProfileList(List<MIGProfile> migProfiles) {
	}
}
