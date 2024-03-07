package com.xiilab.moduleuser.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;

import lombok.Getter;

@Getter
public class GroupSummaryDTO {
	private String id;
	private String name;
	private LocalDate createdDate;
	private String createdBy;
	private String description;
	private long userCount;

	public GroupSummaryDTO(GroupRepresentation groupRep) {
		Map<String, List<String>> attributes = groupRep.getAttributes();
		this.id = groupRep.getId();
		this.name = groupRep.getName();
		if (attributes != null) {
			this.createdDate =
				attributes.get("createdDate") != null ? LocalDate.parse(attributes.get("createdDate").get(0)) : null;
			this.createdBy =
				attributes.get("createdBy") != null ? attributes.get("createdBy").get(0) : null;
			this.description =
				attributes.get("description") != null ? attributes.get("description").get(0) : null;
		}
	}
	public GroupSummaryDTO(GroupResource group) {
		GroupRepresentation groupRepresentation = group.toRepresentation();
		Map<String, List<String>> attributes = groupRepresentation.getAttributes();
		this.id = groupRepresentation.getId();
		this.name = groupRepresentation.getName();
		this.userCount = group.members().size();
		if (attributes != null) {
			this.createdDate =
				attributes.get("createdDate") != null ? LocalDate.parse(attributes.get("createdDate").get(0)) : null;
			this.createdBy =
				attributes.get("createdBy") != null ? attributes.get("createdBy").get(0) : null;
			this.description =
				attributes.get("description") != null ? attributes.get("description").get(0) : null;
		}
	}
}
