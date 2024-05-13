package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
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
import com.xiilab.moduleuser.service.UserService;

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
public class BatchJobInformer extends JobInformer {
	private final K8sAdapter k8sAdapter;
	private final GroupService groupService;
	private final SystemAlertRepository systemAlertRepository;
	private final WorkspaceAlertSetRepository workspaceAlertSetRepository;
	private final ApplicationEventPublisher publisher;
	private final MailService mailService;
	private final UserService userService;

	public BatchJobInformer(WorkloadHistoryRepo workloadHistoryRepo,
		DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository,
		ModelWorkLoadMappingRepository modelWorkLoadMappingRepository,
		CodeWorkLoadMappingRepository codeWorkLoadMappingRepository,
		ImageWorkloadMappingRepository imageWorkloadMappingRepository, VolumeRepository volumeRepository,
		K8sAdapter k8sAdapter,
		DatasetRepository datasetRepository, ModelRepository modelRepository, CodeRepository codeRepository,
		ImageRepository imageRepository, CredentialRepository credentialRepository, SvcRepository svcRepository,
		GroupService groupService, SystemAlertRepository systemAlertRepository,
		WorkspaceAlertSetRepository workspaceAlertSetRepository,
		ApplicationEventPublisher publisher, MailService mailService, UserService userService) {
		super(workloadHistoryRepo, datasetWorkLoadMappingRepository, modelWorkLoadMappingRepository,
			codeWorkLoadMappingRepository, imageWorkloadMappingRepository, datasetRepository, modelRepository,
			codeRepository, imageRepository, credentialRepository, svcRepository, volumeRepository);
		this.k8sAdapter = k8sAdapter;
		this.groupService = groupService;
		this.systemAlertRepository = systemAlertRepository;
		this.workspaceAlertSetRepository = workspaceAlertSetRepository;
		this.publisher = publisher;
		this.mailService = mailService;
		this.userService = userService;
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
			}

			@Override
			public void onUpdate(Job job1, Job job2) {

				// 잡상태 조회
				WorkloadStatus beforeStatus = getJobStatus(job1.getStatus());
				WorkloadStatus afterStatus = getJobStatus(job2.getStatus());

				// 상태 pending -> running으로 변경되면 잡 실행 알림 발송
				if (beforeStatus != WorkloadStatus.RUNNING && afterStatus == WorkloadStatus.RUNNING) {
					sendRunningNotification(job2);

				}
				// 워크로드 에러 발생하면 알림 발송
				if (beforeStatus != WorkloadStatus.ERROR && afterStatus == WorkloadStatus.ERROR) {
					sendErrorNotification(job2);
				}
				if (!Objects.equals(job1.getMetadata().getResourceVersion(), job2.getMetadata().getResourceVersion())) {
					if (beforeStatus != WorkloadStatus.END && afterStatus == WorkloadStatus.END) {
						log.info("{} job이 완료 되었습니다.", job2.getMetadata().getName());
						String namespace = job2.getMetadata().getNamespace();
						K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job2);
						String creator =
							metadataFromResource.getCreatorId() != null ? metadataFromResource.getCreatorId() :
								"SYSTEM";

						//워크로드 종료 알림 발송
						sendJobEndNotification(metadataFromResource);

						Pod pod = kubernetesClient.pods()
							.inNamespace(namespace)
							.withLabels(
								Map.of(LabelField.APP.getField(), metadataFromResource.getWorkloadResourceName()))
							.list()
							.getItems()
							.get(0);
						String logResult = kubernetesClient.pods()
							.inNamespace(namespace)
							.withName(pod.getMetadata().getName())
							.getLog();
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
				log.info(metadataFromResource.getCpuReq() + " " + metadataFromResource.getMemReq());
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
				deleteServices(metadataFromResource.getWorkspaceResourceName(),
					metadataFromResource.getWorkloadResourceName());
			}
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}

