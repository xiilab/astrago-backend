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
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.moduleuser.common.KeycloakConfig;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.dto.SearchDTO;
import com.xiilab.moduleuser.dto.UpdateUserDTO;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.dto.UserSearchCondition;
import com.xiilab.moduleuser.dto.UserSummary;
import com.xiilab.moduleuser.enums.UserAttribute;
import com.xiilab.moduleuser.enums.UserEnable;
import com.xiilab.moduleuser.enums.UserSort;
import com.xiilab.moduleuser.vo.UserReqVO;

import io.micrometer.common.util.StringUtils;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserRepositoryImpl implements UserRepository {
	private final KeycloakConfig keycloakConfig;
	private final MailService mailService;
	// private final String UserAttribute.APPROVAL_YN.getKey() = "approvalYN";
	@Value("${admin.init-password}")
	private String initPassword;

	@Override
	public UserDTO.UserInfo joinUser(UserReqVO userReqVO) {
		UserRepresentation userRepresentation = userReqVO.convertUserRep();
		// User 중복 체크
		checkUserDuplicate(userReqVO);
		userRepresentation.setRequiredActions(List.of("Verify Email"));
		userRepresentation.setEmailVerified(true);
		Response response = keycloakConfig.getRealmClient().users().create(userRepresentation);
		if (response.getStatus() != 200 && response.getStatus() != 201) {
			throw new RestApiException(UserErrorCode.USER_CREATE_FAIL);
		}
		log.info(response.getStatusInfo().getReasonPhrase());
		UserRepresentation userRep = getUserByUsername(userReqVO.getEmail());
		UserResource userResource = getUserResourceById(userRep.getId());
		userResource.resetPassword(userReqVO.createCredentialRep());
		userResource.roles().realmLevel().add(List.of(getRolerepByName(AuthType.ROLE_USER.name())));
		return new UserDTO.UserInfo(userResource.toRepresentation(), null);
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
				&& user.getAttributes().containsKey(UserAttribute.APPROVAL_YN.getKey())
				&& user.getAttributes().get(UserAttribute.APPROVAL_YN.getKey()).get(0).equals("true")
				&& searchName(searchCondition != null ? searchCondition.getSearchText() : null, user)
			)
			// .sorted(searchCondition != null ?
			// 	searchCondition.getUserSort() == UserSort.CREATED_AT_ASC ?
			// Comparator.comparing(UserRepresentation::getCreatedTimestamp) :
			// 		searchCondition.getUserSort() == UserSort.CREATED_AT_DESC ?
			// 			Comparator.comparing(UserRepresentation::getCreatedTimestamp).reversed() :
			// 			searchCondition.getUserSort() == UserSort.ENABLE_ASC ?
			// 				Comparator.comparing(UserRepresentation::isEnabled) :
			// 				Comparator.comparing(UserRepresentation::isEnabled).reversed()
			// 	: Comparator.comparing(UserRepresentation::getCreatedTimestamp)
			// )
			.toList();
		int userSize = users.size();

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
					List<String> roles = roleRepresentations.stream()
						.filter(role -> role.getName().contains("ROLE_"))
						.map(role -> role.getName())
						.toList();
					if (roles != null && roles.size() > 0) {
						userSummary.setAuthType(
							roles.contains(AuthType.ROLE_ADMIN.name()) ? AuthType.ROLE_ADMIN : AuthType.ROLE_USER);
					}
					return userSummary;
				}
			).sorted(searchCondition != null ?
				searchCondition.getUserSort() == UserSort.CREATED_AT_ASC ?
					Comparator.comparing(UserSummary::getJoinDate) :
					searchCondition.getUserSort() == UserSort.CREATED_AT_DESC ?
						Comparator.comparing(UserSummary::getJoinDate).reversed() :
						searchCondition.getUserSort() == UserSort.ENABLE_ASC ?
							Comparator.comparing(UserSummary::getEnable) :
								searchCondition.getUserSort() == UserSort.ENABLE_DESC ?
									Comparator.comparing(UserSummary::getEnable).reversed() :
										searchCondition.getUserAuth() == AuthType.ROLE_ADMIN ?
											Comparator.comparing(UserSummary::getAuthType) :
											Comparator.comparing(UserSummary::getAuthType).reversed()
				: Comparator.comparing(UserSummary::getJoinDate)
			)
			.toList();

		return getPageUsersDTO(pageNo, pageSize, userSize, userSummaries);
	}

	private boolean enableEq(UserEnable userEnable, UserRepresentation user) {
		if (userEnable == null) {
			return true;
		}
		return userEnable.isEnable() == user.isEnabled();
	}

	@Override
	public UserDTO.UserInfo getUserInfoById(String userId) {
		UserResource userResource = getUserResourceById(userId);
		List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
		UserRepresentation userRepresentation = userResource.toRepresentation();
		try {
			List<String> roles = roleRepresentations.stream()
				.filter(role -> role.getName().contains("ROLE_"))
				.map(RoleRepresentation::getName)
				.toList();
			userRepresentation.setRealmRoles(roles);
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
	public UserDTO.UserInfo getUserById(String userId) {
		UserResource userResource = getUserResourceById(userId);
		List<RoleRepresentation> roleRepresentations = userResource.roles().realmLevel().listAll();
		UserRepresentation userRepresentation = userResource.toRepresentation();
		try {
			List<String> roles = roleRepresentations.stream()
				.filter(role -> role.getName().contains("ROLE_"))
				.map(user -> user.getName())
				.toList();
			userRepresentation.setRealmRoles(roles);
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
		int userSize;
		List<UserRepresentation> users = realmClient.users().list(0, Integer.MAX_VALUE)
			.stream().filter(user
				-> user.getAttributes() != null
				&& user.getAttributes().containsKey(UserAttribute.APPROVAL_YN.getKey())
				&& user.getAttributes().get(UserAttribute.APPROVAL_YN.getKey()).get(0).equals("false")
				&& searchName(searchCondition.getSearchText(), user)
			)
			.sorted(
				searchCondition.getUserSort() == UserSort.CREATED_AT_DESC ?
					Comparator.comparing(UserRepresentation::getCreatedTimestamp).reversed() :
					Comparator.comparing(UserRepresentation::getCreatedTimestamp)
			)
			.toList();
		userSize = users.size();
		List<UserSummary> userSummaries = users.stream()
			.map(userRepresentation -> {
				List<GroupRepresentation> groups = realmClient.users()
					.get(userRepresentation.getId())
					.groups(0, Integer.MAX_VALUE)
					.stream()
					.filter(groupRepresentation -> !groupRepresentation.getName().equals("ws") &&
						!groupRepresentation.getName().equals("owner") &&
						!groupRepresentation.getName().equals("user") &&
						!groupRepresentation.getName().equals("default"))
					.toList();
				UserSummary userSummary = new UserSummary(userRepresentation);
				userSummary.setGroup(groups.size() == 0 ? null : groups.get(0).getName());
				return userSummary;
			})
			.toList();
		return getPageUsersDTO(pageNo, pageSize, userSize, userSummaries);
	}

	private static UserDTO.PageUsersDTO getPageUsersDTO(Integer pageNo, Integer pageSize,
		int userSize, List<UserSummary> userSummaries) {
		int totalCount = userSize;
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
		userIdList.forEach(user ->
			getUserResourceById(user).remove()
		);
	}

	@Override
	public void refuseUserById(List<String> userIdList) {
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
			CredentialRepresentation authenticationSettings = getAuthenticationSettings(false, "astrago");
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

		search = (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText.toLowerCase())) ||
			(userRealName != null && userRealName.toLowerCase().contains(searchText.toLowerCase())) ||
			(userName != null && userName.toLowerCase().contains(searchText.toLowerCase()));
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
		// 기존 ROLE 삭제 - user -> admin 변경일 경우 user, admin 권한 가지고있어야하고, admin -> user 변경이면 user 권한만 가지고있어야함
		if (!roleRepresentations.isEmpty()) {
			userResource.roles().realmLevel().remove(roleRepresentations);
		}
		if (updateUserDTO.getAuth() == AuthType.ROLE_ADMIN) {
			RoleRepresentation adminRole = getRolerepByName(AuthType.ROLE_ADMIN.name());
			RoleRepresentation userRole = getRolerepByName(AuthType.ROLE_USER.name());
			userResource.roles().realmLevel().add(List.of(adminRole, userRole));
		} else {
			// ROLE 추가
			RoleRepresentation roleRepresentation = getRolerepByName(AuthType.ROLE_USER.name());
			userResource.roles().realmLevel().add(List.of(roleRepresentation));
		}

		if (!StringUtils.isEmpty(updateUserDTO.getPassword())) {
			CredentialRepresentation authenticationSettings = getAuthenticationSettings(false,
				updateUserDTO.getPassword());
			userResource.resetPassword(authenticationSettings);
		}

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
	public List<UserDTO.UserInfo> getAdminList() {
		List<UserRepresentation> adminList = keycloakConfig.getRealmClient().roles().get("ROLE_ADMIN").getUserMembers();
		return adminList.stream().map(userRepresentation ->
			new UserDTO.UserInfo(userRepresentation, null)).toList();
	}

	@Override
	public String joinAdmin(UserReqVO userReqVO) {
		UserRepresentation userRepresentation = userReqVO.convertAdminRep();
		Response response = keycloakConfig.getRealmClient().users().create(userRepresentation);
		if (response.getStatus() != 200 && response.getStatus() != 201) {
			throw new RestApiException(UserErrorCode.USER_CREATE_FAIL);
		}
		log.info(response.getStatusInfo().getReasonPhrase());
		UserRepresentation userRep = getUserByUsername(userReqVO.getEmail());
		UserResource userResource = getUserResourceById(userRep.getId());
		userResource.resetPassword(userReqVO.createCredentialRep());
		userResource.roles().realmLevel().add(List.of(getRolerepByName(AuthType.ROLE_ADMIN.name())));
		joinDefaultGroup(userRep.getId());
		return userRep.getId();
	}
}
