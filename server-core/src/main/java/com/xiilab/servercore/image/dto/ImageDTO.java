package com.xiilab.servercore.image.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class ImageDTO {
	protected String name;
	protected String tag;
	protected String description;
	@SuperBuilder
	@NoArgsConstructor
	public static class ReqDTO extends ImageDTO{

	}

	@SuperBuilder
	@NoArgsConstructor
	public static class ResDTO extends ImageDTO {
		private Long id;
	}
}
