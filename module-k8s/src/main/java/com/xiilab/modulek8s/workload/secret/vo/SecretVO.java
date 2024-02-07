package com.xiilab.modulek8s.workload.secret.vo;

import java.util.HashMap;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.workload.enums.SecretType;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SecretVO extends K8SResourceReqVO {
	private String workspaceName;
	private Map<String, String> dataMap;
	private SecretType secretType;

	@Override
	public Secret createResource() {
		return new SecretBuilder()
			.withMetadata(createMeta())
			.withType(secretType.getType())
			.withStringData(dataMap)
			.build();
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getUniqueResourceName())
			.withNamespace(workspaceName)
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.SECRET;
	}
}
