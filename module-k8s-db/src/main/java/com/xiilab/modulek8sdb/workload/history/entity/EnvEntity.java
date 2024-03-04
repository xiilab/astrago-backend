package com.xiilab.modulek8sdb.workload.history.entity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "TB_ENV")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class EnvEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ENV_ID")
	private Long id;
	@Column(name = "ENV_KEY")
	private String key;
	@Column(name = "ENV_VALUE")
	private String value;
	@ManyToOne(fetch = FetchType.LAZY)
	private WorkloadEntity workload;

	public static List<EnvEntity> generateEnvList(Map<String, String> envs, WorkloadEntity workload) {
		if (CollectionUtils.isEmpty(envs)) {
			return Collections.emptyList();
		}
		return envs.entrySet().stream()
			.map(entry ->
				EnvEntity.builder()
					.key(entry.getKey())
					.value(entry.getValue())
					.workload(workload)
					.build()
			)
			.toList();
	}
}
