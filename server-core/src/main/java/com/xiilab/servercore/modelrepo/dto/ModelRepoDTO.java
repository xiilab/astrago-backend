package com.xiilab.servercore.modelrepo.dto;

import java.util.List;

import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;

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

	protected List<LabelDTO> labels;
	protected String storagePath;
	protected String modelName;
	protected String description;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class RequestDTO extends ModelRepoDTO {
		public ModelRepoEntity convertEntity() {
			return ModelRepoEntity.builder()
				.storagePath(this.getStoragePath())
				.description(this.getDescription())
				.modelName(this.getModelName())
				.build();
		}
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@SuperBuilder
	public static class ResponseDTO extends ModelRepoDTO {
		private String version;

	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class LabelDTO {

		// 라벨은 model에 종속됨
		private String name;
	}

}
