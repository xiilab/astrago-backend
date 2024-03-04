package com.xiilab.servercore.workload.dto.response;

import java.util.List;
import java.util.Optional;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.workload.history.entity.EnvEntity;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkloadHistoryResDTO {
	protected String regUserName;
	protected String regUserRealName;
	@Getter
	@Builder
	public static class FindWorkload {
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
				.build();
		}

	}

	@Getter
	public static class Image {
		private Long id;
		private String name;
		private ImageType type;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;

		public Image (ImageEntity imageEntity) {
			this.id = imageEntity.getId();
			this.name = imageEntity.getImageName();
			this.type = imageEntity.getImageType();
			this.repositoryAuthType = imageEntity.getRepositoryAuthType();
			if (imageEntity.getImageType() == ImageType.CUSTOM && ((CustomImageEntity)imageEntity).getCredentialEntity() != null) {
				this.credentialId = ((CustomImageEntity)imageEntity).getCredentialEntity().getId();
			}
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

		public Volume(DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity) {
			this.id = datasetWorkLoadMappingEntity.getId();
			this.name = datasetWorkLoadMappingEntity.getDataset().getDatasetName();
			this.mountPath = datasetWorkLoadMappingEntity.getMountPath();
			this.regUserName = datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserName();
			this.regUserRealName = datasetWorkLoadMappingEntity.getDataset().getRegUser().getRegUserRealName();
		}

		public Volume(ModelWorkLoadMappingEntity modelWorkLoadMappingEntity) {
			this.id = modelWorkLoadMappingEntity.getId();
			this.name = modelWorkLoadMappingEntity.getModel().getModelName();
			this.mountPath = modelWorkLoadMappingEntity.getMountPath();
			this.regUserName = modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserName();
			this.regUserRealName = modelWorkLoadMappingEntity.getModel().getRegUser().getRegUserRealName();
		}
	}

	@Getter
	public static class Code extends WorkloadHistoryResDTO {
		private String repositoryURL;
		private String branch;
		private String mountPath;
		private RepositoryAuthType repositoryAuthType;
		private Long credentialId;
		private RepositoryType repositoryType;

		public Code(CodeWorkLoadMappingEntity codeWorkLoadMappingEntity) {
			this.repositoryURL = codeWorkLoadMappingEntity.getCode().getCodeURL();
			this.branch = codeWorkLoadMappingEntity.getBranch();
			this.mountPath = codeWorkLoadMappingEntity.getMountPath();
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
		}
	}
}
