package com.xiilab.moduleuser.repository;

import com.xiilab.moduleuser.common.FindDTO;
import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.AuthType;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.vo.UserReqVO;
import io.micrometer.common.util.StringUtils;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserRepository implements UserRepository {
    private final KeycloakConfig keycloakConfig;

    @Value("${admin.init-password}")
    private String initPassword;

    @Override
    public UserInfo joinUser(UserReqVO userReqVO) {
        Response response = keycloakConfig.getRealmClient().users().create(userReqVO.convertUserRep());
        if (response.getStatus() != 200 && response.getStatus() != 201) {
            throw new IllegalArgumentException(response.getStatusInfo().getReasonPhrase());
        }
        log.info(response.getStatusInfo().getReasonPhrase());
        UserRepresentation userRep = getUserByUsername(userReqVO.getUsername());
        UserResource userResource = getUserResourceById(userRep.getId());
        userResource.resetPassword(userReqVO.createCredentialRep());
        return new UserInfo(userResource.toRepresentation());
    }

    @Override
    public List<UserSummary> getUserList(FindDTO findDTO) {
        RealmResource realmClient = keycloakConfig.getRealmClient();
        List<UserRepresentation> userList = realmClient.users().list().stream().filter(user
                        -> user.getAttributes() != null
                        && user.getAttributes().containsKey("approvalYN")
                        && user.getAttributes().containsValue(List.of("true"))
                        && searchInfo(findDTO, user)
                )
                .toList();
        return userList.stream().map(UserSummary::new).toList();
    }

    @Override
    public UserInfo getUserInfoById(String userId) {
        UserResource userResource = getUserResourceById(userId);
        List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
        UserRepresentation userRepresentation = userResource.toRepresentation();
        try {
            RoleRepresentation roleRepresentation = roleRepresentations.stream()
                    .filter(role -> role.getName().contains("ROLE_"))
                    .toList()
                    .get(0);
            userRepresentation.setRealmRoles(List.of(roleRepresentation.getName()));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ArrayIndexOutOfBoundsException("일치하는 정보가 없습니다.");
        }
        List<GroupRepresentation> groupList;
        try {
            groupList = userResource.groups();
        } catch (NullPointerException e) {
            groupList = null;
        }
        return new UserInfo(userRepresentation, groupList);
    }

    @Override
    public List<UserSummary> getUserListSearchByAttribute(String attribute) {
        RealmResource realmClient = keycloakConfig.getRealmClient();
        List<UserRepresentation> userList = realmClient.users().list().stream().filter(user
                        -> user.getAttributes() != null
                        && user.getAttributes().containsKey(attribute)
                        && user.getAttributes().containsValue(List.of("false")))
                .toList();
        return userList.stream().map(UserSummary::new).toList();
    }

    @Override
    public void updateUserAttribute(String userId, Map<String, String> map) {
        UserResource userResource = getUserResourceById(userId);
        UserRepresentation representation = userResource.toRepresentation();
        Map<String, List<String>> attributes = representation.getAttributes();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            attributes.put(key, List.of(value));
        }
        userResource.update(representation);
    }

    @Override
    public void updateUserActivationYN(String userId, boolean activationYN) {
        UserResource userResource = getUserResourceById(userId);
        UserRepresentation representation = userResource.toRepresentation();
        representation.setEnabled(activationYN);
        userResource.update(representation);
    }

    @Override
    public void deleteUserById(String userId) {
        getUserResourceById(userId).remove();
    }

    @Override
    public void updateUserRole(String userId, AuthType authType) {
        UserResource userResource = getUserResourceById(userId);
        // ROLE list 조회
        List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll()
                .stream().filter(role -> role.getName().contains("ROLE_"))
                .toList();
        // 기존 ROLE 삭제
        if (!roleRepresentations.isEmpty()) {
            userResource.roles().realmLevel().remove(roleRepresentations);
            RoleRepresentation roleRepresentation = getRolerepByName(authType.name());
            userResource.roles().realmLevel().add(List.of(roleRepresentation));
        }
        // ROLE 추가
        RoleRepresentation roleRepresentation = getRolerepByName(authType.name());
        userResource.roles().realmLevel().add(List.of(roleRepresentation));
    }

    @Override
    public void joinGroup(String groupId, String userId) {
        UserResource userResource = getUserResourceById(userId);
        userResource.joinGroup(groupId);
    }

    @Override
    public void resetUserPassWord(String userId) {
        try {
            UserResource userResource = getUserResourceById(userId);
            // userId 유효 체크
            userResource.toRepresentation();
            // 비밀번호 변경을 위해 credential 설정
            CredentialRepresentation authenticationSettings = getAuthenticationSettings(true, userId);
            //비밀번호 리셋
            userResource.resetPassword(authenticationSettings);
        } catch (NotFoundException e) {
            throw new NotFoundException("일치하는 사용자가 없습니다.");
        }

    }

    private UserResource getUserResourceById(String userId) {
        return keycloakConfig.getRealmClient().users().get(userId);
    }

    private UserRepresentation getUserByUsername(String username) {
        return keycloakConfig.getRealmClient().users().search(username).get(0);
    }

    private RoleRepresentation getRolerepByName(String roleName) {
        return keycloakConfig.getRealmClient().roles().get(roleName).toRepresentation();
    }

    /**
     * 사용자 비밀번호를 초기화 해주기 위해 세팅하는 메서드
     *
     * @param isTemporary true 비밀번호 초기화 하여 임의이 비밀번호가 세팅되었을 때
     *                    false 사용자가 비밀번호를 변경하였을 때
     * @param password    비밀번호 정보
     * @return
     */
    private CredentialRepresentation getAuthenticationSettings(boolean isTemporary, String password) {
        CredentialRepresentation newCredential = new CredentialRepresentation();
        String pw = password;
        if (StringUtils.isEmpty(password)) {
            pw = initPassword;
        }
        // credential Type 설정
        newCredential.setType(CredentialRepresentation.PASSWORD);
        // credential value 설정
        newCredential.setValue(pw);
        // password temporary 설정
        newCredential.setTemporary(isTemporary);

        return newCredential;
    }

    private boolean searchInfo(FindDTO findDTO, UserRepresentation user) {
        boolean search = true;
        if (StringUtils.isBlank(findDTO.getSearchCondition().getOption()) && StringUtils.isBlank(findDTO.getSearchCondition().getKeyword())) {
            return search;
        }
        if (findDTO.getSearchCondition().getOption().equalsIgnoreCase("ALL")) {
            search = user.getFirstName().contains(findDTO.getSearchCondition().getKeyword())
                    || user.getLastName().contains(findDTO.getSearchCondition().getKeyword())
                    || user.getEmail().contains(findDTO.getSearchCondition().getKeyword());
        }
        return search;
    }
}
