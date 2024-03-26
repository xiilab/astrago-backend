package com.xiilab.modulecommon.alert.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SystemAlertMessage {
	// 사용자
	USER_CREATE("[Astrago] 회원가입 알림", "회원가입 알림","%s(%s)님이 회원가입을 요청하였습니다."),
	USER_UPDATE("[Astrago] 회원정보 변경 알림", "회원정보 변경", "%s님의 개인 정보가 변경 되었습니다."),
	USER_ENABLED("[Astrago] 계정 활성화 알림", "계정 활성화 알림", "%s님의 계정이 활성화 되었습니다."),
	USER_DISABLED("[Astrago] 계정 비활성화 알림", "계정 비활성화 알림", "%s님의 계정이 비활성화 되었습니다."),
	// 워크스페이스
	WORKSPACE_CREATE_ADMIN("[Astrago] 워크스페이스(%s) 생성 알림", "워크스페이스 생성", "%s님이 워크스페이스(%s)를 생성하였습니다."),
	WORKSPACE_CREATE_OWNER("[Astrago] 워크스페이스(%s) 생성 알림", "워크스페이스 생성", "워크스페이스(%s)를 생성하였습니다."),
	WORKSPACE_RESOURCE_OVER_ADMIN("[Astrago] 워크스페이스(%s) 리소스 초과 알림", "워크스페이스 리소스 초과", "%s님의 워크스페이스(%s)이(가) 리소스 초과되었습니다."),
	WORKSPACE_RESOURCE_REQUEST_ADMIN("[Astrago] 워크스페이스(%s) 리소스 요청 알림", "워크스페이스 리소스 요청", "%s님이 워크스페이스(%s)의 리소스 추가 요청을 하였습니다."),
	WORKSPACE_RESOURCE_REQUEST_OWNER("[Astrago] 워크스페이스(%s) 리소스 요청 알림", "워크스페이스 리소스 요청", "워크스페이스(%s)의 리소스 추가 요청을 하였습니다."),
	WORKSPACE_RESOURCE_REQUEST_RESULT_OWNER("[Astrago] 워크스페이스(%s) 리소스 결과 알림 ", "워크스페이스 리소스 요청 결과", "관리자(%s)님이 워크스페이스(%s)의 리소스 요청을 %s 하였습니다."),
	WORKSPACE_DELETE_ADMIN("[Astrago] 워크스페이스(%s) 삭제 알림", "워크스페이스 삭제", "관리자(%s)님이 워크스페이스(%s)를 삭제하였습니다."),
	WORKSPACE_DELETE_OWNER("[Astrago] 워크스페이스(%s) 삭제 알림", "워크스페이스 삭제", "워크스페이스(%s)가 삭제되었습니다."),
	WORKSPACE_MEMBER_UPDATE("[Astrago] 워크스페이스(%s) 멤버 변경 알림", "워크스페이스 멤버", "워크스페이스(%s) 멤버를 변경하였습니다."),
	// 워크로드
	WORKLOAD_START_CREATOR("[Astrago] 워크로드(%s) 실행 알림", "워크로드 실행", "워크로드(%s)가 실행되었습니다."),
	WORKLOAD_END_CREATOR("[Astrago] 워크로드(%s) 종료 알림", "워크로드 종료", "워크로드(%s)가 종료되었습니다."),
	WORKLOAD_ERROR_CREATOR("[Astrago] 워크로드(%s) 에러 알림", "워크로드 에러", "워크로드(%s)에 에러가 발생하였습니다."),
	WORKLOAD_DELETE_ADMIN("[Astrago] 워크로드(%s) 삭제 알림", "워크로드 삭제", "%s(%s)가 워크로드(%s)을(를) 삭제하였습니다."),
	WORKLOAD_DELETE_CREATOR("[Astrago] 워크로드(%s) 삭제 알림", "워크로드 삭제", "워크로드(%s)를 삭제했습니다."),
	// 라이센스
	LICENSE_EXPIRATION("[Astrago] 라이센스 만료 경고 알림", "라이센스 만료", "라이센스 만료 기한이 %s 까지입니다."),
	// 노드
	NODE_ERROR("[Astrago] 노드 장애 알림", "노드 장애 알림", "%s에 에러가 발생하였습니다."),
	NODE_MIG_APPLY("[Astrago] 노드 적용 알림", "노드 적용 알림", "%s에 MIG 적용이 완료되었습니다."),
	NODE_MIG_ERROR("[Astrago] 노드 장애 알림", "노드 장애 알림", "%s에 MIG 적용이 실패하였습니다.")
	;

	private final String mailTitle;
	private final String title;
	private final String message;
}
