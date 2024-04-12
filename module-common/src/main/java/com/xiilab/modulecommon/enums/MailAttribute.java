package com.xiilab.modulecommon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MailAttribute{
	USER_JOIN("[Astrago] 회원가입 알림", "%s(%s)님이 회원가입을 요청하였습니다.", "회원가입 요청내역은 아래와 같습니다.", "", "관리자 로그인 > 설정 > 계정관리 > 가입요청관리에서 <br>승인/반려 하실 수 있습니다. <br>감사합니다."),
	USER_UPDATE("[Astrago] 회원정보 변경 알림","%s(%s)님의 개인 정보가 변경 되었습니다.","개인정보 변경 내역은 아래와 같습니다","","감사합니다."),
	USER_ENABLE("[Astrago] 계정 활성화 알림","%s(%s)님의 계정이 정상적으로 활성화 되었습니다. <br>이제 astrago 서비스를 이용하실수 있습니다.","","","감사합니다."),
	USER_UNABLE("[Astrago] 계정 비활성화 알림","%s(%s)님의 계정이  비활성화 되었습니다. <br>astrago 서비스를 이용하시려면 관리자에게 문의해 주십시오.","","관리자 이메일 : %s","감사합니다."),
	WORKSPACE_CREATE("[Astrago] 워크스페이스(%s) 생성 알림","%s(%s)님이 <br>워크스페이스(%s)을(를) 생성하였습니다.","워크스페이스 생성내역은 아래와 같습니다.","<리소스 신청량>","감사합니다."),
	WORKSPACE_RESOURCE_OVER("[Astrago] 워크스페이스(%s) 리소스 초과 알림","%s(%s)님의 <br>워크스페이스(%s)이(가) <br>리소스 초과되었습니다.","","<현재 사용 리소스>","감사합니다."),
	WORKSPACE_RESOURCE_REQUEST("[Astrago] 워크스페이스(%s) 리소스 요청 알림","%s(%s)님의 <br>워크스페이스(%s) <br>리소스 추가 요청을 하였습니다.","","<리소스 신청량>","감사합니다."),
	WORKSPACE_RESOURCE_RESULT("[Astrago] 워크스페이스(%s) 리소스 결과 알림","%s(%s)님이 <br>워크스페이스(%s) 리소스 요청을 %s 하였습니다","%s","<리소스 신청량>","감사합니다."),
	WORKSPACE_DELETE("[Astrago] 워크스페이스(%s) 삭제 알림","%s워크스페이스(%s)을(를) 삭제하였습니다.","삭제 일시 : %s ","","감사합니다."),
	WORKSPACE_MEMBER_UPDATE("[Astrago] 워크스페이스(%s) 멤버 변경 알림","워크스페이스(%s) 멤버를 변경하였습니다.","변경 일시 : %s","","감사합니다."),
	WORKLOAD_START("[Astrago] 워크로드(%s) 실행 알림","워크로드(%s)이(가) 실행되었습니다.","","", "감사합니다."),
	WORKLOAD_END("[Astrago] 워크로드(%s) 종료 알림","워크로드(%s)이(가) 종료되었습니다.","","", "감사합니다."),
	WORKLOAD_ERROR("[Astrago] 워크로드(%s) 에러 알림","%s(%s)에 에러가 발생하였습니다.","","", "감사합니다."),
	WORKLOAD_DELETE("[Astrago] 워크로드(%s) 삭제 알림","%s워크로드(길동의 워크로드)를 삭제하였습니다.","삭제일시 : %s","", "감사합니다."),
	LICENSE("[Astrago] 라이센스 만료 경고 알림","라이센스 만료 기한이 <br> %s까지 입니다. <br>관리팀에 문의 바랍니다.","","", "감사합니다."),
	NODE_ERROR("[Astrago] 노드 장애 알림","서버 (%s) 에 장애가 발생했습니다. <br>관리팀에 문의바랍니다.","","내용", "감사합니다."),
	MIG_ON("[Astrago] 노드 적용 알림","%s에 MIG 적용이 완료되었습니다.","","", "감사합니다."),
	MIG_ERROR("[Astrago] 노드 장애 알림","node1에 MIG 적용이 실패했습니다.","","사유", "감사합니다."),

	;

	private String subject;
	private String title;
	private String subTitle;
	private String contentTitle;
	private String footer;

}
