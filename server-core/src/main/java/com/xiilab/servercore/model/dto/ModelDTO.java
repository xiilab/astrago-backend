package com.xiilab.servercore.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.common.enums.RepositoryDivision;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.model.entity.LocalModelEntity;
import com.xiilab.modulek8sdb.model.entity.ModelWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.model.entity.AstragoModelEntity;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.servercore.common.utils.CoreFileUtils;
import com.xiilab.servercore.dataset.dto.DatasetDTO;

import lombok.Builder;
import lombok.Getter;

public class ModelDTO {
	@Getter
	public static class CreateAstragoModel {
		private String modelName;
		private Long storageId;
	}

	@Getter
	public static class CreateLocalModel {
		private String modelName;
		private String ip;
		private String storagePath;
	}

	@Getter
	@Builder
	public static class ResModel {
		private Long modelId;
		private StorageType storageType;
		private String modelName;
		private Integer requestVolume;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;
		private RepositoryDivision division;
		private String size;
		private boolean isAvailable;

		public static ModelDTO.ResModel toDto(Model model) {
			if (model.isAstragoModel()) {
				return ResModel.builder()
					.modelId(model.getModelId())
					.storageType(((AstragoModelEntity)model).getStorageEntity().getStorageType())
					.modelName(model.getModelName())
					.requestVolume(((AstragoModelEntity)model).getStorageEntity().getRequestVolume())
					.creator(model.getRegUser().getRegUserName())
					.creatorName(model.getRegUser().getRegUserRealName())
					.createdAt(model.getRegDate())
					.isAvailable(model.isAvailable())
					.division(model.getDivision())
					.size(CoreFileUtils.formatFileSize(model.getModelSize()))
					.build();
			} else if (model.isLocalModel()) {
				return ResModel.builder()
					.modelId(model.getModelId())
					.storageType(((LocalModelEntity)model).getStorageType())
					.modelName(model.getModelName())
					.requestVolume(null)
					.creator(model.getRegUser().getRegUserName())
					.creatorName(model.getRegUser().getRegUserRealName())
					.createdAt(model.getRegDate())
					.isAvailable(model.isAvailable())
					.division(model.getDivision())
					.size(CoreFileUtils.formatFileSize(model.getModelSize()))
					.build();
			}
			return null;
		}
	}

	@Getter
	@Builder
	public static class ResModels {
		List<ModelDTO.ResModel> models;
		long totalCount;

		public static ModelDTO.ResModels entitiesToDtos(List<Model> models, long totalCount) {
			return ResModels
				.builder()
				.totalCount(totalCount)
				.models(models.stream().map(ModelDTO.ResModel::toDto).toList())
				.build();
		}
	}

	@Getter
	@Builder
	public static class ResModelWithStorage {
		private Long modelId;
		private String modelName;
		private StorageType storageType;
		private String storageName;
		private String ip;
		private String storagePath;
		private String modelPath;
		private Integer requestVolume;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;
		private RepositoryDivision division;
		private String size;
		private List<WorkloadResDTO.UsingModelDTO> usingModels;

		public static ModelDTO.ResModelWithStorage toDto(Model model){
			if (model.isAstragoModel()) {
				return ResModelWithStorage.builder()
					.modelId(model.getModelId())
					.storageType(((AstragoModelEntity)model).getStorageEntity().getStorageType())
					.modelName(model.getModelName())
					.requestVolume(((AstragoModelEntity)model).getStorageEntity().getRequestVolume())
					.creator(model.getRegUser().getRegUserName())
					.creatorName(model.getRegUser().getRegUserRealName())
					.createdAt(model.getRegDate())
					.division(model.getDivision())
					.ip(((AstragoModelEntity)model).getStorageEntity().getIp())
					.storagePath(((AstragoModelEntity)model).getStorageEntity().getStoragePath())
					.modelPath(((AstragoModelEntity)model).getModelPath())
					.storageName(((AstragoModelEntity)model).getStorageEntity().getStorageName())
					.size(CoreFileUtils.formatFileSize(((AstragoModelEntity)model).getModelSize()))
					.build();
			} else if (model.isLocalModel()) {
				return ResModelWithStorage.builder()
					.modelId(model.getModelId())
					.storageType(((LocalModelEntity)model).getStorageType())
					.modelName(model.getModelName())
					.creator(model.getRegUser().getRegUserName())
					.creatorName(model.getRegUser().getRegUserRealName())
					.createdAt(model.getRegDate())
					.division(model.getDivision())
					.ip(((LocalModelEntity)model).getIp())
					.storagePath(((LocalModelEntity)model).getStoragePath())
					.modelPath("/")
					.build();
			}
			return null;
		}
	}

