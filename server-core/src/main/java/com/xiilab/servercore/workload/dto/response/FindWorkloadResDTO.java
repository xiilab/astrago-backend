package com.xiilab.servercore.workload.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.Hibernate;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.workload.history.entity.EnvEntity;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;
import com.xiilab.servercore.common.dto.ResDTO;
import com.xiilab.servercore.common.utils.CoreFileUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FindWorkloadResDTO extends ResDTO {
	@Getter
	@SuperBuilder
	public static class WorkloadDetail extends FindWorkloadResDTO {
		private Long id;
		private String uid;
		private String workloadName;
		private String workloadResourceName;
		private String workspaceName;
		private String workSpaceResourceName;
		private String description;
		private WorkloadType workloadType;
		private FindWorkloadResDTO.Image image;
		private List<FindWorkloadResDTO.Port> ports;
		private List<FindWorkloadResDTO.Env> envs;
		private List<FindWorkloadResDTO.Volume> datasets;
		private List<FindWorkloadResDTO.Volume> models;
		private List<FindWorkloadResDTO.Code> codes;
		private String workingDir;
		private String command;
		private Map<String,String> args;
		private Float cpuRequest;
		private Integer gpuRequest;
		private Float memRequest;
		private WorkloadStatus status;
		private String ide;
		private String nodeName;
		private String estimatedInitialTime;
		private String estimatedRemainingTime;
		public static <T extends ModuleWorkloadResDTO> FindWorkloadResDTO.WorkloadDetail from(
			T moduleJobResDTO
			, FindWorkloadResDTO.Image image
			, List<FindWorkloadResDTO.Volume> models
			, List<FindWorkloadResDTO.Volume> datasets
			, List<FindWorkloadResDTO.Code> codes
			, List<FindWorkloadResDTO.Port> ports
			, List<FindWorkloadResDTO.Env> envs
		 	, String nodeName) {
			return WorkloadDetail.builder()
				.uid(moduleJobResDTO.getUid())
				.workloadName(moduleJobResDTO.getName())
				.workloadResourceName(moduleJobResDTO.getResourceName())
				.workspaceName(moduleJobResDTO.getWorkspaceName())
				.workSpaceResourceName(moduleJobResDTO.getWorkspaceResourceName())
				.description(moduleJobResDTO.getDescription())
				.workloadType(moduleJobResDTO.getType())
				.image(image)
				.ports(ports)
				.envs(envs)
				.datasets(datasets)
				.models(models)
				.codes(codes)
				.workingDir(moduleJobResDTO.getWorkingDir())
				.args(moduleJobResDTO.getArgs())
				.command(moduleJobResDTO.getCommand())
				.cpuRequest(Float.parseFloat(moduleJobResDTO.getCpuRequest()))
				.gpuRequest(Integer.parseInt(moduleJobResDTO.getGpuRequest()))
				.memRequest(Float.parseFloat(moduleJobResDTO.getMemRequest()))
				.regUserId(moduleJobResDTO.getCreatorId())
				.regUserName(moduleJobResDTO.getCreatorUserName())
				.regUserRealName(moduleJobResDTO.getCreatorFullName())
				.regDate(moduleJobResDTO.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(null)
				.status(moduleJobResDTO.getStatus())
				.ide(moduleJobResDTO.getIde())
				.nodeName(nodeName)
				.estimatedInitialTime(!ObjectUtils.isEmpty(moduleJobResDTO.getEstimatedInitialTime())? moduleJobResDTO.getEstimatedInitialTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
				.estimatedRemainingTime(!ObjectUtils.isEmpty(moduleJobResDTO.getEstimatedRemainingTime())? moduleJobResDTO.getEstimatedRemainingTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
				.build();
		}

		public static FindWorkloadResDTO.WorkloadDetail from(JobEntity jobEntity) {
			return WorkloadDetail.builder()
				.id(jobEntity.getId())
				.uid(jobEntity.getUid())
				.workloadName(jobEntity.getName())
				.workloadResourceName(jobEntity.getResourceName())
				.workspaceName(jobEntity.getWorkspaceName())
				.workSpaceResourceName(jobEntity.getWorkspaceResourceName())
				.description(jobEntity.getDescription())
				.workloadType(jobEntity.getWorkloadType())
				.image(new Image(jobEntity.getImage()))
				.ports(jobEntity.getPortList().stream().map(Port::new).toList())
				.envs(jobEntity.getEnvList().stream().map(Env::new).toList())
				.datasets(jobEntity.getDatasetWorkloadMappingList().stream().map(Volume::new).toList())
				.models(jobEntity.getModelWorkloadMappingList().stream().map(Volume::new).toList())
				.codes(jobEntity.getCodeWorkloadMappingList().stream().map(Code::new).toList())
				.command(jobEntity.getWorkloadCMD())
				.cpuRequest(jobEntity.getCpuRequest().floatValue())
				.gpuRequest(jobEntity.getGpuRequest() == null ? 0 : jobEntity.getGpuRequest())
				.memRequest(jobEntity.getMemRequest().floatValue())
				.regUserId(jobEntity.getCreatorId())
				.regUserName(jobEntity.getCreatorName())
				.regUserRealName(jobEntity.getCreatorRealName())
				.regDate(jobEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(null)
				.status(WorkloadStatus.END)
				.build();
		}
	}

	@Getter
	public static class Image extends ResDTO {
		private Long id;
		private String title;
		private String name;
		private ImageType type;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;
		private String credentialName;

		public Image(ImageEntity imageEntity) {
			super(imageEntity.getRegUser().getRegUserId(), imageEntity.getRegUser().getRegUserName(),
				imageEntity.getRegUser().getRegUserRealName(), imageEntity.getRegDate(), imageEntity.getModDate());
			this.id = imageEntity.getId();
			this.title = imageEntity.isBuiltInImage()? ((BuiltInImageEntity)imageEntity).getTitle() : imageEntity.getImageName();
			this.name = imageEntity.getImageName();
			this.type = imageEntity.getImageType();
			this.repositoryAuthType = imageEntity.getRepositoryAuthType();
			if (imageEntity.getImageType() == ImageType.CUSTOM
				&& ((CustomImageEntity)imageEntity).getCredentialEntity() != null) {
				this.credentialId = ((CustomImageEntity)imageEntity).getCredentialEntity().getId();
				this.credentialName = ((CustomImageEntity)imageEntity).getCredentialEntity().getName();
			}
		}

		@Builder(builderClassName = "customTypeImageResDTO", builderMethodName = "customTypeImageResDTO")
		public Image(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate,
			LocalDateTime modDate, String title, String name, ImageType type,
			RepositoryAuthType repositoryAuthType, Long credentialId, String credentialName) {
			super(regUserId, regUserName, regUserRealName, regDate, modDate);
			this.title = title;
			this.name = name;
			this.type = type;
			this.repositoryAuthType = repositoryAuthType;
			this.credentialId = credentialId;
			this.credentialName = credentialName;
		}

		@Builder(builderClassName = "otherTypeImageResDTO", builderMethodName = "otherTypeImageResDTO")
		public Image(String regUserId, String regUserName, String regUserRealName, String regDate,
			String modDate, String title, Long id, String name, ImageType type,
			RepositoryAuthType repositoryAuthType) {
			super(regUserId, regUserName, regUserRealName, regDate, modDate);
			this.title = title;
			this.id = id;
			this.name = name;
			this.type = type;
			this.repositoryAuthType = repositoryAuthType;
		}
	}

	@Getter
	public static class Port {
		private String name;
		private Integer port;
		private String url;

		public Port(PortEntity port) {
			this.name = port.getName();
			this.port = port.getPortNum();
		}

		public Port(String name, Integer port, String url) {
			this.name = name;
			this.port = port;
			this.url = url;
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

		public Env(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	@Getter
	public static class Volume extends ResDTO {
		private Long id;
		private String name;
		private String mountPath;
		private String size;
		private RepositoryDivision division;
		private StorageType storageType;

		public Volume(DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity) {
			super(datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserId(),
				datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserName(),
				datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserRealName(),
				datasetWorkLoadMappingEntity.getDataset().getRegDate(),
				datasetWorkLoadMappingEntity.getDataset().getModDate());
			// dataset size null이면 0L 반환
			Long datasetSize = Objects.requireNonNullElse(datasetWorkLoadMappingEntity.getDataset().getDatasetSize(),
				0L);

			this.id = datasetWorkLoadMappingEntity.getDataset().getDatasetId();
			this.name = datasetWorkLoadMappingEntity.getDataset().getDatasetName();
			this.mountPath = datasetWorkLoadMappingEntity.getMountPath();
			this.size = CoreFileUtils.formatFileSize(datasetSize);
			this.division = datasetWorkLoadMappingEntity.getDataset().getDivision();
			if (datasetWorkLoadMappingEntity.getDataset().isAstragoDataset()) {
				this.storageType = ((AstragoDatasetEntity)Hibernate.unproxy(
					datasetWorkLoadMappingEntity.getDataset())).getStorageEntity()
					.getStorageType();
			} else if (datasetWorkLoadMappingEntity.getDataset().isLocalDataset()) {
				this.storageType = ((LocalDatasetEntity)Hibernate.unproxy(
					datasetWorkLoadMappingEntity.getDataset())).getStorageType();
			}
		}

		public Volume(ModelWorkLoadMappingEntity modelWorkLoadMappingEntity) {
			super(modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserId(),
				modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserName(),
				modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserRealName(),
				modelWorkLoadMappingEntity.getModel().getRegDate(),
				modelWorkLoadMappingEntity.getModel().getModDate());
			// model size null이면 0L 반환
			Long modelSize = Objects.requireNonNullElse(modelWorkLoadMappingEntity.getModel().getModelSize(), 0L);

			this.id = modelWorkLoadMappingEntity.getModel().getModelId();
			this.name = modelWorkLoadMappingEntity.getModel().getModelName();
			this.mountPath = modelWorkLoadMappingEntity.getMountPath();
			this.size = CoreFileUtils.formatFileSize(modelSize);
			this.division = modelWorkLoadMappingEntity.getModel().getDivision();
			if (modelWorkLoadMappingEntity.getModel().isAstragoModel()) {
				this.storageType = ((AstragoModelEntity)Hibernate.unproxy(
					modelWorkLoadMappingEntity.getModel())).getStorageEntity()
					.getStorageType();
			} else if (modelWorkLoadMappingEntity.getModel().isLocalModel()) {
				this.storageType = ((LocalModelEntity)Hibernate.unproxy(
					modelWorkLoadMappingEntity.getModel())).getStorageType();
			}
		}

		@Builder(builderClassName = "VolumeResDTO", builderMethodName = "volumeResDTO")
		public Volume(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate,
			LocalDateTime modDate, Long id, String name, String mountPath, Long size,
			RepositoryDivision division, StorageType storageType) {
			super(regUserId, regUserName, regUserRealName, regDate, modDate);
			this.id = id;
			this.name = name;
			this.mountPath = mountPath;
			this.size = CoreFileUtils.formatFileSize(size);
			this.division = division;
			this.storageType = storageType;
		}
	}

	@Getter
	public static class Code extends ResDTO {
		private Long id;
		private String title;
		private String repositoryURL;
		private String branch;
		private String mountPath;
		private CodeType codeType;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;
		private String credentialName;
		private RepositoryType repositoryType;

		public Code(CodeWorkLoadMappingEntity codeWorkLoadMappingEntity) {
			super(codeWorkLoadMappingEntity.getCode().getRegUser().getRegUserId(),
				codeWorkLoadMappingEntity.getCode().getRegUser().getRegUserName(),
				codeWorkLoadMappingEntity.getCode().getRegUser().getRegUserRealName(),
				codeWorkLoadMappingEntity.getCode().getRegDate(),
				codeWorkLoadMappingEntity.getCode().getModDate());
			this.id = codeWorkLoadMappingEntity.getCode().getId();
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
		}

		@Builder(builderClassName = "codeResDTO", builderMethodName = "codeResDTO")
		public Code(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate,
			LocalDateTime modDate, Long id, String title, String repositoryURL, String branch,
			String mountPath,
			CodeType codeType, RepositoryAuthType repositoryAuthType, Long credentialId,
			String credentialName, RepositoryType repositoryType) {
			super(regUserId, regUserName, regUserRealName, regDate, modDate);
			this.id = id;
			this.title = title;
			this.repositoryURL = repositoryURL;
			this.branch = branch;
			this.mountPath = mountPath;
			this.codeType = codeType;
			this.repositoryAuthType = repositoryAuthType;
			this.credentialId = credentialId;
			this.credentialName = credentialName;
			this.repositoryType = repositoryType;
		}
	}
}
