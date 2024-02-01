package com.xiilab.modulek8s.workload.svc.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateClusterIPSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.dto.request.CreateSvcReqDTO;
import com.xiilab.modulek8s.workload.svc.enums.SvcType;

import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.ServicePortBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.ServiceSpecBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ClusterIPSvcVO extends K8SResourceReqVO {
	private String namespace;        //워크스페이스
	private SvcType svcType;        // 서비스 타입
	private String deploymentName;	// 잡 메타데이터 이름 (selector로 검색하기 위해 추가)

	@Override
	public Service createResource() {
		return new ServiceBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	public static ClusterIPSvcVO createServiceDtoToServiceVO(CreateClusterIPSvcReqDTO createSvcReqDTO) {
		return ClusterIPSvcVO.builder()
			.resourceName(createSvcReqDTO.getSvcName())
			.namespace(createSvcReqDTO.getNamespace())
			.svcType(createSvcReqDTO.getSvcType())
			.deploymentName(createSvcReqDTO.getDeploymentName())
			.build();
	}

	protected ResourceType getType() {
		return ResourceType.SERVICE;
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getResourceName())
			.withNamespace(namespace)
			.withLabels(Map.of(
				LabelField.CONTROL_BY.getField(), "astra",
				LabelField.APP.getField(), deploymentName
			))
			.build();
	}

	public ServiceSpec createSpec() {
		ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder()
			.withType(this.svcType.getType())
			.withSelector(Map.of(
				LabelField.APP.getField(), deploymentName
			));
		serviceSpecBuilder.addToPorts(convertNodePort());
		return serviceSpecBuilder.build();
	}

	public ServicePort convertNodePort() {
		return new ServicePortBuilder()
				.withName("http")
				.withPort(80)
				.withTargetPort(new IntOrString(80))
				.build();
	}
}
