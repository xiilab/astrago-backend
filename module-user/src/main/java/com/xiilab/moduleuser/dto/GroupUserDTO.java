package com.xiilab.moduleuser.dto;

import java.util.List;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GroupUserDTO {
	private String id;
	private String name;
	private String email;
	private String firstName;
	private String lastName;

	public GroupUserDTO(UserRepresentation userRep) {
		this.id = userRep.getId();
		this.name = userRep.getUsername();
		this.email = userRep.getEmail();
		this.firstName = userRep.getFirstName();
		this.lastName = userRep.getLastName();
	}


	@Getter
	public static class SubGroupUserDto{
		private List<GroupDTO> groups;
		private List<UserDTO> users;

		public SubGroupUserDto(List<GroupRepresentation> subGroups, List<UserRepresentation> users) {
			this.groups = subGroups.stream().map(GroupDTO::new).toList();
			this.users = users.stream().map(UserDTO::new).toList();
		}
	}
	@Getter
	public static class GroupDTO{
		private String id;
		private String name;
		private long userCount;

		public GroupDTO(GroupRepresentation group) {
			this.id = group.getId();
			this.name = group.getName();
		}
	}

	@Getter
	public static class UserDTO{
		private String userId;
		private String name;
		private String email;
		private String firstName;
		private String lastName;
		public UserDTO(UserRepresentation userRep) {
			this.userId = userRep.getId();
			this.name = userRep.getUsername();
			this.email = userRep.getEmail();
			this.firstName = userRep.getFirstName();
			this.lastName = userRep.getLastName();
		}
	}
}
