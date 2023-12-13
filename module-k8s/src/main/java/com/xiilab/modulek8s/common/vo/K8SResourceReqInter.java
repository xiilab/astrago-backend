package com.xiilab.modulek8s.common.vo;

import com.xiilab.modulek8s.common.enumeration.ResourceType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;

/**
 * K8SResourceInterface는 Kubernetes 리소스를 생성하고 해당 메타데이터를 정의하는 방법을 정의하는 인터페이스입니다.
 */
public interface K8SResourceReqInter {
	//k8s resource 객체를 생성하는 메소드
	HasMetadata createResource();

	//k8s Resource의 ObjectMeta를 정의하기 위한 메소드
	ObjectMeta createMeta();

	//자식 클래스의 ResourceType을 조회하기 위한 메소드
	ResourceType getType();
}
