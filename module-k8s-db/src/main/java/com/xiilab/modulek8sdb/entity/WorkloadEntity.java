package com.xiilab.modulek8sdb.entity;

import java.time.LocalDateTime;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
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
	@Column(name = "WORKLOAD_REQ_GPU")
	protected Integer gpuRequest;
	@Column(name = "WORKLOAD_REQ_CPU")
	protected Integer cpuRequest;
	@Column(name = "WORKLOAD_REQ_MEM")
	protected Integer memRequest;
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
	@OneToOne(fetch = FetchType.EAGER)
	protected ImageEntity image;
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<EnvEntity> envList;
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<VolumeEntity> volumeList;
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<PortEntity> portList;
}
