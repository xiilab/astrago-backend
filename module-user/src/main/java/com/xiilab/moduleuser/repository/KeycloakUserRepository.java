package com.xiilab.moduleuser.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.enums.AuthType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.UserErrorCode;
import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserInfo;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.enums.UserCreatedAt;
import com.xiilab.moduleuser.enums.UserEnable;
import com.xiilab.moduleuser.vo.UserReqVO;

import io.micrometer.common.util.StringUtils;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserRepository implements UserRepository {
	private final KeycloakConfig keycloakConfig;
	private final String KEY_APPROVAL_YN = "approvalYN";

	@Value("${admin.init-password}")
	private String initPassword;

	@Override
	public UserInfo joinUser(UserReqVO userReqVO) {
		UserRepresentation userRepresentation = userReqVO.convertUserRep();
		// User 중복 체크
		checkUserDuplicate(userReqVO);

		Response response = keycloakConfig.getRealmClient().users().create(userRepresentation);
		if (response.getStatus() != 200 && response.getStatus() != 201) {
			throw new RestApiException(UserErrorCode.USER_CREATE_FAIL);
		}
		log.info(response.getStatusInfo().getReasonPhrase());
		UserRepresentation userRep = getUserByUsername(userReqVO.getUsername());
		UserResource userResource = getUserResourceById(userRep.getId());
		userResource.resetPassword(userReqVO.createCredentialRep());
		userResource.roles().realmLevel().add(List.of(getRolerepByName(AuthType.ROLE_USER.name())));
		return new UserInfo(userResource.toRepresentation());
	}

	private void checkUserDuplicate(UserReqVO userReqVO) {
		List<UserRepresentation> list = keycloakConfig.getRealmClient().users().list();
		// 이름 중복 검사
		boolean isUsernameExists = list.stream()
			.anyMatch(userRepresentation -> userRepresentation.getUsername().equals(userReqVO.getUsername()));
		if (isUsernameExists) {
			throw new RestApiException(UserErrorCode.USER_CREATE_FAIL_SAME_NAME);
		}
		// 메일 중복검사
		boolean isEmailExists = list.stream()
			.anyMatch(userRepresentation -> userRepresentation.getEmail().equals(userReqVO.getEmail()));
		if (isEmailExists) {
			throw new RestApiException(UserErrorCode.USER_CREATE_FAIL_SAME_EMAIL);
		}
	}

	@Override
	public UserDTO.PageUsersDTO getUserList(Integer pageNo, Integer pageSize, UserSearchCondition searchCondition) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> users = realmClient.users().list(0, Integer.MAX_VALUE)
			.stream().filter(user
				-> user.getAttributes() != null
				&& user.getAttributes().containsKey(KEY_APPROVAL_YN)
				&& searchName(searchCondition.getSearchText(), user)
				&& enableEq(searchCondition.getUserEnable(), user)
			)
			.sorted(
				searchCondition.getCreatedAt() == UserCreatedAt.DESC ?
					Comparator.comparing(UserRepresentation::getCreatedTimestamp).reversed() :
					Comparator.comparing(UserRepresentation::getCreatedTimestamp)
			)
			.toList();

		List<UserSummary> userSummaries = users.stream().map(userRepresentation -> {
				// 워크스페이스 관련 그룹 제외
				List<GroupRepresentation> groups = realmClient.users()
					.get(userRepresentation.getId())
					.groups(0, Integer.MAX_VALUE)
					.stream()
					.filter(
						groupRepresentation -> !groupRepresentation.getName().equals("ws") && !groupRepresentation.getName()
							.equals("owner") && !groupRepresentation.getName().equals("user"))
					.toList();
				return new UserSummary(userRepresentation, groups);
			})
			.map(userSummary -> {
					UserResource userResource = realmClient.users().get(userSummary.getUid());
					List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
				List<RoleRepresentation> roles = roleRepresentations.stream()
					.filter(role -> role.getName().contains("ROLE_"))
					.toList();
				if(roles != null && roles.size() > 0){
					userSummary.setAuthType(AuthType.valueOf(roles.get(0).getName()));
				}
					return userSummary;
				}
			)
			.toList();

		return getPageUsersDTO(pageNo, pageSize, users, userSummaries);
	}

	private boolean enableEq(UserEnable userEnable, UserRepresentation user) {
		if (userEnable == null) {
			return true;
		}
		return userEnable.isEnable() == user.isEnabled();
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
			throw new RestApiException(UserErrorCode.USER_NOT_FOUND_INFO);
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
	public UserDTO.UserInfo getUserById(String userId) {
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
			throw new RestApiException(UserErrorCode.USER_NOT_FOUND_INFO);
		}
		List<GroupRepresentation> groupList;
		try {
			groupList = userResource.groups();
		} catch (NullPointerException e) {
			groupList = null;
		}
		return new UserDTO.UserInfo(userRepresentation, groupList);
	}

	@Override
	public UserDTO.PageUsersDTO getWaitingApprovalUserList(Integer pageNo, Integer pageSize,
		UserSearchCondition searchCondition) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		List<UserRepresentation> users = realmClient.users().list(0, Integer.MAX_VALUE)
			.stream().filter(user
				-> user.getAttributes() != null
				&& user.getAttributes().containsKey(KEY_APPROVAL_YN)
				&& user.getAttributes().get(KEY_APPROVAL_YN).get(0).equals("false")
			)
			.sorted(
				searchCondition.getCreatedAt() == UserCreatedAt.DESC ?
					Comparator.comparing(UserRepresentation::getCreatedTimestamp).reversed() :
					Comparator.comparing(UserRepresentation::getCreatedTimestamp)
			)
			.toList();
		List<UserSummary> userSummaries = users.stream()
			.map(userRepresentation -> new UserSummary(userRepresentation))
			.toList();
		return getPageUsersDTO(pageNo, pageSize, users, userSummaries);
	}

	private static UserDTO.PageUsersDTO getPageUsersDTO(Integer pageNo, Integer pageSize,
		List<UserRepresentation> users, List<UserSummary> userSummaries) {
		int totalCount = users.size();
		int startIndex = (pageNo - 1) * pageSize;
		int endIndex = Math.min(startIndex + pageSize, totalCount);
		if (startIndex >= totalCount || endIndex <= startIndex) {
			// 페이지 범위를 벗어나면 빈 리스트 반환
			return UserDTO.PageUsersDTO.builder()
				.users(null)
				.totalCount(totalCount)
				.build();
		}
		return UserDTO.PageUsersDTO.builder()
			.users(userSummaries.subList(startIndex, endIndex))
			.totalCount(totalCount)
			.build();
	}

	@Override
	public List<UserSummary> getUserListSearchByAttribute(String attribute) {
		RealmResource realmClient = keycloakConfig.getRealmClient();
		return realmClient.users().list().stream().filter(user
				-> user.getAttributes() != null
				&& user.getAttributes().containsKey(attribute)
				&& user.getAttributes().containsValue(List.of("false")))
			.map(userRepresentation -> {
				List<GroupRepresentation> groups = realmClient.users().get(userRepresentation.getId()).groups(0, 100);
				return new UserSummary(userRepresentation, groups);
			}).toList();
		// return userList.stream().map(UserSummary::new).toList();
	}

	@Override
	public void updateUserAttribute(List<String> userIdList, Map<String, String> map) {
		userIdList.forEach(user -> {
			UserResource userResource = getUserResourceById(user);
			UserRepresentation representation = userResource.toRepresentation();
			Map<String, List<String>> attributes = representation.getAttributes();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				attributes.put(key, List.of(value));
			}
			userResource.update(representation);
		});
	}

	@Override
	public void updateUserActivationYN(List<String> userIdList, boolean activationYN) {
		userIdList.forEach(user -> {
			UserResource userResource = getUserResourceById(user);
			UserRepresentation representation = userResource.toRepresentation();
			representation.setEnabled(activationYN);
			userResource.update(representation);
		});
	}

	@Override
	public void deleteUserById(List<String> userIdList) {
		userIdList.forEach(user -> {
			getUserResourceById(user).remove();
		});
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
	public void joinDefaultGroup(String userId) {
		UserResource userResource = getUserResourceById(userId);

		GroupRepresentation defaultGroup = keycloakConfig.getRealmClient()
			.groups()
			.groups("default", 0, 1)
			.stream()
			.findFirst()
			.get().getSubGroups().stream().filter(subGroup -> subGroup.getName().equals("default")).findFirst().get();

		userResource.joinGroup(defaultGroup.getId());
	}

	@Override
	public void resetUserPassWord(String userId) {
		try {
			UserResource userResource = getUserResourceById(userId);
			// userId 유효 체크
			userResource.toRepresentation();
			// 비밀번호 변경을 위해 credential 설정
			CredentialRepresentation authenticationSettings = getAuthenticationSettings(true, "astrago");
			//비밀번호 리셋
			userResource.resetPassword(authenticationSettings);
		} catch (NotFoundException e) {
			throw new RestApiException(UserErrorCode.USER_NOT_FOUND_BY_ID);
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

	private boolean searchName(String searchText, UserRepresentation user) {
		boolean search = true;
		if (StringUtils.isBlank(searchText)) {
			return search;
		}

		String userRealName = user.getLastName() + user.getFirstName();
		String userName = user.getUsername();

		search = user.getEmail().contains(searchText) ||
			userRealName.contains(searchText) ||
			userName.contains(searchText);
		return search;
	}

	@Override
	public List<SearchDTO> getUserAndGroupBySearch(String search) {

		List<SearchDTO> result = new ArrayList<>();

		RealmResource realmClient = keycloakConfig.getRealmClient();

		List<UserRepresentation> userList = realmClient.users()
			.list()
			.stream()
			.filter(
				userRepresentation -> (userRepresentation.getLastName() + userRepresentation.getFirstName()).contains(
					search))
			.toList();

		// 검색 조회된 사용자 정보 리스트
		List<SearchDTO> searchUserList = userList.stream().map(user ->
			SearchDTO.builder().id(user.getId())
				.name(user.getUsername())
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.email(user.getEmail())
				.groupYN(false)
				.userGroupDTOS(
					realmClient.users().get(user.getId())
						.groups(0, Integer.MAX_VALUE)
						.stream()
						.filter(groupRepresentation -> !groupRepresentation.getName().equals("owner")
							&& !groupRepresentation.getName().equals("ws") &&
							!groupRepresentation.getName().equals("user"))
						.map(groupRepresentation ->
							UserSummary.UserGroupDTO.builder()
								.groupId(groupRepresentation.getId())
								.groupName(groupRepresentation.getName())
								.build()).toList())
				.build()).toList();

		// account Group
		GroupRepresentation accountGroup = realmClient.groups()
			.groups()
			.stream()
			.filter(groupRepresentation -> groupRepresentation.getName().equals("account"))
			.findFirst()
			.get();

		// account Group의 SubGroup search
		List<SearchDTO> searchGroupList = accountGroup.getSubGroups().stream().filter(groupRepresentation ->
				groupRepresentation.getName().contains(search))
			.map(groupRepresentation ->
				SearchDTO.builder()
					.id(groupRepresentation.getId())
					.name(groupRepresentation.getName())
					.groupYN(true)
					.groupUserDTOS(
						realmClient.groups().group(groupRepresentation.getId())
							.members().stream().map(GroupUserDTO::new).toList())
					.build()).toList();
		// 조회된 group, user List에 추가
		result.addAll(searchGroupList);
		result.addAll(searchUserList);

		return result;
	}

	@Override
	public void updateUserInfoById(String id, UpdateUserDTO updateUserDTO) {

		UserResource userResource = getUserResourceById(id);

		UserRepresentation representation = userResource.toRepresentation();
		representation.setFirstName(updateUserDTO.getFirstName());
		representation.setLastName(updateUserDTO.getLastName());

		List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll()
			.stream().filter(role -> role.getName().contains("ROLE_"))
			.toList();
		// 기존 ROLE 삭제
		if (!roleRepresentations.isEmpty()) {
			userResource.roles().realmLevel().remove(roleRepresentations);
			RoleRepresentation roleRepresentation = getRolerepByName(updateUserDTO.getAuth().name());
			userResource.roles().realmLevel().add(List.of(roleRepresentation));
		}
		// ROLE 추가
		RoleRepresentation roleRepresentation = getRolerepByName(updateUserDTO.getAuth().name());
		userResource.roles().realmLevel().add(List.of(roleRepresentation));

		keycloakConfig.getRealmClient().users().get(id).update(representation);
	}

	@Override
	public void updateUserEnable(String id, boolean enable) {
		try {
			UserResource userResource = getUserResourceById(id);
			UserRepresentation representation = userResource.toRepresentation();
			representation.setEnabled(enable);
			userResource.update(representation);
		} catch (NotFoundException e) {
			throw new RestApiException(UserErrorCode.USER_NOT_FOUND_BY_ID);
		}
	}

	@Override
	public List<UserInfo> getAdminList() {
		List<UserRepresentation> adminList = keycloakConfig.getRealmClient().roles().get("ROLE_ADMIN").getUserMembers();
		return adminList.stream().map(userRepresentation ->
			new UserInfo(userRepresentation, null)).toList();
	}

}
