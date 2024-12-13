package com.xiilab.modulek8sdb.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
// @AllArgsConstructor
@Embeddable
public class RegUser {
	@Column(name = "REG_USER_ID", updatable = false)
	protected String regUserId;
	@Column(name = "REG_USER_NAME", updatable = false)
	protected String regUserName;
	@Column(name = "REG_USER_REAL_NAME", updatable = false)
	protected String regUserRealName;

	public RegUser(String regUserId, String regUserName, String regUserRealName) {
		this.regUserId = regUserId;
		this.regUserName = regUserName;
		this.regUserRealName = regUserRealName;
	}
}
