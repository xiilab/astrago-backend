package com.xiilab.moduleuser.repository;

import java.util.List;
import java.util.Optional;

import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.UserErrorCode;
import com.xiilab.modulecommon.exception.errorcode.WorkspaceErrorCode;
import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.GroupCategory;
import com.xiilab.moduleuser.dto.GroupInfoDTO;
import com.xiilab.moduleuser.dto.GroupSummaryDTO;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.vo.GroupModiVO;
import com.xiilab.moduleuser.vo.GroupReqVO;

import io.micrometer.common.util.StringUtils;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupRepository implements GroupRepository {
	private final KeycloakConfig keycloakConfig;
	private final UserRepository userRepository;

	@Override
	public List<GroupSummaryDTO> getGroupList(FindDTO findDTO) {
		GroupResource rootGroup = getGroupResourceByName(GroupCategory.ACCOUNT.getValue());
		return rootGroup.toRepresentation().getSubGroups().stream()
			.filter(group -> searchInfo(findDTO, group))
			.map(GroupSummaryDTO::new).toList();
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
		try {
			List<UserInfo> groupUsers = group.members()
				.stream()
				.map(member -> userRepository.getUserInfoById(member.getId()))
				.toList();
			return new GroupInfoDTO(group, groupUsers);
		} catch (NotFoundException e) {
			throw new K8sException(UserErrorCode.GROUP_NOT_FOUND);
		}
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
	public List<GroupUserDTO> getWorkspaceMember(String groupName) {

		GroupRepresentation subGroup = getWsSubGroupByGroupName(groupName);

		return findUsersByGroupId(subGroup.getId());
	}
	@Override
	public void deleteWorkspaceMemberByUserId(String groupName, List<String> userIdList){
		// ws 그룹 조회 조회
		GroupRepresentation swGroup = getWsSubGroupByGroupName(groupName);

		for(String userId : userIdList){
			UserResource userResource = keycloakConfig.getRealmClient().users().get(userId);
			userResource.toRepresentation();
			// 회원 WS 삭제
			userResource.leaveGroup(swGroup.getId());
		}
	}
	@Override
	public void addWorkspaceMemberByUserId(String groupName, List<String> userIdList){

		GroupRepresentation subgroup = getWsSubGroupByGroupName(groupName);

		for(String userId : userIdList){
			keycloakConfig.getRealmClient().users().get(userId).joinGroup(subgroup.getId());
			// 워크스페이스 회원 추가 검사를 위한 GroupUser 조회
			Optional<GroupUserDTO> group = findUsersByGroupId(subgroup.getId()).stream()
				.filter(groupUserDTO -> groupUserDTO.getId().equals(userId))
				.findFirst();

			if(group.isEmpty()){
				throw new K8sException(UserErrorCode.USER_ADD_WORKSPACE_FAIL);
			}
		}

	}

	private GroupRepresentation getWsSubGroupByGroupName(String subGroupName){
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
}
