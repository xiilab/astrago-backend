package com.xiilab.modulek8sdb.code.entity;

import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@SQLDelete(sql = "UPDATE TB_CODE_WORKLOAD_MAPPING tcwm SET tcwm.DELETE_YN = 'Y' WHERE tcwm.CODE_WORKLOAD_MAPPING_ID = ?")
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

	@Column(name = "branch")
	private String branch;

	@Column(name = "mountPath")
	private String mountPath;

	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	private DeleteYN deleteYN = DeleteYN.N;

	@Column(name = "CMD")
	private String cmd;

	@Builder
	public CodeWorkLoadMappingEntity(CodeEntity code, WorkloadEntity workload, String branch, String mountPath,
		String cmd) {
		this.code = code;
		this.workload = workload;
		this.branch = branch;
		this.mountPath = mountPath;
		this.cmd = cmd;
	}
}
