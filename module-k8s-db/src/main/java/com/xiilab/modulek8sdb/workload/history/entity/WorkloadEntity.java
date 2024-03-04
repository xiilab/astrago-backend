package com.xiilab.modulek8sdb.workload.history.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity(name = "TB_WORKLOAD")
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class WorkloadEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WORKLOAD_ID")
	protected Long id;
	@Column(name = "WORKLOAD_NAME")
	protected String name;
	@Column(name = "WORKLOAD_DESCRIPTION")
	protected String description;
	@Column(name = "WORKLOAD_RESOURCE_NAME")
	protected String resourceName;
	@Column(name = "WORKSPACE_NAME")
	protected String workspaceName;
	@Column(name = "WORKSPACE_RESOURCE_NAME")
	protected String workspaceResourceName;
	@Column(name = "WORKLOAD_REQ_GPU", precision = 10, scale = 1)
	protected Integer gpuRequest;
	@Column(name = "WORKLOAD_REQ_CPU")
	protected BigDecimal cpuRequest;
	@Column(name = "WORKLOAD_REQ_MEM", precision = 10, scale = 1)
	protected BigDecimal memRequest;
	@Column(name = "WORKLOAD_CREATOR")
	protected String creatorName;
	@Column(name = "WORKLOAD_CREATOR_ID")
	protected String creatorId;
	@Column(name = "WORKLOAD_CREATED_AT")
	protected LocalDateTime createdAt;
	@Column(name = "WORKLOAD_DELETED_AT")
	protected LocalDateTime deletedAt;
	@Column(name = "WORKLOAD_TYPE")
	@Enumerated(value = EnumType.STRING)
	protected WorkloadType workloadType;
	@Column(name = "WORKLOAD_CMD")
	protected String workloadCMD;
	@ManyToOne(fetch = FetchType.EAGER)
	protected ImageEntity image;
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<EnvEntity> envList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<VolumeEntity> volumeList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<PortEntity> portList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<DatasetWorkLoadMappingEntity> datasetWorkloadMappingList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<ModelWorkLoadMappingEntity> modelWorkloadMappingList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", fetch = FetchType.LAZY)
	protected List<CodeWorkLoadMappingEntity> codeWorkloadMappingList = new ArrayList<>();
}
