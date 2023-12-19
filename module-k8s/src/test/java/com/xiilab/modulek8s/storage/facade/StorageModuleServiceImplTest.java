// package com.xiilab.modulek8s.storage.facade;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// import com.xiilab.modulek8s.common.enumeration.AnnotationField;
// import com.xiilab.modulek8s.common.enumeration.LabelField;
// import com.xiilab.modulek8s.config.K8sAdapter;
// import com.xiilab.modulek8s.facade.StorageModuleServiceImpl;
// import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
// import com.xiilab.modulek8s.common.enumeration.StorageType;
// import com.xiilab.modulek8s.storage.volume.dto.response.PageVolumeResDTO;
// import com.xiilab.modulek8s.storage.volume.dto.response.VolumeResDTO;
// import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
//
// import io.fabric8.kubernetes.api.model.HasMetadata;
// import io.fabric8.kubernetes.api.model.Namespace;
// import io.fabric8.kubernetes.api.model.NamespaceList;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
// import io.fabric8.kubernetes.api.model.Quantity;
// import io.fabric8.kubernetes.api.model.apps.Deployment;
// import io.fabric8.kubernetes.api.model.apps.StatefulSet;
// import io.fabric8.kubernetes.api.model.batch.v1.Job;
// import io.fabric8.kubernetes.api.model.storage.StorageClass;
// import io.fabric8.kubernetes.client.KubernetesClient;
// import io.fabric8.kubernetes.client.dsl.Resource;
// import lombok.extern.slf4j.Slf4j;
//
// @SpringBootTest
// @Slf4j
// class StorageModuleServiceImplTest {
// 	@Autowired
// 	private K8sAdapter k8sAdapter;
// 	@Autowired
// 	private StorageModuleServiceImpl storageModuleServiceImpl;
// 	@Test
// 	void getStorageClasses(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.storage().v1().storageClasses().list().getItems()
// 				.forEach(sc -> log.info(" - {}", sc.getMetadata().getName()));
// 		}
// 	}
// 	@Test
// 	void getStorageClasseByLabel(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.storage().v1().storageClasses().withLabel("storage-type", "NFS").list().getItems().get(0).getProvisioner();
// 		}
// 	}
//
// 	@Test
// 	void getCSIDrivers(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.storage().v1().csiDrivers().list().getItems()
// 				.forEach(sc -> log.info(" - {}", sc.getMetadata().getName()));
// 		}
// 	}
// 	@Test
// 	void getHelmStatusByRelease(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			// final OpenShiftClient openShiftClient = client.adapt(OpenShiftClient.class);
// 			// HelmChartRepositoryList list = openShiftClient.helmChartRepositories().list();
// 			// System.out.println(list.getItems().size());
// 		}
// 	}
//
// 	@Test
// 	void getNSByWorkspaceName(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			String namespaceName = client.namespaces()
// 				.withLabel("workspace-name", "ws1")
// 				.list()
// 				.getItems()
// 				.get(0)
// 				.getMetadata()
// 				.getName();
// 		}
// 	}
//
// 	@Test
// 	void getAllNS(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			NamespaceList namespaceList = client.namespaces().list();
//
// 		}
//
// 	}
//
// 	@Test
// 	void createVolume(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			PersistentVolumeClaim persistentVolumeClaim = new PersistentVolumeClaimBuilder()
// 				.withNewMetadata()
// 				.withName("testpvc1")
// 				.withNamespace("yc-test-ns")
// 				.addToAnnotations("volume-name", "한글 띄어쓰기asdfasd#$@#$")
// 				.addToAnnotations("volume-created", "이용춘")
// 				.endMetadata()
// 				.withNewSpec()
// 				.withStorageClassName("nfs-csi")
// 				.withAccessModes("ReadWriteMany")
// 				.withNewResources()
// 				.addToRequests("storage", new Quantity("5Gi"))
// 				.endResources()
// 				.endSpec()
// 				.build();
//
// 			client.persistentVolumeClaims().resource(persistentVolumeClaim).create();
// 		}
// 	}
// 	@Test
// 	void getVolumesByNamespace(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			List<PersistentVolumeClaim> items = client.persistentVolumeClaims()
// 				.inNamespace("yc-test-ns")
// 				.list()
// 				.getItems();
// 			for (PersistentVolumeClaim persistentVolumeClaim : items) {
// 				System.out.println(persistentVolumeClaim);
// 			}
//
// 		}
// 	}
// 	@Test
// 	void getVolumesByMetaName(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			PersistentVolumeClaim pvc = client.persistentVolumeClaims()
// 				.inNamespace("")
// 				.withName("vo-422e4d40-3500-47df-ba74-b5851ab33eff")
// 				.get();
//
// 			VolumeWithWorkloadsResDTO build = VolumeWithWorkloadsResDTO.builder()
// 				.hasMetadata(pvc)
// 				.workloadNames(List.of("asdf","sdfsdf"))
// 				.build();
// 			System.out.println(pvc);
// 		}
// 	}
// 	@Test
// 	void getAllResourceByLabels(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.apps().statefulSets().withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff","true").list().getItems().get(0).getMetadata().getAnnotations().get("name");
// 			client.apps().deployments().withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff","true").list().getItems().get(0).getMetadata().getAnnotations().get("name");
// 			client.batch().v1().jobs().withLabelIn("vo-422e4d40-3500-47df-ba74-b5851ab33eff","true").list().getItems().get(0).getMetadata().getAnnotations().get("name");
//
// 		}
// 	}
//
// 	@Test
// 	void updateVolume(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			// client.persistentVolumeClaims().
// 		}
// 	}
//
//
// 	@Test
// 	void createVolumeService(){
// 		CreateVolumeDTO request = CreateVolumeDTO.builder()
// 			.storageType(StorageType.NFS)
// 			.requestVolume(5)
// 			.name("vo1user123")
// 			.workspaceMetaDataName("yc-test-ns")
// 			.build();
//
// 		storageModuleServiceImpl.createVolume(request);
// 	}
//
// 	@Test
// 	void findVolumeWithWorkloadsByMetaName(){
// 		VolumeWithWorkloadsResDTO result = storageModuleServiceImpl.findVolumeWithWorkloadsByMetaName("yc-test-ns",
// 			"vo-422e4d40-3500-47df-ba74-b5851ab33eff");
// 		System.out.println(result);
// 	}
//
// 	@Test
// 	void 볼륨수정기능(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			PersistentVolumeClaim edit = client.persistentVolumeClaims()
// 				.inNamespace("yc-test-ns")
// 				.withName("vo-422e4d40-3500-47df-ba74-b5851ab33eff")
// 				.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
// 					.addToAnnotations("name", "수정된 이름입니당")
// 					.endMetadata()
// 					.editSpec()
// 					.editResources()
// 					.addToRequests("storage", new Quantity("11Gi"))
// 					.endResources()
// 					.endSpec()
// 					.editStatus()
// 					.addToCapacity("storage", new Quantity("11Gi"))
// 					.endStatus()
// 					.build());
// 		}
// 	}
//
// 	@Test
// 	void 볼륨삭제기능(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.persistentVolumeClaims().inNamespace("yc-test-ns").withName("testtest").delete();
//
// 		}
// 	}
//
// 	@Test
// 	void 워크스페이스명으로볼륨조회(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()) {
// 			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
// 				.inNamespace("yc-test-ns")
// 				.withLabel(LabelField.STORAGE_TYPE.getField(), "NFS")
// 				.list()
// 				.getItems();
// 			List<VolumeResDTO> collect = pvcs.stream().map(VolumeResDTO::toDTO).collect(Collectors.toList());
//
// 		}
// 	}
// 	@Test
// 	void 볼륨조회페이징검색포함(){
// 		String option = "creator-name";
// 		String workspaceMetaName = "yc-test-ns";
// 		String keyword = "서준오";
// 		String searchOption = "";
// 		if(option.equalsIgnoreCase(AnnotationField.CREATOR_FULL_NAME.getField())){
// 			searchOption = AnnotationField.CREATOR_FULL_NAME.getField();
// 		}else if(option.equalsIgnoreCase(AnnotationField.NAME.getField())){
// 			searchOption = AnnotationField.NAME.getField();
// 		}
// 		try(final KubernetesClient client = k8sAdapter.configServer()) {
// 			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
// 				.inNamespace(workspaceMetaName)
// 				.list()
// 				.getItems();
// 			String finalSearchOption = searchOption;
// 			List<PageVolumeResDTO> collect = pvcs.stream()
// 				.filter(pvc -> pvc.getMetadata().getAnnotations().get(finalSearchOption).equalsIgnoreCase(keyword))
// 				.map(pvc -> {
// 					String volumeName = pvc.getMetadata().getName();
// 					boolean isUsed = checkUsedVolume(volumeName, client);
// 					PageVolumeResDTO pageVolumeResDTO = PageVolumeResDTO.toDTO(pvc);
// 					pageVolumeResDTO.setIsUsed(isUsed);
// 					return pageVolumeResDTO;
// 				})
// 				.collect(Collectors.toList());
// 			System.out.println(collect.size());
// 		}
// 	}
// 	@Test
// 	void 전체네임스페이스의볼륨조회(){
// 		List<String> workloadNames = new ArrayList<>();
// 		try(final KubernetesClient client = k8sAdapter.configServer()) {
// 			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
// 				.inAnyNamespace()
// 				.list()
// 				.getItems();
// 			PersistentVolumeClaim persistentVolumeClaim = pvcs.stream()
// 				.filter(pvc -> pvc.getMetadata().getName().equals("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89"))
// 				.findFirst()
// 				.orElseThrow(() -> new RuntimeException("볼륨이 존재하지 않습니다."));
//
// 			String namespace = persistentVolumeClaim.getMetadata().getNamespace();
// 			String workspaceName = client.namespaces().withName(namespace).get().getMetadata().getAnnotations().get(AnnotationField.NAME.getField());
//
// 			//사용중인 statefulSets 조회
// 			List<StatefulSet> statefulSets = getStatefulSetsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89", client);
// 			setWorkloadInUseVolume(statefulSets, workloadNames);
// 			//사용중인 deployment 조회
// 			List<Deployment> deployments = getDeploymentsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89", client);
// 			setWorkloadInUseVolume(deployments, workloadNames);
// 			//사용중인 job 조회
// 			List<Job> jobs = getJobsInUseVolume("vo-dc4a488b-06a9-4a59-bde6-ceb7f58d7b89", client);
// 			setWorkloadInUseVolume(jobs, workloadNames);
//
//
// 			//sc
// 			String storageSavePath = client.storage().v1().storageClasses().withName("nfs-csi").get().getParameters().get("share");
//
// 			VolumeWithWorkloadsResDTO dto = VolumeWithWorkloadsResDTO.builder()
// 				.hasMetadata(persistentVolumeClaim)
// 				.workspaceName(workspaceName)
// 				.workloadNames(workloadNames)
// 				.requestVolume(persistentVolumeClaim.getSpec().getResources().getRequests().get("storage").toString())
// 				.storageType(StorageType.valueOf(
// 					persistentVolumeClaim.getMetadata().getLabels().get(LabelField.STORAGE_TYPE.getField())))
// 				.build();
//
// 			System.out.println(dto);
// 		}
// 	}
// 	@Test
// 	void 관리자_볼륨상세보기(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()) {
// 			List<PersistentVolumeClaim> pvcs = client.persistentVolumeClaims()
// 				.inAnyNamespace()
// 				.list()
// 				.getItems();
// 			String namespace = "ws-uuid123";
// 			String workspaceName = client.namespaces().withName(namespace).get().getMetadata().getAnnotations().get(AnnotationField.NAME.getField());
// 			System.out.println(pvcs.size());
// 		}
// 	}
//
// 	@Test
// 	void 관리자_볼륨삭제(){
// 		try (final KubernetesClient client = k8sAdapter.configServer()) {
// 			//삭제
// 			PersistentVolumeClaim pvc = new PersistentVolumeClaimBuilder().withNewMetadata()
// 				.withName("vo-bd72cad5-65f7-42c9-a16f-9c924a3c5219")
// 				.endMetadata()
// 				.build();
//
// 			client.persistentVolumeClaims().inAnyNamespace().resource(pvc).delete();
// 		}
// 	}
//
// 	@Test
// 	void 관리자_볼륨수정(){
// 		try (final KubernetesClient client = k8sAdapter.configServer()) {
//
// 			Resource<PersistentVolumeClaim> persistentVolumeClaimResource = client.persistentVolumeClaims()
// 				.inAnyNamespace()
// 				.resources()
// 				.filter(pvcr -> pvcr.get().getMetadata().getName().equals("vo-619ccb4a-f1bb-4ba7-962a-691d94554214"))
// 				.findFirst()
// 				.orElseThrow(() -> new RuntimeException("볼륨이 존재하지않습니다."));
//
// 			persistentVolumeClaimResource.edit(pvc -> new PersistentVolumeClaimBuilder(pvc).editMetadata()
// 				.addToAnnotations(AnnotationField.NAME.getField(), "수정한 이름이지롱")
// 				.endMetadata()
// 				.build());
// 		}
// 	}
//
// 	private boolean checkUsedVolume(String volumeMetaName, KubernetesClient client){
// 		List<Job> jobsInUseVolume = getJobsInUseVolume(volumeMetaName, client);
// 		List<Deployment> deploymentsInUseVolume = getDeploymentsInUseVolume(volumeMetaName, client);
// 		List<StatefulSet> statefulSetsInUseVolume = getStatefulSetsInUseVolume(volumeMetaName, client);
// 		return !jobsInUseVolume.isEmpty() || !deploymentsInUseVolume.isEmpty() || !statefulSetsInUseVolume.isEmpty();
// 	}
// 	private static List<Job> getJobsInUseVolume(String volumeMetaName, KubernetesClient client) {
// 		return client.batch().v1().jobs().withLabelIn(volumeMetaName, "true")
// 			.list()
// 			.getItems();
// 	}
//
// 	/**
// 	 * 해당 볼륨을 사용중인 Deployment list 조회
// 	 * @param volumeMetaName
// 	 * @param client
// 	 * @return
// 	 */
// 	private static List<Deployment> getDeploymentsInUseVolume(String volumeMetaName, KubernetesClient client) {
// 		return client.apps().deployments().withLabelIn(volumeMetaName, "true")
// 			.list()
// 			.getItems();
// 	}
//
// 	/**
// 	 * 해당 볼륨을 사용중인 StatefulSet list 조회
// 	 * @param volumeMetaName
// 	 * @param client
// 	 * @return
// 	 */
// 	private static List<StatefulSet> getStatefulSetsInUseVolume(String volumeMetaName, KubernetesClient client) {
// 		return client
// 			.apps()
// 			.statefulSets()
// 			.withLabelIn(volumeMetaName, "true")
// 			.list()
// 			.getItems();
// 	}
// 	/**
// 	 * 해당 볼륨을 사용중인 workload 주입
// 	 * @param resources
// 	 * @param workloadNames
// 	 */
// 	private void setWorkloadInUseVolume(List<? extends HasMetadata> resources, List<String> workloadNames){
// 		for (HasMetadata resource : resources) {
// 			Map<String, String> annotations = resource.getMetadata().getAnnotations() == null ? null : resource.getMetadata().getAnnotations();
// 			if (annotations != null) {
// 				String name = annotations.get(AnnotationField.NAME.getField());
// 				if (name != null) {
// 					workloadNames.add(name);
// 				}
// 			}
// 		}
// 	}
// }