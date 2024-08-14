package com.xiilab.modulek8s.storage.facade;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.TestConfiguration;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.ProvisionerType;
import com.xiilab.modulek8s.common.enumeration.ReclaimPolicyType;
import com.xiilab.modulek8s.config.K8sAdapter;
import com.xiilab.modulek8s.facade.dto.CreateLocalDatasetDTO;
import com.xiilab.modulek8s.facade.dto.CreateStorageReqDTO;
import com.xiilab.modulek8s.facade.dto.SecretDTO;
import com.xiilab.modulek8s.facade.storage.StorageModuleServiceImpl;
import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeServiceImpl;
import com.xiilab.modulek8s.node.dto.ResponseDTO;
import com.xiilab.modulek8s.node.enumeration.ScheduleType;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Chart;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmReleaseSpec;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Install;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.SourceRef;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Spec;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.Conditions;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.HelmReleaseStatus;
import com.xiilab.modulek8s.storage.storageclass.vo.StorageClassVO;
import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeBuilder;
import io.fabric8.kubernetes.api.model.NodeSelectorRequirementBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.api.model.ObjectReference;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.SecretBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinitionBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.fabric8.kubernetes.api.model.storage.StorageClassBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ContextConfiguration(classes = TestConfiguration.class)
class StorageModuleServiceImplTest {
	private static final Logger log = LoggerFactory.getLogger(StorageModuleServiceImplTest.class);

	@Autowired
	private K8sAdapter k8sAdapter;
	@Autowired
	private StorageModuleServiceImpl storageModuleServiceImpl;
	@Autowired
	private WorkloadModuleFacadeServiceImpl workloadModuleFacadeService;
	@MockBean
	private ObjectMapper objectMapper;

