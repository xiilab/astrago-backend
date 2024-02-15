package com.xiilab.moduleuser.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.GroupRepresentation;

import com.xiilab.moduleuser.enumeration.GroupCategory;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GroupReqVO {
	private String name;
	private String description;
	private GroupCategory groupCategory;
	private String createdBy;

	public GroupRepresentation createGroupRep() {
		GroupRepresentation groupRepresentation = new GroupRepresentation();
		groupRepresentation.setName(name);
		groupRepresentation.setAttributes(
			Map.of(
				"description", List.of(description),
				"createdBy", List.of(createdBy),
				"createdDate", List.of(LocalDate.now().toString())
			));
		if (groupCategory == GroupCategory.WORKSPACE) {
			GroupRepresentation userGroup = new GroupRepresentation();
			userGroup.setName("USER");
			userGroup.setAttributes(
				Map.of(
					"description", List.of(description),
					"createdBy", List.of(createdBy),
					"createdDate", List.of(LocalDate.now().toString())
				)
			);
			GroupRepresentation ownerGroup = new GroupRepresentation();
			ownerGroup.setName("OWNER");
			ownerGroup.setAttributes(
				Map.of(
					"description", List.of(description),
					"createdBy", List.of(createdBy),
					"createdDate", List.of(LocalDate.now().toString())
				)
			);
			groupRepresentation.setSubGroups(List.of(
				userGroup, ownerGroup
			));
		}
		return groupRepresentation;
	}

	@Getter
	@SuperBuilder
	public static class ChildGroupReqVO extends GroupReqVO {
		private String parentGroupId;
	}
}
