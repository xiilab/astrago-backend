package com.xiilab.servercore.hub.dto;

import java.util.Map;
import java.util.Set;

import com.xiilab.servercore.hub.entity.HubEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class HubResDTO {
	private long hubId;
	private String title;
	private String description;
	private String savePath;
	private String[] types;

	public HubResDTO(HubEntity hubEntity, Map<Long, Set<String>> typesMap) {
		this.hubId = hubEntity.getHubId();
		this.title = hubEntity.getTitle();
		this.description = hubEntity.getDescription();
		this.savePath = hubEntity.getSavePath();
		this.types = typesMap.get(hubEntity.getHubId()).toArray(String[]::new);
	}
}
