package com.xiilab.modulek8s.node.dto;

import lombok.Builder;

@Builder
public record MigMixedDTO(String name,
						  int count,
						  String memory) {
}