	@Getter
	public static class ModifyModel {
		private String modelName;
	}

	@Getter
	public static class ReqFilePathDTO {
		private String path;
	}
	@Getter
	public static class ReqFilePathsDTO {
		private String[] paths;
	}

	@Getter
	@Builder
	public static class ModelInWorkspace {
		private Long modelId;
		private String modelName;
		private StorageType storageType;
		private String creator;
		private LocalDateTime createdAt;
		private RepositoryDivision division;
		private String size;
		private boolean isAvailable;

		public static ModelInWorkspace entityToDto(Model model) {
			if (model.isAstragoModel()) {
				return ModelInWorkspace.builder()
					.modelId(model.getModelId())
					.modelName(model.getModelName())
					.storageType(((AstragoModelEntity)model).getStorageEntity().getStorageType())
					.creator(model.getRegUser().getRegUserName())
					.createdAt(model.getRegDate())
					.isAvailable(model.isAvailable())
					.division(model.getDivision())
					.size(CoreFileUtils.formatFileSize(model.getModelSize()))
					.build();
			} else if (model.isLocalModel()) {
				return ModelInWorkspace.builder()
					.modelId(model.getModelId())
					.storageType(((LocalModelEntity)model).getStorageType())
					.modelName(model.getModelName())
					.creator(model.getRegUser().getRegUserName())
					.createdAt(model.getRegDate())
					.isAvailable(model.isAvailable())
					.division(model.getDivision())
					.build();
			}
			return null;
		}
		public static ModelInWorkspace mappingEntityToDto(ModelWorkSpaceMappingEntity model){
			if (model.getModel().isAstragoModel()) {
				return ModelInWorkspace.builder()
					.modelId(model.getModel().getModelId())
					.modelName(model.getModel().getModelName())
					.storageType(((AstragoModelEntity)model.getModel()).getStorageEntity().getStorageType())
					.creator(model.getRegUser().getRegUserName())
					.createdAt(model.getRegDate())
					.isAvailable(model.getModel().isAvailable())
					.division(model.getModel().getDivision())
					.size(CoreFileUtils.formatFileSize(model.getModel().getModelSize()))
					.build();
			} else if (model.getModel().isLocalModel()) {
				return ModelInWorkspace.builder()
					.modelId(model.getModel().getModelId())
					.storageType(((LocalModelEntity)model.getModel()).getStorageType())
					.modelName(model.getModel().getModelName())
					.creator(model.getRegUser().getRegUserName())
					.createdAt(model.getRegDate())
					.isAvailable(model.getModel().isAvailable())
					.division(model.getModel().getDivision())
					.build();
			}
			return null;
		}
	}

	@Getter
	@Builder
	public static class ModelsInWorkspace {
		private List<ModelDTO.ModelInWorkspace> datasets;

		public static ModelDTO.ModelsInWorkspace entitiesToDtos(List<Model> models) {
			return ModelDTO.ModelsInWorkspace.builder()
				.datasets(models.stream().map(ModelDTO.ModelInWorkspace::entityToDto).toList())
				.build();
		}

		public static ModelDTO.ModelsInWorkspace mappingEntitiesToDtos(List<ModelWorkSpaceMappingEntity> models) {
			return ModelDTO.ModelsInWorkspace.builder()
				.datasets(models.stream().map(ModelDTO.ModelInWorkspace::mappingEntityToDto).toList())
				.build();
		}
	}
}
