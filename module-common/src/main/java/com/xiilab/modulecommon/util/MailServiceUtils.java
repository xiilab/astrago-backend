package com.xiilab.modulecommon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.MailAttribute;

import lombok.experimental.UtilityClass;

// @Service
@UtilityClass
public class MailServiceUtils {

	public static MailDTO createUserMail(String userName, String email, LocalDateTime joinDate) {
		MailAttribute mail = MailAttribute.USER_JOIN;

		// Mail Contents 작성
		List<MailDTO.Content> contents = List.of(MailDTO.Content.builder()
				.col1("사용자 이름 : ")
				.col2(userName)
				.build(),
			MailDTO.Content.builder().col1("이메일 주소 : ").col2(email).build(),
			MailDTO.Content.builder()
				.col1("가입 일시 : ")
				.col2(joinDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build());

		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), userName, email))
			.contents(contents)
			.subTitle(mail.getSubTitle())
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO createMIGErrorMail(String nodeName) {
		MailAttribute mail = MailAttribute.MIG_ERROR;

		// MIG 적용 실패 에러 관리자에게 전송
		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), nodeName))
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO approvalUserMail(String userName, String email) {
		MailAttribute mail = MailAttribute.USER_APPROVAL;

		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), userName, email))
			.receiverEmail(email)
			.footer(mail.getFooter())
			.build();
	}
	public static MailDTO refuseUserMail(String userName, String email) {
		MailAttribute mail = MailAttribute.USER_REFUSE;

		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), userName, email))
			.receiverEmail(email)
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO createWorkspaceMail(int reqGPU, int reqCPU, int reqMEM, String workspaceName, String userName,
		String email) {
		MailAttribute mail = MailAttribute.WORKSPACE_CREATE;

		List<MailDTO.Content> contents = List.of(
			MailDTO.Content.builder().col1("GPU :").col2(reqGPU + " 개").build(),
			MailDTO.Content.builder().col1("CPU :").col2(reqCPU + " Core").build(),
			MailDTO.Content.builder().col1("MEM :").col2(reqMEM + " GB").build()
		);

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workspaceName))
			.title(String.format(mail.getTitle(), userName, email,
				workspaceName))
			.subTitle(mail.getSubTitle())
			.contentTitle(mail.getContentTitle())
			.contents(contents)
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO deleteWorkspaceMail(String userName, String email, String workspaceName, String creatorMail) {

		MailAttribute mail = MailAttribute.WORKSPACE_DELETE;

		String mailTitle = userName + " (" + email + ")님이 워크스페이스(" + workspaceName + ")을(를) 삭제하였습니다.";

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workspaceName))
			.title(mailTitle)
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.receiverEmail(creatorMail)
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO resourceRequestMail(int reqGPU, int reqCPU, int reqMEM, String workspaceName, String userName,
		String email) {
		// 메일 메시지 조회
		MailAttribute mail = MailAttribute.WORKSPACE_RESOURCE_REQUEST;

		// 메일 컨텐츠
		List<MailDTO.Content> contents = List.of(
			MailDTO.Content.builder()
				.col1("GPU :")
				.col2(reqGPU + " 개")
				.build(),
			MailDTO.Content.builder()
				.col1("CPU :")
				.col2(reqCPU + " Core")
				.build(),
			MailDTO.Content.builder()
				.col1("MEM :")
				.col2(reqMEM + " GB")
				.build()
		);

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workspaceName))
			.title(String.format(mail.getTitle(), userName, email, workspaceName))
			.subTitle(mail.getSubTitle())
			.contentTitle(mail.getContentTitle())
			.contents(contents)
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO resourceApproveMail(int gpu, int cpu, int mem,String workspaceName, String userName, String userMail, String receiverEmail){
		MailAttribute mail = MailAttribute.WORKSPACE_RESOURCE_APPROVE;
		List<MailDTO.Content> contents = List.of(
			MailDTO.Content.builder().col1("GPU : ").col2(gpu + " 개").build(),
			MailDTO.Content.builder().col1("CPU : ").col2(cpu + " Core").build(),
			MailDTO.Content.builder().col1("MEM : ").col2(mem + " GB").build()
		);

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workspaceName))
			.title(String.format(mail.getTitle(), userName, userMail, workspaceName))
			.subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.contentTitle(mail.getContentTitle())
			.contents(contents)
			.receiverEmail(receiverEmail)
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO resourceRefuseMail(String workspaceName, String reason, String userName, String userMail, String receiverEmail) {
		MailAttribute mail = MailAttribute.WORKSPACE_RESOURCE_REFUSE;

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workspaceName))
			.title(String.format(mail.getTitle(), userName, userMail, workspaceName))
			.subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), reason))
			.receiverEmail(receiverEmail)
			.footer(mail.getFooter())
			.build();
	}


	public static MailDTO deleteWorkloadMail(String workloadName, String receiverMail) {
		MailAttribute mail = MailAttribute.WORKLOAD_DELETE;

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workloadName))
			.title(String.format(mail.getTitle(), workloadName))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}

	public static MailDTO startWorkloadMail(String workloadName, String receiverMail) {
		MailAttribute mail = MailAttribute.WORKLOAD_START;

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workloadName))
			.title(String.format(mail.getTitle(), workloadName))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}

	public static MailDTO errorWorkloadMail(String workloadName, String receiverMail) {
		MailAttribute mail = MailAttribute.WORKLOAD_ERROR;
		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workloadName))
			.title(String.format(mail.getTitle(), workloadName))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}

	public static MailDTO endWorkloadMail(String workloadName, String receiverMail){
		MailAttribute mail = MailAttribute.WORKLOAD_END;
		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), workloadName))
			.title(String.format(mail.getTitle(), workloadName))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}

	public static MailDTO licenseMail(LocalDate endDate) {
		MailAttribute mail = MailAttribute.LICENSE;
		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), endDate))
			.footer(mail.getFooter())
			.build();
	}

	public static MailDTO nodeErrorMail(String nodeName, String receiverMail) {
		MailAttribute mail = MailAttribute.NODE_ERROR;

		return MailDTO.builder()
			.subject(mail.getSubject())
			.title(String.format(mail.getTitle(), nodeName))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}

	public static MailDTO reportMail(String reportName, String period, String link, String receiverMail) {
		MailAttribute mail = MailAttribute.REPORT;

		return MailDTO.builder()
			.subject(String.format(mail.getSubject(), reportName))
			.title(mail.getTitle())
			.subTitle(String.format(mail.getSubTitle(), reportName, LocalDateTime.now(), period))
			.contentTitle(mail.getContentTitle())
			.contents(List.of(MailDTO.Content.builder().col1("링크 : ").col2(link).build()))
			.footer(mail.getFooter())
			.receiverEmail(receiverMail)
			.build();
	}
}
