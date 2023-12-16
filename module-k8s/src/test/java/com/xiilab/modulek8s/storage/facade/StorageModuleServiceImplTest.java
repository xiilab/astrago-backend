// package com.xiilab.modulek8s.storage.facade;
//
// import java.util.List;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// import com.xiilab.modulek8s.config.K8sAdapter;
// import com.xiilab.modulek8s.facade.StorageModuleServiceImpl;
// import com.xiilab.modulek8s.facade.dto.CreateVolumeDTO;
// import com.xiilab.modulek8s.storage.storageclass.enums.StorageType;
// import com.xiilab.modulek8s.storage.volume.dto.response.VolumeWithWorkloadsResDTO;
//
// import io.fabric8.kubernetes.api.model.NamespaceList;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
// import io.fabric8.kubernetes.api.model.Quantity;
// import io.fabric8.kubernetes.client.KubernetesClient;
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
// }