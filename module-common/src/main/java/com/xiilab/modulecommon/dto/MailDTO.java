package com.xiilab.modulecommon.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDTO {
	private String subject;
	private String title;
	private String subTitle;
	private String contentTitle;
	private List<Content> contents;
	private String footer;
	private String receiverEmail;   // 받는 이메일
	private String senderEmail;     // 보내는 이메일
	private String senderUserName;  // 보내는 이름

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class Content {
		private String col1;
		private String col2;
	}
	public void setReceiverEmail(String receiverEmail) {
 		this.receiverEmail = receiverEmail;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
}
