package com.xiilab.servercore.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class RegUser{
	@Column(name = "REG_USER_ID")
	protected String regUserId;
	@Column(name = "REG_USER_NAME")
	protected String regUserName;
	@Column(name = "REG_USER_REAL_NAME")
	protected String regUserRealName;
}
