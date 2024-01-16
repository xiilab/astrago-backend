package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8sdb.entity.JobEntity;
import com.xiilab.modulek8sdb.entity.WorkloadType;
import com.xiilab.modulek8sdb.repository.JobHistoryRepo;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobInformer {
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
				log.info("{} job이 생성되었습니다.", job.getMetadata().getName());
			}

			@Override
			public void onUpdate(Job job1, Job job2) {
			}

			@Override
			public void onDelete(Job job, boolean b) {
				log.info("{}가 삭제되었습니다.", job.getMetadata().getName());

				Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
				K8SResourceMetadataDTO metadataFromResource = getMetadataFromResource(job);
				jobHistoryRepo.save(JobEntity.jobBuilder()
					.name(metadataFromResource.getName())
					.description(metadataFromResource.getDescription())
					.envs(getEnvFromContainer(container))
					.workloadType(WorkloadType.BATCH)
					.build());
			}
		});

		log.info("Starting all registered informers");
		informers.startAllRegisteredInformers();
	}
}
