package com.xiilab.moduleuser.dto;

import java.security.Principal;

public record UserInfo(
	String id,
	String userUUID,
	String userName,
	String firstName,
	String lastName,
	String email,
	AuthType auth,
	String phoneNumber,
	String company,
	String companyImagePath,
	Long limitAppCount,
	Double limitCPU,
	Long limitMEM
) {
	public static UserInfo convertFromPrincipal(Principal principal) {
		return null;
	}
}
