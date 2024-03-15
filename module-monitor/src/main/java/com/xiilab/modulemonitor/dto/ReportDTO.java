package com.xiilab.modulemonitor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportDTO {

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceUtilDTO{
		private long gpuUtil;
		private long cpuUtil;
		private long memUtil;
		private double resourceScore;
	}

	@Builder
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ResourceDTO{
		private String resourceName;
		private List<ValueDTO> valueDTOS;
	}


	@Builder
	public record ValueDTO(String dateTime,
						   String value) {
	}

}
