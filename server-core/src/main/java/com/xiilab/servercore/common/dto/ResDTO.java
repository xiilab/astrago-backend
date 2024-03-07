package com.xiilab.servercore.common.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.util.ObjectUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ResDTO {
	private String regUserId;
	private String regUserName;
	private String regUserRealName;
	private String regDate;
	private String modDate;

	protected ResDTO(String regUserId, String regUserName, String regUserRealName, LocalDateTime regDate, LocalDateTime modDate) {
		this.regUserId = regUserId;
		this.regUserName = regUserName;
		this.regUserRealName = regUserRealName;
		// .regDate(imageEntity.getRegDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
		this.regDate = !ObjectUtils.isEmpty(regDate)? regDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
		this.modDate = !ObjectUtils.isEmpty(modDate)? modDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null;
	}
}
