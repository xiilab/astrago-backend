package com.xiilab.modulek8sdb.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ModUser {
	@Column(name = "MOD_USER_ID")
	protected String modUserId;
	@Column(name = "MOD_USER_NAME")
	protected String modUserName;
}
