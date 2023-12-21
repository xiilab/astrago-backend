package com.xiilab.modulek8s.service.vo;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.common.vo.K8SResourceReqVO;
import com.xiilab.modulek8s.service.dto.request.CreateServiceDTO;
import com.xiilab.modulek8s.service.enums.ServiceType;
import io.fabric8.kubernetes.api.model.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@SuperBuilder
public class ServiceVO extends K8SResourceReqVO {
	private String workspace;        //워크스페이스
	private ServiceType serviceType;        // 서비스 타입
	private String jobName;
	private List<Integer> ports;

	@Override
	public Service createResource() {
		return new ServiceBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
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
				AnnotationField.TYPE.getField(), getServiceType().getType()
			))
			.build();
	}

	protected ResourceType getType() {
		return ResourceType.SERVICE;
	}

	public ServiceSpec createSpec() {
		ServiceSpecBuilder serviceSpecBuilder = new ServiceSpecBuilder()
			.withType(ServiceType.NODE_PORT.getType())
			.withSelector(Map.of(
				"job-name", jobName
			));

		if (!CollectionUtils.isEmpty(ports)) {
			serviceSpecBuilder.addAllToPorts(convertNodePort());
		}

		return serviceSpecBuilder.build();
	}

	public List<ServicePort> convertNodePort() {
		return ports.stream()
			.map(port -> new ServicePortBuilder()
				.withPort(port)
				.withTargetPort(new IntOrString(port))
				.build()
			).collect(Collectors.toList());
	}

	public static ServiceVO CreateServiceDtoTOServiceVO(CreateServiceDTO createServiceDTO) {
		return ServiceVO.builder()
			.name(createServiceDTO.getName())
			.description(createServiceDTO.getDescription())
			.creatorName(createServiceDTO.getCreatorName())
			.creator(createServiceDTO.getCreator())
			.workspace(createServiceDTO.getWorkspace())
			.serviceType(createServiceDTO.getServiceType())
			.jobName(createServiceDTO.getJobName())
			.ports(createServiceDTO.getPorts())
			.build();
	}
}
