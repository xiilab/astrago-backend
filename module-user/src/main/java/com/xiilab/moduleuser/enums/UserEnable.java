package com.xiilab.moduleuser.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserEnable {
	TRUE(true),
	FALSE(false)
	;
	private boolean enable;
}
