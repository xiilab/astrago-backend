package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.SystemAlertMessage;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.SystemAlertRepository;
import com.xiilab.modulek8sdb.alert.systemalert.repository.WorkspaceAlertSetRepository;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.image.repository.ImageWorkloadMappingRepository;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.moduleuser.service.GroupService;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BatchJobInformer extends JobInformer{
	private final K8sAdapter k8sAdapter;
	private final GroupService groupService;
	private final SystemAlertRepository systemAlertRepository;
	private final WorkspaceAlertSetRepository workspaceAlertSetRepository;
	private final ApplicationEventPublisher publisher;

	public BatchJobInformer(WorkloadHistoryRepo workloadHistoryRepo,
		DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository,
		ModelWorkLoadMappingRepository modelWorkLoadMappingRepository,
		CodeWorkLoadMappingRepository codeWorkLoadMappingRepository,
		ImageWorkloadMappingRepository imageWorkloadMappingRepository, VolumeRepository volumeRepository, K8sAdapter k8sAdapter,
		DatasetRepository datasetRepository, ModelRepository modelRepository, CodeRepository codeRepository,
		ImageRepository imageRepository, CredentialRepository credentialRepository, SvcRepository svcRepository,
		GroupService groupService, SystemAlertRepository systemAlertRepository, WorkspaceAlertSetRepository workspaceAlertSetRepository,
		ApplicationEventPublisher publisher) {
		super(workloadHistoryRepo, datasetWorkLoadMappingRepository, modelWorkLoadMappingRepository,
			codeWorkLoadMappingRepository, imageWorkloadMappingRepository, datasetRepository, modelRepository,
			codeRepository, imageRepository, credentialRepository, svcRepository, volumeRepository);
		this.k8sAdapter = k8sAdapter;
		this.groupService = groupService;
		this.systemAlertRepository = systemAlertRepository;
		this.workspaceAlertSetRepository = workspaceAlertSetRepository;
		this.publisher = publisher;
	}

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

				//워크로드 생성 알림 발송
				String workloadName = batchWorkloadInfoFromResource.getWorkloadName();
				String emailTitle = String.format(SystemAlertMessage.WORKLOAD_START_CREATOR.getMailTitle(), workloadName);
				String title = SystemAlertMessage.WORKLOAD_START_CREATOR.getTitle();
				String message = String.format(SystemAlertMessage.WORKLOAD_START_CREATOR.getMessage(), workloadName);
				WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_START,
					emailTitle, title, message, batchWorkloadInfoFromResource.getWorkspaceResourceName());

				publisher.publishEvent(workspaceUserAlertEvent);
			}

			@Override
			public void onUpdate(Job job1, Job job2) {
				if (!Objects.equals(job1.getMetadata().getResourceVersion(), job2.getMetadata().getResourceVersion())) {
					if (getJobCompleted(job2.getStatus())) {
						log.info("{} job이 완료 되었습니다.", job2.getMetadata().getName());
						String namespace = job2.getMetadata().getNamespace();
						K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job2);
						Pod pod = kubernetesClient.pods()
							.inNamespace(namespace)
							.withLabels(Map.of(LabelField.APP.getField(), metadataFromResource.getWorkloadResourceName()))
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
						//워크로드 종료 알림 발송
						String workloadName = metadataFromResource.getWorkloadName();
						String emailTitle = String.format(SystemAlertMessage.WORKLOAD_END_CREATOR.getMailTitle(), workloadName);
						String title = SystemAlertMessage.WORKLOAD_END_CREATOR.getTitle();
						String message = String.format(SystemAlertMessage.WORKLOAD_END_CREATOR.getMessage(), workloadName);
						WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_END,
							emailTitle, title, message, metadataFromResource.getWorkspaceResourceName());

						publisher.publishEvent(workspaceUserAlertEvent);
						try {
							FileUtils.saveLogFile(logResult, metadataFromResource.getWorkloadResourceName(), creator);
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
				String namespace = job.getMetadata().getNamespace();
				Namespace namespaceObject = kubernetesClient.namespaces().withName(namespace).get();
				Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
				K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job);

				// 잡 히스토리 저장
				if (metadataFromResource != null) {
					saveJobHistory(namespace, namespaceObject, container, metadataFromResource);
				}

				// PV, PVC 삭제
				List<Volume> volumes = job.getSpec().getTemplate().getSpec().getVolumes();
				for (Volume volume : volumes) {
					if (!ObjectUtils.isEmpty(volume.getPersistentVolumeClaim())) {
						deletePvAndPVC(namespace, volume.getName(), volume.getPersistentVolumeClaim().getClaimName());
					}
				}

				// 서비스 삭제
				deleteServices(metadataFromResource.getWorkspaceResourceName(), metadataFromResource.getWorkloadResourceName());

				//워크로드 종료 알림 발송
				String workloadName = metadataFromResource.getWorkloadName();
				String emailTitle = String.format(SystemAlertMessage.WORKLOAD_END_CREATOR.getMailTitle(), workloadName);
				String title = SystemAlertMessage.WORKLOAD_END_CREATOR.getTitle();
				String message = String.format(SystemAlertMessage.WORKLOAD_END_CREATOR.getMessage(), workloadName);
				WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_END,
					emailTitle, title, message, metadataFromResource.getWorkspaceResourceName());

				publisher.publishEvent(workspaceUserAlertEvent);
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
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}

	private boolean getJobCompleted(JobStatus jobStatus) {
		if (jobStatus.getSucceeded() != null && jobStatus.getSucceeded() > 0) {
			return true;
		} else if(jobStatus.getFailed() != null && jobStatus.getFailed() > 0) {
			return true;
		} else {
			return false;
		}
	}
}
