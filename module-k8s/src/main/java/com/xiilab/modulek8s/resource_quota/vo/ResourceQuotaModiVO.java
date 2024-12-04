package com.xiilab.modulek8s.resource_quota.vo;

import static com.xiilab.modulek8s.resource_quota.enumeration.ResourceQuotaKey.*;

import java.time.LocalDateTime;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceQuotaBuilder;
import io.fabric8.kubernetes.api.model.ResourceQuotaSpec;
import io.fabric8.kubernetes.api.model.ResourceQuotaSpecBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResourceQuotaModiVO extends K8SResourceReqVO {
	private int reqCpu;
	private int reqMem;
	private int reqGpu;
	private int limitCpu;
	private int limitMem;
	private int limitGpu;

	@Override
	public HasMetadata createResource(String userUUID) {
		return new ResourceQuotaBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getUniqueResourceName())
			.withLabels(
				Map.of(
					AnnotationField.NAME.getField(), getName(),
					AnnotationField.DESCRIPTION.getField(), getDescription(),
					AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString(),
					AnnotationField.CREATOR_USER_NAME.getField(), getCreatorUserName(),
					AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorFullName()
				))
			.withAnnotations(
				Map.of(
					LabelField.CREATOR_ID.getField(), getCreatorId()
				))
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.RESOURCE_QUOTA;
	}

	private ResourceQuotaSpec createSpec() {
		return new ResourceQuotaSpecBuilder()
			.addToHard(Map.of(
				REQUEST_CPU_KEY.getKey(), new Quantity(String.valueOf(this.reqCpu)),
				REQUEST_MEMORY_KEY.getKey(), new Quantity(String.valueOf(this.reqMem), "Gi"),
				REQUEST_GPU_KEY.getKey(), new Quantity(String.valueOf(this.reqGpu)),
				LIMITS_CPU_KEY.getKey(), new Quantity(String.valueOf(this.limitCpu)),
				LIMITS_MEMORY_KEY.getKey(), new Quantity(String.valueOf(this.limitMem), "Gi"),
				LIMITS_GPU_KEY.getKey(), new Quantity(String.valueOf(this.limitGpu))
			))
			.build();
	}

	@Override
	public HasMetadata createResource() {
		throw new UnsupportedOperationException("Unimplemented method 'createResource'");
	}
}