	// @Test
	// void getStorageClasses() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		client.storage().v1().storageClasses().list().getItems()
	// 			.forEach(sc -> log.info(sc.getMetadata().getName()));
	// 	}
	// }
	//
	// @Test
	// void getStorageClasseByLabel() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		client.storage().v1().storageClasses().withLabel("storage-repositoryType", "NFS3").list().getItems().get(0);
	// 	}
	// }
	//
	// @Test
	// void getCSIDrivers() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		client.storage().v1().csiDrivers().list().getItems()
	// 			.forEach(sc -> log.info(" - {}", sc.getMetadata().getName()));
	// 	}
	// }
	//
	// @Test
	// void getHelmStatusByRelease() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		// final OpenShiftClient openShiftClient = client.adapt(OpenShiftClient.class);
	// 		// HelmChartRepositoryList list = openShiftClient.helmChartRepositories().list();
	// 		// System.out.println(list.getItems().size());
	// 	}
	// }
	//
	// @Test
	// void getNSByWorkspaceName() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		String namespaceName = client.namespaces()
	// 			.withLabel("workspace-name", "ws1")
	// 			.list()
	// 			.getItems()
	// 			.get(0)
	// 			.getMetadata()
	// 			.getName();
	// 	}
	// }
	//
	// @Test
	// void getAllNS() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		NamespaceList namespaceList = client.namespaces().list();
	//
	// 	}
	//
	// }
	//
	// @Test
	// void createVolume() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
	// 			.withNewMetadata()
	// 			.withName("testpvc1")
	// 			.withNamespace("yc-test-ns")
	// 			.addToAnnotations("volume-name", "한글 띄어쓰기asdfasd#$@#$")
	// 			.addToAnnotations("volume-created", "이용춘")
	// 			.endMetadata()
	// 			.withNewSpec()
	// 			.withStorageClassName("nfs-csi")
	// 			.withAccessModes("ReadWriteMany")
	// 			.withNewResources()
	// 			.addToRequests("storage", new Quantity("5Gi"))
	// 			.endResources()
	// 			.endSpec()
	// 			.build();
	//
	// 		client.persistentVolumeClaims().resource(persistentVolumeClaim).create();
	// 	}
	// }
	//
	// @Test
	// void getVolumesByNamespace() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<PersistentVolumeClaim> items = client.persistentVolumeClaims()
	// 			.inNamespace("yc-test-ns")
	// 			.list()
	// 			.getItems();
	// 		for (PersistentVolumeClaim persistentVolumeClaim : items) {
	// 			System.out.println(persistentVolumeClaim);
	// 		}
	//
	// 	}
	// }
	//
	// @Test
	// void getVolumesByMetaName() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		PersistentVolumeClaim pvc = client.persistentVolumeClaims()
	// 			.inNamespace("")
	// 			.withName("vo-422e4d40-3500-47df-ba74-b5851ab33eff")
	// 			.get();
	//
	// 		VolumeWithWorkloadsResDTO build = VolumeWithWorkloadsResDTO.builder()
	// 			.hasMetadata(pvc)
	// 			.workloadNames(List.of("asdf", "sdfsdf"))
	// 			.build();
	// 		System.out.println(pvc);
	// 	}
	// }
	//
	// @Test
	// void getAllResourceByLabels() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		client.apps()
	// 			.statefulSets()
	// 			.withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff", "true")
	// 			.list()
	// 			.getItems()
	// 			.get(0)
	// 			.getMetadata()
	// 			.getAnnotations()
	// 			.get("name");
	// 		client.apps()
	// 			.deployments()
	// 			.withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff", "true")
	// 			.list()
	// 			.getItems()
	// 			.get(0)
	// 			.getMetadata()
	// 			.getAnnotations()
	// 			.get("name");
	// 		client.batch()
	// 			.v1()
	// 			.jobs()
	// 			.withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff", "true")
	// 			.list()
	// 			.getItems()
	// 			.get(0)
	// 			.getMetadata()
	// 			.getAnnotations()
	// 			.get("name");
	//
	// 	}
	// }
	//
	// @Test
	// void updateVolume() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		// client.persistentVolumeClaims().
	// 	}
	// }
	//
	// @Test
	// void createVolumeService() {
	// 	CreateVolumeDTO request = CreateVolumeDTO.builder()
	// 		.storageType(StorageType.NFS)
	// 		.requestVolume(5)
	// 		.name("vo1user123")
	// 		.workspaceMetaDataName("yc-test-ns")
	// 		.build();
	//
	// 	storageModuleServiceImpl.createVolume(request);
	// }
	//
	// @Test
	// void findVolumeWithWorkloadsByMetaName() {
	// 	VolumeWithWorkloadsResDTO result = storageModuleServiceImpl.findVolumeWithWorkloadsByMetaName("yc-test-ns",
	// 		"vo-422e4d40-3500-47df-ba74-b5851ab33eff");
	// 	System.out.println(result);
	// }
	//
	// @Test
	// void 볼륨수정기능() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		PersistentVolumeClaim edit = client.persistentVolumeClaims()
	// 			.inNamespace("yc-test-ns")
	// 			.withName("vo-422e4d40-3500-47df-ba74-b5851ab33eff")
	// 			.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
	// 				.addToAnnotations("name", "수정된 이름입니당")
	// 				.endMetadata()
	// 				.editSpec()
	// 				.editResources()
	// 				.addToRequests("storage", new Quantity("11Gi"))
	// 				.endResources()
	// 				.endSpec()
	// 				.editStatus()
	// 				.addToCapacity("storage", new Quantity("11Gi"))
	// 				.endStatus()
	// 				.build());
	// 	}
	// }
	//
	// @Test
	// void 볼륨삭제기능() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		client.persistentVolumeClaims().inNamespace("yc-test-ns").withName("testtest").delete();
	//
	// 	}
	// }
	//
	// @Test
	// void 워크스페이스명으로볼륨조회() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
	// 			.inNamespace("yc-test-ns")
	// 			.withLabel(LabelField.STORAGE_TYPE.getField(), "NFS")
	// 			.list()
	// 			.getItems();
	// 		List<VolumeResDTO> collect = pvcs.stream().map(VolumeResDTO::toDTO).collect(Collectors.toList());
	//
	// 	}
	// }
	//
	// @Test
	// void 볼륨조회페이징검색포함() {
	// 	String option = "creator-user-name";
	// 	String workspaceMetaName = "yc-test-ns";
	// 	String keyword = "서준오";
	// 	String searchOption = "";
	// 	if (option.equalsIgnoreCase(AnnotationField.CREATOR_FULL_NAME.getField())) {
	// 		searchOption = AnnotationField.CREATOR_FULL_NAME.getField();
	// 	} else if (option.equalsIgnoreCase(AnnotationField.NAME.getField())) {
	// 		searchOption = AnnotationField.NAME.getField();
	// 	}
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
	// 			.inNamespace(workspaceMetaName)
	// 			.list()
	// 			.getItems();
	// 		String finalSearchOption = searchOption;
	// 		List<PageVolumeResDTO> collect = pvcs.stream()
	// 			.filter(pvc -> pvc.getMetadata().getAnnotations().get(finalSearchOption).equalsIgnoreCase(keyword))
	// 			.map(pvc -> {
	// 				String volumeName = pvc.getMetadata().getName();
	// 				boolean isUsed = checkUsedVolume(volumeName, client);
	// 				PageVolumeResDTO pageVolumeResDTO = PageVolumeResDTO.toDTO(pvc);
	// 				pageVolumeResDTO.setIsUsed(isUsed);
	// 				return pageVolumeResDTO;
	// 			})
	// 			.collect(Collectors.toList());
	// 		System.out.println(collect.size());
	// 	}
	// }
	//
	// @Test
	// void 전체네임스페이스의볼륨조회() {
	// 	List<String> workloadNames = new ArrayList<>();
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
	// 			.inAnyNamespace()
	// 			.list()
	// 			.getItems();
	// 		PersistentVolumeClaim persistentVolumeClaim = pvcs.stream()
	// 			.filter(pvc -> pvc.getMetadata().getName().equals("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89"))
	// 			.findFirst()
	// 			.orElseThrow(() -> new RuntimeException("볼륨이 존재하지 않습니다."));
	//
	// 		String namespace = persistentVolumeClaim.getMetadata().getNamespace();
	// 		String workspaceName = client.namespaces()
	// 			.withName(namespace)
	// 			.get()
	// 			.getMetadata()
	// 			.getAnnotations()
	// 			.get(AnnotationField.NAME.getField());
	//
	// 		//사용중인 statefulSets 조회
	// 		List<StatefulSet> statefulSets = getStatefulSetsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89",
	// 			client);
	// 		setWorkloadInUseVolume(statefulSets, workloadNames);
	// 		//사용중인 deployment 조회
	// 		List<Deployment> deployments = getDeploymentsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89", client);
	// 		setWorkloadInUseVolume(deployments, workloadNames);
	// 		//사용중인 job 조회
	// 		List<Job> jobs = getJobsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89", client);
	// 		setWorkloadInUseVolume(jobs, workloadNames);
	//
	// 		//sc
	// 		String storageSavePath = client.storage()
	// 			.v1()
	// 			.storageClasses()
	// 			.withName("nfs-csi")
	// 			.get()
	// 			.getParameters()
	// 			.get("share");
	//
	// 		VolumeWithWorkloadsResDTO dto = VolumeWithWorkloadsResDTO.builder()
	// 			.hasMetadata(persistentVolumeClaim)
	// 			.workspaceName(workspaceName)
	// 			.workloadNames(workloadNames)
	// 			.requestVolume(persistentVolumeClaim.getSpec().getResources().getRequests().get("storage").toString())
	// 			.storageType(StorageType.valueOf(
	// 				persistentVolumeClaim.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
	// 			.build();
	//
	// 		System.out.println(dto);
	// 	}
	// }
	//
	// @Test
	// void 관리자_볼륨상세보기() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
	// 			.inAnyNamespace()
	// 			.list()
	// 			.getItems();
	// 		String namespace = "ws-uuid123";
	// 		String workspaceName = client.namespaces()
	// 			.withName(namespace)
	// 			.get()
	// 			.getMetadata()
	// 			.getAnnotations()
	// 			.get(AnnotationField.NAME.getField());
	// 		System.out.println(pvcs.size());
	// 	}
	// }
	//
	// @Test
	// void 관리자_볼륨삭제() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		//삭제
	// 		PersistentVolumeClaim pvc = new PersistentVolumeClaimBuilder().withNewMetadata()
	// 			.withName("vo-bd72cad5-65f7-42c9-a16f-9c924a3c5219")
	// 			.endMetadata()
	// 			.build();
	//
	// 		client.persistentVolumeClaims().inAnyNamespace().resource(pvc).delete();
	// 	}
	// }
	//
	// @Test
	// void 관리자_볼륨수정() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	//
	// 		Resource<PersistentVolumeClaim> persistentVolumeClaimResource = client.persistentVolumeClaims()
	// 			.inAnyNamespace()
	// 			.resources()
	// 			.filter(pvcr -> pvcr.get().getMetadata().getName().equals("vo-619ccb4a-f1bb-4ba7-962a-691d94554214"))
	// 			.findFirst()
	// 			.orElseThrow(() -> new RuntimeException("볼륨이 존재하지않습니다."));
	//
	// 		persistentVolumeClaimResource.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
	// 			.addToAnnotations(AnnotationField.NAME.getField(), "수정한 이름이지롱")
	// 			.endMetadata()
	// 			.build());
	// 	}
	// }
	//
	// @Test
	// void 스토리지클래스생성() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		String name = "내가생성한 스토리지 클래스 이름";
	// 		String description = "설명이지요";
	// 		ProvisionerType provisioner = ProvisionerType.NFS;
	// 		HashMap<String, String> parameters = new HashMap<>();
	// 		parameters.put("server", "10.61.3.19");
	// 		parameters.put("share", "/kube-storage");
	// 		LocalDateTime createdAt = LocalDateTime.now();
	// 		String creatorName = "이용춘";
	// 		String creator = "test1";
	//
	// 		StorageClassVO storageClassVO = StorageClassVO.builder()
	// 			.name(name)
	// 			.description(description)
	// 			.storageType(StorageType.NFS)
	// 			.provisioner(provisioner)
	// 			.parameters(parameters)
	// 			.reclaimPolicy(ReclaimPolicyType.DELETE)
	// 			.createdAt(createdAt)
	// 			.creatorFullName(creatorName)
	// 			.creatorUserName(creator)
	// 			.build();
	//
	// 		StorageClass resource = (StorageClass)storageClassVO.createResource();
	// 		client.storage().v1().storageClasses().resource(resource).create();
	// 	}
	// }
	//
	// @Test
	// void 관리자_스토리지클래스_수정() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		final StorageClass updatedStorageClass = client.storage()
	// 			.v1()
	// 			.storageClasses()
	// 			.withName("nfs-csi")
	// 			.edit(
	// 				s -> new StorageClassBuilder(s).editMetadata()
	// 					.addToAnnotations(AnnotationField.NAME.getField(), "수정 이름")
	// 					.addToAnnotations(AnnotationField.DESCRIPTION.getField(), "설명 수정")
	// 					.addToLabels(LabelField.STORAGE_TYPE.getField(), "NFS")
	// 					.endMetadata()
	// 					.build());
	// 		System.out.println(updatedStorageClass);
	// 	}
	// }
	//
	// @Test
	// void NFS설치유무_확인() {
	// 	//app.kubernetes.io/name=csi-driver-nfs
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> nfsClient = client.resources(
	// 			HelmRelease.class);
	// 		String storageType = "NFS";
	// 		List<HelmRelease> items = nfsClient.inAnyNamespace()
	// 			.withLabel(LabelField.STORAGE_TYPE.getField(), storageType)
	// 			.list().getItems();
	// 		System.out.println(items);
	// 	}
	// }
	//
	// @Test
	// void NFS설치() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		ObjectMeta objectMeta = new ObjectMetaBuilder()
	// 			.withName("csi-" + UUID.randomUUID()) //pr-uuid
	// 			.addToAnnotations(AnnotationField.NAME.getField(), "자바단에서 만든 플로그인")
	// 			.addToLabels(LabelField.STORAGE_TYPE.getField(), "NFS")
	// 			.build();
	// 		HelmRelease helmRelease = new HelmRelease();
	// 		helmRelease.setMetadata(objectMeta);
	//
	// 		SourceRef sourceRef = SourceRef.builder()
	// 			.kind("HelmRepository")
	// 			.name("nfs-helmrepository")
	// 			.build();
	//
	// 		Spec spec = Spec.builder()
	// 			.chart("csi-driver-nfs")
	// 			.sourceRef(sourceRef)
	// 			.build();
	//
	// 		Chart chart = Chart.builder()
	// 			.spec(spec)
	// 			.build();
	//
	// 		Install install = Install.builder()
	// 			.createNamespace(true)
	// 			.build();
	//
	// 		HelmReleaseSpec helmReleaseSpec = HelmReleaseSpec.builder()
	// 			.chart(chart)
	// 			.interval("1m0s")
	// 			.install(install)
	// 			.releaseName("csi")
	// 			.storageNamespace("csi")
	// 			.targetNamespace("csi")
	// 			.build();
	//
	// 		helmRelease.setSpec(helmReleaseSpec);
	// 		MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
	// 			HelmRelease.class);
	//
	// 		helmClient.inNamespace("csi").resource(helmRelease).create();
	//
	// 	}
	// }
	//
	// @Test
	// void unInstallCSI() {
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		MixedOperation<HelmRelease, KubernetesResourceList<HelmRelease>, Resource<HelmRelease>> helmClient = client.resources(
	// 			HelmRelease.class);
	// 		helmClient.inNamespace("csi").withLabel(LabelField.STORAGE_TYPE.getField(), "NFS")
	// 			.delete();
	// 	}
	// }
	//
	// private boolean checkUsedVolume(String volumeMetaName, KubernetesClient client) {
	// 	List<Job> jobsInUseVolume = getJobsInUseVolume(volumeMetaName, client);
	// 	List<Deployment> deploymentsInUseVolume = getDeploymentsInUseVolume(volumeMetaName, client);
	// 	List<StatefulSet> statefulSetsInUseVolume = getStatefulSetsInUseVolume(volumeMetaName, client);
	// 	return !jobsInUseVolume.isEmpty() || !deploymentsInUseVolume.isEmpty() || !statefulSetsInUseVolume.isEmpty();
	// }
	//
	// private static List<Job> getJobsInUseVolume(String volumeMetaName, KubernetesClient client) {
	// 	return client.batch().v1().jobs().withLabelIn(volumeMetaName, "true")
	// 		.list()
	// 		.getItems();
	// }
	//
	// /**
	//  * 해당 볼륨을 사용중인 Deployment list 조회
	//  * @param volumeMetaName
	//  * @param client
	//  * @return
	//  */
	// private static List<Deployment> getDeploymentsInUseVolume(String volumeMetaName, KubernetesClient client) {
	// 	return client.apps().deployments().withLabelIn(volumeMetaName, "true")
	// 		.list()
	// 		.getItems();
	// }
	//
	// /**
	//  * 해당 볼륨을 사용중인 StatefulSet list 조회
	//  * @param volumeMetaName
	//  * @param client
	//  * @return
	//  */
	// private static List<StatefulSet> getStatefulSetsInUseVolume(String volumeMetaName, KubernetesClient client) {
	// 	return client
	// 		.apps()
	// 		.statefulSets()
	// 		.withLabelIn(volumeMetaName, "true")
	// 		.list()
	// 		.getItems();
	// }
	//
	// /**
	//  * 해당 볼륨을 사용중인 workload 주입
	//  * @param resources
	//  * @param workloadNames
	//  */
	// private void setWorkloadInUseVolume(List<? extends HasMetadata> resources, List<String> workloadNames) {
	// 	for (HasMetadata resource : resources) {
	// 		Map<String, String> annotations =
	// 			resource.getMetadata().getAnnotations() == null ? null : resource.getMetadata().getAnnotations();
	// 		if (annotations != null) {
	// 			String name = annotations.get(AnnotationField.NAME.getField());
	// 			if (name != null) {
	// 				workloadNames.add(name);
	// 			}
	// 		}
	// 	}
	// }
	//
	// @Test
	// @DisplayName("관리자 스토리지 생성")
	// void createStorage() throws IOException {
	// 	//1. host에 스토리지 path 디렉토리 생성
	// 	Path hostPath = Paths.get("/Users/leeyoungchun/kube-storage/storage1");
	// 	Files.createDirectories(hostPath);
	// 	//2. nfs pv 생성
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		String pvcName = "astrago-pvc-"+ UUID.randomUUID().toString().substring(6);
	// 		String pvName = "astrago-pv-"+ UUID.randomUUID().toString().substring(6);
	// 		String deploymentName = "astrago-deployment-"+ UUID.randomUUID().toString().substring(6);
	// 		String volumeLabelSelector = "storage-volume-"+ UUID.randomUUID().toString().substring(6);
	//
	// 		ObjectReference objectReference = new ObjectReference().toBuilder().build();
	// 		objectReference.setKind("PersistentVolumeClaim");
	// 		objectReference.setApiVersion("v1");
	// 		objectReference.setNamespace("astrago");
	// 		objectReference.setName(pvcName);
	//
	// 		PersistentVolume pv = new PersistentVolumeBuilder()
	// 			.withNewMetadata().withName(pvName).endMetadata()
	// 			.withNewSpec()
	// 			.addToCapacity(Collections.singletonMap("storage", new Quantity("50Gi")))
	// 			.withAccessModes("ReadWriteMany")
	// 			.withPersistentVolumeReclaimPolicy("Retain")
	// 			.withNewNfs()
	// 			.withServer("10.61.3.2")
	// 			.withPath("/kube_storage/")
	// 			.endNfs()
	// 			.withClaimRef(objectReference)
	// 			.endSpec()
	// 			.build();
	// 		client.persistentVolumes().resource(pv).create();
	//
	// 		//3. pvc 생성
	// 		PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
	// 			.withNewMetadata()
	// 			.withName(pvcName)
	// 			.withNamespace("astrago")
	// 			.endMetadata()
	// 			.withNewSpec()
	// 			.withAccessModes("ReadWriteMany")
	// 			.withNewResources()
	// 			.addToRequests("storage", new Quantity("50Gi"))
	// 			.endResources()
	// 			.endSpec()
	// 			.build();
	//
	// 		client.persistentVolumeClaims().resource(persistentVolumeClaim).create();
	//
	// 		//4. connect test pod 생성 후 pvc 연결 테스트
	// 		Volume vol = new VolumeBuilder()
	// 			.withName(volumeLabelSelector)
	// 			.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(pvcName, false))
	// 			.build();
	//
	// 		Deployment deployment = new DeploymentBuilder()
	// 			.withNewMetadata()
	// 			.withName(deploymentName)
	// 			.withNamespace("astrago")
	// 			.endMetadata()
	//
	// 			.withNewSpec()
	// 			.withReplicas(3)
	// 			.withNewSelector()
	// 			.withMatchLabels(Map.of("app","connect-test"))
	// 			.endSelector()
	//
	// 			.withNewTemplate()
	// 			.withNewMetadata()
	// 			.addToLabels("app", "connect-test")
	// 			.endMetadata()
	//
	// 			.withNewSpec()
	// 			.addAllToVolumes(List.of(vol))
	//
	// 			.addNewContainer()
	// 			.withName("nginx")
	// 			.withImage("nginx:1.14.2")
	//
	// 			.addNewVolumeMount()
	// 			.withName(volumeLabelSelector)
	// 			.withMountPath(String.valueOf(hostPath))
	// 			.endVolumeMount()
	// 			.endContainer()
	// 			.endSpec()
	// 			.endTemplate()
	// 			.endSpec()
	// 			.build();
	// 			client.resource(deployment).create();
	//
	// 		Thread.sleep(5000);
	//
	// 		Pod connectPod = client.pods().inNamespace("astrago").withLabel("app", "connect-test").list().getItems().get(0);
	// 		// Pod connectPod = client.pods().inNamespace("astrago").withName("astrago-deployment-7f-6a3d-40b4-9753-5be8619a6e41-6bb858b8tnc79").get();
	// 		//5. 파드 상태값 조회
	// 		List<PodCondition> conditions = connectPod.getStatus().getConditions();
	// 		boolean isAvailable = false;
	// 		for (PodCondition condition : conditions) {
	// 			String status = condition.getStatus();
	// 			isAvailable = "true".equalsIgnoreCase(status) ? true : false;
	// 			if(!isAvailable){
	// 				break;
	// 			}
	// 		}
	// 		//connection 실패
	// 		if(!isAvailable){
	// 			//pvc, pv, connect deployment 삭제
	// 			client.apps().deployments().inNamespace("astrago").withName(deploymentName).delete();
	// 			client.persistentVolumeClaims().inNamespace("astrago").withName(pvcName).delete();
	// 			client.persistentVolumes().withName(pvName).delete();
	// 			//연결 실패 응답
	// 		}
	// 		//connection 성공
	// 		//connect deployment 삭제 -> 스토리지 정보 디비 저장
	// 		client.apps().deployments().inNamespace("astrago").withName(deploymentName).delete();
	//
	// 		//아스트라고 deployment mount edit
	// 		client.apps().deployments().inNamespace("astrago").withName("astrago-deployment-19-d79f-4c48-8870-f4dd661b7cc4")
	// 			.edit(d -> new DeploymentBuilder(d)
	// 				.editSpec()
	// 				.editOrNewTemplate()
	// 				.editSpec()
	// 				.addAllToVolumes(List.of(vol))
	// 				.editContainer(0)
	// 				.addNewVolumeMount()
	// 				.withName(volumeLabelSelector)
	// 				.withMountPath("/kube_storage/test2/")
	// 				.endVolumeMount()
	// 				.endContainer()
	// 				.endSpec()
	// 				.endTemplate()
	// 				.endSpec()
	// 				.build());
	// 	} catch (InterruptedException e) {
	// 		throw new RuntimeException(e);
	// 	}
	// }
	//
	// @Test
	// @DisplayName("스토리지 생성 테스트")
	// void createStorageTest(){
	// 	CreateStorageReqDTO createStorageReqDTO = CreateStorageReqDTO.builder()
	// 		.storageName("storage1")
	// 		.description("설명어어엉")
	// 		.storageType(StorageType.NFS)
	// 		.ip("10.61.3.2")
	// 		.storagePath("/kube_storage/")
	// 		.namespace("astrago")
	// 		.astragoDeploymentName("astrago-backend-core")
	// 		.hostPath(System.getProperty("user.home"))
	// 		.requestVolume(50)
	// 		.build();
	// 	storageModuleServiceImpl.createStorage(createStorageReqDTO);
	// }
	//
	// @Test
	// @DisplayName("스토리지 삭제 테스트")
	// void deleteStorageTest(){
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		String pvcName = "astrago-pvc-93-3b8f-4524-bd86-634ee18e9741";
	// 		String volName = "storage-volume-f3-ec9c-429d-bf88-7a08311a0b80";
	// 		String hostPath = "/Users/leeyoungchun";
	// 		Volume vol = new VolumeBuilder()
	// 			.withName(volName)
	// 			.withPersistentVolumeClaim(new PersistentVolumeClaimVolumeSource(pvcName, null))
	// 			.build();
	//
	// 		client.apps().deployments().inNamespace("astrago").withName("astrago-backend-core")
	// 			.edit(d -> new DeploymentBuilder(d)
	// 				.editSpec()
	// 				.editOrNewTemplate()
	// 				.editSpec()
	// 				.removeFromVolumes(vol)
	// 				.editContainer(0)
	// 				.removeFromVolumeMounts(new VolumeMount(hostPath, null, volName, null, null, null))
	// 				.endContainer()
	// 				.endSpec()
	// 				.endTemplate()
	// 				.endSpec()
	// 				.build());
	//
	// 	}
	// }
	//
	// @Test
	// @DisplayName("데이터 셋 deployment 생성")
	// void createDatasetDeployment(){
	// 	CreateLocalDatasetDTO createLocalDatasetDTO = CreateLocalDatasetDTO.builder()
	// 		.datasetName("dataname123")
	// 		.namespace("astrago")
	// 		.ip("10.61.3.2")
	// 		.storagePath("/kube_storage/")
	// 		.build();
	// 	workloadModuleFacadeService.createLocalDataset(createLocalDatasetDTO);
	// }
	// @Test
	// @DisplayName("데이터 셋 deployment 조회")
	// void getDeployment(){
	// 	try (final KubernetesClient client = k8sAdapter.configServer()) {
	// 		Deployment deployment = client.apps()
	// 			.deployments()
	// 			.inNamespace("astrago")
	// 			.withName("nginx-deployment")
	// 			.get();
	//
	// 		List<ReplicaSet> replicaSets = client.apps().replicaSets().inNamespace("astrago").list().getItems();
	//
	// 		ReplicaSet replicaSet = replicaSets.stream()
	// 			.parallel()
	// 			.filter(replicaSet1 -> replicaSet1.getMetadata().getAnnotations().get("name") != null &&
	// 				replicaSet1.getMetadata().getAnnotations().get("name").equalsIgnoreCase("dataset_test1"))
	// 			.findAny()
	// 			.orElseThrow(() -> new RuntimeException(""));
	//
	// 		// replicaSet.getMetadata().getOwnerReferences().get(0).getName()
	// 		System.out.println(deployment);
	// 	}
	// }
	// @Test
	// @DisplayName("node 목록 조회")
	// void 노드목록조회(){
	// 	// String gpuName = "nvidia.com/gpu.product";
	// 	// String gpuCount = "nvidia.com/gpu.count";
	// 	// String address = "projectcalico.org/IPv4Address";
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<Pod> pods = client.pods().list().getItems();
	// 		for (Pod pod : pods) {
	// 			if(pod.getSpec().getNodeName() != null &&
	// 				pod.getSpec().getNodeName().equals("worker-27") &&
	// 				pod.getStatus().getPhase().equals("Running")){
	// 				Quantity gpuQuantity = pod.getSpec()
	// 					.getContainers()
	// 					.get(0)
	// 					.getResources()
	// 					.getRequests()
	// 					.get("nvidia.com/gpu");
	// 				Quantity sharedGpuQuantity = pod.getSpec()
	// 					.getContainers()
	// 					.get(0)
	// 					.getResources()
	// 					.getRequests()
	// 					.get("nvidia.com/gpu.shared");
	// 				boolean usedCheck = false;
	// 				if(gpuQuantity != null){
	// 					int gpuCount = Integer.parseInt(gpuQuantity
	// 						.getAmount());
	// 					if (gpuCount > 0) {
	// 						usedCheck = true;
	// 					}
	// 				}else if(sharedGpuQuantity != null){
	// 					int sharedGpuCount = Integer.parseInt(sharedGpuQuantity
	// 						.getAmount());
	// 					if(sharedGpuCount > 0){
	// 						usedCheck = true;
	// 					}
	// 				}
	// 				if(usedCheck){
	// 					System.out.println("사용중임");
	// 					break;
	// 				}
	// 			}
	// 		}
	// 	}
	// }
	// @Test
	// @DisplayName("노드 스케쥴 설정")
	// void nodeSchedule(){
	// 	String resourceName = "worker-27";
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		Node node = client.nodes().withName(resourceName).get();
	// 		NodeBuilder nodeBuilder = new NodeBuilder().editMetadata().addToLabels("mps_status", "COMPLETE").endMetadata();
	// 		node.edit().withNewMetadata().addToLabels("mps_status", "COMPLETE").endMetadata().build();
	// 		Node build = node.edit().editMetadata().addToLabels("mps_status", "COMPLETE").endMetadata().build();
	// 		node.setMetadata(build.getMetadata());
	// 	}
	// }
	//
	// @Test
	// void test1(){
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		RollableScalableResource<Deployment> deployment = client.apps()
	// 			.deployments()
	// 			.inNamespace("astrago")
	// 			.withName("astrago-backend-core");
	// 		List<VolumeMount> volumeMounts = deployment.get()
	// 			.getSpec()
	// 			.getTemplate()
	// 			.getSpec()
	// 			.getContainers()
	// 			.get(0)
	// 			.getVolumeMounts();
	// 		for (VolumeMount volumeMount : volumeMounts) {
	// 			System.out.println(volumeMount.getName());
	// 		}
	// 	}
	// }
	//
	// @Test
	// void insertIbmSec(){
	// 	SecretDTO secretDTO = SecretDTO.builder()
	// 		.secretName("test-secret")
	// 		.userName("testUserName")
	// 		.password("testPassword")
	// 		.build();
	//
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		Secret secret = new SecretBuilder()
	// 			.withNewMetadata()
	// 			.withName(secretDTO.getSecretName())
	// 			.withNamespace("ibm")
	// 			.endMetadata()
	// 			.withType("Opaque")
	// 			.addToData("username", secretDTO.getUserName())
	// 			.addToData("password", Base64.getEncoder()
	// 				.encodeToString(secretDTO.getPassword().getBytes(StandardCharsets.UTF_8)))
	// 			.build();
	// 		client.secrets()
	// 			.resource(secret)
	// 			.serverSideApply();
	// 		Secret ibmSecret = client.secrets().inNamespace("ibm").withName(secret.getMetadata().getName()).get();
	//
	// 		if(ibmSecret.getMetadata().getName().equals(secretDTO.getSecretName())){
	// 			client.secrets().inNamespace("ibm").withName(secret.getMetadata().getName()).delete();
	// 		}
	// 	}
	// }
	//
	// @Test
	// void createIbmStorage(){
	// 	StorageClass storageClass = new StorageClassBuilder()
	// 		.withNewMetadata()
	// 		.withName("ibm-block-" + "test-storage")
	// 		.endMetadata()
	// 		.withProvisioner("block.csi.ibm.com")
	// 		.addToParameters("pool", "demo-pool")
	// 		.addToParameters("SpaceEfficiency", "thin")
	// 		.addToParameters("virt_snap_func", "false")
	// 		.addToParameters("csi.storage.k8s.io/fstype", "xfs")
	// 		.addToParameters("csi.storage.k8s.io/secret-name", secretName)
	// 		.addToParameters("csi.storage.k8s.io/secret-namespace", "default")
	// 		.withAllowVolumeExpansion(true)
	// 		.build();
	// }
	//
	// @Test
	// void test2(){
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		Job job = client.batch().v1().jobs().inAnyNamespace()
	// 			.withLabel("app", "wl-e8a6df95-163f-4f4b-9231-f2bf292b9723")
	// 			// .withLabel("app", "wl-982fe371-a3c2-4ec4-bf97-da1164ae2ad4-7675559c8f-vj7wf")
	// 			.list().getItems().get(0);
	// 		String label = job
	// 			.getMetadata().getLabels().get("app");
	//
	// 		List<Pod> pods = client.pods().inAnyNamespace().withLabel("app", label).list().getItems();
	// 		String nodeName = "";
	// 		for (Pod pod : pods) {
	// 			boolean isRunning = pod.getStatus().getPhase().equalsIgnoreCase("Running");
	// 			if(isRunning){
	// 				nodeName = pod.getSpec().getNodeName();
	// 				break;
	// 			}
	// 		}
	// 		Node node = client.nodes().withName(nodeName).get();
	// 		String migCapable = node.getMetadata().getLabels().get("nvidia.com/mig.capable");
	// 		String mpsCapable = node.getMetadata().getLabels().get("nvidia.com/mps.capable");
	// 		int memory = 0;
	// 		String gpuName = "";
	// 		if(Boolean.valueOf(migCapable)){ //mig
	// 			String strategy = node.getMetadata().getLabels().get("nvidia.com/mig.strategy");
	// 			//single
	// 			if(strategy.equalsIgnoreCase("single")){
	// 				memory = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.memory"));
	// 			}else{
	// 			//mixed
	// 				//db에서 gpuName 조회 후 .memory 문자열 합쳐서 라벨 검색 후 memory 조회
	// 			}
	// 		}else if(Boolean.valueOf(mpsCapable)){//mps
	// 			int gpuMemory = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.memory"));
	// 			int mpsCount = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.replicas"));
	// 			memory = gpuMemory / mpsCount;
	//
	// 		}else{//normal
	// 			memory = Integer.parseInt(node.getMetadata().getLabels().get("nvidia.com/gpu.memory"));
	// 			gpuName = node.getMetadata().getLabels().get("nvidia.com/gpu.product");
	// 		}
	//
	// 		System.out.println(memory);
	//
	// 	}
	// }
	// @Test
	// void test3(){
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<Node> nodes = client.nodes()
	// 			.withLabel("nvidia.com/gpu.product", "Tesla-V100-PCIE-16GB")
	// 			.withLabel("nvidia.com/mps.capable", "true")
	// 			.list()
	// 			.getItems();
	// 	}
	// }
	// @Test
	// void test3(){
	// 	try (KubernetesClient client = k8sAdapter.configServer()) {
	// 		List<Service> items = client.services()
	// 			.inNamespace("ws-8f4b85d4-ca46-4db3-9f4b-43056aef0dd6")
	// 			.withLabel(LabelField.WORKLOAD_RESOURCE_NAME.getField(), "wl-0242f27a-9079-41e0-af06-29e34f000e27")
	// 			.list()
	// 			.getItems();
	// 		for (Service item : items) {
	// 			List<ServicePort> ports = item.getSpec().getPorts();
	// 			for (ServicePort port : ports) {
	// 				String name = port.getName();
	// 				Integer nodePort = port.getNodePort();
	// 				Integer cPort = port.getPort();
	// 			}
	// 		}
	// 		System.out.println(items.size());
	// 	}
	// }
}
