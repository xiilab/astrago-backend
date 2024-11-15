package com.xiilab.moduleuser.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.enums.UserAttribute;
import com.xiilab.modulecommon.enums.WorkspaceRole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserDTO {

	@Getter
	@Builder
	public static class PageUsersDTO {
		private List<UserSummary> users;
		private int totalCount;
	}

	@Getter
	public static class ReqUserIds {
		private List<String> ids;
	}

	@Getter
	@Builder
	public static class SearchGroupAndUser {
		private List<SearchUser> users;
		private List<SearchGroup> groups;
	}

	@Getter
	@Builder
	public static class SearchUser {
		private String userId;
		private String userName;
		private String group;
		private String email;
	}

	@Getter
	@Builder
	public static class SearchGroup {
		private String groupId;
		private String groupName;
	}

	@Getter
	public static class UserInfo {
		private String id;
		private String userName;
		private String email;
		private LocalDateTime joinDate;
		private SignUpPath signUpPath;
		private Integer workspaceCreateLimit;
		private AuthType auth;
		private String enable;
		private String approval;
		private List<String> groups;
		private List<String> workspaces;
		private String userFullName;
		private String firstName;
		private String lastName;
		@Setter
		private Integer ownerWorkspaceCount;

		public UserInfo(UserRepresentation userRepresentation, List<GroupRepresentation> groupReps) {
			this.id = userRepresentation.getId();
			this.userName = userRepresentation.getUsername();
			this.firstName = userRepresentation.getFirstName();
			this.lastName = userRepresentation.getLastName();
			this.userFullName = lastName + firstName;
			this.email = userRepresentation.getEmail();
			
			// je.kim signUpPath 은 ASTRAGO 으로 하드코딩
			// this.signUpPath = userRepresentation.getAttributes().get(UserAttribute.SIGN_UP_PATH.getKey()) != null ?
			// 	SignUpPath.valueOf(userRepresentation.getAttributes().get(UserAttribute.SIGN_UP_PATH.getKey()).get(0)) :
			// 	null;
			this.signUpPath = SignUpPath.ASTRAGO;
			
			// je.kim workspaceCreateLimit 은 1 으로 하드코딩
			
			// this.workspaceCreateLimit =
			// 	userRepresentation.getAttributes().containsKey(UserAttribute.WORKSPACE_CREATE_LIMIT.getKey()) ?
			// 		Integer.parseInt(
			// 			userRepresentation.getAttributes().get(UserAttribute.WORKSPACE_CREATE_LIMIT.getKey()).get(0)) :
			// 		null;
			this.workspaceCreateLimit = 1;

			// 에포크 시간을 Instant로 변환
			Instant instant = Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp());
			// 특정 시간대에 맞춰 LocalDateTime으로 변환
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			this.joinDate = localDateTime;
			this.enable = String.valueOf(userRepresentation.isEnabled());
			// je.kim approvalYN 은 true 으로 하드코딩
			// this.approval = userRepresentation.getAttributes().get(UserAttribute.APPROVAL_YN.getKey()).get(0);
			this.approval = "true";
			this.auth = getUserRole(userRepresentation.getRealmRoles());
			if (!CollectionUtils.isEmpty(groupReps)) {
				this.groups = groupReps.stream()
					.filter(group -> group.getPath().contains("/account/"))
					.map(GroupRepresentation::getName)
					.toList();
			}
			if (!CollectionUtils.isEmpty(groupReps)) {
				this.workspaces = groupReps.stream()
					.filter(group -> group.getPath().contains("/ws/"))
					.map(group -> group.getPath().split("/ws/")[1])
					.toList();
			}
		}

		private AuthType getUserRole(List<String> roles) {
			//admin이면 admin 권한 넣고 user는 user넣고
			return roles != null && roles.contains(AuthType.ROLE_ADMIN.name()) ? AuthType.ROLE_ADMIN :
				AuthType.ROLE_USER;
		}

		public Set<String> getWorkspaceList(boolean isMyWorkspace) {
			if (workspaces != null && !workspaces.isEmpty()) {
				if (isMyWorkspace) {
					//내가 생성한 워크스페이스만 리턴
					return getMyWorkspaces();
				} else {
					//내가 생성했거나, 멤버로 속해있는 워크스페이스 리턴
					return getAllWorkspaces();
				}
			} else {
				return new HashSet<>();
			}
		}

		public boolean isMyWorkspace(String workspaceName) {
			if (auth == AuthType.ROLE_ADMIN) {
				return true;
			}
			if (workspaces == null) {
				return false;
			}
			List<String> workspaces = this.workspaces.stream()
				.filter(ws -> ws.contains("/owner"))
				.map(group -> group.split("/")[0])
				.toList();
			return workspaces.contains(workspaceName);
		}

		/**
		 * 내가 생성한 워크스페이스를 리턴하는 메소드(owner 권한을 가지고 있는 워크스페이스)
		 *
		 * @return workspace set
		 */
		public Set<String> getMyWorkspaces() {
			if (!CollectionUtils.isEmpty(workspaces)) {
				return workspaces.stream()
					.filter(ws -> ws.contains("/owner"))
					.map(group -> group.split("/")[0])
					.collect(Collectors.toSet());
			} else {
				return new HashSet<>();
			}
		}

		/**
		 * 내가 생성한 워크스페이스 + 멤버로 속해있는 워크스페이스를 리턴하는 메소드(owner, user 권한을 가지고 있는 워크스페이스)
		 *
		 * @return workspace set
		 */
		public Set<String> getAllWorkspaces() {
			if (!CollectionUtils.isEmpty(workspaces)) {
				return workspaces.stream()
					.map(group -> group.split("/")[0])
					.collect(Collectors.toSet());
			} else {
				return new HashSet<>();
			}
		}

		/**
		 * 유저가 workspace에 대한 권한이 있는지 체크하는 메소드
		 *
		 * @param workspaceName 조회 할 워크스페이스
		 * @return 권한여부
		 */
		public boolean isAccessAuthorityWorkspace(String workspaceName) {
			if (auth == AuthType.ROLE_ADMIN) {
				return true;
			}
			if (workspaces == null) {
				return false;
			}

			Set<String> allWorkspaces = getAllWorkspaces();

			return allWorkspaces.contains(workspaceName);
		}

		/**
		 * 일반 유저가 워크스페이스에 권한을 가지고 있는지 체크하는 메소드
		 *
		 * @param workspaceName 조회 할 워크스페이스 이름
		 * @return 권한 여부
		 */
		public boolean isAccessAuthorityWorkspaceNotAdmin(String workspaceName) {
			if (CollectionUtils.isEmpty(workspaces)) {
				return false;
			}
			Set<String> allWorkspaces = getAllWorkspaces();
			return allWorkspaces.contains(workspaceName);
		}

		public WorkspaceRole getWorkspaceAuthority(String workspaceName) {
			if (!CollectionUtils.isEmpty(workspaces)) {
				Set<String> myWorkspaces = getMyWorkspaces();
				return myWorkspaces.contains(workspaceName) ? WorkspaceRole.ROLE_OWNER : WorkspaceRole.ROLE_USER;
			} else {
				return null;
			}
		}
	}
}
