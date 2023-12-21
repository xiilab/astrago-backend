package com.xiilab.moduleuser.vo;

import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.GroupRepresentation;

import lombok.Getter;

@Getter
public class GroupModiVO {
	private String id;
	private String name;
	private String description;
	private List<String> userId;

	public GroupRepresentation modiGroupRep(GroupRepresentation groupRep) {
		groupRep.setName(name);
		Map<String, List<String>> attributes = groupRep.getAttributes();
		if (attributes != null) {
			attributes.put("description",List.of(description));
		} else {
			groupRep.setAttributes(Map.of("description", List.of(description)));
		}
		return groupRep;
	}
}
