package com.xiilab.servercore.dataset.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.xiilab.modulek8s.common.enumeration.StorageType;
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
	public static class ResDataset {
		private StorageType storageType;
		private String datasetName;
		private Integer requestVolume;
		private String creator;
		private LocalDateTime createdAt;
		private boolean isAvailable;

		public static ResDataset toDto(Dataset dataset) {
			if (dataset instanceof AstragoDatasetEntity) {
				return ResDataset.builder()
					.storageType(((AstragoDatasetEntity)dataset).getStorageEntity().getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(((AstragoDatasetEntity)dataset).getStorageEntity().getRequestVolume())
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
					.build();
			} else if (dataset instanceof LocalDatasetEntity) {
				return ResDataset.builder()
					.storageType(((LocalDatasetEntity)dataset).getStorageType())
					.datasetName(dataset.getDatasetName())
					.requestVolume(null)
					.creator(dataset.getRegUser().getRegUserName())
					.createdAt(dataset.getRegDate())
					.isAvailable(dataset.isAvailable())
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
