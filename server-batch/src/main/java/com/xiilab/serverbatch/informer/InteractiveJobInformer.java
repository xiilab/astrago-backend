package com.xiilab.serverbatch.informer;

import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertEntity;
import com.xiilab.modulek8sdb.alert.systemalert.entity.SystemAlertSetEntity;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertMessage;
import com.xiilab.modulek8sdb.alert.systemalert.enumeration.SystemAlertType;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertSetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.moduleuser.dto.GroupUserDTO;
import com.xiilab.moduleuser.service.GroupService;

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
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final GroupService groupService;
	private final SystemAlertRepository systemAlertRepository;
	private final SystemAlertSetRepository systemAlertSetRepository;

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

				SystemAlertSetEntity workspaceAlertSet = systemAlertSetRepository.getAlertSetEntityByWorkspaceName(
					deployment.getMetadata().getName());
				// 해당 워크스페이스 알림 설정이 True인 경우
				if(workspaceAlertSet.isWorkloadStartAlert()){
					GroupUserDTO workspaceOwner = groupService.getWorkspaceOwner(deployment.getMetadata().getName());
					systemAlertRepository.save(SystemAlertEntity.builder()
						.recipientId(workspaceOwner.getId())
						.systemAlertType(SystemAlertType.WORKLOAD)
						.message(String.format(
							SystemAlertMessage.WORKSPACE_START.getMessage(), deployment.getMetadata().getName()))
						.senderId("SYSTEM")
						.build());
				}
			}

			@Override
			public void onUpdate(Deployment deployment1, Deployment deployment2) {
			}

			@Override
			public void onDelete(Deployment deployment, boolean b) {
				// log.info("interactive job {}가 삭제되었습니다.", deployment.getMetadata().getName());
				// String namespace = deployment.getMetadata().getNamespace();
				// Namespace namespaceObject = kubernetesClient.namespaces().withName(namespace).get();
				// Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
				// K8SResourceMetadataDTO metadataFromResource = getInteractiveWorkloadInfoFromResource(deployment);
				// if (metadataFromResource != null) {
				// 	JobEntity jobEntity = JobEntity.jobBuilder()
				// 		.name(metadataFromResource.getName())
				// 		.description(metadataFromResource.getDescription())
				// 		.resourceName(metadataFromResource.getResourceName())
				// 		.workspaceName(namespaceObject.getMetadata().getLabels().get(""))
				// 		.workspaceResourceName(namespace)
				// 		.envs(getEnvFromContainer(container))
				// 		.cpuReq(metadataFromResource.getCpuReq())
				// 		.memReq(metadataFromResource.getMemReq())
				// 		.gpuReq(metadataFromResource.getGpuReq())
				// 		.cmd(String.join(" ", container.getCommand()))
				// 		.createdAt(metadataFromResource.getCreatedAt())
				// 		.deletedAt(metadataFromResource.getDeletedAt())
				// 		.creatorName(metadataFromResource.getCreatorUserName())
				// 		.creatorId(metadataFromResource.getCreatorId())
				// 		.workloadType(WorkloadType.INTERACTIVE)
				// 		.build();
				// 	workloadHistoryRepo.save(jobEntity);
				// 	// dataset, model mapping insert
				// 	String datasetIds = metadataFromResource.getDatasetIds();
				// 	String[] datasetIdList = datasetIds != null ? datasetIds.split(",") : null;
				// 	saveDataMapping(datasetIdList, datasetRepository::findById, jobEntity, VolumeType.DATASET);
				//
				// 	String modelIds = metadataFromResource.getModelIds();
				// 	String[] modelIdList = modelIds != null ? modelIds.split(",") : null;
				// 	saveDataMapping(modelIdList, modelRepository::findById, jobEntity, VolumeType.MODEL);
				// }

				SystemAlertSetEntity workspaceAlertSet = systemAlertSetRepository.getAlertSetEntityByWorkspaceName(
					deployment.getMetadata().getName());
				// 해당 워크스페이스 알림 설정이 True인 경우
				if(workspaceAlertSet.isWorkloadEndAlert()){
					GroupUserDTO workspaceOwner = groupService.getWorkspaceOwner(deployment.getMetadata().getName());
					systemAlertRepository.save(SystemAlertEntity.builder()
						.recipientId(workspaceOwner.getId())
						.systemAlertType(SystemAlertType.WORKLOAD)
						.message(String.format(SystemAlertMessage.WORKSPACE_END.getMessage(), deployment.getMetadata().getName()))
						.senderId("SYSTEM")
						.build());
				}
			}
		});

		log.info("Starting all registered interative job informers");
		informers.startAllRegisteredInformers();
	}
}
