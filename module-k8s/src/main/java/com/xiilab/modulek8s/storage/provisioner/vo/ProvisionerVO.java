package com.xiilab.modulek8s.storage.provisioner.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Chart;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmReleaseSpec;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmRepository;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmRepositorySpec;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Install;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.SourceRef;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Spec;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProvisionerVO {
	public static HelmRelease createNFSResource() {
		ObjectMeta objectMeta = new ObjectMetaBuilder().withName("csi-" + UUID.randomUUID()) //pr-uuid
			.addToAnnotations(AnnotationField.NAME.getField(), "NFS 플러그인")
			.addToLabels(LabelField.STORAGE_TYPE.getField(), StorageType.NFS.name())
			.build();
		HelmRelease helmRelease = new HelmRelease();
		helmRelease.setMetadata(objectMeta);

		SourceRef sourceRef = SourceRef.builder().kind("HelmRepository").name("nfs-helmrepository").build();

		Spec spec = Spec.builder().chart("csi-driver-nfs").sourceRef(sourceRef).build();

		Chart chart = Chart.builder().spec(spec).build();

		Install install = Install.builder().createNamespace(true).build();

		HelmReleaseSpec helmReleaseSpec = HelmReleaseSpec.builder()
			.chart(chart)
			.interval("1m0s")
			.install(install)
			.releaseName("csi")
			.storageNamespace("csi")
			.targetNamespace("csi")
			.build();

		helmRelease.setSpec(helmReleaseSpec);
		return helmRelease;
	}

	public static HelmRelease createDellProvisioner() {
		// ObjectMeta 설정
		ObjectMeta objectMeta = new ObjectMetaBuilder().withName("csi-" + UUID.randomUUID()) // 고유한 이름 생성
			.addToAnnotations(AnnotationField.NAME.getField(), "Dell Unity 플러그인") // 주석 추가
			.addToLabels(LabelField.STORAGE_TYPE.getField(), StorageType.DELL_UNITY.name()) // 라벨 추가
			.build();

		// HelmRelease 객체 생성 및 메타데이터 설정
		HelmRelease helmRelease = new HelmRelease();
		helmRelease.setMetadata(objectMeta);

		// SourceRef 설정: Helm 차트 소스 참조
		SourceRef sourceRef = SourceRef.builder()
			.kind("HelmRepository")
			.name("unity-helmrepository") // Dell Unity 관련 Helm 저장소 이름
			.build();

		// Spec 설정: 차트 정보 및 소스 참조
		Spec spec = Spec.builder().chart("csi-unity") // Dell Unity CSI 드라이버 차트 이름
			.sourceRef(sourceRef).build();

		// Chart 객체 생성
		Chart chart = Chart.builder().spec(spec).build();

		// Install 설정: 네임스페이스 자동 생성
		Install install = Install.builder().createNamespace(true).build();

		// HelmReleaseSpec 설정: 차트, 설치 간격, 릴리즈 이름 및 네임스페이스 지정
		HelmReleaseSpec helmReleaseSpec = HelmReleaseSpec.builder().chart(chart).interval("1m0s") // 1분 간격으로 동기화
			.install(install).releaseName("csi-unity") // Helm 릴리즈 이름
			.storageNamespace("unity") // 스토리지 네임스페이스
			.targetNamespace("unity") // 타겟 네임스페이스
			.values(getValues()).build();

		// Spec을 HelmRelease 객체에 설정
		helmRelease.setSpec(helmReleaseSpec);

		// 최종 HelmRelease 객체 반환
		return helmRelease;
	}

	private static Map<String, Object> getValues() {
		// Values 설정: values.yaml의 내용을 추가
		Map<String, Object> values = new HashMap<>();
		values.put("version", "v2.11.0");
		values.put("images",
			Map.of("driver", "dellemc/csi-unity:v2.11.0", "attacher", "registry.k8s.io/sig-storage/csi-attacher:v4.6.1",
				"provisioner", "registry.k8s.io/sig-storage/csi-provisioner:v5.0.1", "snapshotter",
				"registry.k8s.io/sig-storage/csi-snapshotter:v8.0.1", "resizer",
				"registry.k8s.io/sig-storage/csi-resizer:v1.11.1", "registrar",
				"registry.k8s.io/sig-storage/csi-node-driver-registrar:v2.10.1", "healthmonitor",
				"registry.k8s.io/sig-storage/csi-external-health-monitor-controller:v0.12.1", "podmon",
				"dellemc/podmon:v1.10.0"));
		values.put("logLevel", "info");
		values.put("certSecretCount", 1);
		values.put("imagePullPolicy", "Always");
		values.put("kubeletConfigDir", "/var/lib/kubelet");
		values.put("fsGroupPolicy", "None");
		values.put("controller", Map.of("controllerCount", 2, "volumeNamePrefix", "csivol", "snapshot",
			Map.of("enabled", true, "snapNamePrefix", "csi-snap"), "resizer", Map.of("enabled", true), "healthMonitor",
			Map.of("enabled", false, "interval", "60s")));
		values.put("node", Map.of("dnsPolicy", "ClusterFirstWithHostNet", "healthMonitor", Map.of("enabled", false)));
		values.put("podmon", Map.of("enabled", false, "controller", Map.of("args",
			List.of("--csisock=unix:/var/run/csi/csi.sock", "--labelvalue=csi-unity",
				"--driverPath=csi-unity.dellemc.com", "--mode=controller", "--skipArrayConnectionValidation=false",
				"--driver-config-params=/unity-config/driver-config-params.yaml", "--driverPodLabelValue=dell-storage",
				"--ignoreVolumelessPods=false")), "node", Map.of("args",
			List.of("--csisock=unix:/var/lib/kubelet/plugins/unity.emc.dell.com/csi_sock", "--labelvalue=csi-unity",
				"--driverPath=csi-unity.dellemc.com", "--mode=node", "--leaderelection=false",
				"--driver-config-params=/unity-config/driver-config-params.yaml", "--driverPodLabelValue=dell-storage",
				"--ignoreVolumelessPods=false"))));
		values.put("syncNodeInfoInterval", 15);
		values.put("allowRWOMultiPodAccess", "false");
		values.put("maxUnityVolumesPerNode", 0);
		values.put("tenantName", "");
		values.put("storageCapacity", Map.of("enabled", true, "pollInterval", "5m"));

		return values;
	}

	public static HelmRepository createRepository() {
		HelmRepository helmRepository = new HelmRepository();
		helmRepository.setKind("HelmRepository");
		helmRepository.setMetadata(
			new ObjectMeta().toBuilder()
				.withNamespace("unity")
				.withName("unity-helmrepository")
				.build());
		helmRepository.setApiVersion("source.toolkit.fluxcd.io/v1");
		helmRepository.setSpec(
			HelmRepositorySpec.builder()
				.url("https://dell.github.io/helm-charts")
				.interval("5m")
				.build());
		return helmRepository;
	}
}
