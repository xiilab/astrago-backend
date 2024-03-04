package com.xiilab.modulek8s.workload.repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.xiilab.modulek8s.facade.dto.ModifyLocalDatasetDeploymentDTO;
import com.xiilab.modulek8s.facade.dto.ModifyLocalModelDeploymentDTO;
import com.xiilab.modulek8s.workload.dto.request.ConnectTestDTO;
import com.xiilab.modulek8s.workload.dto.request.CreateDatasetDeployment;
import com.xiilab.modulek8s.workload.dto.request.CreateModelDeployment;
import com.xiilab.modulek8s.workload.dto.request.EditAstragoDeployment;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.WorkloadResDTO;
import com.xiilab.modulek8s.workload.vo.BatchJobVO;
import com.xiilab.modulek8s.workload.vo.InteractiveJobVO;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;

@Repository
public interface WorkloadRepository {
	/**
	 * 워크로드 - 배치 잡 타입 생성
	 *
	 * @param batchJobVO
	 * @return BatchJobResDTO
	 */
	ModuleJobResDTO createBatchJobWorkload(BatchJobVO batchJobVO);

	/**
	 * 워크로드 - IDE 잡 타입 생성
	 *
	 * @param interactiveJobVO
	 * @return InteractiveJobResDTO
	 */
	ModuleJobResDTO createInteractiveJobWorkload(InteractiveJobVO interactiveJobVO);

	void createConnectTestDeployment(ConnectTestDTO connectTestDTO);

	boolean testConnectPodIsAvailable(String connectTestLabelName, String namespace);

	void deleteConnectTestDeployment(String deploymentName, String namespace);

	void editAstragoDeployment(EditAstragoDeployment editAstragoDeployment);

	/**
	 * batch job workload 조회
	 *
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	ModuleBatchJobResDTO getBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 조회
	 *
	 * @param workSpaceName
	 * @param workloadName
	 * @return
	 */
	ModuleInteractiveJobResDTO getInteractiveJobWorkload(String workSpaceName, String workloadName);

	/**
	 * batch job workload list 조회
	 *
	 * @param workSpaceName
	 * @return
	 */
	List<ModuleBatchJobResDTO> getBatchWorkloadListByWorkspaceName(String workSpaceName);

	List<ModuleBatchJobResDTO> getBatchWorkloadListByCreator(String userId);

	/**
	 * interactive job workload list 조회
	 *
	 * @param workSpaceName
	 * @return
	 */
	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadListByWorkspace(String workSpaceName);

	List<ModuleInteractiveJobResDTO> getInteractiveWorkloadByCreator(String creator);

	/**
	 * batch job workload 삭제
	 *
	 * @param workSpaceName
	 * @param workloadName
	 */
	String deleteBatchJobWorkload(String workSpaceName, String workloadName);

	/**
	 * interactive job workload 삭제
	 *
	 * @param workSpaceName
	 * @param workloadName
	 */
	String deleteInteractiveJobWorkload(String workSpaceName, String workloadName);

	ExecListenable connectBatchJobTerminal(String workspaceName, String workloadName);

	ExecListenable connectInteractiveJobTerminal(String workspaceName, String workloadName);

	Pod getBatchJobPod(String workspaceName, String workloadName);

	Pod getInteractiveJobPod(String workspaceName, String workloadName);

	WorkloadResDTO.PageUsingDatasetDTO workloadsUsingDataset(Integer pageNo, Integer pageSize, Long id);

	void createDatasetDeployment(CreateDatasetDeployment createDeployment);

	void modifyLocalDatasetDeployment(ModifyLocalDatasetDeploymentDTO modifyLocalDatasetDeploymentDTO);

	boolean isUsedDataset(Long datasetId);

	void deleteDeploymentByResourceName(String deploymentName, String namespace);

	void createModelDeployment(CreateModelDeployment createDeployment);

	WorkloadResDTO.PageUsingModelDTO workloadsUsingModel(Integer pageNo, Integer pageSize, Long id);

	void modifyLocalModelDeployment(ModifyLocalModelDeploymentDTO modifyLocalModelDeploymentDTO);

	boolean isUsedModel(Long modelId);

	List<String> getFileListInWorkloadContainer(String podName, String namespace, String path) throws IOException;
	List<String> getFileInfoInWorkloadContainer(String podName, String namespace, String path) throws IOException;

	int getDirectoryFileCount(String podName, String namespace, String path) throws IOException;

	CopyOrReadable downloadFileFromPod(String podName, String namespace, String filePath);

	CopyOrReadable downloadFolderFromPod(String podName, String namespace, String folderPath);

	void deleteFileFromPod(String podName, String namespace, String filePath);

	Boolean uploadFileToPod(String podName, String namespace, String path, File file);
	boolean mkdirToPod(String podName, String namespace, String path);
}
