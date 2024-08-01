package com.xiilab.modulek8sdb.workload.history.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;

import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;

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
import jakarta.persistence.Transient;
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
@SQLDelete(sql = "UPDATE TB_WORKLOAD tw SET tw.DELETE_YN = 'Y' WHERE tw.WORKLOAD_ID = ?")
public abstract class WorkloadEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "WORKLOAD_ID")
	protected Long id;
	@Column(name = "WORKLOAD_UID")
	protected String uid;
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
	@Column(name = "WORKLOAD_CREATOR")
	protected String creatorName;
	@Column(name = "WORKLOAD_CREATOR_REAL_NAME")
	protected String creatorRealName;
	@Column(name = "WORKLOAD_CREATOR_ID")
	protected String creatorId;
	@Column(name = "WORKLOAD_CREATED_AT")
	protected LocalDateTime createdAt;
	@Column(name = "WORKLOAD_DELETED_AT")
	protected LocalDateTime deletedAt;
	@Column(name = "WORKLOAD_TYPE")
	@Enumerated(value = EnumType.STRING)
	protected WorkloadType workloadType;
	@Column(name = "WORKING_DIR")
	protected String workingDir;
	@Column(name = "WORKLOAD_CMD")
	protected String workloadCMD;
	@Enumerated(value = EnumType.STRING)
	@Column(name = "WORKLOAD_STATUS")
	protected WorkloadStatus workloadStatus;
	// @ManyToOne(fetch = FetchType.EAGER)
	// protected ImageEntity image;
	@Column(name = "DELETE_YN")
	@Enumerated(EnumType.STRING)
	protected DeleteYN deleteYN;
	@Column(name = "START_TIME")
	protected LocalDateTime startTime;
	@Column(name = "END_TIME")
	protected LocalDateTime endTime;
	@Column(name = "NODE_NAME")
	protected String nodeName;
	@Column(name = "GPU_NAME")
	protected String gpuName;
	@Enumerated(EnumType.STRING)
	@Column(name = "GPU_TYPE")
	protected GPUType gpuType;
	@Column(name = "GPU_ONE_PER_MEMORY")
	protected Integer gpuOnePerMemory;
	@Column(name = "RESOURCE_PRESET_ID")
	protected Integer resourcePresetId;
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<EnvEntity> envList = new ArrayList<>();
	// TODO 삭제 예정
	// @Builder.Default
	// @OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	// protected List<VolumeEntity> volumeList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<PortEntity> portList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<LabelWorkloadMappingEntity> labelList = new ArrayList<>();
	@Builder.Default
	@OneToMany(mappedBy = "workload", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	protected List<ExperimentEntity> experimentList = new ArrayList<>();
	@Transient
	protected boolean canBeDeleted;

	// public void updateImage(ImageEntity image) {
	// 	this.image = image;
	// }

	public void updateJob(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public void updateCanBeDeleted(String creator, Set<String> ownerWorkspace) {
		if (this.creatorId.equals(creator) || ownerWorkspace.contains(this.workspaceResourceName)) {
			this.canBeDeleted = true;
		}
	}

	public void updateCanBeDeleted(boolean isAdmin) {
		if (isAdmin) {
			this.canBeDeleted = true;
		}
	}

	public void addExperiment(String uuid) {
		Optional<ExperimentEntity> expOpt = this.experimentList.stream()
			.filter(ex -> ex.getUuid().equals(uuid))
			.findAny();
		if (expOpt.isEmpty()) {
			this.experimentList.add(ExperimentEntity.builder()
				.uuid(uuid)
				.createdTime(LocalDateTime.now())
				.workload(this)
				.build());
		}
	}
}
