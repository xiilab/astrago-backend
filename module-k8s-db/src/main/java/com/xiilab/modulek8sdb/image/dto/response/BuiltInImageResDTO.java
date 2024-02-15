package com.xiilab.modulek8sdb.image.dto.response;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BuiltInImageResDTO {
	private Long id;
	private String title;
	private String description;
	private String image;
	private String thumbnailSavePath;
	private WorkloadType type;

	public static BuiltInImageResDTO builtInImageEntityToDTO(BuiltInImageEntity builtInImageEntity) {
		return BuiltInImageResDTO.builder()
			.id(builtInImageEntity.getId())
			.title(builtInImageEntity.getTitle())
			.description(builtInImageEntity.getDescription())
			.image(builtInImageEntity.getImage())
			.thumbnailSavePath(builtInImageEntity.getThumbnailSavePath())
			.type(builtInImageEntity.getType())
			.build();
	}
}
