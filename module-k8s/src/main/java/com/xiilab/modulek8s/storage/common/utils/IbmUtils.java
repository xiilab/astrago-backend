package com.xiilab.modulek8s.storage.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.StorageErrorCode;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IbmUtils {
	public static void ibmInstall(KubernetesClient client) {
		long runningCount = getIbmPodCount(client);

		if(runningCount == 4){
			throw new RestApiException(StorageErrorCode.STORAGE_ALREADY_INSTALLED_IBM);
		}else{
			// operator 설치
			ibmOperatorInstall();
			// driver 설치
			ibmDriverInstall();

			if(0 < runningCount && runningCount < 4){
				throw new RestApiException(StorageErrorCode.STORAGE_AFTER_AGAIN_INSTALL_IBM);
			}
		}
	}

	public static void ibmDelete(KubernetesClient client){
		boolean  operatorInstallCheck= operatorInstallCheck(client);
		if(operatorInstallCheck){
			ibmDriverUnInstall();
		}
		ibmOperatorUnInstall();
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

	private String runShellCommand(String command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("sh", "-c", command);

		Process process = processBuilder.start();
		int exitCode = process.waitFor();

		StringBuilder output = new StringBuilder();

 		if (exitCode == 0) {
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append(System.lineSeparator());
				}
			}
		} else {
			throw new RestApiException(StorageErrorCode.STORAGE_AFTER_AGAIN_INSTALL_IBM);
		}
		return output.toString();
	}

}
