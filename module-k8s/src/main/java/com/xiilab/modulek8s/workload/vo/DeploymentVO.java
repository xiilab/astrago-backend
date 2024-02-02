package com.xiilab.modulek8s.workload.vo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.enums.WorkloadType;

import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.PodSecurityContext;
import io.fabric8.kubernetes.api.model.PodSecurityContextBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodSpecBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.PodTemplateSpecBuilder;
import io.fabric8.kubernetes.api.model.SecurityContext;
import io.fabric8.kubernetes.api.model.SecurityContextBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecBuilder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class DeploymentVO extends WorkloadVO {
	private String volumeLabelSelectorName;
	private String pvName;
	private String pvcName;
	private String deploymentName;
	private String connectTestLabelName;
	private String namespace;
	private String hostPath;
	private String dockerImage;
	private String datasetName;

	public static DeploymentVO dtoToEntity(ConnectTestDTO connectTestDTO) {
		return DeploymentVO.builder()
			.deploymentName(connectTestDTO.getDeploymentName())
			.volumeLabelSelectorName(connectTestDTO.getVolumeLabelSelectorName())
			.pvName(connectTestDTO.getPvName())
			.pvcName(connectTestDTO.getPvcName())
			.connectTestLabelName(connectTestDTO.getConnectTestLabelName())
			.namespace(connectTestDTO.getNamespace())
			.hostPath(connectTestDTO.getHostPath())
			.dockerImage(connectTestDTO.getDockerImage())
			.datasetName("connection-test")
			.build();
	}
	public static DeploymentVO dtoToEntity(CreateDatasetDeployment createDatasetDTO) {
		return DeploymentVO.builder()
			.deploymentName(createDatasetDTO.getDeploymentName())
			.volumeLabelSelectorName(createDatasetDTO.getVolumeLabelSelectorName())
			.pvName(createDatasetDTO.getPvName())
			.pvcName(createDatasetDTO.getPvcName())
			.connectTestLabelName(createDatasetDTO.getConnectTestLabelName())
			.namespace(createDatasetDTO.getNamespace())
			.hostPath(createDatasetDTO.getHostPath())
			.dockerImage(createDatasetDTO.getDockerImage())
			.datasetName(createDatasetDTO.getDatasetName())
			.build();
	}

	@Override
	public HasMetadata createResource() {
		return new DeploymentBuilder()
			.withMetadata(createMeta())
			.withSpec(createSpec())
			.build();
	}

	@Override
	protected ObjectMeta createMeta() {
		return new ObjectMetaBuilder()
			.withName(deploymentName)
			.withNamespace(namespace)
			.withAnnotations(
				Map.of(
					AnnotationField.DATASET_NAME.getField(), this.datasetName
				))
			.build();
	}

	@Override
	protected ResourceType getType() {
		return ResourceType.WORKLOAD;
	}

	@Override
	public DeploymentSpec createSpec() {
		return new DeploymentSpecBuilder()
			.withReplicas(1)
			.withNewSelector()
			.withMatchLabels(Map.of("app", connectTestLabelName))
			.endSelector()
			.withTemplate(new PodTemplateSpecBuilder()
				.withNewMetadata()
				.withLabels(Collections.singletonMap("app", connectTestLabelName))
				.endMetadata()
				.withSpec(createPodSpec())
				.build()
			)
			.build();
	}

	@Override
	public PodSpec createPodSpec() {
		PodSpecBuilder podSpecBuilder = new PodSpecBuilder();
		// PodSecurityContext securityContext = podSpecBuilder.buildSecurityContext().toBuilder().withFsGroup(101L).build();
		PodSecurityContext securityContext = new PodSecurityContext();
		securityContext.setFsGroup(101L);
		podSpecBuilder.withSecurityContext(securityContext);

		addVolumes(podSpecBuilder, List.of(new JobVolumeVO(volumeLabelSelectorName, pvcName)));

		PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer = podSpecBuilder
			.addNewContainer()
			.withName("nginx")
			.withImage(this.dockerImage);

		addVolumeMount(podSpecContainer);

		return podSpecContainer.endContainer().build();
	}

	private void addVolumeMount(PodSpecFluent<PodSpecBuilder>.ContainersNested<PodSpecBuilder> podSpecContainer) {
		podSpecContainer
			.addNewVolumeMount()
			.withName(volumeLabelSelectorName)
			.withMountPath(hostPath)
			.endVolumeMount();
	}

	@Override
	public List<ContainerPort> convertContainerPort() {
		return null;
	}

	@Override
	public List<EnvVar> convertEnv() {
		return null;
	}

	@Override
	public List<String> convertCmd() {
		return null;
	}

	@Override
	public WorkloadType getWorkloadType() {
		return null;
	}
}
