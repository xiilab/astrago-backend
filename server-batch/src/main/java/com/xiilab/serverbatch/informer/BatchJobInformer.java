package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.xiilab.modulealert.dto.AlertDTO;
import com.xiilab.modulealert.dto.AlertSetDTO;
import com.xiilab.modulealert.enumeration.AlertMessage;
import com.xiilab.modulealert.enumeration.AlertType;
import com.xiilab.modulealert.service.AlertService;
import com.xiilab.modulealert.service.AlertSetService;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
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
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
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
public class BatchJobInformer {
	private final K8sAdapter k8sAdapter;
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final CodeRepository codeRepository;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;
	private final AlertService alertService;
	private final AlertSetService alertSetService;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	public void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Job> jobSharedIndexInformer = informers.sharedIndexInformerFor(
			Job.class, 30 * 60 * 1000L);
		jobSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {
			@Override
			public void onAdd(Job job) {
				log.info("{} batch job이 생성되었습니다.", job.getMetadata().getName());

				K8SResourceMetadataDTO batchWorkloadInfoFromResource = getBatchWorkloadInfoFromResource(job);

				AlertSetDTO.ResponseDTO workspaceAlertSet = getAlertSet(job.getMetadata().getName());
				// 해당 워크스페이스 알림 설정이 True인 경우
				if(workspaceAlertSet.isWorkloadStartAlert()){
					alertService.sendAlert(AlertDTO.builder()
						.recipientId(batchWorkloadInfoFromResource.getCreatorId())
						.alertType(AlertType.WORKLOAD)
						.message(String.format(AlertMessage.WORKSPACE_START.getMessage(), job.getMetadata().getName()))
						.senderId("SYSTEM")
						.build());
				}
			}

			@Override
			public void onUpdate(Job job1, Job job2) {
				if (!Objects.equals(job1.getMetadata().getResourceVersion(), job2.getMetadata().getResourceVersion())) {
					if (job2.getStatus().getSucceeded() != null && job2.getStatus().getSucceeded() > 0) {
						log.info("{} job이 완료 되었습니다.", job2.getMetadata().getName());
						String namespace = job2.getMetadata().getNamespace();
						K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job2);
						Pod pod = kubernetesClient.pods()
							.inNamespace(namespace)
							.withLabels(Map.of(LabelField.APP.getField(), metadataFromResource.getResourceName()))
							.list()
							.getItems()
							.get(0);
						String logResult = kubernetesClient.pods()
							.inNamespace(namespace)
							.withName(pod.getMetadata().getName())
							.getLog();
						String creator =
							metadataFromResource.getCreatorId() != null ? metadataFromResource.getCreatorId() :
								"SYSTEM";
						try {
							FileUtils.saveLogFile(logResult, metadataFromResource.getResourceName(), creator);
						} catch (IOException e) {
							log.error("로그 파일 저장 중 에러가 발생하였습니다.\n" + e.getMessage());
						}
					}
				}
			}

			@Override
			public void onDelete(Job job, boolean b) {
				log.info("batch job {}가 삭제되었습니다.", job.getMetadata().getName());
				String namespace = job.getMetadata().getNamespace();
				Namespace namespaceObject = kubernetesClient.namespaces().withName(namespace).get();
				Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
				K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job);
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
						.workloadType(WorkloadType.BATCH)
						.build();
					workloadHistoryRepo.save(jobEntity);

					// dataset, model mapping insert
					String datasetIds = metadataFromResource.getDatasetIds();
					String[] datasetIdList = datasetIds != null ? datasetIds.split(",") : null;
					saveDataMapping(datasetIdList, datasetRepository::findById, jobEntity, VolumeType.DATASET);

					String modelIds = metadataFromResource.getModelIds();
					String[] modelIdList = modelIds != null ? modelIds.split(",") : null;
					saveDataMapping(modelIdList, modelRepository::findById, jobEntity, VolumeType.MODEL);

					//소스코드 mapping insert
					String codeIds = metadataFromResource.getCodeIds();
					String[] codeIdList = codeIds != null ? codeIds.split(",") : null;
					saveDataMapping(codeIdList, codeRepository::findById, jobEntity, VolumeType.CODE);

					AlertSetDTO.ResponseDTO workspaceAlertSet = getAlertSet(job.getMetadata().getName());
					// 해당 워크스페이스 알림 설정이 True인 경우
					if(workspaceAlertSet.isWorkloadEndAlert()){
						alertService.sendAlert(AlertDTO.builder()
							.recipientId(metadataFromResource.getCreatorId())
							.alertType(AlertType.WORKLOAD)
							.message(String.format(AlertMessage.WORKSPACE_END.getMessage(), job.getMetadata().getName()))
							.senderId("SYSTEM")
							.build());
					}
				}
			}
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}
	// 데이터셋 또는 모델 정보를 저장하는 메서드
	private void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, JobEntity jobEntity, VolumeType type) {
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
						}else if(type == VolumeType.MODEL){
							Model model = (Model)entity;
							ModelWorkLoadMappingEntity modelWorkLoadMappingEntity = ModelWorkLoadMappingEntity.builder()
								.model(model)
								.workload(jobEntity)
								.build();
							modelWorkLoadMappingRepository.save(modelWorkLoadMappingEntity);
						}else{
							CodeEntity code = (CodeEntity)entity;
							CodeWorkLoadMappingEntity codeWorkLoadMappingEntity = CodeWorkLoadMappingEntity.builder()
								.workload(jobEntity)
								.code(code)
								.build();
							codeWorkLoadMappingRepository.save(codeWorkLoadMappingEntity);
						}
					});
				}
			}
		}
	}

	private AlertSetDTO.ResponseDTO getAlertSet(String workspaceName){
		return alertSetService.getWorkspaceAlertSet(workspaceName);
	}
}
