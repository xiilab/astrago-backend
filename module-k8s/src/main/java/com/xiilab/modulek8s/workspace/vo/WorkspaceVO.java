package com.xiilab.modulek8s.workspace.vo;

import java.time.LocalDateTime;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WorkspaceVO {
	@Getter
	@SuperBuilder
	public static class RequestVO extends K8SResourceReqVO {
		@Override
		public HasMetadata createResource() {
			return new NamespaceBuilder()
				.withMetadata(createMeta())
				.build();
		}

		@Override
		protected ObjectMeta createMeta() {
			return new ObjectMetaBuilder()
				.withName(getUniqueResourceName())
				.withAnnotations(
					Map.of(
						AnnotationField.NAME.getField(), getName(),
						AnnotationField.DESCRIPTION.getField(), getDescription(),
						AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString(),
						AnnotationField.CREATOR_USER_NAME.getField(), getCreatorUserName(),
						AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorFullName()
					))
				.withLabels(
					Map.of(
						LabelField.CREATOR_ID.getField(), getCreatorId(),
						LabelField.CONTROL_BY.getField(), "astra"
					))
				.build();
		}

		@Override
		protected ResourceType getType() {
			return ResourceType.WORKSPACE;
		}

		@Override
		public HasMetadata createResource(String userUUID) {
			throw new UnsupportedOperationException("Unimplemented method 'createResource'");
		}
	}

	@Getter
	@SuperBuilder
	public static class ResponseVO extends K8SResourceResVO {

		public ResponseVO(Namespace namespace) {
			super(namespace);
		}
		@Override
		protected ResourceType getType() {
			return ResourceType.WORKSPACE;
		}
	}
}
