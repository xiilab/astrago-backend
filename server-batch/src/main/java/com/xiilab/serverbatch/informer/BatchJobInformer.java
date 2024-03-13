package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.alert.systemalert.entity.WorkspaceAlertSetEntity;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertSetRepository;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;

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
	private final SystemAlertRepository systemAlertRepository;
	private final WorkspaceAlertSetRepository workspaceAlertSetRepository;

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

			@Transactional
			@Override
			public void onDelete(Job job, boolean b) {
				log.info("batch job {}가 삭제되었습니다.", job.getMetadata().getName());
				// Namespace namespaceObject = kubernetesClient.namespaces().withName(namespace).get();
				// Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
				K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job);

				if (metadataFromResource != null) {
					JobEntity endJob = workloadHistoryRepo.findByResourceName(job.getMetadata().getName())
						.orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_UPDATE_END_WORKLOAD_INFO));

					// 워크로드 종료될 때, deleteAt 업데이트
					endJob.updateDeletedAt(metadataFromResource.getDeletedAt());
					workloadHistoryRepo.save(endJob);

					/**
					 * TODO DELETE_YN 업데이트 필요, 서비스 삭제 필요
					 * 모델, 데이터셋은 매핑 엔티티만 업데이트
					 * 코드, 이미지는 커스텀이면 매핑, 원본 업데이트, 아니면 매핑만 업데이트
					 */

					// List<ModelWorkLoadMappingEntity> modelWorkloadMappingList = endJob.getModelWorkloadMappingList();
					// List<DatasetWorkLoadMappingEntity> datasetWorkloadMappingList = endJob.getDatasetWorkloadMappingList();
					// List<CodeWorkLoadMappingEntity> codeWorkloadMappingList = endJob.getCodeWorkloadMappingList();
					// ImageWorkloadMappingEntity imageWorkloadMappingEntity = endJob.getImageWorkloadMappingEntity();

					// WorkspaceAlertSetEntity workspaceAlertSet = getAlertSet(job.getMetadata().getName());
					// // 해당 워크스페이스 알림 설정이 True인 경우
					// if(workspaceAlertSet.isWorkloadEndAlert()){
					// 	systemAlertRepository.save(SystemAlertEntity.builder()
					// 		.recipientId(metadataFromResource.getCreatorId())
					// 		.systemAlertType(SystemAlertType.WORKLOAD)
					// 		.message(String.format(SystemAlertMessage.WORKSPACE_END.getMessage(), job.getMetadata().getName()))
					// 		.senderId("SYSTEM")
					// 		.build());
					// }
				}
			}
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}

	private WorkspaceAlertSetEntity getAlertSet(String workspaceName){
		return workspaceAlertSetRepository.getAlertSetEntityByWorkspaceName(workspaceName);
	}
}
