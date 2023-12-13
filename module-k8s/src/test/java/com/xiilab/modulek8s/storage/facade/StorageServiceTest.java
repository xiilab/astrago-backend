// package com.xiilab.modulek8s.storage.facade;
//
// import java.util.List;
//
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
//
// import com.xiilab.modulek8s.config.K8sAdapter;
//
// import io.fabric8.kubernetes.api.model.NamespaceList;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaimBuilder;
// import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
// import io.fabric8.kubernetes.api.model.Quantity;
// import io.fabric8.kubernetes.client.KubernetesClient;
// import lombok.extern.slf4j.Slf4j;
//
// @SpringBootTest
// @Slf4j
// class StorageServiceTest {
// 	@Autowired
// 	private K8sAdapter k8sAdapter;
//
// 	@Test
// 	void getStorageClasses(){
// 		try(final KubernetesClient client = k8sAdapter.configServer()){
// 			client.storage().v1().storageClasses().list().getItems()
// 				.forEach(sc -> log.info(" - {}", sc.getMetadata().getName()));
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
// 	void getVolume(){
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
// }