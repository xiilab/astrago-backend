package com.xiilab.servercore.dataset.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8sdb.dataset.entity.AstragoDatasetEntity;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkSpaceMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.LocalDatasetEntity;
import com.xiilab.modulek8sdb.common.enums.DatasetDivision;
import com.xiilab.servercore.common.utils.CoreFileUtils;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DatasetDTO {

	@Getter
	public static class ReqFilePathDTO {
		private String path;
	}
	@Getter
	public static class CreateAstragoDataset {
		private String datasetName;
		private Long storageId;
	}
	@Getter
	public static class CreateLocalDataset {
		private String datasetName;
		private String ip;
		private String storagePath;
	}

	@Getter
	public static class ModifyDatset{
		private String datasetName;
	}


	@Getter
	@Builder
	public static class ResDatasetWithStorage{
		private Long datasetId;
		private String datasetName;
		private StorageType storageType;
		private String storageName;
		private String ip;
		private String storagePath;
		private String datasetPath;
		private Integer requestVolume;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;
		private DatasetDivision division;
		private String size;
		private List<WorkloadResDTO.UsingDatasetDTO> usingDatasets;

		public static ResDatasetWithStorage toDto(Dataset dataset){
			if (dataset.isAstragoDataset()) {
				return ResDatasetWithStorage.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(((AstragoDatasetEntity)dataset).getStorageEntity().getRequestVolume())
					.creator(dataset.getRegUser().getRegUserName())
					.creatorName(dataset.getRegUser().getRegUserRealName())
					.createdAt(dataset.getRegDate())
					.division(dataset.getDivision())
					.ip(((AstragoDatasetEntity)dataset).getStorageEntity().getIp())
					.storagePath(((AstragoDatasetEntity)dataset).getStorageEntity().getStoragePath())
					.datasetPath(((AstragoDatasetEntity)dataset).getDatasetPath())
					.storageName(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageName())
					.size(CoreFileUtils.formatFileSize(((AstragoDatasetEntity)dataset).getDatasetSize()))
					.build();
			}else if (dataset.isLocalDataset()) {
				return ResDatasetWithStorage.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.creator(dataset.getRegUser().getRegUserName())
					.creatorName(dataset.getRegUser().getRegUserRealName())
					.createdAt(dataset.getRegDate())
					.division(dataset.getDivision())
					.ip(((LocalDatasetEntity)dataset).getIp())
					.storagePath(((LocalDatasetEntity)dataset).getStoragePath())
					.datasetPath("/")
					.build();
			}
			return null;
		}
		public void addUsingDatasets(List<WorkloadResDTO.UsingDatasetDTO> usingDatasets){
			this.usingDatasets = usingDatasets;
		}
	}

	@Getter
	@Builder
	public static class ResDataset {
		private Long datasetId;
		private StorageType storageType;
		private String datasetName;
		private Integer requestVolume;
		private String creator;
		private String creatorName;
		private LocalDateTime createdAt;
		private DatasetDivision division;
		private String size;
		private boolean isAvailable;

		public static ResDataset toDto(Dataset dataset) {
			if (dataset.isAstragoDataset()) {
				return ResDataset.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(((AstragoDatasetEntity)dataset).getStorageEntity().getRequestVolume())
					.creator(dataset.getRegUser().getRegUserName())
					.creatorName(dataset.getRegUser().getRegUserRealName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.division(dataset.getDivision())
					.size(CoreFileUtils.formatFileSize(((AstragoDatasetEntity)dataset).getDatasetSize()))
					.build();
			} else if (dataset.isLocalDataset()) {
				return ResDataset.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(null)
					.creator(dataset.getRegUser().getRegUserName())
					.creatorName(dataset.getRegUser().getRegUserRealName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.division(dataset.getDivision())
					.build();
			}
			return null;
		}
	}

	@Getter
	@Builder
	public static class ResDatasets {
		List<ResDataset> datasets;
		long totalCount;

		public static ResDatasets entitiesToDtos(List<Dataset> datasets, long totalCount) {
			return ResDatasets
				.builder()
				.totalCount(totalCount)
				.datasets(datasets.stream().map(ResDataset::toDto).toList())
				.build();
		}
	}
	@Getter
	@Builder
	public static class FileInfo{
		private String fileName;
		private String size;
		private String lastModifiedTime;
		private String contentPath;
	}

	@Getter
	@Builder
	public static class DatasetInWorkspace{
		private Long datasetId;
		private String datasetName;
		private StorageType storageType;
		private String creator;
		private LocalDateTime createdAt;
		private DatasetDivision division;
		private String size;
		private boolean isAvailable;

		public static DatasetInWorkspace entityToDto(Dataset dataset) {
			if (dataset.isAstragoDataset()) {
				return DatasetInWorkspace.builder()
					.datasetId(dataset.getDatasetId())
					.datasetName(dataset.getDatasetName())
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.division(dataset.getDivision())
					.size(CoreFileUtils.formatFileSize(((AstragoDatasetEntity)dataset).getDatasetSize()))
					.build();
			} else if (dataset.isLocalDataset()) {
				return DatasetInWorkspace.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.division(dataset.getDivision())
					.build();
			}
			return null;
		}
		public static DatasetInWorkspace mappingEntityToDto(DatasetWorkSpaceMappingEntity dataset){
			if (dataset.getDataset().isAstragoDataset()) {
				return DatasetInWorkspace.builder()
					.datasetId(dataset.getDataset().getDatasetId())
					.datasetName(dataset.getDataset().getDatasetName())
					.storageType(((AstragoDatasetEntity)dataset.getDataset()).getStorageEntity().getStorageType())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.getDataset().isAvailable())
					.division(dataset.getDataset().getDivision())
					.size(CoreFileUtils.formatFileSize(((AstragoDatasetEntity)dataset.getDataset()).getDatasetSize()))
					.build();
			}else if (dataset.getDataset().isLocalDataset()) {
				return DatasetInWorkspace.builder()
					.datasetId(dataset.getDataset().getDatasetId())
					.storageType(((LocalDatasetEntity)dataset.getDataset()).getStorageType())
					.datasetName(dataset.getDataset().getDatasetName())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.getDataset().isAvailable())
					.division(dataset.getDataset().getDivision())
					.build();
			}
			return null;
		}
	}
	@Getter
	@Builder
	public static class DatasetsInWorkspace {
		private List<DatasetInWorkspace> datasets;

		public static DatasetsInWorkspace entitiesToDtos(List<Dataset> datasets) {
			return DatasetsInWorkspace.builder()
				.datasets(datasets.stream().map(DatasetInWorkspace::entityToDto).toList())
				.build();
		}
		public static DatasetsInWorkspace mappingEntitiesToDtos(List<DatasetWorkSpaceMappingEntity> datasets) {
			return DatasetsInWorkspace.builder()
				.datasets(datasets.stream().map(DatasetInWorkspace::mappingEntityToDto).toList())
				.build();
		}
	}
}
