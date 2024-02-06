package com.xiilab.modulek8s.workload.svc.vo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
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
public class NodeSvcVO extends K8SResourceReqVO {
	private String workspace;        //워크스페이스
	private SvcType svcType;        // 서비스 타입
	private String jobName;	// 잡 메타데이터 이름 (selector로 검색하기 위해 추가)
	private List<SvcPortVO> ports; // 연결할 포트 목록

	@Override
	public Service createResource() {
		return new ServiceBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	protected ResourceType getType() {
		return ResourceType.SERVICE;
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(getUniqueResourceName())
			.withNamespace(workspace)
			.withAnnotations(Map.of(
				AnnotationField.NAME.getField(), getName(),
				AnnotationField.DESCRIPTION.getField(), getDescription(),
				AnnotationField.CREATED_AT.getField(), LocalDateTime.now().toString(),
				AnnotationField.CREATOR_FULL_NAME.getField(), getCreatorName(),
				AnnotationField.TYPE.getField(), getSvcType().getType()
			))
			.withLabels(Map.of(
				LabelField.CONTROL_BY.getField(), "astra",
				LabelField.JOB_NAME.getField(), jobName,
				LabelField.APP.getField(), jobName
			))
			.build();
	}

	public ServiceSpec createSpec() {
		ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder()
			.withType(SvcType.NODE_PORT.getType())
			.withSelector(Map.of(
				LabelField.APP.getField(), jobName
			));

		if (!CollectionUtils.isEmpty(ports)) {
			serviceSpecBuilder.addAllToPorts(convertNodePort());
		}

		return serviceSpecBuilder.build();
	}

	public List<ServicePort> convertNodePort() {
		return ports.stream()
			.map(port -> new ServicePortBuilder()
				.withName(port.name())
				.withPort(port.port())
				.withTargetPort(new IntOrString(port.port()))
				.build()
			).toList();
	}

	public static NodeSvcVO createServiceDtoToServiceVO(CreateSvcReqDTO createSvcReqDTO) {
		return NodeSvcVO.builder()
			.name(createSvcReqDTO.getName())
			.description(createSvcReqDTO.getDescription())
			.creatorName(createSvcReqDTO.getCreatorName())
			.creator(createSvcReqDTO.getCreator())
			.workspace(createSvcReqDTO.getWorkspace())
			.svcType(createSvcReqDTO.getSvcType())
			.jobName(createSvcReqDTO.getJobName())
			.ports(createSvcReqDTO.getPorts().stream().map(port -> new SvcPortVO(port.name(), port.port())).toList())
			.build();
	}
}
