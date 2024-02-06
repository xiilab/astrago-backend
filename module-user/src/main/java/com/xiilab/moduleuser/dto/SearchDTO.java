package com.xiilab.moduleuser.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
	private String id;
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private boolean groupYN;
	private List<GroupUserDTO> groupUserDTOS;
	private List<UserSummary.UserGroupDTO> userGroupDTOS;

}
