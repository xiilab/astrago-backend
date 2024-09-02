package com.xiilab.modulek8s.storage.common.crd.NFS.spec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiilab.modulek8s.storage.common.crd.NFS.status.HelmReleaseStatus;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Version("v1")
@Group("source.toolkit.fluxcd.io")
public class HelmRepository extends CustomResource<HelmRepositorySpec, HelmReleaseStatus> implements Namespaced {
}
