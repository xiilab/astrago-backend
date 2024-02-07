package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;
import com.xiilab.modulek8sdb.repository.JobHistoryRepo;

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
	private final JobHistoryRepo jobHistoryRepo;

	@PostConstruct
	void doInformer() {
		jobInformer();
	}

	private void jobInformer() {
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
				if (!Objects.equals(job1.getMetadata().getResourceVersion(), job2.getMetadata().getResourceVersion())) {
					if (job2.getStatus().getSucceeded() != null && job2.getStatus().getSucceeded() > 0) {
						log.info("{} job이 완료 되었습니다.", job2.getMetadata().getName());
						String namespace = job2.getMetadata().getNamespace();
						K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job2);
						Pod pod = kubernetesClient.pods()
							.inNamespace(namespace)
							.withLabels(Map.of("app", metadataFromResource.getResourceName()))
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
					jobHistoryRepo.save(JobEntity.jobBuilder()
						.name(metadataFromResource.getName())
						.description(metadataFromResource.getDescription())
						.resourceName(metadataFromResource.getResourceName())
						.workspaceName(namespaceObject.getMetadata().getLabels().get(""))
						.workspaceResourceName(namespace)
						.envs(getEnvFromContainer(container))
						.cpuReq(metadataFromResource.getCpuReq())
						.memReq(metadataFromResource.getMemReq())
						.gpuReq(metadataFromResource.getGpuReq())
						.createdAt(metadataFromResource.getCreatedAt())
						.deletedAt(metadataFromResource.getDeletedAt())
						.creatorName(metadataFromResource.getCreatorName())
						.creatorId(metadataFromResource.getCreatorId())
						.workloadType(WorkloadType.BATCH)
						.build());
				}
			}
		});

		log.info("Starting all registered batch job informers");
		informers.startAllRegisteredInformers();
	}
}
