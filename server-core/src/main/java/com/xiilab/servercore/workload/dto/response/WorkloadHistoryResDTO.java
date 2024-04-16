package com.xiilab.servercore.workload.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.Hibernate;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.workload.history.entity.EnvEntity;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;
import com.xiilab.servercore.common.utils.CoreFileUtils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class WorkloadHistoryResDTO {
	protected String regUserName;
	protected String regUserRealName;
	protected LocalDateTime createdAt;

	@Getter
	@SuperBuilder
	public static class FindWorkload extends WorkloadHistoryResDTO {
		private String workloadName;
		private String workloadResourceName;
		private String description;
		private String workspaceName;
		private String workSpaceResourceName;
		private WorkloadType workloadType;
		private WorkloadHistoryResDTO.Image image;
		private List<WorkloadHistoryResDTO.Port> ports;
		private List<WorkloadHistoryResDTO.Env> envs;
		private List<WorkloadHistoryResDTO.Volume> datasets;
		private List<WorkloadHistoryResDTO.Volume> models;
		private List<WorkloadHistoryResDTO.Code> codes;
		private String command;
		private Float cpuRequest;
		private Float gpuRequest;
		private Float memRequest;

		public static FindWorkload from(JobEntity jobEntity) {
			return FindWorkload.builder()
				.workloadName(jobEntity.getName())
				.workloadResourceName(jobEntity.getResourceName())
				.description(jobEntity.getDescription())
				.workspaceName(jobEntity.getWorkspaceName())
				.workSpaceResourceName(jobEntity.getWorkspaceResourceName())
				.workloadType(jobEntity.getWorkloadType())
				.image(new Image(jobEntity.getImage()))
				.ports(jobEntity.getPortList().stream().map(Port::new).toList())
				.envs(jobEntity.getEnvList().stream().map(Env::new).toList())
				.datasets(jobEntity.getDatasetWorkloadMappingList().stream().map(Volume::new).toList())
				.models(jobEntity.getModelWorkloadMappingList().stream().map(Volume::new).toList())
				.codes(jobEntity.getCodeWorkloadMappingList().stream().map(Code::new).toList())
				.command(jobEntity.getWorkloadCMD())
				.cpuRequest(jobEntity.getCpuRequest().floatValue())
				.gpuRequest(jobEntity.getGpuRequest().floatValue())
				.memRequest(jobEntity.getMemRequest().floatValue())
				.regUserName(jobEntity.getCreatorName())
				.regUserRealName(jobEntity.getCreatorRealName())
				.createdAt(jobEntity.getCreatedAt())
				.build();
		}

	}

	@Getter
	public static class Image extends WorkloadHistoryResDTO {
		private Long id;
		private String name;
		private ImageType type;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;

		public Image(ImageEntity imageEntity) {
			this.id = imageEntity.getId();
			this.name = imageEntity.getImageNameHub();
			this.type = imageEntity.getImageType();
			this.repositoryAuthType = imageEntity.getRepositoryAuthType();
			if (imageEntity.getImageType() == ImageType.CUSTOM
				&& ((CustomImageEntity)imageEntity).getCredentialEntity() != null) {
				this.credentialId = ((CustomImageEntity)imageEntity).getCredentialEntity().getId();
			}
			this.regUserName = imageEntity.getRegUser().getRegUserName();
			this.regUserRealName = imageEntity.getRegUser().getRegUserRealName();
			this.createdAt = imageEntity.getRegDate();
		}
	}

	@Getter
	public static class Port {
		private String name;
		private Integer port;

		public Port(PortEntity port) {
			this.name = port.getName();
			this.port = port.getPortNum();
		}
	}

	@Getter
	public static class Env {
		private String name;
		private String value;

		public Env(EnvEntity env) {
			this.name = env.getKey();
			this.value = env.getValue();
		}
	}

	@Getter
	public static class Volume extends WorkloadHistoryResDTO {
		private Long id;
		private String name;
		private String mountPath;
		private String size;
		private RepositoryDivision division;
		private StorageType storageType;

		public Volume(DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity) {
			// model size null이면 0L 반환
			Long datasetSize = Objects.requireNonNullElse(datasetWorkLoadMappingEntity.getDataset().getDatasetSize(), 0L);

			this.id = datasetWorkLoadMappingEntity.getId();
			this.name = datasetWorkLoadMappingEntity.getDataset().getDatasetName();
			this.mountPath = datasetWorkLoadMappingEntity.getMountPath();
			this.size = CoreFileUtils.formatFileSize(datasetSize);
			this.regUserName = datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserName();
			this.regUserRealName = datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserRealName();
			this.createdAt = datasetWorkLoadMappingEntity.getDataset().getRegDate();
			this.division = datasetWorkLoadMappingEntity.getDataset().getDivision();
			if (datasetWorkLoadMappingEntity.getDataset().isAstragoDataset()) {
				this.storageType = ((AstragoDatasetEntity)Hibernate.unproxy(datasetWorkLoadMappingEntity.getDataset())).getStorageEntity()
					.getStorageType();
			} else if (datasetWorkLoadMappingEntity.getDataset().isLocalDataset()) {
				this.storageType = ((LocalDatasetEntity)Hibernate.unproxy(datasetWorkLoadMappingEntity.getDataset())).getStorageType();
			}
		}

		public Volume(ModelWorkLoadMappingEntity modelWorkLoadMappingEntity) {
			// model size null이면 0L 반환
			Long modelSize = Objects.requireNonNullElse(modelWorkLoadMappingEntity.getModel().getModelSize(), 0L);

			this.id = modelWorkLoadMappingEntity.getId();
			this.name = modelWorkLoadMappingEntity.getModel().getModelName();
			this.mountPath = modelWorkLoadMappingEntity.getMountPath();
			this.size = CoreFileUtils.formatFileSize(modelSize);
			this.regUserName = modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserName();
			this.regUserRealName = modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserRealName();
			this.createdAt = modelWorkLoadMappingEntity.getModel().getRegDate();
			this.division = modelWorkLoadMappingEntity.getModel().getDivision();
			if (modelWorkLoadMappingEntity.getModel().isAstragoModel()) {
				this.storageType = ((AstragoModelEntity)Hibernate.unproxy(modelWorkLoadMappingEntity.getModel())).getStorageEntity()
					.getStorageType();
			} else if (modelWorkLoadMappingEntity.getModel().isLocalModel()) {
				this.storageType = ((LocalModelEntity)Hibernate.unproxy(modelWorkLoadMappingEntity.getModel())).getStorageType();
			}
		}
	}

	@Getter
	public static class Code extends WorkloadHistoryResDTO {
		private String title;
		private String repositoryURL;
		private String branch;
		private String mountPath;
		private CodeType codeType;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;
		private RepositoryType repositoryType;

		public Code(CodeWorkLoadMappingEntity codeWorkLoadMappingEntity) {
			this.title = codeWorkLoadMappingEntity.getCode().getTitle();
			this.repositoryURL = codeWorkLoadMappingEntity.getCode().getCodeURL();
			this.branch = codeWorkLoadMappingEntity.getBranch();
			this.mountPath = codeWorkLoadMappingEntity.getMountPath();
			this.codeType = codeWorkLoadMappingEntity.getCode().getCodeType();
			if (codeWorkLoadMappingEntity.getCode().getCredentialEntity() == null) {
				this.repositoryAuthType = RepositoryAuthType.PUBLIC;
				this.credentialId = null;
			} else {
				this.repositoryAuthType = RepositoryAuthType.PRIVATE;
				this.credentialId = codeWorkLoadMappingEntity.getCode().getCredentialEntity().getId();
			}
			this.repositoryType = codeWorkLoadMappingEntity.getCode().getRepositoryType();
			this.regUserName = codeWorkLoadMappingEntity.getCode().getRegUser().getRegUserName();
			this.regUserRealName = codeWorkLoadMappingEntity.getCode().getRegUser().getRegUserRealName();
			this.createdAt = codeWorkLoadMappingEntity.getCode().getRegDate();
		}
	}
}
