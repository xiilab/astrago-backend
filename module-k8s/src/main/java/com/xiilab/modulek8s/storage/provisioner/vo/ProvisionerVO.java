package com.xiilab.modulek8s.storage.provisioner.vo;

import java.util.UUID;

import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8s.common.enumeration.LabelField;
import com.xiilab.modulek8s.common.enumeration.StorageType;
import com.xiilab.modulek8s.storage.common.crd.NFS.HelmRelease;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.Chart;
import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmReleaseSpec;
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
	public static HelmRelease createNFSResource(){
		ObjectMeta objectMeta = new ObjectMetaBuilder()
			.withName("csi-" + UUID.randomUUID()) //pr-uuid
			.addToAnnotations(AnnotationField.NAME.getField(), "NFS 플러그인")
			.addToLabels(LabelField.STORAGE_TYPE.getField(), StorageType.NFS.name())
			.build();
		HelmRelease helmRelease = new HelmRelease();
		helmRelease.setMetadata(objectMeta);

		SourceRef sourceRef = SourceRef.builder()
			.kind("HelmRepository")
			.name("nfs-helmrepository")
			.build();

		Spec spec = Spec.builder()
			.chart("csi-driver-nfs")
			.sourceRef(sourceRef)
			.build();

		Chart chart = Chart.builder()
			.spec(spec)
			.build();

		Install install = Install.builder()
			.createNamespace(true)
			.build();

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
}
