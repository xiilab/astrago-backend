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
public class User {
	@Column(name = "USER_ID")
	protected String id;
	@Column(name = "USER_NAME")
	protected String name;
}
