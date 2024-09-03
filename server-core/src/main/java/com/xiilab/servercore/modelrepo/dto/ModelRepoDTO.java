package com.xiilab.servercore.modelrepo.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
import com.xiilab.modulek8sdb.modelrepo.enums.ModelRepoType;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.servercore.label.dto.LabelDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ModelRepoDTO {

	protected String modelName;
	protected String description;
	protected String workspaceResourceName;
	protected String modelPath;
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class RequestDTO extends ModelRepoDTO {
		private long storageId;
		private List<Long> labelIds;

		public ModelRepoEntity convertEntity(StorageEntity storageEntity) {
			return ModelRepoEntity.builder()
				.description(this.getDescription())
				.modelName(this.getModelName())
				.modelRepoRealName(
					this.modelPath != null ? this.getModelPath() : "model-" + UUID.randomUUID().toString().substring(6))
				.workspaceResourceName(this.getWorkspaceResourceName())
				.storageEntity(storageEntity)
				.build();
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends ModelRepoDTO {
		private long modelRepoId;
		private String modelRealName;
		private List<VersionDTO> version;
		private StorageType storageType;
		private String storageName;
		private String ip;
		private String storagePath;
		private List<LabelDTO.ResponseDTO> labels;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;

		public static ModelRepoDTO.ResponseDTO convertModelRepoDTO(ModelRepoEntity modelRepoEntity) {
			return ModelRepoDTO.ResponseDTO.builder()
				.modelRepoId(modelRepoEntity.getId())
				.workspaceResourceName(modelRepoEntity.getWorkspaceResourceName())
				.modelRealName(modelRepoEntity.getModelRepoRealName())
				.modelName(modelRepoEntity.getModelName())
				.description(modelRepoEntity.getDescription())
				.storageName(modelRepoEntity.getStorageEntity().getStorageName())
				.storageType(modelRepoEntity.getStorageEntity().getStorageType())
				.storagePath(modelRepoEntity.getStorageEntity().getStoragePath())
				.modelPath(modelRepoEntity.getModelPath())
				.ip(modelRepoEntity.getStorageEntity().getIp())
				.labels(modelRepoEntity.getModelLabelEntityList()
					.stream()
					.map(modelLabelEntity -> LabelDTO.ResponseDTO.convertLabelDTO(modelLabelEntity.getLabelEntity()))
					.toList())
				.version(modelRepoEntity.getModelVersionList().stream().map(VersionDTO::convertVersionDTO)
					.sorted(Comparator.comparing(VersionDTO::getVersionName).reversed()).toList())
				.creator(modelRepoEntity.getRegUser().getRegUserName())
				.creatorName(modelRepoEntity.getRegUser().getRegUserRealName())
				.createdAt(modelRepoEntity.getRegDate())
				.build();
		}
		public void setModelPath(String modelPath){
			this.modelPath = modelPath;
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class VersionDTO {
		private long versionId;
		// 라벨은 model에 종속됨
		private String versionName;
		private String modelFilePath;
		private String labelFilePath;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;

		public static VersionDTO convertVersionDTO(ModelVersionEntity versionEntity) {
			return VersionDTO.builder()
				.versionId(versionEntity.getId())
				.versionName(versionEntity.getVersion())
				.modelFilePath(versionEntity.getModelFileName())
				.creator(versionEntity.getRegUser().getRegUserName())
				.creatorName(versionEntity.getRegUser().getRegUserRealName())
				.createdAt(versionEntity.getRegDate())
				.build();
		}
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UpdateDTO {
		private String modelName;
		private String description;
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class WlModelRepoDTO {
		private String modelName;
		private String description;
		private String workspaceResourceName;
		private List<Long> labelIds;
		private ModelRepoType modelType;
		private String wlModelPaths;
		private long storageId;
		private long modelRepoId;

		public ModelRepoDTO.RequestDTO convertRequestDTO() {
			return RequestDTO.builder()
				.modelName(this.getModelName())
				.description(this.getDescription())
				.workspaceResourceName(this.getWorkspaceResourceName())
				.storageId(this.getStorageId())
				.labelIds(this.getLabelIds())
				.build();
		}

	}
}
