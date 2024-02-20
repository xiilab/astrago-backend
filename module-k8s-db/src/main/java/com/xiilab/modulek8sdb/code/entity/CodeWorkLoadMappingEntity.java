package com.xiilab.modulek8sdb.code.entity;


import org.aspectj.apache.bcel.classfile.Code;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_CODE_WORKLOAD_MAPPING")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CodeWorkLoadMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CODE_WORKLOAD_MAPPING_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CODE_ID")
	@JsonIgnore
	private CodeEntity code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "WORKLOAD_ID")
	private WorkloadEntity workload;

	@Builder
	public CodeWorkLoadMappingEntity(CodeEntity code, WorkloadEntity workload) {
		this.code = code;
		this.workload = workload;
	}
}
