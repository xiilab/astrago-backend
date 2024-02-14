package com.xiilab.modulek8sdb.pin.dto;

import com.xiilab.modulek8sdb.pin.enumeration.PinType;

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
