package com.xiilab.servercore.workload.dto.response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.Hibernate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.CodeType;
import com.xiilab.modulecommon.enums.GPUType;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.JsonConvertUtil;
import com.xiilab.modulek8s.common.dto.DistributedResourceDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractDistributedWorkloadResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractSingleWorkloadResDTO;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
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
import com.xiilab.modulek8sdb.workload.history.entity.DistributedJobEntity;
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
	protected Long id;
	protected String uid;
	protected String workloadName;
	protected String workloadResourceName;
	protected String workspaceName;
	protected String workSpaceResourceName;
	protected String description;
	protected WorkloadType workloadType;
	protected FindWorkloadResDTO.Image image;
	protected List<FindWorkloadResDTO.Port> ports;
	protected List<FindWorkloadResDTO.Env> envs;
	protected List<FindWorkloadResDTO.Volume> datasets;
	protected List<FindWorkloadResDTO.Volume> models;
	protected List<FindWorkloadResDTO.Code> codes;
	protected String workingDir;
	protected String command;
	protected WorkloadStatus status;
	protected String nodeName;
	protected boolean canBeDeleted;
	protected String startTime;
	protected GPUType gpuType;
	protected String gpuName;


	@Getter
	@SuperBuilder
	public static class SingleWorkloadDetail extends FindWorkloadResDTO {
		private Float cpuRequest;
		private Integer gpuRequest;
		private Float memRequest;
		private Map<String, String> parameter;
		private String ide;
		private String estimatedInitialTime;
		private String estimatedRemainingTime;

		public static <T extends AbstractSingleWorkloadResDTO> SingleWorkloadDetail from(
			T moduleJobResDTO
			, FindWorkloadResDTO.Image image
			, List<FindWorkloadResDTO.Volume> models
			, List<FindWorkloadResDTO.Volume> datasets
			, List<FindWorkloadResDTO.Code> codes
			, List<FindWorkloadResDTO.Port> ports
			, List<FindWorkloadResDTO.Env> envs
			, String nodeName) {

			return SingleWorkloadDetail.builder()
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
				.command(moduleJobResDTO.getCommand())
				.parameter(moduleJobResDTO.getParameter())
				.cpuRequest(moduleJobResDTO.getCpuRequest())
				.memRequest(moduleJobResDTO.getMemRequest())
				.gpuRequest(moduleJobResDTO.getGpuRequest())
				.regUserId(moduleJobResDTO.getCreatorId())
				.regUserName(moduleJobResDTO.getCreatorUserName())
				.regUserRealName(moduleJobResDTO.getCreatorFullName())
				.regDate(moduleJobResDTO.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(null)
				.workingDir(moduleJobResDTO.getWorkingDir())
				.status(moduleJobResDTO.getStatus())
				.ide(moduleJobResDTO.getIde())
				.nodeName(nodeName)
				.estimatedInitialTime(!ObjectUtils.isEmpty(moduleJobResDTO.getEstimatedInitialTime()) ?
					moduleJobResDTO.getEstimatedInitialTime() : null)
				.estimatedRemainingTime(!ObjectUtils.isEmpty(moduleJobResDTO.getEstimatedRemainingTime()) ?
					moduleJobResDTO.getEstimatedRemainingTime() : null)
				.canBeDeleted(moduleJobResDTO.isCanBeDeleted())
				.startTime(StringUtils.hasText(moduleJobResDTO.getStartTime()) ? moduleJobResDTO.getStartTime() : null)
				.gpuName(moduleJobResDTO.getGpuName())
				.gpuType(moduleJobResDTO.getGpuType())
				.build();
		}

		public static SingleWorkloadDetail from(JobEntity workloadEntity) {
			return SingleWorkloadDetail.builder()
				.id(workloadEntity.getId())
				.uid(workloadEntity.getUid())
				.workloadName(workloadEntity.getName())
				.workloadResourceName(workloadEntity.getResourceName())
				.workspaceName(workloadEntity.getWorkspaceName())
				.workSpaceResourceName(workloadEntity.getWorkspaceResourceName())
				.description(workloadEntity.getDescription())
				.workloadType(workloadEntity.getWorkloadType())
				.image(new Image(workloadEntity.getImage()))
				.ports(workloadEntity.getPortList().stream().map(Port::new).toList())
				.envs(workloadEntity.getEnvList().stream().map(Env::new).toList())
				.datasets(workloadEntity.getDatasetWorkloadMappingList().stream().map(Volume::new).toList())
				.models(workloadEntity.getModelWorkloadMappingList().stream().map(Volume::new).toList())
				.codes(workloadEntity.getCodeWorkloadMappingList().stream().map(Code::new).toList())
				.command(workloadEntity.getWorkloadCMD())
				.parameter(JsonConvertUtil.convertJsonToMap(workloadEntity.getParameter()))
				.cpuRequest(workloadEntity.getCpuRequest())
				.gpuRequest(workloadEntity.getGpuRequest() == null ? 0 : workloadEntity.getGpuRequest())
				.memRequest(workloadEntity.getMemRequest())
				.regUserId(workloadEntity.getCreatorId())
				.regUserName(workloadEntity.getCreatorName())
				.regUserRealName(workloadEntity.getCreatorRealName())
				.regDate(workloadEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(null)
				.workingDir(workloadEntity.getWorkingDir())
				.status(WorkloadStatus.END)
				.ide(workloadEntity.getIde())
				.canBeDeleted(workloadEntity.isCanBeDeleted())
				.gpuName(workloadEntity.getGpuName())
				.gpuType(workloadEntity.getGpuType())
				.build();
		}
	}

	@Getter
	@SuperBuilder
	public static class DistributedWorkloadDetail extends FindWorkloadResDTO {
		private DistributedResourceDTO.LauncherInfo launcherInfo;
		private DistributedResourceDTO.WorkerInfo workerInfo;

		public static DistributedWorkloadDetail from(
			AbstractDistributedWorkloadResDTO moduleJobResDTO
			, FindWorkloadResDTO.Image image
			, List<FindWorkloadResDTO.Volume> models
			, List<FindWorkloadResDTO.Volume> datasets
			, List<FindWorkloadResDTO.Code> codes
			, List<FindWorkloadResDTO.Port> ports
			, List<FindWorkloadResDTO.Env> envs
			, String nodeName) {

			return DistributedWorkloadDetail.builder()
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
				.command(moduleJobResDTO.getCommand())
				.regUserId(moduleJobResDTO.getCreatorId())
				.regUserName(moduleJobResDTO.getCreatorUserName())
				.regUserRealName(moduleJobResDTO.getCreatorFullName())
				.regDate(moduleJobResDTO.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.modDate(null)
				.status(moduleJobResDTO.getStatus())
				.nodeName(nodeName)
				.canBeDeleted(moduleJobResDTO.isCanBeDeleted())
				.workingDir(moduleJobResDTO.getWorkingDir())
				.startTime(StringUtils.hasText(moduleJobResDTO.getStartTime()) ? moduleJobResDTO.getStartTime() : null)
				.launcherInfo(DistributedResourceDTO.LauncherInfo.builder()
					.cpuRequest(moduleJobResDTO.getLauncherInfo().getCpuRequest())
					.memRequest(moduleJobResDTO.getLauncherInfo().getMemRequest())
					.build())
				.workerInfo(DistributedResourceDTO.WorkerInfo.builder()
					.cpuRequest(moduleJobResDTO.getWorkerInfo().getCpuRequest())
					.memRequest(moduleJobResDTO.getWorkerInfo().getMemRequest())
					.gpuRequest(moduleJobResDTO.getWorkerInfo().getGpuRequest())
					.workerCnt(moduleJobResDTO.getWorkerInfo().getWorkerCnt())
					.build())
				.gpuName(moduleJobResDTO.getGpuName())
				.gpuType(moduleJobResDTO.getGpuType())
				.build();
		}

		public static DistributedWorkloadDetail from(DistributedJobEntity distributedJobEntity) {
			return DistributedWorkloadDetail.builder()
				.id(distributedJobEntity.getId())
				.uid(distributedJobEntity.getUid())
				.workloadName(distributedJobEntity.getName())
				.workloadResourceName(distributedJobEntity.getResourceName())
				.workspaceName(distributedJobEntity.getWorkspaceName())
				.workSpaceResourceName(distributedJobEntity.getWorkspaceResourceName())
				.description(distributedJobEntity.getDescription())
				.workloadType(distributedJobEntity.getWorkloadType())
				.image(new Image(distributedJobEntity.getImage()))
				.ports(distributedJobEntity.getPortList().stream().map(Port::new).toList())
				.envs(distributedJobEntity.getEnvList().stream().map(Env::new).toList())
				.datasets(distributedJobEntity.getDatasetWorkloadMappingList().stream().map(Volume::new).toList())
				.models(distributedJobEntity.getModelWorkloadMappingList().stream().map(Volume::new).toList())
				.codes(distributedJobEntity.getCodeWorkloadMappingList().stream().map(Code::new).toList())
				.command(distributedJobEntity.getWorkloadCMD())
				.regUserId(distributedJobEntity.getCreatorId())
				.regUserName(distributedJobEntity.getCreatorName())
				.regUserRealName(distributedJobEntity.getCreatorRealName())
				.regDate(distributedJobEntity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.workingDir(distributedJobEntity.getWorkingDir())
				.modDate(null)
				.status(WorkloadStatus.END)
				.canBeDeleted(distributedJobEntity.isCanBeDeleted())
				.launcherInfo(DistributedResourceDTO.LauncherInfo.builder()
					.cpuRequest(distributedJobEntity.getLauncherCpuRequest())
					.memRequest(distributedJobEntity.getLauncherMemRequest())
					.build())
				.workerInfo(DistributedResourceDTO.WorkerInfo.builder()
					.cpuRequest(distributedJobEntity.getWorkerCpuRequest())
					.memRequest(distributedJobEntity.getWorkerMemRequest())
					.gpuRequest(distributedJobEntity.getWorkerGpuRequest())
					.workerCnt(distributedJobEntity.getWorkerCount())
					.build())
				.gpuName(null)
				.gpuType(null)
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
			this.title = imageEntity.isBuiltInImage() ? ((BuiltInImageEntity)imageEntity).getTitle() :
				imageEntity.getImageNameHub();
			this.name = imageEntity.getImageNameHub();
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
		private DeleteYN deleteYN;

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
			this.deleteYN = datasetWorkLoadMappingEntity.getDeleteYN();
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
			this.deleteYN = modelWorkLoadMappingEntity.getDeleteYN();
		}

		@Builder(builderClassName = "VolumeResDTO", builderMethodName = "volumeResDTO")
		public Volume(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate,
			LocalDateTime modDate, Long id, String name, String mountPath, Long size,
			RepositoryDivision division, StorageType storageType, DeleteYN deleteYN) {
			super(regUserId, regUserName, regUserRealName, regDate, modDate);
			this.id = id;
			this.name = name;
			this.mountPath = mountPath;
			this.size = CoreFileUtils.formatFileSize(size);
			this.division = division;
			this.storageType = storageType;
			this.deleteYN = deleteYN;
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
