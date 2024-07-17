package com.xiilab.servercore.modelrepo.dto;

import java.util.List;
import java.util.UUID;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelVersionEntity;
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
				.modelRepoRealName("model-" + UUID.randomUUID().toString().substring(6))
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
		private List<VersionDTO> version;
		private StorageType storageType;
		private String storageName;
		private String ip;
		private String storagePath;
		private List<LabelDTO.ResponseDTO> labels;

		public static ModelRepoDTO.ResponseDTO convertModelRepoDTO(ModelRepoEntity modelRepoEntity) {
			return ModelRepoDTO.ResponseDTO.builder()
				.modelRepoId(modelRepoEntity.getId())
				.workspaceResourceName(modelRepoEntity.getWorkspaceResourceName())
				.modelName(modelRepoEntity.getModelName())
				.description(modelRepoEntity.getDescription())
				.storageName(modelRepoEntity.getStorageEntity().getStorageName())
				.storageType(modelRepoEntity.getStorageEntity().getStorageType())
				.storagePath(modelRepoEntity.getStorageEntity().getStoragePath())
				.ip(modelRepoEntity.getStorageEntity().getIp())
				.labels(modelRepoEntity.getModelLabelEntityList().stream().map(modelLabelEntity -> LabelDTO.ResponseDTO.convertLabelDTO(modelLabelEntity.getLabelEntity())).toList())
				.version(modelRepoEntity.getModelVersionList().stream().map(VersionDTO::convertVersionDTO).toList())
				.build();
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

		public static VersionDTO convertVersionDTO(ModelVersionEntity versionEntity) {
			return VersionDTO.builder()
				.versionId(versionEntity.getId())
				.versionName(versionEntity.getVersion())
				.build();
		}
	}

}
