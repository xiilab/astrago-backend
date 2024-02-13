package com.xiilab.modulek8s.facade.dto;

import com.xiilab.modulek8s.common.dto.K8SResourceReqDTO;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DeleteVolumeDTO extends K8SResourceReqDTO {
	private String workspaceMetaName;
	private String volumeMetaName;
}
