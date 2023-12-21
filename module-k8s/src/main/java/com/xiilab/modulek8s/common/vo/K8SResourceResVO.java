package com.xiilab.modulek8s.common.vo;

import java.time.LocalDateTime;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;

import io.fabric8.kubernetes.api.model.HasMetadata;
import lombok.Getter;

/**
 * K8SResourceReq 클래스는 Kubernetes 리소스 요청 생성을 위한 기반 역할을 하는 추상 클래스입니다.
 * 다양한 Kubernetes 리소스 신청시 일반적으로 사용되는 속성과 메서드가 포함되어 있습니다.
 */
@Getter
public abstract class K8SResourceResVO {
	String uid;
	String resourceName;
	//annotation
	String name;
	String description;
	LocalDateTime createdAt;
	String creatorName;
	//label
	String creator;
	ResourceType type;

	protected K8SResourceResVO(HasMetadata hasMetadata) {
		//astra로 생성한 리소스 정보 매핑
		if (isControlledByAstra(hasMetadata.getMetadata().getLabels())) {
			this.uid = hasMetadata.getMetadata().getUid();
			this.resourceName = hasMetadata.getMetadata().getName();
			this.name = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.NAME.getField());
			this.description = hasMetadata.getMetadata().getAnnotations().get(AnnotationField.DESCRIPTION.getField());
			this.createdAt = LocalDateTime.parse(
				hasMetadata.getMetadata().getAnnotations().get(AnnotationField.CREATED_AT.getField()));
			this.creatorName = hasMetadata.getMetadata()
				.getAnnotations()
				.get(AnnotationField.CREATOR_FULL_NAME.getField());
			this.creator = hasMetadata.getMetadata().getLabels().get(LabelField.CREATOR.getField());
			this.type = getType();
		} else {
			this.uid = hasMetadata.getMetadata().getUid();
			this.resourceName = hasMetadata.getMetadata().getName();
			this.name = hasMetadata.getMetadata().getName();
			this.description = null;
		}

	}

	//자식 클래스의 ResourceType을 조회하기 위한 메소드
	protected abstract ResourceType getType();

	private boolean isControlledByAstra(Map<String, String> map) {
		return map != null && "astra".equals(map.get("control-by"));
	}
}
