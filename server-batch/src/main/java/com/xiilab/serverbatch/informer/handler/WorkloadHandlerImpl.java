package com.xiilab.serverbatch.informer.handler;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.kubeflow.v2beta1.MPIJob;
import org.kubeflow.v2beta1.mpijobspec.mpireplicaspecs.template.spec.Volumes;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.alert.enums.AlertMessage;
import com.xiilab.modulecommon.alert.enums.AlertName;
import com.xiilab.modulecommon.alert.enums.AlertRole;
import com.xiilab.modulecommon.alert.event.WorkspaceUserAlertEvent;
import com.xiilab.modulecommon.dto.MailDTO;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.MailServiceUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.common.enumeration.DistributedJobRole;
import com.xiilab.modulek8s.common.enumeration.EntityMappingType;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.facade.svc.SvcModuleFacadeService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.node.dto.GpuInfoDTO;
import com.xiilab.modulek8s.storage.volume.repository.K8sVolumeRepository;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleCodeResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDeployResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleDistributedJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.abst.AbstractModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.deploy.entity.DeployEntity;
import com.xiilab.modulek8sdb.deploy.repository.DeployRepository;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.image.repository.ImageWorkloadMappingRepository;
import com.xiilab.modulek8sdb.label.entity.LabelEntity;
import com.xiilab.modulek8sdb.label.repository.LabelRepository;
import com.xiilab.modulek8sdb.modelrepo.entity.ModelRepoEntity;
import com.xiilab.modulek8sdb.modelrepo.repository.ModelRepoRepository;
import com.xiilab.modulek8sdb.storage.dto.StorageDto;
import com.xiilab.modulek8sdb.storage.entity.StorageEntity;
import com.xiilab.modulek8sdb.storage.service.StorageService;
import com.xiilab.modulek8sdb.volume.entity.VolumeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.volume.repository.VolumeRepository;
import com.xiilab.modulek8sdb.volume.repository.VolumeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.entity.DistributedJobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.LabelWorkloadMappingEntity;
import com.xiilab.modulek8sdb.workload.history.entity.PortEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.LabelWorkloadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.repository.PortRepository;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.moduleuser.service.UserService;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadHandlerImpl implements WorkloadHandler {
	private final WorkloadHistoryRepo workloadHistoryRepo;
	// TODO 삭제 예정
	// private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	// private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final VolumeWorkLoadMappingRepository volumeWorkLoadMappingRepository;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;
	private final ImageWorkloadMappingRepository imageWorkloadMappingRepository;
	// TODO 삭제 예정
	// private final DatasetRepository datasetRepository;
	// private final ModelRepository modelRepository;
	private final VolumeRepository volumeRepository;
	private final CodeRepository codeRepository;
	private final ImageRepository imageRepository;
	private final LabelRepository labelRepository;
	private final LabelWorkloadMappingRepository labelWorkloadMappingRepository;
	private final CredentialRepository credentialRepository;
	private final SvcRepository k8sSvcRepository;
	private final ApplicationEventPublisher publisher;
	private final UserService userService;
	private final StorageService storageService;
	private final StorageModuleService storageModuleService;
	private final K8sVolumeRepository k8sVolumeRepository;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;
	private final DeployRepository deployRepository;
	private final ModelRepoRepository modelRepoRepository;
	private final PortRepository portRepository;
	private final SvcModuleFacadeService svcModuleFacadeService;


	@Override
	public void batchJobAddHandler(Job job) {
		//astrago에서 생성된 job filtering
		if (isAstragoResource(job) && K8sInfoPicker.isBatchJobYN(job)) {
			log.info("batch job {}가 생성되었습니다.", job.getMetadata().getName());
			String namespace = job.getMetadata().getNamespace();
			ModuleBatchJobResDTO batchWorkloadInfoFromResource = getBatchWorkloadInfoFromResource(job);
			// 잡 히스토리 저장
			if (batchWorkloadInfoFromResource != null) {
				saveBatchWorkloadHistory(namespace, batchWorkloadInfoFromResource);
			}
		}
	}

	@Override
	public void batchJobUpdateHandler(Job beforeJob, Job afterJob) {
		if (isAstragoResource(afterJob) && isResourceUpdate(beforeJob, afterJob) && isBatchJobYN(afterJob)) {
			// 잡상태 조회
			WorkloadStatus beforeStatus = K8sInfoPicker.getBatchWorkloadStatus(beforeJob.getStatus());
			WorkloadStatus afterStatus = K8sInfoPicker.getBatchWorkloadStatus(afterJob.getStatus());
			if (isStatusChanged(beforeStatus, afterStatus)) {
				if (afterStatus == WorkloadStatus.ERROR || afterStatus == WorkloadStatus.END) {
					// 로그 저장
					workloadHistoryRepo.findByResourceName(afterJob.getMetadata().getName())
						.ifPresent(wl -> {
							// 파드 종료시간 저장
							workloadHistoryRepo.updateWorkloadEndTime(afterJob.getMetadata().getName(), LocalDateTime.now());
							if (wl.getWorkloadType() == WorkloadType.BATCH) {
								saveWorkloadLogFile(wl);
							}
						});
				} else if (afterStatus == WorkloadStatus.RUNNING) {
					//label로 구분
					//gpuName, gpuMemory 데이터 저장
					//normal은 gpuName, memory 저장
					//mps, mig는 memory 저장
					String resourceName = afterJob.getMetadata().getName();
					List<Pod> pods = workloadModuleFacadeService.getWorkloadByWorkloadName(
						resourceName);
					String nodeName = "";
					for (Pod pod : pods) {
						boolean isRunning = pod.getStatus().getPhase().equalsIgnoreCase("Running");
						if(isRunning){
							nodeName = pod.getSpec().getNodeName();
							break;
						}
					}
					//mixedMigGpuName 조회
					WorkloadEntity workloadEntity = workloadHistoryRepo.findByResourceName(resourceName).get();
					String gpuName = workloadEntity.getGpuName();
					//db gpu, memory 정보 저장
					GpuInfoDTO gpuInfo = workloadModuleFacadeService.getGpuInfoByNodeName(
						gpuName, nodeName);
					workloadHistoryRepo.insertGpuInfo(resourceName, gpuInfo.getGpuName(), gpuInfo.getMemory(),
						nodeName);
					workloadHistoryRepo.insertWorkloadStartTime(afterJob.getMetadata().getName(), LocalDateTime.now());
				}

				//job 상태에 따른 status 업데이트 및 노티 발송
				checkJobStatusAndUpdateStatus(afterJob);
			}
		}
	}

	@Override
	public void batchJobDeleteHandler(Job job) {
		if (!isAstragoResource(job) || !isBatchJobYN(job)) {
			return;
		}

		ModuleBatchJobResDTO batchJobResDTO = getBatchWorkloadInfoFromResource(job);
		if (batchJobResDTO == null) {
			return;
		}

		updateDeleteJobStatusAndNoti(job);

		List<Volume> volumes = job.getSpec().getTemplate().getSpec().getVolumes();
		deleteVolume(volumes, job.getMetadata().getNamespace());
		deleteServices(batchJobResDTO.getWorkspaceResourceName(), batchJobResDTO.getResourceName());
	}

	@Override
	public void DeploymentAddHandler(Deployment deployment) {
		if (isAstragoResource(deployment)) {
			if(WorkloadType.DEPLOY.getType().equalsIgnoreCase(deployment.getMetadata().getAnnotations().get("type"))){
				log.info("deploy {}가 생성되었습니다.", deployment.getMetadata().getName());
				String namespace = deployment.getMetadata().getNamespace();
				ModuleDeployResDTO moduleDeployResDTO = getDeployInfoFromResource(deployment);
				// 히스토리 저장
				if (moduleDeployResDTO != null) {
					saveDeployHistory(namespace, moduleDeployResDTO);
				}
			}else{
				log.info("interactive job {}가 생성되었습니다.", deployment.getMetadata().getName());
				String namespace = deployment.getMetadata().getNamespace();
				ModuleInteractiveJobResDTO interactiveJobResDTO = getInteractiveWorkloadInfoFromResource(deployment);
				// 잡 히스토리 저장
				if (interactiveJobResDTO != null) {
					saveInteractiveWorkloadHistory(namespace, interactiveJobResDTO);
				}
			}
		} else if (deployment.getMetadata().getName().equals("astrago-backend-core")) {
			List<StorageDto> storages = storageService.getStorages();
			List<AstragoDeploymentConnectPVC> mounts = storages.stream().map(storageDto ->
				AstragoDeploymentConnectPVC.builder()
					.pvcName(storageDto.getPvcName())
					.hostPath(storageDto.getHostPath())
					.volumeName(storageDto.getVolumeName())
					.build()).toList();
			storageModuleService.astragoCoreDeploymentConnectPVC(mounts);
		}
	}

	@Transactional
	protected void saveDeployHistory(String namespace, ModuleDeployResDTO moduleDeployResDTO) {
		//이미 저장된 워크로드 일 경우 조기 리턴
		if (deployRepository.findByResourceName(moduleDeployResDTO.getResourceName()).isPresent()) {
			return;
		}
		//model id가 없을수있음
		Optional<ModelRepoEntity> modelRepo = modelRepoRepository.findById(moduleDeployResDTO.getDeployModelId());
		ModelRepoEntity modelRepoEntity = null;
		if(modelRepo.isPresent()){
			modelRepoEntity = modelRepo.get();
		}
		Optional<StorageEntity> storageEntity = storageService.findById(moduleDeployResDTO.getStorageId());
		DeployEntity deployEntity = DeployEntity.builder()
			.uid(moduleDeployResDTO.getUid())
			.name(moduleDeployResDTO.getName())
			.description(moduleDeployResDTO.getDescription())
			.resourceName(moduleDeployResDTO.getResourceName())
			.workspaceName(moduleDeployResDTO.getWorkspaceName())
			.workspaceResourceName(namespace)
			.ports(moduleDeployResDTO.getPortsMap())
			.cpuReq(moduleDeployResDTO.getCpuRequest())
			.memReq(moduleDeployResDTO.getMemRequest())
			.gpuReq(moduleDeployResDTO.getGpuRequest())
			.workloadCmd(moduleDeployResDTO.getCommand())
			.workingDir(moduleDeployResDTO.getWorkingDir())
			.createdAt(moduleDeployResDTO.getCreatedAt())
			.deletedAt(moduleDeployResDTO.getDeletedAt())
			.creatorName(moduleDeployResDTO.getCreatorUserName())
			.creatorId(moduleDeployResDTO.getCreatorId())
			.creatorRealName(moduleDeployResDTO.getCreatorFullName())
			.workloadType(moduleDeployResDTO.getType())
			.workspaceName(moduleDeployResDTO.getWorkspaceName())
			.deleteYN(DeleteYN.N)
			.ide(moduleDeployResDTO.getIde())
			.workloadStatus(WorkloadStatus.PENDING)
			.gpuType(moduleDeployResDTO.getGpuType() != null ? moduleDeployResDTO.getGpuType() : null)
			.gpuName(moduleDeployResDTO.getGpuName() != null ? moduleDeployResDTO.getGpuName() : null)
			.nodeName(moduleDeployResDTO.getNodeName())
			.gpuOnePerMemory(moduleDeployResDTO.getGpuOnePerMemory())
			.replica(moduleDeployResDTO.getReplica())
			.deployType(moduleDeployResDTO.getDeployType())
			.modelRepoEntity(modelRepoEntity)
			.modelVersion(moduleDeployResDTO.getModelVersion())
			.modelPath(moduleDeployResDTO.getModelPath())
			.storageEntity(storageEntity.orElse(null))
			.build();
		deployEntity.addEnvs(moduleDeployResDTO.getEnvsMap());
		deployRepository.save(deployEntity);
		saveMappings(moduleDeployResDTO, deployEntity);
	}

	@Override
	public void deploymentUpdateHandler(Deployment beforeDeployment, Deployment afterDeployment) {
		if (isAstragoResource(afterDeployment) && isResourceUpdate(beforeDeployment, afterDeployment)) {
			// 인터렉티브 상태 조회
			WorkloadStatus beforeStatus = getInteractiveWorkloadStatus(beforeDeployment.getStatus());
			WorkloadStatus afterStatus = getInteractiveWorkloadStatus(afterDeployment.getStatus());
			//status가 변경되었는지 체크
			if (isStatusChanged(beforeStatus, afterStatus)) {
				checkJobStatusAndUpdateStatus(afterDeployment);
				if (afterStatus == WorkloadStatus.RUNNING) {
					//label로 구분
					//gpuName, gpuMemory 데이터 저장
					//normal은 gpuName, memory 저장
					//mps, mig는 memory 저장
					String resourceName = afterDeployment.getMetadata().getName();
					List<Pod> pods = workloadModuleFacadeService.getWorkloadByWorkloadName(
						resourceName);
					String nodeName = "";
					for (Pod pod : pods) {
						boolean isRunning = pod.getStatus().getPhase().equalsIgnoreCase("Running");
						if (isRunning) {
							nodeName = pod.getSpec().getNodeName();
							break;
						}
					}
					//db gpu, memory 정보 저장
					WorkloadEntity workloadEntity = workloadHistoryRepo.findByResourceName(resourceName).get();
					String gpuName = workloadEntity.getGpuName();
					GpuInfoDTO gpuInfo = workloadModuleFacadeService.getGpuInfoByNodeName(
						gpuName, nodeName);

					workloadHistoryRepo.insertGpuInfo(resourceName, gpuInfo.getGpuName(), gpuInfo.getMemory(),
						nodeName);

					workloadHistoryRepo.insertWorkloadStartTime(afterDeployment.getMetadata().getName(),
						LocalDateTime.now());
				} else if (afterStatus == WorkloadStatus.ERROR || afterStatus == WorkloadStatus.END) {
					workloadHistoryRepo.updateWorkloadEndTime(afterDeployment.getMetadata().getName(),
						LocalDateTime.now());
				}
			}
		}
	}

	@Override
	public void deploymentDeleteHandler(Deployment deployment) {
		if (!isAstragoResource(deployment)) {
			return;
		}

		ModuleInteractiveJobResDTO interactiveJobResDTO = getInteractiveWorkloadInfoFromResource(
			deployment);
		if (interactiveJobResDTO == null) {
			return;
		}

		updateDeleteJobStatusAndNoti(deployment);
		List<Volume> volumes = deployment.getSpec().getTemplate().getSpec().getVolumes();
		deleteVolume(volumes, deployment.getMetadata().getNamespace());
		deleteServices(interactiveJobResDTO.getWorkspaceResourceName(), interactiveJobResDTO.getResourceName());
	}

	@Override
	public void distributedJobAddHandler(MPIJob mpiJob) {
		//astrago에서 생성된 job filtering
		if (isAstragoResource(mpiJob)) {
			log.info("distributed job {}가 생성되었습니다.", mpiJob.getMetadata().getName());
			String namespace = mpiJob.getMetadata().getNamespace();
			ModuleDistributedJobResDTO distributedJobResDTO = getDistirubtedWorkloadInfoFromResource(mpiJob);
			// 잡 히스토리 저장
			if (distributedJobResDTO != null) {
				saveDistributedWorkloadHistory(namespace, distributedJobResDTO);
			}
		}
	}

	@Override
	public void distributedJobUpdateHandler(MPIJob beforeJob, MPIJob afterJob) {
		if (isAstragoResource(beforeJob) && isResourceUpdate(beforeJob, afterJob)) {
			WorkloadStatus beforeJobStatus = getDistributedWorkloadStatus(beforeJob.getStatus());
			WorkloadStatus afterJobStatus = getDistributedWorkloadStatus(afterJob.getStatus());

			if (isStatusChanged(beforeJobStatus, afterJobStatus)) {
				if (afterJobStatus == WorkloadStatus.ERROR || afterJobStatus == WorkloadStatus.END) {
					// 로그 저장
					workloadHistoryRepo.findByResourceName(afterJob.getMetadata().getName())
						.ifPresent(wl -> {
							if (wl.getWorkloadType() == WorkloadType.DISTRIBUTED) {
								saveWorkloadLogFile(wl);
							}
						});
					//job이 완료될 경우 mpiJob Resource 삭제
					if (afterJobStatus == WorkloadStatus.END) {
						workloadModuleFacadeService.deleteDistributedWorkload(afterJob.getMetadata().getNamespace(),
							afterJob.getMetadata().getName());
					}
				} else if (afterJobStatus == WorkloadStatus.RUNNING) {
					workloadHistoryRepo.insertWorkloadStartTime(afterJob.getMetadata().getName(), LocalDateTime.now());
				}
				//job 상태에 따른 status 업데이트 및 노티 발송
				checkJobStatusAndUpdateStatus(afterJob);
			}
		}
	}

	@Override
	public void distributedJobDeleteHandler(MPIJob mpiJob) {
		if (!isAstragoResource(mpiJob)) {
			return;
		}

		ModuleDistributedJobResDTO distributedJobResDTO = getDistirubtedWorkloadInfoFromResource(mpiJob);
		if (distributedJobResDTO == null) {
			return;
		}

		updateDeleteDistributedJobStatusAndNoti(mpiJob);

		List<Volumes> volumes = mpiJob.getSpec()
			.getMpiReplicaSpecs()
			.get(DistributedJobRole.LAUNCHER.getName())
			.getTemplate()
			.getSpec()
			.getVolumes();
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.stream()
				.filter(volume -> volume.getPersistentVolumeClaim() != null)
				.forEach(volume -> deletePvAndPVC(mpiJob.getMetadata().getNamespace(), volume.getName(),
					volume.getPersistentVolumeClaim().getClaimName()));
		}
	}

	private void deleteVolume(List<Volume> volumes, String namespace) {
		if (!CollectionUtils.isEmpty(volumes)) {
			volumes.stream()
				.filter(volume -> volume.getPersistentVolumeClaim() != null)
				.forEach(volume -> deletePvAndPVC(namespace, volume.getName(),
					volume.getPersistentVolumeClaim().getClaimName()));
		}
	}

	private boolean isStatusChanged(WorkloadStatus beforeStatus, WorkloadStatus afterStatus) {
		return beforeStatus != afterStatus;
	}

	private void checkJobStatusAndUpdateStatus(Job job) {
		WorkloadStatus workloadStatus = getBatchWorkloadStatus(job.getStatus());
		Optional<WorkloadEntity> workload = workloadHistoryRepo.findByResourceName(job.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(workloadStatus, job.getMetadata().getName());
			handleNotificationsAndLog(wl, workloadStatus);
		});
	}

	private void updateDeleteJobStatusAndNoti(Job job) {
		Optional<WorkloadEntity> workload = workloadHistoryRepo.findByResourceName(job.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(WorkloadStatus.END, job.getMetadata().getName());
			handleNotificationsAndLog(wl, WorkloadStatus.END);
		});
	}

	private void updateDeleteJobStatusAndNoti(Deployment deployment) {
		Optional<WorkloadEntity> workload = workloadHistoryRepo.findByResourceName(deployment.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(WorkloadStatus.END,
				deployment.getMetadata().getName());
			workloadHistoryRepo.updateWorkloadEndTime(deployment.getMetadata().getName(), LocalDateTime.now());
			handleNotificationsAndLog(wl, WorkloadStatus.END);
		});
	}

	private void updateDeleteDistributedJobStatusAndNoti(MPIJob mpiJob) {
		Optional<WorkloadEntity> workload = workloadHistoryRepo.findByResourceName(mpiJob.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(WorkloadStatus.END, mpiJob.getMetadata().getName());
			handleNotificationsAndLog(wl, WorkloadStatus.END);
		});
	}

	private void checkJobStatusAndUpdateStatus(Deployment deployment) {
		WorkloadStatus workloadStatus = getInteractiveWorkloadStatus(deployment.getStatus());
		Optional<WorkloadEntity> workload = workloadHistoryRepo.findByResourceName(deployment.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(workloadStatus, deployment.getMetadata().getName());
			handleNotificationsAndLog(wl, workloadStatus);
		});
	}

	private void checkJobStatusAndUpdateStatus(MPIJob mpiJob) {
		WorkloadStatus workloadStatus = getDistributedWorkloadStatus(mpiJob.getStatus());
		Optional<WorkloadEntity> job = workloadHistoryRepo.findByResourceName(mpiJob.getMetadata().getName());
		job.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(workloadStatus, mpiJob.getMetadata().getName());
			handleNotificationsAndLog(wl, workloadStatus);
		});
	}

	private void handleNotificationsAndLog(WorkloadEntity workload, WorkloadStatus status) {
		if (status == WorkloadStatus.RUNNING) {
			sendRunningNotification(workload);
		} else if (status == WorkloadStatus.ERROR) {
			sendErrorNotification(workload);
		} else if (status == WorkloadStatus.END) {
			sendJobSucceedNotification(workload);
		}
	}

	private void saveWorkloadLogFile(WorkloadEntity wl) {
		String logResult = workloadModuleFacadeService.getWorkloadLogByWorkloadName(
			wl.getWorkspaceResourceName(), wl.getResourceName(), wl.getWorkloadType());
		try {
			FileUtils.saveLogFile(logResult, wl.getResourceName(), wl.getCreatorId());
			log.info("{}의 {} 로그가 저장 되었습니다.", wl.getWorkspaceResourceName(), wl.getResourceName());
		} catch (IOException e) {
			log.error("An error occurred while saving the log file.\n" + e.getMessage());
			log.info("{}의 {} 로그가 저장에 실패하였습니다.", wl.getWorkspaceResourceName(), wl.getResourceName());
		}
	}

	private void sendJobSucceedNotification(WorkloadEntity workload) {
		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(workload.getWorkspaceResourceName())
			.workloadResourceName(workload.getResourceName())
			.workloadType(workload.getWorkloadType())
			.build();

		String workloadName = workload.getName();
		String emailTitle = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMailTitle(),
			workloadName);
		String title = AlertMessage.WORKLOAD_END_CREATOR.getTitle();
		String message = String.format(AlertMessage.WORKLOAD_END_CREATOR.getMessage(), workloadName);

		String receiverMail = userService.getUserById(workload.getCreatorId()).getEmail();
		MailDTO mailDTO = MailServiceUtils.endWorkloadMail(workload.getName(), receiverMail);

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_END, null, workload.getCreatorId(), emailTitle, title, message,
			workload.getWorkspaceResourceName(), pageNaviParam, null);

		publisher.publishEvent(workspaceUserAlertEvent);
	}

	private void sendRunningNotification(WorkloadEntity workload) {

		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(workload.getWorkspaceResourceName())
			.workloadResourceName(workload.getResourceName())
			.workloadType(workload.getWorkloadType())
			.build();

		//워크로드 생성 알림 발송
		String workloadName = workload.getName();
		String emailTitle = String.format(AlertMessage.WORKLOAD_START_CREATOR.getMailTitle(), workloadName);
		String title = AlertMessage.WORKLOAD_START_CREATOR.getTitle();
		String message = String.format(AlertMessage.WORKLOAD_START_CREATOR.getMessage(), workloadName);

		String receiverMail = userService.getUserById(workload.getCreatorId()).getEmail();
		MailDTO mailDTO = MailServiceUtils.startWorkloadMail(workloadName, receiverMail);

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_START,
			null, workload.getCreatorId(), emailTitle, title, message,
			workload.getWorkspaceResourceName(), pageNaviParam, mailDTO);

		publisher.publishEvent(workspaceUserAlertEvent);
	}

	private void sendErrorNotification(WorkloadEntity workload) {
		PageNaviParam pageNaviParam = PageNaviParam.builder()
			.workspaceResourceName(workload.getWorkspaceResourceName())
			.workloadResourceName(workload.getResourceName())
			.workloadType(workload.getWorkloadType())
			.build();

		//워크로드 에러 알림 발송
		String workloadName = workload.getName();
		AlertMessage workloadErrorCreator = AlertMessage.WORKLOAD_ERROR_CREATOR;
		String emailTitle = String.format(workloadErrorCreator.getMailTitle(), workloadName);
		String title = workloadErrorCreator.getTitle();
		String message = String.format(workloadErrorCreator.getMessage(), workloadName);

		String receiverMail = userService.getUserById(workload.getCreatorId()).getEmail();
		MailDTO mailDTO = MailServiceUtils.errorWorkloadMail(workloadName, receiverMail);

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_ERROR,
			null, workload.getCreatorId(), emailTitle, title, message, workload.getWorkspaceResourceName(),
			pageNaviParam, mailDTO);

		publisher.publishEvent(workspaceUserAlertEvent);
	}

	@Transactional
	public void saveInteractiveWorkloadHistory(String namespace, ModuleInteractiveJobResDTO interactiveJobResDTO) {
		//이미 저장된 워크로드 일 경우 조기 리턴
		if (workloadHistoryRepo.findByResourceName(interactiveJobResDTO.getResourceName()).isPresent()) {
			return;
		}
		JobEntity jobEntity = JobEntity.jobBuilder()
			.uid(interactiveJobResDTO.getUid())
			.name(interactiveJobResDTO.getName())
			.description(interactiveJobResDTO.getDescription())
			.resourceName(interactiveJobResDTO.getResourceName())
			.workspaceName(interactiveJobResDTO.getWorkspaceName())
			.workspaceResourceName(namespace)
			// .envs(interactiveJobResDTO.getEnvsMap())
			.cpuReq(interactiveJobResDTO.getCpuRequest())
			.memReq(interactiveJobResDTO.getMemRequest())
			.gpuReq(interactiveJobResDTO.getGpuRequest())
			.workloadCmd(interactiveJobResDTO.getCommand())
			.workingDir(interactiveJobResDTO.getWorkingDir())
			.createdAt(interactiveJobResDTO.getCreatedAt())
			.deletedAt(interactiveJobResDTO.getDeletedAt())
			.creatorName(interactiveJobResDTO.getCreatorUserName())
			.creatorId(interactiveJobResDTO.getCreatorId())
			.creatorRealName(interactiveJobResDTO.getCreatorFullName())
			.workloadType(interactiveJobResDTO.getType())
			.workspaceName(interactiveJobResDTO.getWorkspaceName())
			.deleteYN(DeleteYN.N)
			.ide(interactiveJobResDTO.getIde())
			.parameter(interactiveJobResDTO.getParameter())
			.workloadStatus(WorkloadStatus.PENDING)
			.gpuType(interactiveJobResDTO.getGpuType() != null? interactiveJobResDTO.getGpuType() : null)
			.gpuName(interactiveJobResDTO.getGpuName() != null? interactiveJobResDTO.getGpuName() : null)
			.nodeName(interactiveJobResDTO.getNodeName())
			.gpuOnePerMemory(interactiveJobResDTO.getGpuOnePerMemory())
			.build();

		jobEntity.addEnvs(interactiveJobResDTO.getEnvsMap());
		workloadHistoryRepo.save(jobEntity);

		//port entity 생성해야함
		String resourceName = interactiveJobResDTO.getResourceName();
		List<List<ServicePort>> ports = svcModuleFacadeService.getPortsByWorkloadResourceName(
			namespace, resourceName);
		for (List<ServicePort> port : ports) {
			for (ServicePort servicePort : port) {
				PortEntity portEntity = PortEntity.builder()
					.name(servicePort.getName())
					.portNum(servicePort.getPort())
					.targetPortNum(servicePort.getNodePort())
					.build();
				portEntity.setWorkload(jobEntity);
				portRepository.save(portEntity);
			}
		}
		saveMappings(interactiveJobResDTO, jobEntity);
	}

	private void saveBatchWorkloadHistory(String namespace, ModuleBatchJobResDTO batchJobResDTO) {
		//이미 저장된 워크로드 일 경우 조기 리턴
		if (workloadHistoryRepo.findByResourceName(batchJobResDTO.getResourceName()).isPresent()) {
			return;
		}

		JobEntity jobEntity = JobEntity.jobBuilder()
			.uid(batchJobResDTO.getUid())
			.name(batchJobResDTO.getName())
			.description(batchJobResDTO.getDescription())
			.resourceName(batchJobResDTO.getResourceName())
			.workspaceName(batchJobResDTO.getWorkspaceName())
			.workspaceResourceName(namespace)
			// .envs(batchJobResDTO.getEnvsMap())
			.ports(batchJobResDTO.getPortsMap())
			.cpuReq(batchJobResDTO.getCpuRequest())
			.memReq(batchJobResDTO.getMemRequest())
			.gpuReq(batchJobResDTO.getGpuRequest())
			.workloadCmd(batchJobResDTO.getCommand())
			.workingDir(batchJobResDTO.getWorkingDir())
			.createdAt(batchJobResDTO.getCreatedAt())
			.deletedAt(batchJobResDTO.getDeletedAt())
			.creatorName(batchJobResDTO.getCreatorUserName())
			.creatorId(batchJobResDTO.getCreatorId())
			.creatorRealName(batchJobResDTO.getCreatorFullName())
			.workloadType(batchJobResDTO.getType())
			.workspaceName(batchJobResDTO.getWorkspaceName())
			.deleteYN(DeleteYN.N)
			.ide(batchJobResDTO.getIde())
			.parameter(batchJobResDTO.getParameter())
			.workloadStatus(WorkloadStatus.PENDING)
			.gpuType(batchJobResDTO.getGpuType() != null? batchJobResDTO.getGpuType() : null)
			.gpuName(batchJobResDTO.getGpuName() != null? batchJobResDTO.getGpuName() : null)
			.nodeName(batchJobResDTO.getNodeName())
			.gpuOnePerMemory(batchJobResDTO.getGpuOnePerMemory())
			.resourcePresetId(batchJobResDTO.getResourcePresetId())
			.build();

		jobEntity.addEnvs(batchJobResDTO.getEnvsMap());
		workloadHistoryRepo.save(jobEntity);

		saveMappings(batchJobResDTO, jobEntity);
	}

	private void saveDistributedWorkloadHistory(String namespace, ModuleDistributedJobResDTO distributedJobResDTO) {
		//이미 저장된 워크로드 일 경우 조기 리턴
		if (workloadHistoryRepo.findByResourceName(distributedJobResDTO.getResourceName()).isPresent()) {
			return;
		}

		DistributedJobEntity distributedJob = DistributedJobEntity.jobBuilder()
			.uid(distributedJobResDTO.getUid())
			.name(distributedJobResDTO.getName())
			.description(distributedJobResDTO.getDescription())
			.resourceName(distributedJobResDTO.getResourceName())
			.workspaceName(distributedJobResDTO.getWorkspaceName())
			.workspaceResourceName(namespace)
			// .envs(distributedJobResDTO.getEnvsMap())
			.ports(distributedJobResDTO.getPortsMap())
			.launcherCpuRequest(distributedJobResDTO.getLauncherInfo().getCpuRequest())
			.launcherMemRequest(distributedJobResDTO.getLauncherInfo().getMemRequest())
			.workerCpuRequest(distributedJobResDTO.getWorkerInfo().getCpuRequest())
			.workerMemRequest(distributedJobResDTO.getWorkerInfo().getMemRequest())
			.workerGpuRequest(distributedJobResDTO.getWorkerInfo().getGpuRequest())
			.workerCount(distributedJobResDTO.getWorkerInfo().getWorkerCnt())
			.workloadCmd(distributedJobResDTO.getCommand())
			.workingDir(distributedJobResDTO.getWorkingDir())
			.createdAt(distributedJobResDTO.getCreatedAt())
			.deletedAt(distributedJobResDTO.getDeletedAt())
			.creatorName(distributedJobResDTO.getCreatorUserName())
			.creatorId(distributedJobResDTO.getCreatorId())
			.creatorRealName(distributedJobResDTO.getCreatorFullName())
			.workloadType(distributedJobResDTO.getType())
			.workspaceName(distributedJobResDTO.getWorkspaceName())
			.deleteYN(DeleteYN.N)
			.ide(distributedJobResDTO.getIde())
			.parameter(distributedJobResDTO.getParameter())
			.workloadStatus(WorkloadStatus.PENDING)
			.gpuType(distributedJobResDTO.getGpuType() != null? distributedJobResDTO.getGpuType() : null)
			.gpuName(distributedJobResDTO.getGpuName() != null? distributedJobResDTO.getGpuName() : null)
			.nodeName(distributedJobResDTO.getNodeName())
			.gpuOnePerMemory(distributedJobResDTO.getGpuOnePerMemory())
			.resourcePresetId(distributedJobResDTO.getResourcePresetId())
			.build();

		distributedJob.addEnvs(distributedJobResDTO.getEnvsMap());
		workloadHistoryRepo.save(distributedJob);

		saveMappings(distributedJobResDTO, distributedJob);
	}

	private void saveMappings(AbstractModuleWorkloadResDTO jobResDTO, WorkloadEntity workload) {
		// dataset, model mapping insert
		// TODO 삭제 예정
		/*String datasetIds = jobResDTO.getDatasetIds();
		if (StringUtils.hasText(datasetIds)) {
			saveDataMapping(getSplitIds(datasetIds), datasetRepository::findById, workload, EntityMappingType.DATASET,
				jobResDTO.getDatasetMountPathMap(), null);
		}

		// 모델 mapping insert
		String modelIds = jobResDTO.getModelIds();
		if (StringUtils.hasText(modelIds)) {
			saveDataMapping(getSplitIds(modelIds), modelRepository::findById, workload, EntityMappingType.MODEL,
				jobResDTO.getModelMountPathMap(), null);
		}*/
		//

		String volumeIds = jobResDTO.getVolumeIds();
		if (StringUtils.hasText(volumeIds)) {
			saveDataMapping(getSplitIds(volumeIds), volumeRepository::findById, workload, EntityMappingType.VOLUME,
				jobResDTO.getVolumeMountPathMap(), null);
		}

		RegUser regUser = new RegUser(jobResDTO.getCreatorId(), jobResDTO.getCreatorUserName(),
			jobResDTO.getCreatorFullName());

		// 커스텀 소스코드 등록 후 코드 mapping insert
		String codeIds = saveCustomCode(regUser, jobResDTO.getWorkspaceResourceName(), jobResDTO.getCodeIds(),
			jobResDTO.getCodes());
		if (StringUtils.hasText(codeIds)) {
			saveDataMapping(getSplitIds(codeIds), codeRepository::findById, workload, EntityMappingType.CODE,
				null, jobResDTO.getCodeMountPathMap());
		}
		// 커스텀 이미지 등록 후 이미지 mapping insert
		Long imageId = saveCustomImage(regUser, jobResDTO);
		if (imageId != null) {
			saveDataMapping(getSplitIds(String.valueOf(imageId)), imageRepository::findById, workload,
				EntityMappingType.IMAGE, null, null);
		}

		String labelIds = jobResDTO.getLabelIds();
		if (StringUtils.hasText(labelIds)) {
			saveDataMapping(getSplitIds(labelIds), labelRepository::findById, workload, EntityMappingType.LABEL,
				null, null);
		}
	}

	private void deleteServices(String workspaceResourceName, String workloadResourceName) {
		ServiceList serviceList = k8sSvcRepository.getServicesByResourceName(workspaceResourceName,
			workloadResourceName);
		serviceList.getItems()
			.forEach(service -> k8sSvcRepository.deleteServiceByResourceName(service.getMetadata().getName(),
				workspaceResourceName));
	}

	private void deletePvAndPVC(String workspaceResourceName, String pvName, String pvcName) {
		k8sVolumeRepository.deletePVC(pvcName, workspaceResourceName);
		k8sVolumeRepository.deletePV(pvName);
	}

	private String[] getSplitIds(String ids) {
		return ids != null ? ids.split(",") : null;
	}

	// 커스텀 소스코드 DB에 등록 후 ID 추가해서 반환
	private String saveCustomCode(RegUser regUser, String namespace, String codeIds,
		List<ModuleCodeResDTO> codes) {
		StringBuilder result =
			StringUtils.hasText(codeIds) && !"null".equals(codeIds) ? new StringBuilder(codeIds) : new StringBuilder();
		if (!CollectionUtils.isEmpty(codes)) {
			for (ModuleCodeResDTO code : codes) {
				// 커스텀 소스코드일 경우
				if (ValidUtils.isNullOrZero(code.getSourceCodeId())
					&& code.getRepositoryType() == RepositoryType.CUSTOM) {
					// 크레덴셜 정보 조회
					Optional<CredentialEntity> findCredential = findCredentialById(code.getCredentialId());

					// 커스텀 코드는 타이틀이 없음, 타이틀을 URL로 대체
					CodeEntity saveCode = new CodeEntity(
						regUser,
						code.getRepositoryUrl(),
						code.getCodeType(),
						code.getRepositoryType(),
						code.getRepositoryUrl(),
						findCredential.orElseGet(() -> null),
						namespace,
						DeleteYN.N
					);
					CodeEntity savedCode = codeRepository.save(saveCode);
					result.append(result.isEmpty() ? String.valueOf(savedCode.getId()) : "," + savedCode.getId());
				}
			}
		}

		return result.toString();
	}

	private Long saveCustomImage(RegUser regUser, AbstractModuleWorkloadResDTO workloadResDTO) {
		Long id = workloadResDTO.getImageId();
		if (ValidUtils.isNullOrZero(workloadResDTO.getImageId()) &&
			workloadResDTO.getImageType() == ImageType.CUSTOM) {

			Optional<CredentialEntity> findCredential = Optional.empty();
			if (!ObjectUtils.isEmpty(workloadResDTO.getImageCredentialId())
				&& workloadResDTO.getImageCredentialId() > 0) {
				// 이미지 크레덴셜 조회
				findCredential = findCredentialById(workloadResDTO.getImageCredentialId());
			}

			CustomImageEntity customImageEntity = CustomImageEntity.informerBuilder()
				.regUser(regUser)
				.imageName(workloadResDTO.getImage())
				.imageType(workloadResDTO.getImageType())
				.workloadType(workloadResDTO.getType())
				.repositoryAuthType(findCredential.isPresent() ? RepositoryAuthType.PRIVATE : RepositoryAuthType.PUBLIC)
				.credentialEntity(findCredential.orElseGet(() -> null))
				.build();

			CustomImageEntity saveCustomImage = imageRepository.save(customImageEntity);
			id = saveCustomImage.getId();
		}

		return id;
	}

	private Optional<CredentialEntity> findCredentialById(Long id) {
		Optional<CredentialEntity> findCredential = Optional.empty();
		if (!ValidUtils.isNullOrZero(id)) {
			findCredential = credentialRepository.findById(id);
		}
		return findCredential;
	}

	private void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, WorkloadEntity jobEntity,
		EntityMappingType type, Map<Long, String> mountPathMap, Map<String, Map<String, String>> codeInfoMap) {
		if (ids != null) {
			for (String id : ids) {
				if (StringUtils.hasText(id)) {
					Optional<?> optionalEntity = findByIdFunction.apply(Long.valueOf(id));
					optionalEntity.ifPresent(entity -> {
						// TODO 삭제 예정
						/*if (type == EntityMappingType.DATASET) {
							Dataset dataset = (Dataset)entity;
							DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity = DatasetWorkLoadMappingEntity.builder()
								.dataset(dataset)
								.workload(jobEntity)
								.mountPath(mdAnddsMountPathMap.get(dataset.getDatasetId()))
								.build();
							datasetWorkLoadMappingRepository.save(datasetWorkLoadMappingEntity);
						} else if (type == EntityMappingType.MODEL) {
							Model model = (Model)entity;
							ModelWorkLoadMappingEntity modelWorkLoadMappingEntity = ModelWorkLoadMappingEntity.builder()
								.model(model)
								.workload(jobEntity)
								.mountPath(mdAnddsMountPathMap.get(model.getModelId()))
								.build();
							modelWorkLoadMappingRepository.save(modelWorkLoadMappingEntity);
						}*/
						if (type == EntityMappingType.VOLUME) {
							com.xiilab.modulek8sdb.volume.entity.Volume volume = (com.xiilab.modulek8sdb.volume.entity.Volume)entity;
							VolumeWorkLoadMappingEntity volumeWorkLoadMappingEntity = VolumeWorkLoadMappingEntity.builder()
								.volume(volume)
								.workload(jobEntity)
								.mountPath(mountPathMap.get(volume.getVolumeId()))
								.build();
							volumeWorkLoadMappingRepository.save(volumeWorkLoadMappingEntity);
						} else if (type == EntityMappingType.CODE) {
							CodeEntity code = (CodeEntity)entity;
							// Map<String, String> codeMountMap = codeInfoMap.get(code.getCodeURL());
							Map<String, String> codeMountMap = codeInfoMap.computeIfAbsent(code.getCodeURL(), k -> new HashMap<>());

							CodeWorkLoadMappingEntity codeWorkLoadMappingEntity = CodeWorkLoadMappingEntity.builder()
								.workload(jobEntity)
								.code(code)
								.branch(codeMountMap.getOrDefault("branch", ""))
								.mountPath(codeMountMap.getOrDefault("mountPath", ""))
								.cmd(codeMountMap.getOrDefault("command", ""))
								.build();
							codeWorkLoadMappingRepository.save(codeWorkLoadMappingEntity);
						} else if (type == EntityMappingType.IMAGE) {
							ImageEntity image = (ImageEntity)entity;
							// 잡 엔티티 이미지 업데이트
							// jobEntity.updateImage(image);
							// workloadHistoryRepo.save(jobEntity);
							ImageWorkloadMappingEntity imageWorkloadMappingEntity = ImageWorkloadMappingEntity.builder()
								.workload(jobEntity)
								.image(image)
								.build();
							imageWorkloadMappingRepository.save(imageWorkloadMappingEntity);
						} else if (type == EntityMappingType.LABEL) {
							LabelEntity label = (LabelEntity)entity;
							// 잡 엔티티 이미지 업데이트
							// jobEntity.updateImage(image);
							// workloadHistoryRepo.save(jobEntity);
							LabelWorkloadMappingEntity workloadLabel = LabelWorkloadMappingEntity.builder()
								.workload(jobEntity)
								.label(label)
								.build();
							labelWorkloadMappingRepository.save(workloadLabel);
						}
					});
				}
			}
		}
	}
}
