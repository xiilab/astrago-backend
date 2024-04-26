package com.xiilab.modulemonitor.dto;

import java.util.Comparator;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.util.DataConverterUtil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReportDTO {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceUtilDTO {
		private long gpuUtil;
		private long cpuUtil;
		private long memUtil;
		private double resourceScore;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceDTO {
		private String resourceName;
		private List<ValueDTO> valueDTOS;
	}

	@Builder
	public record ValueDTO(String dateTime,
						   String value) {
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class StatisticsDTO {
		private String workspaceName;
		private long gpuRequest;
		private long cpuRequest;
		private long memRequest;
		private long gpuUtil;
		private long cpuUtil;
		private long memUtil;
		private double resourceScore;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceQuotaDTO {
		private long gpuRequest;
		private long gpuApproval;
		private long gpuRefuseCount;
		private long cpuRequest;
		private long cpuApproval;
		private long cpuRefuseCount;
		private long memRequest;
		private long memApproval;
		private long memRefuseCount;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class WorkspaceResourceQuotaDTO {
		private String workspaceName;
		private String workspaceResourceName;
		private String userName;
		private long gpuRequest;
		private long gpuApproval;
		private long gpuRefuseCount;
		private long cpuRequest;
		private long cpuApproval;
		private long cpuRefuseCount;
		private long memRequest;
		private long memApproval;
		private long memRefuseCount;
	}

	@Builder
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SystemInfoDTO {
		private String serverName;
		private String ip;
		private String os;
		private String cpuInfo;
		private long cpu;
		private long mem;
		private long disk;
		private String gpuModelName;
		private long gpu;
	}

	@Builder
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SystemGpuDTO {
		private String serverName;
		private List<String> availableDate;
		private long gpuIndex;
		private List<SystemCategoryDTO> categoryDTOS;

		public void setAvailableDate() {
			if (!CollectionUtils.isEmpty(categoryDTOS)) {
				SystemCategoryDTO systemCategoryDTO = categoryDTOS.get(0);
				availableDate = systemCategoryDTO.getValueDTOS().stream().map(value -> value.date).toList();
			}
		}

		public void sortByCategory() {
			if (!CollectionUtils.isEmpty(categoryDTOS)) {
				this.getCategoryDTOS().sort(Comparator.comparing(SystemCategoryDTO::getCategory));
			}
		}
	}

	@Builder
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SystemResDTO {
		private String serverName;
		private List<String> availableDate;
		private String ip;
		private List<SystemCategoryDTO> categoryDTOS;

		public void setAvailableDate() {
			if (!CollectionUtils.isEmpty(categoryDTOS)) {
				SystemCategoryDTO systemCategoryDTO = categoryDTOS.get(0);
				availableDate = systemCategoryDTO.getValueDTOS().stream().map(value -> value.date).toList();
			}
		}
	}

	@Builder
	@Setter
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SystemValueDTO {
		private String date;
		private double value;

		public static SystemValueDTO weeklyValueBuilder(ResponseDTO.ValueDTO valueDTO) {
			return new SystemValueDTO(
				DataConverterUtil.convertToMonthDay(valueDTO.dateTime()),
				DataConverterUtil.formatRoundTo(valueDTO.value()));
		}

		public static SystemValueDTO monthlyValueBuilder(ResponseDTO.ValueDTO valueDTO) {
			return new SystemValueDTO(
				DataConverterUtil.getDateMonthWeek(valueDTO.dateTime()),
				DataConverterUtil.formatRoundTo(valueDTO.value()));
		}
	}

	@Builder
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SystemCategoryDTO {
		private String category;
		private List<SystemValueDTO> valueDTOS;
	}
}
