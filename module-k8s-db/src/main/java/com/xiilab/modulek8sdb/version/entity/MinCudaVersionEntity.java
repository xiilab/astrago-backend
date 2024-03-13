package com.xiilab.modulek8sdb.version.entity;

import com.xiilab.modulek8sdb.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_MIN_CUDA_VERSION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MinCudaVersionEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MIN_CUDA_VERSION_ID")
	private Long minCudaVersionId;
	@Column(name = "CUDA_VERSION")
	private String cudaVersion;
	@Column(name = "MAX_VERSION")
	private String maxVersion;
	@Column(name = "MIN_VERSION")
	private String minVersion;

	@Builder
	public MinCudaVersionEntity(String cudaVersion, String maxVersion, String minVersion) {
		this.cudaVersion = cudaVersion;
		this.maxVersion = maxVersion;
		this.minVersion = minVersion;
	}
}
