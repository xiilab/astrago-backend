package com.xiilab.modulek8sdb.image.dto.response;

import com.xiilab.modulecommon.enums.WorkloadType;

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

}
