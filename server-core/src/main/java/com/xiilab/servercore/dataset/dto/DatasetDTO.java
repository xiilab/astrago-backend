package com.xiilab.servercore.dataset.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.servercore.common.enums.DatasetDivision;
import com.xiilab.servercore.dataset.entity.AstragoDatasetEntity;
import com.xiilab.servercore.dataset.entity.Dataset;
import com.xiilab.servercore.dataset.entity.LocalDatasetEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DatasetDTO {

	@Getter
	public static class CreateAstragoDataset {
		private String datasetName;
		private Long storageId;
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
		private Integer requestVolume;
		private String creator;
		private LocalDateTime createdAt;
		private DatasetDivision division;
		private List<WorkloadResDTO.UsingDatasetDTO> usingDatasets;

		public static ResDatasetWithStorage toDto(Dataset dataset){
			if (dataset instanceof AstragoDatasetEntity) {
				return ResDatasetWithStorage.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(((AstragoDatasetEntity)dataset).getStorageEntity().getRequestVolume())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.division(dataset.getDivision())
					.ip(((AstragoDatasetEntity)dataset).getStorageEntity().getIp())
					.storagePath(((AstragoDatasetEntity)dataset).getStorageEntity().getStoragePath())
					.storageName(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageName())
					.build();
			}else if (dataset instanceof AstragoDatasetEntity) {
				return ResDatasetWithStorage.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.division(dataset.getDivision())
					.ip(((LocalDatasetEntity)dataset).getIp())
					.storagePath(((LocalDatasetEntity)dataset).getStoragePath())
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
		private LocalDateTime createdAt;
		private DatasetDivision division;
		private boolean isAvailable;

		public static ResDataset toDto(Dataset dataset) {
			if (dataset instanceof AstragoDatasetEntity) {
				return ResDataset.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(((AstragoDatasetEntity)dataset).getStorageEntity().getRequestVolume())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.division(dataset.getDivision())
					.build();
			} else if (dataset instanceof LocalDatasetEntity) {
				return ResDataset.builder()
					.datasetId(dataset.getDatasetId())
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(null)
					.creator(dataset.getRegUser().getRegUserName())
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
}
