package com.xiilab.servercore.image.dto.response;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.servercore.image.entity.BuiltInImageEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class BuiltInImageResDTO {
	private Long id;
	private String name;
	private String description;
	private String image;
	private String thumbnailSavePath;
	private WorkloadType type;

	public static BuiltInImageResDTO builtInImageEntityToDTO(BuiltInImageEntity builtInImageEntity) {
		return BuiltInImageResDTO.builder()
			.id(builtInImageEntity.getId())
			.name(builtInImageEntity.getName())
			.description(builtInImageEntity.getDescription())
			.image(builtInImageEntity.getImage())
			.thumbnailSavePath(builtInImageEntity.getThumbnailSavePath())
			.type(builtInImageEntity.getType())
			.build();
	}
}
