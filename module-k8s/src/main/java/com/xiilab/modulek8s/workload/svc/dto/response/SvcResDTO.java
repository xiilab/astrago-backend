package com.xiilab.modulek8s.workload.svc.dto.response;

import java.util.Arrays;
import java.util.List;

import com.xiilab.modulek8s.workload.svc.enums.SvcType;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SvcResDTO {
	@Getter
	@Builder
	public static class FindSvcDetail {
		private String svcResourceName;
		private SvcType svcType;
		private List<SvcResDTO.Port> ports;

		public static SvcResDTO.FindSvcDetail from(Service service) {
			return FindSvcDetail.builder()
				.svcResourceName(service.getMetadata().getName())
				.svcType(Arrays.stream(SvcType.values()).filter(svcType -> svcType.getType().equals(service.getSpec().getType())).findFirst().orElseGet(null))
				.ports(service.getSpec()
					.getPorts()
					.stream()
					.map(port -> new Port(port.getName(), port.getPort(), port.getNodePort()))
					.toList())
				.build();
		}
	}

	@Getter
	@Builder
	public static class FindSvcs {
		private List<SvcResDTO.FindSvcDetail> services;
		private long totalCount;

		public static SvcResDTO.FindSvcs from(ServiceList services, long totalCount) {
			return FindSvcs.builder()
				.services(services.getItems().stream().map(SvcResDTO.FindSvcDetail::from).toList())
				.totalCount(totalCount)
				.build();
		}
	}


	@Getter
	@AllArgsConstructor
	public static class Port {
		private String name;
		private Integer port;
		private Integer nodePort;
	}
}
