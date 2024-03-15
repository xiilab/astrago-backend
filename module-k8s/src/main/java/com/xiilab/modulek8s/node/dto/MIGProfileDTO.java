package com.xiilab.modulek8s.node.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MIGProfileDTO {
	@Setter
	private String device;
	private List<Map<String, Integer>> profile;
}
