package com.xiilab.moduleuser.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.AddWorkspaceUsersDTO;
import com.xiilab.moduleuser.dto.GroupCategory;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupReqDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.vo.GroupReqVO;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupRepository implements GroupRepository {
	private final KeycloakConfig keycloakConfig;
	private final UserRepository userRepository;

	@Override
	public List<GroupSummaryDTO> getGroupList(String searchText) {
		List<GroupSummaryDTO> groups = new ArrayList<>();
		GroupResource rootGroup = getGroupResourceByName(GroupCategory.ACCOUNT.getValue());

		List<GroupRepresentation> subGroups = rootGroup.toRepresentation().getSubGroups().stream()
			.filter(groupRepresentation -> searchText != null ?
				groupRepresentation.getName().toLowerCase().contains(searchText.toLowerCase()) : true).toList();

		for (GroupRepresentation subGroup : subGroups) {
			groups.add(new GroupSummaryDTO(subGroup));
		}
		return groups;
	}

	@Override
	public List<GroupSummaryDTO> getGroupListByCategory(GroupCategory groupCategory) {
		List<GroupRepresentation> subGroups = getGroupResourceByName(groupCategory.getValue())
			.getSubGroups(0, 1000, true);
		return subGroups.stream().map(GroupSummaryDTO::new).toList();
	}

	@Override
	public GroupInfoDTO getGroupInfoByCategoryAndName(GroupCategory groupCategory, String groupName) {
		List<GroupRepresentation> subGroups = getGroupByName(groupCategory.getValue()).getSubGroups();
		GroupRepresentation groupRepresentation = subGroups.stream()
			.filter(subGroup -> subGroup.getName().equals(groupName))
			.findFirst()
			.orElseThrow();
		return getGroupById(groupRepresentation.getId());
	}

	@Override
	public GroupInfoDTO getGroupById(String groupId) {
		GroupResource group = keycloakConfig.getRealmClient().groups().group(groupId);
		// try {
		// List<UserInfo> groupUsers = group.members()
		// 	.stream()
		// 	.map(member -> userRepository.getUserInfoById(member.getId()))
		// 	.toList();
		return new GroupInfoDTO(group);
		// } catch (NotFoundException e) {
		// 	throw new K8sException(WorkspaceErrorCode.NOT_FOUND_WORKSPACE);
		// }
	}

	@Override
	public GroupSummaryDTO createGroup(GroupReqVO groupReqVO) {
		GroupResource rootGroup = getGroupResourceByName(groupReqVO.getGroupCategory().getValue());
		GroupRepresentation groupRep = groupReqVO.createGroupRep();
		rootGroup.subGroup(groupRep);
		GroupRepresentation matchedGroup = findGroupFromRootGroup(rootGroup, groupRep.getName());
		return new GroupSummaryDTO(matchedGroup);
	}

	@Override
	public GroupSummaryDTO createChildGroup(GroupReqVO.ChildGroupReqVO groupReqVO) {
		GroupResource group = getGroupResourceById(groupReqVO.getParentGroupId());
		GroupRepresentation groupRep = groupReqVO.createGroupRep();
		group.subGroup(groupRep);
		GroupRepresentation matchedGroup = findGroupFromRootGroup(group, groupRep.getName());
		return new GroupSummaryDTO(matchedGroup);
	}

	@Override
	public void deleteGroupById(String groupId) {
		keycloakConfig.getRealmClient().groups().group(groupId).remove();
	}

	@Override
	public void modiGroupById(String groupId, GroupReqDTO.ModifyGroupDTO groupModiVO) {
		GroupResource groupResource = keycloakConfig.getRealmClient().groups().group(groupId);
		GroupRepresentation groupRep = groupResource.toRepresentation();
		groupRep.setName(groupModiVO.getGroupName());
		Map<String, List<String>> attributes = groupRep.getAttributes();
		if (attributes != null) {
			attributes.put("description",List.of(groupModiVO.getDescription()));
		} else {
			groupRep.setAttributes(Map.of("description", List.of(groupModiVO.getDescription())));
		}
		groupResource.update(groupRep);

		//user 추가하기 전 기존 멤버 삭제
		List<UserRepresentation> members = groupResource.members(0, Integer.MAX_VALUE);
		for (UserRepresentation member : members) {
			String memberId = member.getId();
			keycloakConfig.getRealmClient().users().get(memberId).leaveGroup(groupId);
		}

		if(Objects.nonNull(groupModiVO.getUsers())){
			//group에 member join
			joinMembersIntoGroup(groupId, groupModiVO.getUsers());
		}
	}

	@Override
	public GroupUserDTO.SubGroupUserDto findUsersByGroupId(String groupId, AuthType authType) {
		GroupResource group = keycloakConfig.getRealmClient().groups().group(groupId);
		List<GroupRepresentation> subGroups = group.toRepresentation().getSubGroups();
		List<UserRepresentation> groupMembers = new ArrayList<>();
		List<UserRepresentation> members = group.members(0, Integer.MAX_VALUE);

		if(authType == AuthType.ROLE_ADMIN){
			for (UserRepresentation member : members) {
				UserResource userResource = keycloakConfig.getRealmClient().users().get(member.getId());
				List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
				for (RoleRepresentation roleRepresentation : roleRepresentations) {
					if(roleRepresentation.getName().equalsIgnoreCase("ROLE_ADMIN")){
						groupMembers.add(member);
					}
				}
			}
		}else{
			groupMembers = group.members(0,Integer.MAX_VALUE);
		}

		return new GroupUserDTO.SubGroupUserDto(subGroups, groupMembers);
	}

	@Override
	public void joinMembersIntoGroup(String groupId, List<String> userIds) {
		userIds.forEach(userId -> userRepository.joinGroup(groupId, userId));
	}

	private GroupRepresentation getGroupByName(String groupName) {
		return keycloakConfig.getRealmClient()
			.groups()
			.groups()
			.stream()
			.filter(group -> group.getName().equals(groupName))
			.toList()
			.get(0);
	}

	private GroupResource getGroupResourceById(String groupId) {
		return keycloakConfig.getRealmClient().groups().group(groupId);
	}

	private GroupResource getGroupResourceByName(String name) {
		GroupRepresentation group = getGroupByName(name);
		return getGroupResourceById(group.getId());
	}

	private GroupRepresentation findGroupFromRootGroup(GroupResource rootGroup, String groupName) {
		GroupRepresentation representation = rootGroup.toRepresentation();
		return representation.getSubGroups()
			.stream()
			.filter(group -> group.getName().equals(groupName))
			.toList()
			.get(0);
	}

	private boolean searchInfo(FindDTO findDTO, GroupRepresentation group) {
		boolean search = true;
		if (StringUtils.isBlank(findDTO.getSearchCondition().getOption()) && StringUtils.isBlank(
			findDTO.getSearchCondition().getKeyword())) {
			return search;
		}
		if (findDTO.getSearchCondition().getOption().equalsIgnoreCase("groupName")) {
			search = group.getName().contains(findDTO.getSearchCondition().getKeyword());
		}
		return search;
	}

	@Override
	public List<GroupUserDTO.UserDTO> getWorkspaceMember(String groupName) {

		GroupRepresentation subGroup = getWsSubGroupByGroupName(groupName);
		GroupUserDTO.SubGroupUserDto usersByGroupId = findUsersByGroupId(subGroup.getId(), null);

		return usersByGroupId.getUsers();
	}

	@Override
	public void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList) {
		// ws 그룹 조회 조회
		GroupRepresentation swGroup = getWsSubGroupByGroupName(groupName);

		for (String userId : userIdList) {
			UserResource userResource = keycloakConfig.getRealmClient().users().get(userId);
			userResource.toRepresentation();
			// 회원 WS 삭제
			userResource.leaveGroup(swGroup.getId());
		}
	}

	@Override
	public void addWorkspaceMemberByUserId(String groupName, AddWorkspaceUsersDTO addWorkspaceUsersDTO) {
		GroupRepresentation workspace = getWsSubGroupByGroupName(groupName);
		//그룹내 유저, 요청받은 유저 조회 후 중복 삭제
		Set<String> userIds = new HashSet<>();
		for (String userId : addWorkspaceUsersDTO.getUserIds()) {
			userIds.add(userId);
		}
		List<String> groupIds = addWorkspaceUsersDTO.getGroupIds();
		getAllGroupMembers(userIds, groupIds);

		for (String userId : userIds) {
			keycloakConfig.getRealmClient().users().get(userId).joinGroup(workspace.getId());
			// 워크스페이스 회원 추가 검사를 위한 GroupUser 조회
			Optional<GroupUserDTO.UserDTO> group = findUsersByGroupId(workspace.getId(), null).getUsers().stream()
				.filter(groupUserDTO -> groupUserDTO.getUserId().equals(userId))
				.findFirst();

			if (group.isEmpty()) {
				throw new K8sException(WorkspaceErrorCode.WORKSPACE_USER_ADD_FAIL);
			}
		}

	}
	public void getAllGroupMembers(Set<String> userIds, List<String> groupIds) {
		for (String groupId : groupIds) {
			retrieveGroupMembers(userIds, groupId);
		}
	}
	private void retrieveGroupMembers(Set<String> userIds, String groupId) {
		GroupResource groupResource = keycloakConfig.getRealmClient().groups().group(groupId);
		List<UserRepresentation> members = groupResource.members(0, Integer.MAX_VALUE);
		for (UserRepresentation member : members) {
			userIds.add(member.getId());
		}
		//sub group
		List<GroupRepresentation> subGroups = groupResource.toRepresentation().getSubGroups();
		for (GroupRepresentation subGroup : subGroups) {
			retrieveGroupMembers(userIds, subGroup.getId());
		}
	}
	@Override
	public List<GroupUserDTO> getWorkspaceMemberBySearch(String groupName, String search) {

		RealmResource realmClient = keycloakConfig.getRealmClient();

		GroupRepresentation wsSubGroupByGroupName = getWsSubGroupByGroupName(groupName);

		List<UserRepresentation> members = realmClient.groups().group(wsSubGroupByGroupName.getId()).members(0, Integer.MAX_VALUE);

		return members.stream().filter(
				userRepresentation -> (userRepresentation.getLastName() + userRepresentation.getFirstName()).contains(
					search))
			.map(GroupUserDTO::new).toList();
	}

	private GroupRepresentation getWsSubGroupByGroupName(String subGroupName) {
		// ws 그룹 조회
		GroupRepresentation parentGroup = keycloakConfig.getRealmClient()
			.groups()
			.groups()
			.stream()
			.filter(userRepository -> userRepository.getName().equals("ws"))
			.findFirst()
			.get();
		GroupRepresentation subGroup = parentGroup.getSubGroups()
			.stream()
			.filter(groupRepresentation -> groupRepresentation.getName().equals(subGroupName))
			.findFirst()
			.orElseThrow(() -> new K8sException(WorkspaceErrorCode.WORKSPACE_NOT_FOUND));
		return subGroup.getSubGroups().stream()
			.filter(groupRepresentation -> groupRepresentation.getName().equalsIgnoreCase("user")).findFirst().get();
	}

	@Override
	public GroupUserDTO getWorkspaceOwner(String groupName) {

		UserRepresentation owner = getWorkspaceGroupOwner(groupName);

		return GroupUserDTO.builder()
			.id(owner.getId())
			.email(owner.getEmail())
			.name(owner.getUsername())
			.firstName(owner.getFirstName())
			.lastName(owner.getLastName())
			.build();
	}

	@Override
	public UserDTO.SearchGroupAndUser getUserAndGroupBySearchText(String searchText) {
		RealmResource realmClient = keycloakConfig.getRealmClient();

		List<UserRepresentation> userList = realmClient.users()
			.list()
			.stream()
			.filter(
				userRepresentation -> (userRepresentation.getLastName() + userRepresentation.getFirstName())
					.toLowerCase()
					.contains(searchText.toLowerCase()))
			.toList();

		// 검색 조회된 사용자 정보 리스트
		List<UserDTO.SearchUser> searchUsers = new ArrayList<>();
		List<UserDTO.SearchGroup> searchGroups = new ArrayList<>();
		for (UserRepresentation userRepresentation : userList) {
			String userId = userRepresentation.getId();
			String email = userRepresentation.getEmail();
			String userName = userRepresentation.getLastName() + userRepresentation.getFirstName();
			List<GroupRepresentation> groups = realmClient.users().get(userId).groups().stream()
				.filter(groupRepresentation -> groupRepresentation.getPath().contains("account"))
				.toList();

			if(groups.size() == 1){
				UserDTO.SearchUser searchGroupAndUser = UserDTO.SearchUser.builder()
					.userId(userId)
					.userName(userName)
					.group("none")
					.email(email)
					.build();
				searchUsers.add(searchGroupAndUser);
				continue;
			}
			for (GroupRepresentation group : groups) {
				if(group.getPath().equalsIgnoreCase("/account/default")){
					continue;
				}
				String groupPath = group.getPath().replace("/account/", "").replace("/", " > ");

				UserDTO.SearchUser searchGroupAndUser = UserDTO.SearchUser.builder()
					.userId(userId)
					.userName(userName)
					.group(groupPath)
					.email(email)
					.build();
				searchUsers.add(searchGroupAndUser);
			}
		}

		// 	그룹 검색 해야함 -> 하위 그룹 전체 다 검색하고 그 중 중복은 제거해야함
		List<GroupRepresentation> targetGroups = realmClient.groups().groups().stream()
		.filter(groupRepresentation ->
			groupRepresentation.getPath().equalsIgnoreCase("/account")
		).toList();

		searchGroups(searchGroups, targetGroups, searchText);

		return UserDTO.SearchGroupAndUser.builder()
			.users(searchUsers)
			.groups(searchGroups)
			.build();
	}

	private void searchGroups(List<UserDTO.SearchGroup> resultGroups, List<GroupRepresentation> targetGroups, String searchText) {
		for (GroupRepresentation targetGroup : targetGroups) {
			if(!targetGroup.getName().equalsIgnoreCase("account")
				&& !targetGroup.getName().equalsIgnoreCase("default")
				&& targetGroup.getName().toLowerCase().contains(searchText.toLowerCase())){
				UserDTO.SearchGroup searchGroup = UserDTO.SearchGroup.builder()
					.groupName(targetGroup.getName())
					.groupId(targetGroup.getId())
					.build();
				resultGroups.add(searchGroup);
			}
			if(targetGroup.getSubGroups().size() != 0){
				List<GroupRepresentation> subGroups = targetGroup.getSubGroups();
				searchGroups(resultGroups, subGroups, searchText);
			}
		}
	}

	@Override
	public void deleteGroupMemberByUserId(String groupId, List<String> userIdList) {
		GroupRepresentation group = getGroupResourceById(groupId).toRepresentation();
		for (String userId : userIdList) {
			UserResource userResource = keycloakConfig.getRealmClient().users().get(userId);
			userResource.toRepresentation();
			// 회원 삭제
			userResource.leaveGroup(group.getId());
		}
	}

	private UserRepresentation getWorkspaceGroupOwner(String subGroupName) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		// ws 그룹 조회
		GroupRepresentation parentGroup = getParentGroup();
		GroupRepresentation subGroup = parentGroup.getSubGroups()
			.stream()
			.filter(groupRepresentation ->
				groupRepresentation.getName().equals(subGroupName))
			.findFirst()
			.orElseThrow(() -> new K8sException(WorkspaceErrorCode.WORKSPACE_NOT_FOUND));
		GroupRepresentation ownerGroup = subGroup.getSubGroups().stream()
			.filter(groupRepresentation -> groupRepresentation.getName().equalsIgnoreCase("owner")).findFirst().get();
		return realmClient.groups().group(ownerGroup.getId()).members(0, Integer.MAX_VALUE).get(0);
	}

	private GroupRepresentation getParentGroup() {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		return realmClient
			.groups()
			.groups()
			.stream()
			.filter(userRepository -> userRepository.getName().equals("ws"))
			.findFirst()
			.get();
	}
}
