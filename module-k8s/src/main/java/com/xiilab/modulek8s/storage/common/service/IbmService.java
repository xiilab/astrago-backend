package com.xiilab.modulek8s.storage.common.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;
import com.xiilab.modulek8s.storage.common.utils.StorageUtils;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
@Service
public class IbmService extends StorageUtils {
	public void ibmInstall(KubernetesClient client) {
		long runningCount = getIbmPodCount(client);

		if(runningCount == 4){
			throw new RestApiException(StorageErrorCode.STORAGE_ALREADY_INSTALLED_IBM);
		}else{
			// operator 설치
			ibmOperatorInstall();
			// driver 설치
			ibmDriverInstall();
		}
	}

	public void ibmDelete(KubernetesClient client){
		boolean  operatorInstallCheck= operatorInstallCheck(client);
		ibmDriverUnInstall();
		if(!operatorInstallCheck){
			ibmOperatorUnInstall();
		}
	}

	private long getIbmPodCount(KubernetesClient client) {
		List<Pod> storagePods = client.pods()
			.withLabel("product", "ibm-block-csi-driver")
			.list().getItems();

		return storagePods.stream()
			.filter(storagePod -> storagePod.getStatus().getPhase().equalsIgnoreCase("Running"))
			.count();
	}

	private boolean operatorInstallCheck(KubernetesClient client){
		try{
			int result = client.pods()
				.withLabel("app.kubernetes.io/name", "ibm-block-csi-operator")
				.list().getItems().size();
			if(result == 0){
				ibmOperatorInstall();
				Thread.sleep(5000);
				return true;
			}
			return false;
		}catch (InterruptedException e) {
			throw new RestApiException(StorageErrorCode.STORAGE_UNINSTALL_FAIL_IBM);
		}
	}

	private void ibmOperatorInstall(){
		try{
			// 1. IBM 오퍼레이터 매니페스트를 다운로드
			runShellCommand("curl https://raw.githubusercontent.com/IBM/ibm-block-csi-operator/v1.11.3/deploy/installer/generated/ibm-block-csi-operator.yaml > ibm-block-csi-operator.yaml");
			// 2. IBM 오퍼레이터 설치
			runShellCommand("kubectl apply -f ibm-block-csi-operator.yaml");
		} catch (IOException | InterruptedException e) {
			throw new RestApiException(StorageErrorCode.STORAGE_INSTALL_FAIL_IBM);
		}
	}

	private void ibmOperatorUnInstall(){
		try{
			runShellCommand("kubectl delete -f ibm-block-csi-operator.yaml");
		} catch (IOException | InterruptedException e) {
			throw new RestApiException(StorageErrorCode.STORAGE_UNINSTALL_FAIL_IBM);
		}
	}

	private void ibmDriverInstall(){
		try{
			runShellCommand("curl https://raw.githubusercontent.com/IBM/ibm-block-csi-operator/v1.11.3/config/samples/csi.ibm.com_v1_ibmblockcsi_cr.yaml > csi.ibm.com_v1_ibmblockcsi_cr.yaml");
			runShellCommand("kubectl apply -f csi.ibm.com_v1_ibmblockcsi_cr.yaml");
		} catch (IOException | InterruptedException e) {
			throw new RestApiException(StorageErrorCode.STORAGE_INSTALL_FAIL_IBM);
		}
	}

	private void ibmDriverUnInstall(){
		try{
			runShellCommand("kubectl delete -f csi.ibm.com_v1_ibmblockcsi_cr.yaml");
		} catch (IOException | InterruptedException e) {
			throw new RestApiException(StorageErrorCode.STORAGE_UNINSTALL_FAIL_IBM);
		}
	}

}
