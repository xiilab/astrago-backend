package com.xiilab.serverbatch.informer;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.event.AdminAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.MigStatus;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.node.dto.MIGGpuDTO;
import com.xiilab.modulek8s.node.repository.NodeRepository;
import com.xiilab.modulek8sdb.mig.repository.MigRepository;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
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
	private final NodeRepository nodeRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final MigRepository migRepository;

	@PostConstruct
	void doInformer() {
		nodeInformer();
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
				if (!Objects.equals(node1.getMetadata().getResourceVersion(),
					node2.getMetadata().getResourceVersion())) {
					Map<String, String> labels = node2.getMetadata().getLabels();
					String nodeName = node2.getMetadata().getName();
					if (Objects.nonNull(labels)) {
						String migCapable = labels.get("nvidia.com/mig.config.state");
						if (Objects.nonNull(migCapable)) {
							String node1MIGStatus = node1.getMetadata().getLabels().get("nvidia.com/mig.config.state");
							String node2MIGStatus = node2.getMetadata().getLabels().get("nvidia.com/mig.config.state");
							if (!node1MIGStatus.equals(node2MIGStatus)) {
								MigStatus migStatus = MigStatus.valueOf(node2MIGStatus.toUpperCase());
								//성공적으로 변경 시 label에 gi개수 추가
								if (migStatus == MigStatus.SUCCESS) {
									updateNodeInfo(node2.getMetadata().getName());
								}
								switch (migStatus) {
									case SUCCESS -> {
										AlertMessage nodeMigApply = AlertMessage.NODE_MIG_APPLY;
										String mailTitle = nodeMigApply.getMailTitle();
										String title = nodeMigApply.getTitle();
										String message = String.format(nodeMigApply.getMessage(), nodeName);
										eventPublisher.publishEvent(
											new AdminAlertEvent(AlertName.ADMIN_NODE_MIG_APPLY, null, mailTitle, title,
												message, null, null));
									}
									// String.format("node %S의 MIG 적용이 완료되었습니다.", node2.getMetadata().getName());
									case PENDING ->
										log.info("node {}이 MIG 적용이 시작되었습니다.", node2.getMetadata().getName());
									case FAILED -> {
										AlertMessage nodeMigError = AlertMessage.NODE_MIG_ERROR;
										String mailTitle = nodeMigError.getMailTitle();
										String title = nodeMigError.getTitle();
										String message = String.format(nodeMigError.getMessage(), nodeName);

										MailDTO mailDTO = MailServiceUtils.createMIGErrorMail(nodeName);

										eventPublisher.publishEvent(
											new AdminAlertEvent(AlertName.ADMIN_NODE_MIG_ERROR, null, mailTitle, title,
												message, null, mailDTO));
									}
									// String.format("node %S의 MIG 적용을 실패하였습니다.", node2.getMetadata().getName());
									case REBOOTING -> log.info("node {}의 MIG 적용을 위해 관련 pod 및 노드가 재부팅 중입니다.",
										node2.getMetadata().getName());
								}
							}
						}
						// mps 설정 상태
						String node1MpsCapable = node1.getMetadata().getLabels().get("nvidia.com/mps.capable") == null ? "false" : node1.getMetadata().getLabels().get("nvidia.com/mps.capable");
						String node2MpsCapable = node2.getMetadata().getLabels().get("nvidia.com/mps.capable") == null ? "false" : node2.getMetadata().getLabels().get("nvidia.com/mps.capable");
						//mps 업데이트 완료
						if (!node1MpsCapable.equals(node2MpsCapable)) {
							kubernetesClient.nodes().withName(node2.getMetadata().getName()).edit(node ->  new NodeBuilder(node)
								.editMetadata()
								.addToLabels("mps_status", "COMPLETE")
								.endMetadata()
								.build());
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

	private void updateNodeInfo(String nodeName) {
		int migGiCount = getMIGGiCount(nodeName);
		nodeRepository.updateNodeLabel(nodeName, Map.of("nvidia.com/mig-count", String.valueOf(migGiCount)));
	}

	private int getMIGGiCount(String nodeName) {
		int giCount = 0;
		Map<String, Object> migConfigMap = nodeRepository.getMigConfigMap();
		if(migConfigMap.get("custom-" + nodeName) == null){
			syncMigConfig(nodeName);
		}
		MIGGpuDTO.MIGInfoStatus nodeMigStatus = nodeRepository.getNodeMigStatus(nodeName);
		if (CollectionUtils.isEmpty(nodeMigStatus.getMigInfos())) {
			return 0;
		}
		for (MIGGpuDTO.MIGInfoDTO migInfo : nodeMigStatus.getMigInfos()) {
			int gpus = migInfo.getGpuIndexs().size();
			int profileCnt = migInfo.getProfile().values().stream().mapToInt(Integer::intValue).sum();
			giCount += gpus * profileCnt;
		}
		return giCount;
	}

	private void syncMigConfig(String nodeName) {
		List<MIGGpuDTO.MIGInfoDTO> migInfoDTOList = migRepository.getAllByNodeName(nodeName)
			.stream()
			.map(migInfoEntity ->
				MIGGpuDTO.MIGInfoDTO.builder()
					.gpuIndexs(migInfoEntity.getGpuIndexes())
					.profile(migInfoEntity.getProfile())
					.migEnable(migInfoEntity.isMigEnable())
					.build()
			)
			.toList();
		MIGGpuDTO migGpuDTO = MIGGpuDTO.builder()
			.nodeName(nodeName)
			.migInfos(migInfoDTOList)
			.build();
		nodeRepository.syncMigConfigMap(migGpuDTO);
	}

}
