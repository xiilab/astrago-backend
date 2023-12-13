package com.xiilab.modulek8s.common.vo;

import java.time.LocalDateTime;
import java.util.UUID;

import com.xiilab.modulek8s.common.enumeration.ResourceType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/**
 * K8SResourceReq 클래스는 Kubernetes 리소스 요청 생성을 위한 기반 역할을 하는 추상 클래스입니다.
 * 다양한 Kubernetes 리소스 신청시 일반적으로 사용되는 속성과 메서드가 포함되어 있습니다.
 */
@Getter
public abstract class K8SResourceReqVO {
	//metadata.name
	String resourceName;
	//annotation
	//사용자가 실제 입력한 name
	@Pattern(regexp = "^[^-_]*$")
	String name;
	//resource에 대한 설명
	String description;
	//생성 요청 시간
	LocalDateTime createdAt;
	//사용자의 실명
	String creatorName;
	//label
	//사용자의 id
	String creator;
	ResourceType type;

	/**
	* 임의로 생성된 UUID와 리소스 유형을 연결하여 리소스 이름을 반환합니다.
	*
	* @return 리소스 이름
	*/
	public String getResourceName()	{
		return getType().getName() + "-" + UUID.randomUUID();
	}
	//k8s resource 객체를 생성하는 메소드
	public abstract HasMetadata createResource();

	//k8s Resource의 ObjectMeta를 정의하기 위한 메소드
	protected abstract ObjectMeta createMeta();

	//자식 클래스의 ResourceType을 조회하기 위한 메소드
	protected abstract ResourceType getType();
}
