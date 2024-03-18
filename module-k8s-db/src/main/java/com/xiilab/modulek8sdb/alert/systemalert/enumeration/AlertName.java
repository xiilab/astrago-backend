package com.xiilab.modulek8sdb.alert.systemalert.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertName {
	RESOURCE_APPROVAL("리소스 승인 알림"),
	WORKSPACE_DELETE("워크스페이스 삭제 알림"),
	WORKSPACE_MEMBER_UPDATE("워크스페이스 회원 추가 삭제 알림"),
	WORKLOAD_START("워크로드 시작 알림"),
	WORKLOAD_END("워크로드 종료 알림"),
	WORKLOAD_ERROR("워크로드 에러 알림"),
	WORKLOAD_DELETE("워크로드 삭제 알림"),
	ADMIN_LICENSE_EXPIRATION("라이센스 만료 경고 알림"),
	ADMIN_USER_JOIN("유저 회원가입 알림"),
	ADMIN_NODE_ERROR("노드 장애 알림"),
	ADMIN_WORKSPACE_CREATE("워크스페이스 생성 알림"),
	ADMIN_USER_RESOURCE_REQUEST("유저 리소스 요청 알림"),
	ADMIN_WORKSPACE_RESOURCE_OVER("워크스페이스 리소스 초과 알림")
	;
	private String name;
}
