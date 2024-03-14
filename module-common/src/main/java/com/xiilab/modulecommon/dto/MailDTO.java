package com.xiilab.modulecommon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDTO {
	private String title;           // 메일 제목
	private String content;         // 메일 내용
	private String receiverEmail;   // 받는 이메일
	private String senderEmail;     // 보내는 이메일
	private String senderUserName;  // 보내는 이름
}
