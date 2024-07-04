package com.xiilab.modulek8s.storage.common.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.storage.common.utils.StorageUtils;

import io.fabric8.kubernetes.client.KubernetesClient;

@Service
public class WekaFsService extends StorageUtils {

	public void wekaFsInstall(KubernetesClient client) {
		try {
			runShellCommand("helm repo add csi-wekafs https://weka.github.io/csi-wekafs");
			runShellCommand("helm install csi-wekafsplugin csi-wekafs/csi-wekafsplugin --namespace csi-wekafsplugin");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void wekaFsUnInstall(){
		try {
			runShellCommand("helm uninstall csi-wekafsplugin");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
