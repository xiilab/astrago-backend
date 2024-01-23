package com.xiilab.servercore.pin.dto;

import com.xiilab.servercore.pin.enumeration.PinType;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
@Getter
@SuperBuilder
public class PinDTO {
	protected String resourceId;

	@Getter
	@SuperBuilder
	public static class ResponseDTO extends PinDTO{
		private Long id;
		protected PinType type;
		private String userId;
		private String userName;
	}
}
