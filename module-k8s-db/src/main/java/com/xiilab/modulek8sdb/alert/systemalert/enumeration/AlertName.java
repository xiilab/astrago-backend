package com.xiilab.modulek8sdb.alert.systemalert.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlertName {
	RESOURCE_APPROVAL("리소스 승인 알림"),
	WORKSPACE_DELETE("워크스페이스 삭제 알림"),
	WORKSPACE_MEMBER("워크스페이스 멤버 알림"),
	WORKLOAD_START("워크로드 시작 알림"),
	WORKLOAD_END("워크로드 종료 알림"),
	WORKLOAD_ERROR("워크로드 에러 알림"),
	WORKLOAD_DELETE("워크로드 삭제 알림"),
	;
	private String name;
}
