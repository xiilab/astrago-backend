package com.xiilab.servercore.common.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.ObjectUtils;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class ResDTO {
	protected String regUserId;
	protected String regUserName;
	protected String regUserRealName;
	protected String regDate;
	protected String modDate;

	protected ResDTO(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate, LocalDateTime modDate) {
		this.regUserId = regUserId;
		this.regUserName = regUserName;
		this.regUserRealName = regUserRealName;
		this.regDate = !ObjectUtils.isEmpty(regDate)? regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
		this.modDate = !ObjectUtils.isEmpty(modDate)? modDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
	}

	protected ResDTO(String regUserId, String regUserName, String regUserRealName, String regDate, String modDate) {
		this.regUserId = regUserId;
		this.regUserName = regUserName;
		this.regUserRealName = regUserRealName;
		this.regDate = regDate;
		this.modDate = modDate;
	}
}
