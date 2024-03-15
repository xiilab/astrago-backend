package com.xiilab.serverbatch.informer;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.xiilab.modulek8s.config.K8sAdapter;

import io.fabric8.kubernetes.api.model.Node;
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
public class NodeInformer {
	private final K8sAdapter k8sAdapter;

	@PostConstruct
	void doInformer() {

	}

	private void nodeInformer() {
		KubernetesClient kubernetesClient = k8sAdapter.configServer();
		SharedInformerFactory informers = kubernetesClient.informers();
		SharedIndexInformer<Node> nodeSharedIndexInformer = informers.sharedIndexInformerFor(
			Node.class, 30 * 60 * 1000L
		);
		nodeSharedIndexInformer.addEventHandler(new ResourceEventHandler<>() {
			@Override
			public void onAdd(Node node) {
				log.info("{} node가 추가되었습니다.", node.getMetadata().getName());
			}

			@Override
			public void onUpdate(Node node1, Node node2) {
				if (!Objects.equals(node1.getMetadata().getResourceVersion(), node2.getMetadata().getResourceVersion())) {
					Map<String, String> labels = node2.getMetadata().getLabels();
					if (Objects.nonNull(labels)) {
						String migCapable = labels.get("mig-capable");
						if (Objects.nonNull(migCapable) && migCapable.equals("true")) {
							String node1MIGStatus = node1.getMetadata().getLabels().get("nvidia.com/mig.config.state");
							String node2MIGStatus = node2.getMetadata().getLabels().get("nvidia.com/mig.config.state");
							if (!node1MIGStatus.equals(node2MIGStatus) && node2MIGStatus.equals("success")) {
								log.info("MIG 설정이 완료되었습니다.");
							}
						}
					}
				}
			}

			@Override
			public void onDelete(Node node, boolean b) {
				log.info("{} node가 삭제되었습니다.", node.getMetadata().getName());

			}
		});

		log.info("Starting all regisetered node informer");
		informers.startAllRegisteredInformers();
	}

}
