package com.xiilab.moduleuser.repository;

import java.util.List;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Repository;

import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.vo.GroupModiVO;
import com.xiilab.moduleuser.vo.GroupReqVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupRepository implements GroupRepository {
	private final KeycloakConfig keycloakConfig;
	private final UserRepository userRepository;

	@Override
	public List<GroupSummaryDTO> getGroupList() {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		return realmClient.groups().groups().stream().map(GroupSummaryDTO::new).toList();
	}

	@Override
	public GroupInfoDTO getGroupById(String groupId) {
		GroupResource group = keycloakConfig.getRealmClient().groups().group(groupId);
		List<UserInfo> groupUsers = group.members()
			.stream()
			.map(member -> userRepository.getUserInfoById(member.getId()))
			.toList();
		return new GroupInfoDTO(group, groupUsers);
	}

	@Override
	public GroupSummaryDTO createGroup(GroupReqVO groupReqVO) {
		GroupResource rootGroup = getGroupResourceByName(groupReqVO.getGroupCategory().getValue());
		GroupRepresentation groupRep = groupReqVO.createGroupRep();
		rootGroup.subGroup(groupRep);
		GroupRepresentation representation = rootGroup.toRepresentation();
		GroupRepresentation createdGroup = representation.getSubGroups()
			.stream()
			.filter(group -> group.getName().equals(groupReqVO.getName()))
			.toList()
			.get(0);
		return new GroupSummaryDTO(createdGroup);
	}

	@Override
	public GroupSummaryDTO createChildGroup(GroupReqVO.ChildGroupReqVO groupReqVO) {
		GroupResource group = getGroupResourceById(groupReqVO.getParentGroupId());
		GroupRepresentation groupRep = groupReqVO.createGroupRep();
		group.subGroup(groupRep);
		GroupRepresentation representation = group.toRepresentation();
		GroupRepresentation createdGroup = representation.getSubGroups()
			.stream()
			.filter(findGroup -> findGroup.getName().equals(groupReqVO.getName()))
			.toList()
			.get(0);
		return new GroupSummaryDTO(createdGroup);
	}

	@Override
	public void deleteGroupById(String groupId) {
		keycloakConfig.getRealmClient().groups().group(groupId).remove();
	}

	@Override
	public void modiGroupById(String groupId, GroupModiVO groupModiVO) {
		GroupResource groupResource = keycloakConfig.getRealmClient().groups().group(groupId);
		GroupRepresentation groupRep = groupResource.toRepresentation();
		groupModiVO.modiGroupRep(groupRep);
		groupResource.update(groupRep);
	}

	@Override
	public List<GroupUserDTO> findUsersByGroupId(String groupId) {
		GroupResource group = keycloakConfig.getRealmClient().groups().group(groupId);
		return group.members().stream().map(GroupUserDTO::new).toList();
	}

	@Override
	public void joinMembersIntoGroup(String groupId, List<String> userIds) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		GroupResource group = realmClient.groups().group(groupId);
		List<UserRepresentation> userReps = userIds.stream()
			.map(userId -> realmClient.users().get(userId).toRepresentation())
			.toList();
		group.members().addAll(userReps);
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
}
