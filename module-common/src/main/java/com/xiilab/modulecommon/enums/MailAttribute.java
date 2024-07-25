package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MailAttribute{
	// 사용자
	USER_JOIN("[Astrago] 회원가입 알림", "%s(%s)님이 회원가입을 요청하였습니다.", "회원가입 요청내역은 아래와 같습니다.", "", "관리자 로그인 > 설정 > 계정관리 > 가입요청관리에서 <br>승인/반려 하실 수 있습니다. <br>감사합니다."),
	// USER_UPDATE("[Astrago] 회원정보 변경 알림","%s(%s)님의 개인 정보가 변경 되었습니다.","개인정보 변경 내역은 아래와 같습니다","","감사합니다."),
	// USER_ENABLE("[Astrago] 계정 활성화 알림","%s(%s)님의 계정이 정상적으로 활성화 되었습니다. <br>이제 astrago 서비스를 이용하실수 있습니다.","","","감사합니다."),
	// USER_UNABLE("[Astrago] 계정 비활성화 알림","%s(%s)님의 계정이  비활성화 되었습니다. <br>astrago 서비스를 이용하시려면 관리자에게 문의해 주십시오.","","관리자 이메일 : %s",""),
	USER_APPROVAL("[Astrago] 회원가입 승인 알림", "%s(%s)님 회원가입이 승인 되었습니다.", "", "", "감사합니다."),
	USER_REFUSE("[Astrago] 회원가입 거절 알림", "%s(%s)님 회원가입이 거절 되었습니다.", "", "", ""),
	// 워크스페이스
	WORKSPACE_CREATE("[Astrago] 워크스페이스(%s) 생성 알림","%s(%s)님이 <br>워크스페이스(%s)을(를) 생성하였습니다.","워크스페이스 생성내역은 아래와 같습니다.","<리소스 신청량>","감사합니다."),
	// WORKSPACE_RESOURCE_OVER("[Astrago] 워크스페이스(%s) 리소스 초과 알림","%s(%s)님의 <br>워크스페이스(%s)이(가) <br>리소스 초과되었습니다.","","<현재 사용 리소스>","감사합니다."),
	WORKSPACE_RESOURCE_REQUEST("[Astrago] 워크스페이스(%s) 리소스 요청 알림","%s(%s)님의 <br>워크스페이스(%s) <br>리소스 추가 요청을 하였습니다.","","<리소스 신청량>","감사합니다."),
	WORKSPACE_RESOURCE_APPROVE("[Astrago] 워크스페이스(%s) 리소스 결과 알림","%s(%s)님이 <br>워크스페이스(%s) 리소스 요청을 승인 하였습니다","승인 일시 : %s","<리소스 신청량>","감사합니다."),
	WORKSPACE_RESOURCE_REFUSE("[Astrago] 워크스페이스(%s) 리소스 결과 알림","%s(%s)님이 <br>워크스페이스(%s) 리소스 요청을 반려 하였습니다","반려 일시 : %s  <br> 반려 사유 : %s","",""),
	WORKSPACE_DELETE("[Astrago] 워크스페이스(%s) 삭제 알림","%s워크스페이스(%s)을(를) 삭제하였습니다.","삭제 일시 : %s ","",""),
	// WORKSPACE_MEMBER_UPDATE("[Astrago] 워크스페이스(%s) 멤버 변경 알림","워크스페이스(%s) <br>멤버를 변경하였습니다.","변경 일시 : %s","","감사합니다."),
	// 워크로드
	WORKLOAD_START("[Astrago] 워크로드(%s) 실행 알림","워크로드(%s)이(가) 실행되었습니다.","시작 일시 : %s","", "감사합니다."),
	WORKLOAD_END("[Astrago] 워크로드(%s) 종료 알림","워크로드(%s)이(가) 종료되었습니다.","종료 일시 : %s","", "감사합니다."),
	WORKLOAD_ERROR("[Astrago] 워크로드(%s) 에러 알림","워크로드(%s)에 에러가 발생하였습니다.","에러 일시 : %s","", ""),
	WORKLOAD_DELETE("[Astrago] 워크로드(%s) 삭제 알림","워크로드(%s)를 삭제하였습니다.","삭제일시 : %s","", ""),
	WORKLOAD_DELETE_SCHEDULED("[Astrago] 워크로드 삭제 예정 알림", "워크로드 삭제 예정 안내", "<b>자원 사용률이 일정 시간 동안 <br/>기준값을 초과하지 않아 <span style=\"color:#5b29c7;\">1시간 후</span> 삭제될 예정입니다.</b><br/><br/><div>현재 사용자님의 워크로드가 관리자가 설정한 운영시간인 24시간 동안<br/>사용되지 않아 자원 최적화 정책에 따라 1시간 후 삭제될 예정입니다.<br/>삭제된 워크로드에 대한 정보는 복구되지 않습니다.</div>", "워크로드 안내 <br>", ""),
	// 라이센스
	LICENSE("[Astrago] 라이센스 만료 경고 알림","라이센스 만료 기한이 <br> %s까지 입니다. <br>관리팀에 문의 바랍니다.","","", ""),
	// 노드
	NODE_ERROR("[Astrago] 노드 장애 알림","서버 (%s) 에 장애가 발생했습니다. <br>관리팀에 문의바랍니다.","","내용", ""),
	// MIG_ON("[Astrago] 노드 적용 알림","%s에 MIG 적용이 완료되었습니다.","","", "감사합니다."),
	MIG_ERROR("[Astrago] 노드 장애 알림","node1에 MIG 적용이 실패했습니다.","","", ""),
	// 리포트
	REPORT("[Astrago] %s", "예약 발송 내역 입니다. <br>리포팅 내용은 다음과 같습니다:", "• 리포팅 종류 : %s <br>• 발송일시 : %s <br>• 수집기간 : %s", "하위 링크를 클릭하시면 내용을 확인하실 수 있습니다.", "감사합니다."),
	//SMTP
	SMTP_CHECK("[Astrago] SMTP 확인 알림", "SMTP 등록 성공 하였습니다.", "", "", "감사합니다."),
	;

	private String subject;
	private String title;
	private String subTitle;
	private String contentTitle;
	private String footer;

}
