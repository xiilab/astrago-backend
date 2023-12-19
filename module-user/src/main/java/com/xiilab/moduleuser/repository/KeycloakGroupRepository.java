package com.xiilab.moduleuser.repository;

import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.*;
import com.xiilab.moduleuser.vo.GroupModiVO;
import com.xiilab.moduleuser.vo.GroupReqVO;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class KeycloakGroupRepository implements GroupRepository {
    private final KeycloakConfig keycloakConfig;
    private final UserRepository userRepository;

    @Override
    public List<GroupSummaryDTO> getGroupList() {
        GroupResource rootGroup = getGroupResourceByName(GroupCategory.ACCOUNT.getValue());
        return rootGroup.toRepresentation().getSubGroups().stream().map(GroupSummaryDTO::new).toList();
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
            throw new NotFoundException("일치하는 그룹이 없습니다.");
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
}
