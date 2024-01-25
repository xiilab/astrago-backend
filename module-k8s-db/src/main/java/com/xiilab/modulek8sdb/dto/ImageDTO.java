package com.xiilab.modulek8sdb.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDTO {
	private String name;
	private String branch;
	private String url;
}
