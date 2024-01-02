package com.xiilab.modulek8s.storage.common.crd.NFS;

import com.xiilab.modulek8s.storage.common.crd.NFS.spec.HelmReleaseSpec;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.HelmReleaseStatus;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.NoArgsConstructor;

@Version("v2beta2")
@Group("helm.toolkit.fluxcd.io")
@NoArgsConstructor
public class HelmRelease extends CustomResource<HelmReleaseSpec, HelmReleaseStatus> implements Namespaced {

}
