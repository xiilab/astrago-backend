package com.xiilab.modulek8sdb.version.entity;


import java.time.LocalDateTime;

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
@Table(name = "TB_MAX_CUDA_VERSION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MaxCudaVersionEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MAX_CUDA_VERSION_ID")
	private Long maxCudaVersionId;
	@Column(name = "CUDA_VERSION")
	private String cudaVersion;
	@Column(name = "MAJOR_VERSION")
	private String majorVersion;
	@Column(name = "MINOR_VERSION")
	private String minorVersion;
	@Column(name = "REV")
	private String rev;

	@Column(name = "REG_DATE")
	private LocalDateTime regDate;
	@Column(name = "MOD_DATE")
	private LocalDateTime modeDate;

	@Builder
	public MaxCudaVersionEntity(String cudaVersion, String majorVersion, String minorVersion,
		String rev) {
		this.cudaVersion = cudaVersion;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.rev = rev;
		this.regDate = LocalDateTime.now();
		this.modeDate = LocalDateTime.now();
	}
}
