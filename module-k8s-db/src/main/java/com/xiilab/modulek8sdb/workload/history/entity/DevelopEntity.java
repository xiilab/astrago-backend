package com.xiilab.modulek8sdb.workload.history.entity;

import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DevelopEntity extends WorkloadEntity{
	@Column(name = "REMAIN_TIME")
	protected int remainTime;
	@Column(name = "WORKLOAD_PARAMETER")
	protected String parameter;
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<DatasetWorkLoadMappingEntity> datasetWorkloadMappingList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<ModelWorkLoadMappingEntity> modelWorkloadMappingList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<CodeWorkLoadMappingEntity> codeWorkloadMappingList = new ArrayList<>();
	@OneToOne(mappedBy = "workload")
	protected ImageWorkloadMappingEntity imageWorkloadMappingEntity;
}
