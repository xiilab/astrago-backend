package com.xiilab.modulek8sdb.deploy.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulecommon.enums.DeployType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;
import com.xiilab.modulek8sdb.workload.history.entity.EnvEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;

@Entity
@Table(name = "TB_DEPLOY")
@Getter
public class DeployEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DEPLOY_ID")
	private Long deployId;
	@Column(name = "DEPLOY_NAME")
	private Long name;
	@Column(name = "DEPLOY_DESCRIPTION")
	private Long description;
	@Column(name = "DEPLOY_TYPE")
	@Enumerated(EnumType.STRING)
	private DeployType deployType;
	@Column(name = "GPU_NAME")
	protected String gpuName;
	@Enumerated(EnumType.STRING)
	@Column(name = "GPU_TYPE")
	protected GPUType gpuType;
	@Column(name = "GPU_ONE_PER_MEMORY")
	protected Integer gpuOnePerMemory;
	@Column(name = "REPLICA")
	protected Integer replica;
	@Column(name = "REQ_CPU")
	private Float cpuRequest;
	@Column(name = "REQ_MEM")
	private Float memRequest;
	@Column(name = "REQ_GPU")
	private Integer gpuRequest;
	@Column(name = "WORKING_DIR")
	protected String workingDir;
	@Column(name = "DEPLOY_CMD")
	protected String workloadCMD;
	@Column(name = "DEPLOY_STATUS")
	protected WorkloadStatus deployStatus;
	@ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "IMAGE_ID")
	protected ImageEntity image;
	@Enumerated(EnumType.STRING)
	protected DeleteYN deleteYN;
	@Column(name = "DEPLOY_RESOURCE_NAME")
	protected String resourceName;
	@Column(name = "WORKSPACE_NAME")
	protected String workspaceName;
	@Column(name = "WORKSPACE_RESOURCE_NAME")
	protected String workspaceResourceName;
	@Column(name = "DEPLOY_CREATOR")
	protected String creatorName;
	@Column(name = "DEPLOY_CREATOR_REAL_NAME")
	protected String creatorRealName;
	@Column(name = "DEPLOY_CREATOR_ID")
	protected String creatorId;
	@Column(name = "DEPLOY_CREATED_AT")
	protected LocalDateTime createdAt;
	@Column(name = "DEPLOY_DELETED_AT")
	protected LocalDateTime deletedAt;
	@Transient
	protected boolean canBeDeleted;
	// private String modelId;
	// private String versionId;
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<EnvEntity> envList = new ArrayList<>();
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<PortEntity> portList = new ArrayList<>();

}
