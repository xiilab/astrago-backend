package com.xiilab.servercore.dataset.dto;


import lombok.Getter;

@Getter
public class DatasetDTO {

	@Getter
	public static class CreateAstragoDataset{
		private String datasetName;
		private Long storageId;
	}
}
