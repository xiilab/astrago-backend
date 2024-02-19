package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.common.enums.VolumeType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadType;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;

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
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
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
					JobEntity jobEntity = JobEntity.jobBuilder()
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
						.creatorName(metadataFromResource.getCreatorUserName())
						.creatorId(metadataFromResource.getCreatorId())
						.workloadType(WorkloadType.INTERACTIVE)
						.build();
					workloadHistoryRepo.save(jobEntity);
					// dataset, model mapping insert
					String datasetIds = metadataFromResource.getDatasetIds();
					String[] datasetIdList = datasetIds != null ? datasetIds.split(",") : null;
					saveDataMapping(datasetIdList, datasetRepository::findById, jobEntity, VolumeType.DATASET);

					String modelIds = metadataFromResource.getModelIds();
					String[] modelIdList = modelIds != null ? modelIds.split(",") : null;
					saveDataMapping(modelIdList, modelRepository::findById, jobEntity, VolumeType.MODEL);
				}
			}
		});

		log.info("Starting all registered interative job informers");
		informers.startAllRegisteredInformers();
	}
	// 데이터셋 또는 모델 정보를 저장하는 메서드
	public void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, JobEntity jobEntity, VolumeType type) {
		if (ids != null) {
			for (String id : ids) {
				if (StringUtils.hasText(id)) {
					Optional<?> optionalEntity = findByIdFunction.apply(Long.valueOf(id));
					optionalEntity.ifPresent(entity -> {
						if(type == VolumeType.DATASET){
							Dataset dataset = (Dataset)entity;
							DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity = DatasetWorkLoadMappingEntity.builder()
								.dataset(dataset)
								.workload(jobEntity)
								.build();
							datasetWorkLoadMappingRepository.save(datasetWorkLoadMappingEntity);
						}else{
							Model model = (Model)entity;
							ModelWorkLoadMappingEntity modelWorkLoadMappingEntity = ModelWorkLoadMappingEntity.builder()
								.model(model)
								.workload(jobEntity)
								.build();
							modelWorkLoadMappingRepository.save(modelWorkLoadMappingEntity);
						}
					});
				}
			}
		}
	}
}