	private void sendJobEndNotification(K8SResourceMetadataDTO metadataFromResource) {
		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(metadataFromResource.getWorkspaceResourceName())
			.workloadResourceName(metadataFromResource.getWorkloadResourceName())
			.workloadType(metadataFromResource.getWorkloadType())
			.build();

		String workloadName = metadataFromResource.getWorkloadName();
		String emailTitle = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMailTitle(),
			workloadName);
		String title = AlertMessage.WORKLOAD_END_CREATOR.getTitle();
		String message = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMessage(), workloadName);
		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_END, null, metadataFromResource.getCreatorId(), emailTitle, title, message,
			metadataFromResource.getWorkspaceResourceName(), pageNaviParam);

		publisher.publishEvent(workspaceUserAlertEvent);

		MailAttribute mail = MailAttribute.WORKLOAD_END;
		mailService.sendMail(MailDTO.builder()
			.subject(String.format(mail.getSubject(), metadataFromResource.getWorkloadName()))
			.title(String.format(mail.getTitle(), metadataFromResource.getWorkloadName()))
			.subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(userService.getUserById(metadataFromResource.getCreatorId()).getEmail())
			.build());
	}

	private void sendRunningNotification(Job job) {
		K8SResourceMetadataDTO batchWorkloadInfoFromResource = getBatchWorkloadInfoFromResource(job);

		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(batchWorkloadInfoFromResource.getWorkspaceResourceName())
			.workloadResourceName(batchWorkloadInfoFromResource.getWorkloadResourceName())
			.workloadType(batchWorkloadInfoFromResource.getWorkloadType())
			.build();

		//워크로드 생성 알림 발송
		String workloadName = batchWorkloadInfoFromResource.getWorkloadName();
		String emailTitle = String.format(AlertMessage.WORKLOAD_START_CREATOR.getMailTitle(), workloadName);
		String title = AlertMessage.WORKLOAD_START_CREATOR.getTitle();
		String message = String.format(AlertMessage.WORKLOAD_START_CREATOR.getMessage(), workloadName);
		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_START,
			null, batchWorkloadInfoFromResource.getCreatorId(), emailTitle, title, message,
			batchWorkloadInfoFromResource.getWorkspaceResourceName(), pageNaviParam);

		publisher.publishEvent(workspaceUserAlertEvent);

		MailAttribute mail = MailAttribute.WORKLOAD_START;

		mailService.sendMail(MailDTO.builder()
				.subject(String.format(mail.getSubject(), workloadName))
				.title(String.format(mail.getTitle(), workloadName))
				.subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.footer(mail.getFooter())
				.receiverEmail(userService.getUserById(batchWorkloadInfoFromResource.getCreatorId()).getEmail())
			.build());

	}

	private void sendErrorNotification(Job job2) {
		K8SResourceMetadataDTO batchWorkloadInfoFromResource = getBatchWorkloadInfoFromResource(job2);

		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(batchWorkloadInfoFromResource.getWorkspaceResourceName())
			.workloadResourceName(batchWorkloadInfoFromResource.getWorkloadResourceName())
			.workloadType(batchWorkloadInfoFromResource.getWorkloadType())
			.build();

		//워크로드 에러 알림 발송
		String workloadName = batchWorkloadInfoFromResource.getWorkloadName();
		AlertMessage workloadErrorCreator = AlertMessage.WORKLOAD_ERROR_CREATOR;
		String emailTitle = String.format(workloadErrorCreator.getMailTitle(), workloadName);
		String title = workloadErrorCreator.getTitle();
		String message = String.format(workloadErrorCreator.getMessage(), workloadName);
		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_ERROR,
			null, batchWorkloadInfoFromResource.getCreatorId(), emailTitle, title, message,
			batchWorkloadInfoFromResource.getWorkspaceResourceName(), pageNaviParam);

		publisher.publishEvent(workspaceUserAlertEvent);

		MailAttribute mail = MailAttribute.WORKLOAD_ERROR;
		mailService.sendMail(MailDTO.builder()
			.subject(String.format(mail.getSubject(), batchWorkloadInfoFromResource.getWorkloadName()))
			.title(String.format(mail.getTitle(), batchWorkloadInfoFromResource.getWorkloadName()))
			.subTitle(String.format(mail.getSubTitle(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(userService.getUserById(batchWorkloadInfoFromResource.getCreatorId()).getEmail())
			.build());
	}

	private boolean getJobCompleted(JobStatus jobStatus) {
		if (jobStatus.getSucceeded() != null && jobStatus.getSucceeded() > 0) {
			return true;
		} else if (jobStatus.getFailed() != null && jobStatus.getFailed() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private WorkloadStatus getJobStatus(JobStatus jobStatus) {
		Integer active = jobStatus.getActive();
		Integer failed = jobStatus.getFailed();
		Integer ready = jobStatus.getReady();
		if (!ValidUtils.isNullOrZero(failed)) {
			return WorkloadStatus.ERROR;
		} else if (!ValidUtils.isNullOrZero(ready)) {
			return WorkloadStatus.RUNNING;
		} else if (!ValidUtils.isNullOrZero(active)) {
			return WorkloadStatus.PENDING;
		}
		return WorkloadStatus.END;
	}
}
