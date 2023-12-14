package com.xiilab.modulek8s.storage.volume.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.modulek8s.storage.volume.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.vo.VolumeVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VolumeRepositoryImpl implements VolumeRepository {
	private final K8sAdapter k8sAdapter;

	@Override
	public void createVolume(CreateVolumeDTO createVolumeDTO) {
		VolumeVO volumeVO = VolumeVO.dtoToVo(createVolumeDTO);
		try(final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaim resource = (PersistentVolumeClaim)volumeVO.createResource();
			client.persistentVolumeClaims().resource(resource).create();
		}catch (Exception e){
			log.error("k8s cluster connect error {}", e.getMessage(), e);
		}
	}

	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName, String volumeMetaName) {
		try(final KubernetesClient client = k8sAdapter.configServer()) {
			List<String> workloadNames = new ArrayList<>();
			PersistentVolumeClaim pvc = client.persistentVolumeClaims()
				.inNamespace(workspaceMetaName)
				.withName(volumeMetaName)
				.get();
			if(pvc == null){
				throw new NullPointerException("해당 볼륨이 존재하지 않습니다.");
			}
			String requestVolume = pvc.getSpec().getResources().getRequests().get("storage").toString();
			StorageType storageType = StorageType.valueOf(
				pvc.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField()));
			//사용중인 statefulSets 조회
			List<StatefulSet> statefulSets = client.apps().statefulSets().withLabelIn(volumeMetaName, "true")
				.list()
				.getItems();
			setWorkloadInUseVolume(statefulSets, workloadNames);
			//사용중인 deployment 조회
			List<Deployment> deployments = client.apps().deployments().withLabelIn(volumeMetaName, "true")
				.list().getItems();
			setWorkloadInUseVolume(deployments, workloadNames);
			//사용중인 job 조회
			List<Job> jobs = client.batch().v1().jobs().withLabelIn(volumeMetaName, "true")
				.list().getItems();
			setWorkloadInUseVolume(jobs, workloadNames);

			return VolumeWithWorkloadsResDTO.builder()
				.hasMetadata(pvc)
				.workloadNames(workloadNames)
				.requestVolume(requestVolume)
				.storageType(storageType)
				.build();
		}catch (NullPointerException e){
			log.error("volume not found {}", e.getMessage(), e);
			throw e;
		}catch (Exception e){
			log.error("k8s cluster connect error {}", e.getMessage(), e);
			return null;
		}
	}

	private void setWorkloadInUseVolume(List<? extends HasMetadata> resources, List<String> workloadNames){
		for (HasMetadata resource : resources) {
			Map<String, String> annotations = resource.getMetadata().getAnnotations();
			if (annotations != null) {
				String name = annotations.get(AnnotationField.NAME.getField());
				if (name != null) {
					workloadNames.add(name);
				}
			}
		}
	}
}
