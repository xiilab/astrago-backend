package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;
import com.xiilab.modulek8sdb.repository.WorkloadHistoryRepo;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InteractiveJobInformer {
	private final K8sAdapter k8sAdapter;
	private final WorkloadHistoryRepo workloadHistoryRepo;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	private void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Deployment> jobSharedIndexInformer = informers.sharedIndexInformerFor(
			Deployment.class, 30 * 60 * 1000L);
		jobSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {
			@Override
			public void onAdd(Deployment deployment) {
				log.info("{} interactive job이 생성되었습니다.", deployment.getMetadata().getName());
			}

			@Override
			public void onUpdate(Deployment deployment1, Deployment deployment2) {
			}

			@Override
			public void onDelete(Deployment deployment, boolean b) {
				log.info("interactive job {}가 삭제되었습니다.", deployment.getMetadata().getName());
				String namespace = deployment.getMetadata().getNamespace();
				Namespace namespaceObject = kubernetesClient.namespaces().withName(namespace).get();
				Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
				K8SResourceMetadataDTO metadataFromResource = getInteractiveWorkloadInfoFromResource(deployment);
				if (metadataFromResource != null) {
					workloadHistoryRepo.save(JobEntity.jobBuilder()
						.name(metadataFromResource.getName())
						.description(metadataFromResource.getDescription())
						.resourceName(metadataFromResource.getResourceName())
						.workspaceName(namespaceObject.getMetadata().getLabels().get(""))
						.workspaceResourceName(namespace)
						.envs(getEnvFromContainer(container))
						.cpuReq(metadataFromResource.getCpuReq())
						.memReq(metadataFromResource.getMemReq())
						.gpuReq(metadataFromResource.getGpuReq())
						.cmd(String.join(" ", container.getCommand()))
						.createdAt(metadataFromResource.getCreatedAt())
						.deletedAt(metadataFromResource.getDeletedAt())
						.creatorName(metadataFromResource.getCreatorName())
						.creatorId(metadataFromResource.getCreatorId())
						.workloadType(WorkloadType.INTERACTIVE)
						.build());
				}
			}
		});

		log.info("Starting all registered interative job informers");
		informers.startAllRegisteredInformers();
	}
}
