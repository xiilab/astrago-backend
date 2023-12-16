package com.xiilab.modulek8s.storage.volume.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
import com.xiilab.modulek8s.storage.volume.dto.request.CreateDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.vo.VolumeVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
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
	public void createVolume(CreateDTO createDTO) {
		VolumeVO volumeVO = VolumeVO.dtoToVo(createDTO);
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
			List<StatefulSet> statefulSets = getStatefulSetsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(statefulSets, workloadNames);
			//사용중인 deployment 조회
			List<Deployment> deployments = getDeploymentsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(deployments, workloadNames);
			//사용중인 job 조회
			List<Job> jobs = getJobsInUseVolume(volumeMetaName, client);
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
			throw e;
		}
	}


	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		try(final KubernetesClient client = k8sAdapter.configServer()) {
			//본인이 생성한 볼륨인지 체크
			String creator = modifyVolumeDTO.getCreator();
			boolean chk = volumeCreatorCheck(modifyVolumeDTO.getWorkspaceMetaName(), modifyVolumeDTO.getVolumeMetaName(),client, creator);
			if(!chk)
				throw new RuntimeException("자신이 생성한 볼륨만 수정할 수 있습니다.");

			client.persistentVolumeClaims()
				.inNamespace(modifyVolumeDTO.getWorkspaceMetaName())
				.withName(modifyVolumeDTO.getVolumeMetaName())
				.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
					.addToAnnotations("name", modifyVolumeDTO.getName())
					.endMetadata()
					.build());
		}
	}



	@Override
	public void deleteVolumeByMetaName(DeleteVolumeDTO deleteVolumeDTO){
		try(final KubernetesClient client = k8sAdapter.configServer()){
			//본인이 생성한 볼륨인지 체크


			//삭제 전 사용중인지 확인해야함
			String volumeMetaName = deleteVolumeDTO.getVolumeMetaName();
			checkAndThrowIfInUse(() -> getDeploymentsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getStatefulSetsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getJobsInUseVolume(volumeMetaName, client));

			//삭제
			client.persistentVolumeClaims().inNamespace(deleteVolumeDTO.getWorkspaceMetaName())
				.withName(deleteVolumeDTO.getVolumeMetaName()).delete();
		}
	}


	/**
	 * 해당 볼륨을 사용중인 job list 조회
	 * @param volumeMetaName
	 * @param client
	 * @return
	 */
	private static List<Job> getJobsInUseVolume(String volumeMetaName, KubernetesClient client) {
		return client.batch().v1().jobs().withLabelIn(volumeMetaName, "true")
			.list()
			.getItems();
	}

	/**
	 * 해당 볼륨을 사용중인 Deployment list 조회
	 * @param volumeMetaName
	 * @param client
	 * @return
	 */
	private static List<Deployment> getDeploymentsInUseVolume(String volumeMetaName, KubernetesClient client) {
		return client.apps().deployments().withLabelIn(volumeMetaName, "true")
			.list()
			.getItems();
	}

	/**
	 * 해당 볼륨을 사용중인 StatefulSet list 조회
	 * @param volumeMetaName
	 * @param client
	 * @return
	 */
	private static List<StatefulSet> getStatefulSetsInUseVolume(String volumeMetaName, KubernetesClient client) {
		return client
			.apps()
			.statefulSets()
			.withLabelIn(volumeMetaName, "true")
			.list()
			.getItems();
	}

	/**
	 * 해당 볼륨을 사용중인 workload set
	 * @param resources
	 * @param workloadNames
	 */
	private void setWorkloadInUseVolume(List<? extends HasMetadata> resources, List<String> workloadNames){
		for (HasMetadata resource : resources) {
			Map<String, String> annotations = resource.getMetadata().getAnnotations() == null ? null : resource.getMetadata().getAnnotations();
			if (annotations != null) {
				String name = annotations.get(AnnotationField.NAME.getField());
				if (name != null) {
					workloadNames.add(name);
				}
			}
		}
	}

	/**
	 * 사용중인 볼륨인 경우 throw exception
	 * @param resourceFetcher
	 * @param <T>
	 */
	private <T> void checkAndThrowIfInUse(Supplier<List<T>> resourceFetcher) {
		List<T> resourcesInUse = resourceFetcher.get();
		if (resourcesInUse != null && !resourcesInUse.isEmpty()) {
			throw new RuntimeException("사용중인 볼륨은 삭제할 수 없습니다.");
		}
	}

	/**
	 * 자신이 생성한 볼륨이 맞는지 체크
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param client
	 * @param creator
	 */
	private static boolean volumeCreatorCheck(String workspaceMetaName, String volumeMetaName, KubernetesClient client, String creator) {
		PersistentVolumeClaim persistentVolumeClaim = client.persistentVolumeClaims()
			.inNamespace(workspaceMetaName)
			.withName(volumeMetaName).get();
		if(persistentVolumeClaim == null){
			throw new RuntimeException("볼륨이 존재하지 않습니다.");
		}
		String labelCreator = persistentVolumeClaim.getMetadata().getLabels().get(LabelField.CREATOR.getField());

		return labelCreator.equals(creator) ? true : false;
	}
}
