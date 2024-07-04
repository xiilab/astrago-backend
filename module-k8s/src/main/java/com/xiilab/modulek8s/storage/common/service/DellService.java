package com.xiilab.modulek8s.storage.common.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.common.utils.StorageUtils;
@Service
public class DellService extends StorageUtils {

	public void dellCrdInstall() {
		k8sSnapshotInstall();
		dellCsiInstall();
	}

	private void k8sSnapshotInstall() {
		try {
			runShellCommand("kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshotclasses.yaml");
			runShellCommand("kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshotcontents.yaml");
			runShellCommand("kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/client/config/crd/snapshot.storage.k8s.io_volumesnapshots.yaml");
			runShellCommand("kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/snapshot-controller/rbac-snapshot-controller.yaml");
			runShellCommand("kubectl apply -f https://raw.githubusercontent.com/kubernetes-csi/external-snapshotter/master/deploy/kubernetes/snapshot-controller/setup-snapshot-controller.yaml");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void dellCsiInstall() {
		try {
			runShellCommand("git clone https://github.com/dell/csm-operator.git");
			runShellCommand("csm-operator/scripts/install.sh");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void dellCsiUnInstall() {
		try {
			runShellCommand("csm-operator/scripts/uninstall.sh");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
