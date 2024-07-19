package com.xiilab.modulecommon.exception.errorcode;

import org.springframework.http.HttpStatus;

import com.xiilab.modulecommon.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NodeErrorCode implements ErrorCode {

	NOT_SUPPORTED_GPU(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 gpu는 MIG를 지원하지 않습니다."),
	NOT_SUPPORTED_MPS_GPU(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 gpu는 MPS를 지원하지 않습니다."),
	NOT_SUPPORTED_MPS_WITH_MIG(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 노드의 GPU를 사용하는 워크로드가 있습니다. 사용중인 GPU는 MPS 설정이 불가합니다."),
	NODE_IN_USE_NOT_MPS(HttpStatus.INTERNAL_SERVER_ERROR.value(), "MPS는 MIG가 적용된 노드에는 설정할 수 없습니다."),
	NODE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "해당 node는 존재하지 않습니다."),
	NODE_IN_USE_NOT_MIG(HttpStatus.CONFLICT.value(), "해당 노드를 user가 사용중입니다. 사용중인 node는 mig 설정이 불가합니다."),
	GPU_PRODUCT_MEMORY_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR.value(), "gpu product에 메모리가 존재하지 않습니다. ex)A100-PCIE-80GB"),
	MIG_PROFILE_NOT_EXIST(HttpStatus.INTERNAL_SERVER_ERROR.value(), "MIG Profile 파일이 존재하지 않습니다. 관리자에게 문의바랍니다."),
	NOT_FOUND_WORKER_NODE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "WORKER 노드를 찾을 수 없습니다. 관리자에게 문의바랍니다."),

	;

	private final int code;
	private final String message;
}
