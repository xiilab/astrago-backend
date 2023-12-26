package com.xiilab.modulek8s.workspace.vo;

import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceResVO;

import io.fabric8.kubernetes.api.model.Namespace;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WorkspaceResVO extends K8SResourceResVO {
	public WorkspaceResVO(Namespace namespace) {
		super(namespace);
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKSPACE;
	}
}
