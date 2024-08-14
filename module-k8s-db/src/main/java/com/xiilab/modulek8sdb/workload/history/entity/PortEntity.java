package com.xiilab.modulek8sdb.workload.history.entity;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8sdb.workload.history.dto.PortDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Collections;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_PORT")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PortEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PORT_ID")
	private Long id;
	@Column(name = "PORT_NAME")
	private String name;
	@Column(name = "PORT_NUM")
	private Integer portNum;
	@Column(name = "PORT_TARTGET_NUM")
	private Integer targetPortNum;
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkloadEntity workload;

	public void setWorkload(WorkloadEntity workload){
		this.workload = workload;
		workload.getPortList().add(this);
	}
	public static List<PortEntity> generatePortList(Map<String, Integer> ports, WorkloadEntity workload) {
		if (CollectionUtils.isEmpty(ports)) {
			return Collections.emptyList();
		}
		return ports.entrySet().stream()
			.map(entry ->
				PortEntity.builder()
					.name(entry.getKey())
					.portNum(entry.getValue())
					.workload(workload)
					.build()
			)
			.toList();
	}
	// 	return ports.stream().map(port -> PortEntity.builder()
	// 		.name(port.getName())
	// 		.portNum(port.getPortNum())
	// 		.targetPortNum(port.getTargetPortNum())
	// 		.workload(workload)
	// 		.build()).toList();
	// }
}
