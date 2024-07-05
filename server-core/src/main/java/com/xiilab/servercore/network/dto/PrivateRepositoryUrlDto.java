package com.xiilab.servercore.network.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateRepositoryUrlDto {
	private String privateRepositoryUrl;

	@Builder
	public PrivateRepositoryUrlDto(String privateRepositoryUrl) {
		this.privateRepositoryUrl = privateRepositoryUrl;
	}
}
