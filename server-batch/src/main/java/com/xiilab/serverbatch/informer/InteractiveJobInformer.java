package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.enums.SystemAlertMessage;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
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
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class InteractiveJobInformer extends JobInformer{
	private final K8sAdapter k8sAdapter;
	private final GroupService groupService;
	private final SystemAlertRepository systemAlertRepository;
	private final WorkspaceAlertSetRepository workspaceAlertSetRepository;
	private final ApplicationEventPublisher publisher;

	public InteractiveJobInformer(WorkloadHistoryRepo workloadHistoryRepo,
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

	private void jobInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Deployment> jobSharedIndexInformer = informers.sharedIndexInformerFor(
			Deployment.class, 30 * 60 * 1000L);
		jobSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {
			@Override
			public void onAdd(Deployment deployment) {
				log.info("{} interactive job이 생성되었습니다.", deployment.getMetadata().getName());
				K8SResourceMetadataDTO metadataFromResource = getInteractiveWorkloadInfoFromResource(deployment);
				//워크로드 생성 알림 발송
				String workloadName = metadataFromResource.getWorkloadName();
				String emailTitle = String.format(SystemAlertMessage.WORKLOAD_START_CREATOR.getMailTitle(), workloadName);
				String title = SystemAlertMessage.WORKLOAD_START_CREATOR.getTitle();
				String message = String.format(SystemAlertMessage.WORKLOAD_START_CREATOR.getMessage(), workloadName);
				WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_START,
					emailTitle, title, message, metadataFromResource.getWorkspaceResourceName());

				publisher.publishEvent(workspaceUserAlertEvent);
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
					saveJobHistory(namespace, namespaceObject, container, metadataFromResource);
				}

				// PV, PVC 삭제
				List<Volume> volumes = deployment.getSpec().getTemplate().getSpec().getVolumes();
				for (Volume volume : volumes) {
					if (!ObjectUtils.isEmpty(volume.getPersistentVolumeClaim())) {
						deletePvAndPVC(namespace, volume.getName(), volume.getPersistentVolumeClaim().getClaimName());
					}
				}

				// 서비스 삭제
				deleteServices(metadataFromResource.getWorkspaceResourceName(), metadataFromResource.getWorkloadResourceName());
				//워크로드 삭제 알림 발송
				String workloadName = metadataFromResource.getWorkloadName();
				String emailTitle = String.format(SystemAlertMessage.WORKLOAD_DELETE_CREATOR.getMailTitle(), workloadName);
				String title = SystemAlertMessage.WORKLOAD_DELETE_CREATOR.getTitle();
				String message = String.format(SystemAlertMessage.WORKLOAD_DELETE_CREATOR.getMessage(), workloadName);
				WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER, AlertName.USER_WORKLOAD_DELETE,
					emailTitle, title, message, metadataFromResource.getWorkspaceResourceName());

				publisher.publishEvent(workspaceUserAlertEvent);
			}
		});

		log.info("Starting all registered interative job informers");
		informers.startAllRegisteredInformers();
	}
}
