package com.xiilab.modulek8s.storage.volume.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulecommon.exception.K8sException;
import com.xiilab.modulecommon.exception.errorcode.VolumeErrorCode;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ResourceType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.dto.DeleteStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.DeleteVolumeDTO;
import com.xiilab.modulek8s.facade.dto.ModifyVolumeDTO;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePV;
import com.xiilab.modulek8s.storage.volume.dto.request.CreatePVC;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithStorageResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
import com.xiilab.modulek8s.storage.volume.vo.PersistentVolumeClaimVO;
import com.xiilab.modulek8s.storage.volume.vo.PersistentVolumeVO;
import com.xiilab.modulek8s.storage.volume.vo.VolumeVO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.PodSpecFluent;
import io.fabric8.kubernetes.api.model.PodTemplateSpecFluent;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentFluent;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpecFluent;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class K8sVolumeRepositoryImpl implements K8sVolumeRepository {
	private final K8sAdapter k8sAdapter;
	private static final String ASTRA = "astra";

	/**
	 * 유저가 설정한 스토리지 이름 조회
	 *
	 * @param client
	 * @param storageMetaName
	 * @return
	 */
	private static String getStorageClassName(KubernetesClient client, String storageMetaName) {
		return client.storage()
			.v1()
			.storageClasses()
			.withName(storageMetaName)
			.get()
			.getMetadata()
			.getAnnotations()
			.get(AnnotationField.NAME.getField());
	}

	/**
	 * 볼륨 전체 리스트 조회
	 *
	 * @param client
	 * @return
	 */
	private static List<PersistentVolumeClaim> getAllVolumes(KubernetesClient client) {
		return client.persistentVolumeClaims()
			.inAnyNamespace()
			.withLabel(LabelField.CONTROL_BY.getField(), ASTRA)
			.list()
			.getItems();
	}

	@Override
	public String createVolume(CreateVolumeDTO createVolumeDTO) {
		VolumeVO volumeVO = VolumeVO.dtoToVo(createVolumeDTO);
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaim resource = (PersistentVolumeClaim)volumeVO.createResource();
			client.persistentVolumeClaims().resource(resource).create();
			return resource.getMetadata().getName();
		} catch (Exception e) {
			log.error("k8s cluster connect error {}", e.getMessage(), e);
			return null;
		}
	}

	private static PersistentVolumeClaim findPVCByMetaName(String volumeMetaName,
		List<PersistentVolumeClaim> pvcs) {
		return pvcs.stream()
			.filter(pvc -> pvc.getMetadata().getName().equals(volumeMetaName))
			.findFirst()
			.orElseThrow(() -> new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND));
	}

	private static String getStorageSavePath(KubernetesClient client, String storageName) {
		return client.storage().v1().storageClasses().withName(storageName).get().getParameters().get("share");
	}

	private static String getStorageClassName(PersistentVolumeClaim persistentVolumeClaim) {
		return persistentVolumeClaim.getSpec().getStorageClassName();
	}

	private static StorageType getStorageType(PersistentVolumeClaim persistentVolumeClaim) {
		return StorageType.valueOf(
			persistentVolumeClaim.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField()));
	}

	private static String getRequestVolume(PersistentVolumeClaim persistentVolumeClaim) {
		return persistentVolumeClaim.getSpec().getResources().getRequests().get("storage").toString();
	}

	private static String getNamespace(PersistentVolumeClaim persistentVolumeClaim) {
		return persistentVolumeClaim.getMetadata().getNamespace();
	}

	@Override
	public void deleteVolumeByMetaName(String volumeMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {

			//삭제 전 사용중인지 확인해야함
			checkAndThrowIfInUse(() -> getDeploymentsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getStatefulSetsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getJobsInUseVolume(volumeMetaName, client));

			//삭제
			PersistentVolumeClaim pvc = client.persistentVolumeClaims()
				.inAnyNamespace()
				.withLabel(LabelField.CONTROL_BY.getField(), ASTRA)
				.list()
				.getItems()
				.stream()
				.filter(persistentVolumeClaim -> persistentVolumeClaim.getMetadata().getName().equals(volumeMetaName))
				.findFirst()
				.orElseThrow(() -> new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND));

			client.persistentVolumeClaims().resource(pvc).delete();
		}
	}

	@Override
	public void modifyVolume(ModifyVolumeDTO modifyVolumeDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			Resource<PersistentVolumeClaim> persistentVolumeClaimResource = client.persistentVolumeClaims()
				.inAnyNamespace()
				.withLabel(LabelField.CONTROL_BY.getField(), ASTRA)
				.resources()
				.filter(pvcr -> pvcr.get().getMetadata().getName().equals(modifyVolumeDTO.getVolumeMetaName()))
				.findFirst()
				.orElseThrow(() -> new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND));

			persistentVolumeClaimResource.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
				.addToAnnotations(AnnotationField.NAME.getField(), modifyVolumeDTO.getName())
				.endMetadata()
				.build());
		}
	}

	/**
	 * 스토리지 생성 - PV 생성
	 * @param createPV
	 */
	@Override
	public void createPV(CreatePV createPV) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeVO persistentVolumeVO = PersistentVolumeVO.dtoToEntity(createPV);
			PersistentVolume resource = (PersistentVolume)persistentVolumeVO.createResource();
			client.persistentVolumes().resource(resource).create();
		}
	}

	@Override
	public void deletePVC(String pvcName, String namespace) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.persistentVolumeClaims().inNamespace(namespace).withName(pvcName).delete();
		}
	}

	@Override
	public void deleteStorageClass(DeleteStorageReqDTO deleteStorageReqDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.storage().v1().storageClasses().withName(deleteStorageReqDTO.getStorageClassName()).delete();
		}
	}

	@Override
	public void deletePV(String pvName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.persistentVolumes().withName(pvName).delete();
		}
	}

	@Override
	public void deleteStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			String pvcName = deleteStorageReqDTO.getPvcName();
			String volName = deleteStorageReqDTO.getVolumeName();
			String hostPath = deleteStorageReqDTO.getHostPath();
			Volume vol = new VolumeBuilder()
				.withName(volName)
				.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(pvcName, null))
				.build();

			client.apps()
				.deployments()
				.inNamespace(deleteStorageReqDTO.getNamespace())
				.withName(deleteStorageReqDTO.getAstragoDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editSpec()
					.editOrNewTemplate()
					.editSpec()
					.removeFromVolumes(vol)
					.editContainer(0)
					.removeFromVolumeMounts(new VolumeMount(hostPath, null, volName, null, null, null))
					.endContainer()
					.endSpec()
					.endTemplate()
					.endSpec()
					.build());
		}
	}

	@Override
	public void deleteDellStorage(DeleteStorageReqDTO deleteStorageReqDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			String pvcName = deleteStorageReqDTO.getPvcName();
			String volName = deleteStorageReqDTO.getVolumeName();
			String hostPath = deleteStorageReqDTO.getHostPath();

			Volume vol = new VolumeBuilder()
				.withName(volName)
				.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSourceBuilder()
					.withClaimName(pvcName)
					.build()).build();

			VolumeMount volumeMount = new VolumeMountBuilder()
				.withName(volName)
				.withMountPath(hostPath)
				.build();

			client.apps()
				.deployments()
				.inNamespace(deleteStorageReqDTO.getNamespace())
				.withName(deleteStorageReqDTO.getAstragoDeploymentName())
				.edit(d -> new DeploymentBuilder(d)
					.editSpec()
					.editTemplate()
					.editSpec()
					.removeFromVolumes(vol)
					.editContainer(0)
					.removeFromVolumeMounts(volumeMount)
					.endContainer()
					.endSpec()
					.endTemplate()
					.endSpec()
					.build());
		}
	}

	@Override
	public List<VolumeMount> getAstragoVolumes() {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			Deployment deployment = client.apps()
				.deployments()
				.inNamespace("astrago")
				.withName("astrago-backend-core")
				.get();
			return deployment.getSpec()
				.getTemplate()
				.getSpec()
				.getContainers()
				.get(0)
				.getVolumeMounts();
		}
	}

	@Override
	public void astragoCoreDeploymentConnectPVC(List<AstragoDeploymentConnectPVC> missingPVCs) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<Volume> volumes = new ArrayList<>();
			for (AstragoDeploymentConnectPVC missingPVC : missingPVCs) {
				volumes.add(new VolumeBuilder()
					.withName(missingPVC.getVolumeName())
					.withPersistentVolumeClaim(
						new PersistentVolumeClaimVolumeSource(missingPVC.getPvcName(), false))
					.build());
			}
			client.apps()
				.deployments()
				.inNamespace("astrago")
				.withName("astrago-backend-core")
				.edit(d -> {
					if (missingPVCs.size() != 0) {
						PodSpecFluent<io.fabric8.kubernetes.api.model.PodTemplateSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>.SpecNested<io.fabric8.kubernetes.api.model.apps.DeploymentSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>>.ContainersNested<PodTemplateSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>.SpecNested<DeploymentSpecFluent<io.fabric8.kubernetes.api.model.apps.DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>>
							specNestedContainersNested = new DeploymentBuilder(d)
							.editSpec()
							.editOrNewTemplate()
							.editSpec()
							.addAllToVolumes(volumes)
							.editContainer(0);
						PodSpecFluent<PodTemplateSpecFluent<DeploymentSpecFluent<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>.SpecNested<DeploymentSpecFluent<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>>.ContainersNested<PodTemplateSpecFluent<DeploymentSpecFluent<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>.SpecNested<DeploymentSpecFluent<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>.TemplateNested<DeploymentFluent<DeploymentBuilder>.SpecNested<DeploymentBuilder>>>>
							depolymentBuilder = null;
						for (AstragoDeploymentConnectPVC pvc : missingPVCs) {
							depolymentBuilder = specNestedContainersNested.addNewVolumeMount()
								.withName(pvc.getVolumeName())
								.withMountPath(pvc.getHostPath())
								.endVolumeMount();
						}
						return depolymentBuilder.endContainer().endSpec().endTemplate().endSpec().build();
					}
					return d;
				});
		}
	}

	/**
	 * 스토리지 생성 - PVC 생성
	 */
	@Override
	public void createPVC(CreatePVC createPVC) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaimVO pvc = PersistentVolumeClaimVO.dtoToEntity(createPVC);
			PersistentVolumeClaim resource = (PersistentVolumeClaim)pvc.createResource();
			client.persistentVolumeClaims().resource(resource).create();
		}
	}

	/**
	 * 아스트라를 통해 만들어진 PVC가 맞는지 체크
	 * @param pvc
	 * @return
	 */
	private static boolean isVolumePVC(PersistentVolumeClaim pvc) {
		return pvc.getMetadata().getName().contains(ResourceType.VOLUME.getName());
	}

	/**
	 * pageVolumeResDTO 생성 메서드
	 * @param client
	 * @param pvc
	 * @return
	 */
	private PageVolumeResDTO createPageVolumeResDTO(KubernetesClient client, PersistentVolumeClaim pvc) {
		String volumeName = pvc.getMetadata().getName();
		boolean isUsed = checkUsedVolume(volumeName, client);

		String namespace = getNamespace(pvc);
		String workspaceName = getWorkspaceNameByMetaName(client, namespace);

		PageVolumeResDTO pageVolumeResDTO = PageVolumeResDTO.toDTO(pvc);
		pageVolumeResDTO.setIsUsed(isUsed);
		pageVolumeResDTO.setWorkspaceName(workspaceName);
		return pageVolumeResDTO;
	}

	/**
	 * 워크스페이스 메타 이름으로 워크스페이스 사용자가 설정한 이름 조회
	 * @param client
	 * @param namespace
	 * @return
	 */
	private static String getWorkspaceNameByMetaName(KubernetesClient client, String namespace) {
		return client.namespaces()
			.withName(namespace)
			.get()
			.getMetadata()
			.getAnnotations()
			.get(AnnotationField.NAME.getField());
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
	 * 자신이 생성한 볼륨이 맞는지 체크
	 * @param workspaceMetaName
	 * @param volumeMetaName
	 * @param client
	 * @param creator
	 */
	private static boolean volumeCreatorCheck(String workspaceMetaName, String volumeMetaName, KubernetesClient client,
		String creator) {
		PersistentVolumeClaim persistentVolumeClaim = client.persistentVolumeClaims()
			.inNamespace(workspaceMetaName)
			.withName(volumeMetaName).get();
		if (persistentVolumeClaim == null) {
			throw new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND);
		}
		String labelCreator = persistentVolumeClaim.getMetadata().getLabels().get(LabelField.CREATOR_ID.getField());

		return labelCreator.equals(creator);
	}

	@Override
	public void modifyVolumeByMetaName(ModifyVolumeDTO modifyVolumeDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			//본인이 생성한 볼륨인지 체크
			String creator = modifyVolumeDTO.getCreatorId();
			boolean chk = volumeCreatorCheck(modifyVolumeDTO.getWorkspaceMetaName(),
				modifyVolumeDTO.getVolumeMetaName(), client, creator);
			if (!chk) {
				throw new K8sException(VolumeErrorCode.VOLUME_FIX_FORBIDDEN);
			}
			Resource<PersistentVolumeClaim> persistentVolumeClaimResource = client.persistentVolumeClaims()
				.inNamespace(modifyVolumeDTO.getWorkspaceMetaName())
				.withName(modifyVolumeDTO.getVolumeMetaName());

			if (persistentVolumeClaimResource.get() == null || !isControlledByAstra(
				persistentVolumeClaimResource.get().getMetadata().getLabels())) {
				throw new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND);
			}
			persistentVolumeClaimResource
				.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
					.addToAnnotations(AnnotationField.NAME.getField(), modifyVolumeDTO.getName())
					.endMetadata()
					.build());
		}
	}

	@Override
	public void deleteVolumeByWorkspaceMetaNameAndVolumeMetaName(DeleteVolumeDTO deleteVolumeDTO) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			//본인이 생성한 볼륨인지 체크
			String creator = deleteVolumeDTO.getCreatorId();
			boolean chk = volumeCreatorCheck(deleteVolumeDTO.getWorkspaceMetaName(),
				deleteVolumeDTO.getVolumeMetaName(), client, creator);
			if (!chk)
				throw new K8sException(VolumeErrorCode.VOLUME_DELETE_FORBIDDEN);

			//삭제 전 사용중인지 확인해야함
			String volumeMetaName = deleteVolumeDTO.getVolumeMetaName();
			checkAndThrowIfInUse(() -> getDeploymentsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getStatefulSetsInUseVolume(volumeMetaName, client));
			checkAndThrowIfInUse(() -> getJobsInUseVolume(volumeMetaName, client));

			//삭제
			Resource<PersistentVolumeClaim> persistentVolumeClaimResource = client.persistentVolumeClaims()
				.inNamespace(deleteVolumeDTO.getWorkspaceMetaName())
				.withName(deleteVolumeDTO.getVolumeMetaName());
			if (persistentVolumeClaimResource.get() == null || !isControlledByAstra(
				persistentVolumeClaimResource.get().getMetadata().getLabels())) {
				throw new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND);
			}
			persistentVolumeClaimResource.delete();
		}
	}

	@Override
	public List<VolumeResDTO> findVolumesByWorkspaceMetaNameAndStorageMetaName(String workspaceMetaName,
		String storageMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
				.inNamespace(workspaceMetaName)
				.withLabel(LabelField.STORAGE_NAME.getField(), storageMetaName)
				.withLabel(LabelField.CONTROL_BY.getField(), ASTRA)
				.list()
				.getItems();
			return pvcs.stream().map(VolumeResDTO::toDTO).toList();
		}
	}

	@Override
	public List<PageVolumeResDTO> findVolumes(String option, String keyword) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<PersistentVolumeClaim> pvcs = getAllVolumes(client);

			return pvcs.stream()
				.filter(pvc -> isVolumePVC(pvc))
				.filter(pvc -> matchesSearchOption(pvc, option, keyword))
				.map(pvc -> createPageVolumeResDTO(client, pvc)).toList();
		}
	}

	@Override
	public VolumeWithWorkloadsResDTO findVolumeWithWorkloadsByMetaName(String workspaceMetaName,
		String volumeMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<String> workloadNames = new ArrayList<>();
			PersistentVolumeClaim pvc = client.persistentVolumeClaims()
				.inNamespace(workspaceMetaName)
				.withName(volumeMetaName)
				.get();
			if (pvc == null || !isControlledByAstra(pvc.getMetadata().getLabels())) {
				throw new K8sException(VolumeErrorCode.VOLUME_NOT_FOUND);
			}
			String requestVolume = getRequestVolume(pvc);
			StorageType storageType = getStorageType(pvc);
			//사용중인 statefulSets 조회
			List<StatefulSet> statefulSets = getStatefulSetsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(statefulSets, workloadNames);
			//사용중인 deployment 조회
			List<Deployment> deployments = getDeploymentsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(deployments, workloadNames);
			//사용중인 job 조회
			List<Job> jobs = getJobsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(jobs, workloadNames);

			String storageMetaName = pvc.getMetadata().getLabels().get(LabelField.STORAGE_NAME.getField());
			String storageName = getStorageClassName(client, storageMetaName);

			return VolumeWithWorkloadsResDTO.builder()
				.hasMetadata(pvc)
				.workloadNames(workloadNames)
				.requestVolume(requestVolume)
				.storageType(storageType)
				.storageClassName(storageName)
				.build();
		} catch (NullPointerException e) {
			log.error("volume not found {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("k8s cluster connect error {}", e.getMessage(), e);
			throw e;
		}
	}

	@Override
	public List<PageVolumeResDTO> findVolumesWithPagination(String workspaceMetaName, String option, String keyword) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
				.inNamespace(workspaceMetaName)
				.withLabel(LabelField.CONTROL_BY.getField(), ASTRA)
				.list()
				.getItems();

			return pvcs.stream()
				.filter(pvc -> matchesSearchOption(pvc, option, keyword))
				.map(pvc -> {
					String volumeName = pvc.getMetadata().getName();
					boolean isUsed = checkUsedVolume(volumeName, client);
					PageVolumeResDTO pageVolumeResDTO = PageVolumeResDTO.toDTO(pvc);
					pageVolumeResDTO.setIsUsed(isUsed);
					return pageVolumeResDTO;
				})
				// .sorted(Comparator.comparing(PageVolumeResDTO::getCreatedAt).reversed())
				.toList();
		}
	}

	/**
	 * 해당 볼륨을 사용중인 workload 주입
	 * @param resources
	 * @param workloadNames
	 */
	private void setWorkloadInUseVolume(List<? extends HasMetadata> resources, List<String> workloadNames) {
		for (HasMetadata resource : resources) {
			Map<String, String> annotations =
				resource.getMetadata().getAnnotations() == null ? null : resource.getMetadata().getAnnotations();
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
			throw new K8sException(VolumeErrorCode.VOLUME_NOT_DELETE_IN_USE);
		}
	}

	/**
	 * 해당 볼륨이 사용중인지 체크
	 * @param volumeMetaName
	 * @param client
	 * @return
	 */
	private boolean checkUsedVolume(String volumeMetaName, KubernetesClient client) {
		List<Job> jobsInUseVolume = getJobsInUseVolume(volumeMetaName, client);
		List<Deployment> deploymentsInUseVolume = getDeploymentsInUseVolume(volumeMetaName, client);
		List<StatefulSet> statefulSetsInUseVolume = getStatefulSetsInUseVolume(volumeMetaName, client);
		return !jobsInUseVolume.isEmpty() || !deploymentsInUseVolume.isEmpty() || !statefulSetsInUseVolume.isEmpty();
	}

	@Override
	public VolumeWithStorageResDTO findVolumeByMetaName(String volumeMetaName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			List<String> workloadNames = new ArrayList<>();
			List<PersistentVolumeClaim> pvcs = getAllVolumes(client);
			PersistentVolumeClaim persistentVolumeClaim = findPVCByMetaName(volumeMetaName, pvcs);

			String namespace = getNamespace(persistentVolumeClaim);
			String workspaceName = getWorkspaceNameByMetaName(client, namespace);

			String requestVolume = getRequestVolume(persistentVolumeClaim);
			StorageType storageType = getStorageType(persistentVolumeClaim);

			//사용중인 statefulSets 조회
			List<StatefulSet> statefulSets = getStatefulSetsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(statefulSets, workloadNames);
			//사용중인 deployment 조회
			List<Deployment> deployments = getDeploymentsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(deployments, workloadNames);
			//사용중인 job 조회
			List<Job> jobs = getJobsInUseVolume(volumeMetaName, client);
			setWorkloadInUseVolume(jobs, workloadNames);
			String storageMetaName = getStorageClassName(persistentVolumeClaim);
			String storageSavePath = getStorageSavePath(client, storageMetaName);
			String storageName = getStorageClassName(client, storageMetaName);
			return VolumeWithStorageResDTO.builder()
				.hasMetadata(persistentVolumeClaim)
				.workspaceName(workspaceName)
				.requestVolume(requestVolume)
				.storageType(storageType)
				.storageClassName(storageName)
				.workloadNames(workloadNames)
				.savePath(storageSavePath)
				.build();
		} catch (NullPointerException e) {
			log.error("volume not found {}", e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error("k8s cluster connect error {}", e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * 검색 조건 체크
	 * @param pvc
	 * @param option
	 * @param keyword
	 * @return
	 */
	private boolean matchesSearchOption(PersistentVolumeClaim pvc, String option, String keyword) {
		if (option == null || option.isBlank() || keyword == null || keyword.isBlank()) {
			return true;
		}
		Map<String, String> annotations = pvc.getMetadata().getAnnotations();

		if (option.equalsIgnoreCase(AnnotationField.CREATOR_USER_NAME.getField())) {
			return annotations.get(AnnotationField.CREATOR_USER_NAME.getField()).equalsIgnoreCase(keyword);
		} else if (option.equalsIgnoreCase(AnnotationField.NAME.getField())) {
			return annotations.get(AnnotationField.NAME.getField()).equalsIgnoreCase(keyword);
		}

		return false;
	}

	private boolean isControlledByAstra(Map<String, String> map) {
		return map != null && ASTRA.equals(map.get("control-by"));
	}

	@Override
	public PersistentVolumeClaim createIbmPvc(String storageName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
				.withNewMetadata()
				.withName("imb-pvc-" + UUID.randomUUID())
				.endMetadata()
				.withNewSpec()
				.withVolumeMode("Filesystem")
				.withAccessModes(List.of("ReadWriteOnce"))
				.withNewResources()
				.addToRequests("storage", new Quantity("1Gi"))
				.endResources()
				.withStorageClassName(storageName)
				.endSpec()
				.build();
			return client.persistentVolumeClaims().inNamespace("ibm").resource(persistentVolumeClaim).create();
		}
	}

	@Override
	public void deleteIbmPvc(String pvcName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			client.persistentVolumeClaims().inNamespace("ibm").withName(pvcName).delete();
		}
	}

	@Override
	public void createDellPVC(String pvcName, String storageName) {
		try (final KubernetesClient client = k8sAdapter.configServer()) {
			PersistentVolumeClaim pvc = new PersistentVolumeClaim().toBuilder()
				.withNewMetadata()
				.withName(pvcName)
				.withNamespace("astrago")
				.endMetadata()
				.withNewSpec()
				.withAccessModes(List.of("ReadWriteMany"))
				.withVolumeMode("Filesystem")
				.withNewResources()
				.addToRequests("storage", new Quantity("50Gi"))
				.endResources()
				.withStorageClassName(storageName)
				.endSpec()
				.build();
			client.persistentVolumeClaims().inNamespace("astrago").resource(pvc).create();
		}
	}
}
