package com.xiilab.modulek8sdb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImageDTO {
	private String name;
	private String branch;
	private String url;
}
