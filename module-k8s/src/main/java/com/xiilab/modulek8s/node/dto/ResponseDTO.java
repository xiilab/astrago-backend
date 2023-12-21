package com.xiilab.modulek8s.node.dto;

import java.util.List;

import com.xiilab.modulek8s.node.enumeration.MIGProduct;

import lombok.Builder;

public class ResponseDTO {

	@Builder
	public record NodeDTO(String nodeName,
						  List<String> gpuNames){
	}

	@Builder
	public record MIGProfile(MIGProduct migProduct,
							 List<MIGInfo> migInfos){
	}

	@Builder
	public record MIGInfo(String migProfile,
						  int count){
	}
	public record MIGProfileList(List<MIGProfile> migProfiles){
	}
}
