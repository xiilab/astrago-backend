package com.xiilab.moduleuser.dto;

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
}
