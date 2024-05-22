package com.xiilab.serverbatch.informer.handler;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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
import com.xiilab.modulecommon.enums.MailAttribute;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadStatus;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.service.MailService;
import com.xiilab.modulecommon.util.FileUtils;
import com.xiilab.modulecommon.util.ValidUtils;
import com.xiilab.modulecommon.vo.PageNaviParam;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.EntityMappingType;
import com.xiilab.modulek8s.common.utils.K8sInfoPicker;
import com.xiilab.modulek8s.facade.dto.AstragoDeploymentConnectPVC;
import com.xiilab.modulek8s.facade.storage.StorageModuleService;
import com.xiilab.modulek8s.facade.workload.WorkloadModuleFacadeService;
import com.xiilab.modulek8s.storage.volume.repository.VolumeRepository;
import com.xiilab.modulek8s.workload.log.service.LogService;
import com.xiilab.modulek8s.workload.svc.repository.SvcRepository;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.common.entity.RegUser;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.credential.entity.CredentialEntity;
import com.xiilab.modulek8sdb.credential.repository.CredentialRepository;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.image.entity.CustomImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.image.repository.ImageWorkloadMappingRepository;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.storage.dto.StorageDto;
import com.xiilab.modulek8sdb.storage.service.StorageService;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.entity.WorkloadEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.moduleuser.dto.UserDTO;
import com.xiilab.moduleuser.service.UserService;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ServiceList;
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
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;
	private final ImageWorkloadMappingRepository imageWorkloadMappingRepository;
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final CodeRepository codeRepository;
	private final ImageRepository imageRepository;
	private final CredentialRepository credentialRepository;
	private final SvcRepository k8sSvcRepository;
	private final ApplicationEventPublisher publisher;
	private final MailService mailService;
	private final UserService userService;
	private final LogService logService;
	private final StorageService storageService;
	private final StorageModuleService storageModuleService;
	private VolumeRepository volumeRepository;
	private final WorkloadModuleFacadeService workloadModuleFacadeService;

	@Override
	public void batchJobAddHandler(Job job) {
		//astrago에서 생성된 job filtering
		if (isAstragoResource(job)) {
			log.info("batch job {}가 생성되었습니다.", job.getMetadata().getName());
			String namespace = job.getMetadata().getNamespace();
			Container container = job.getSpec().getTemplate().getSpec().getContainers().get(0);
			K8SResourceMetadataDTO metadataFromResource = getBatchWorkloadInfoFromResource(job);
			// 잡 히스토리 저장
			if (metadataFromResource != null) {
				log.info(metadataFromResource.getCpuReq() + " " + metadataFromResource.getMemReq());
				saveJobHistory(namespace, container, metadataFromResource);
			}
		}
	}

	@Override
	@Transactional
	public void batchJobUpdateHandler(Job beforeJob, Job afterJob) {
		if (isAstragoResource(afterJob) && isResourceUpdate(beforeJob, afterJob)) {
			// 잡상태 조회
			WorkloadStatus beforeStatus = K8sInfoPicker.getBatchWorkloadStatus(beforeJob.getStatus());
			WorkloadStatus afterStatus = K8sInfoPicker.getBatchWorkloadStatus(afterJob.getStatus());
			if (isStatusChanged(beforeStatus, afterStatus)) {
				if (afterStatus == WorkloadStatus.ERROR || afterStatus == WorkloadStatus.END) {
					// 로그 저장
					workloadHistoryRepo.findByResourceName(afterJob.getMetadata().getName())
						.ifPresent(wl -> {
						if (wl.getWorkloadType() == WorkloadType.BATCH) {
							saveWorkloadLogFile(wl);
						}
					});
				}else if(afterStatus == WorkloadStatus.RUNNING){
					workloadHistoryRepo.insertWorkloadStartTime(afterJob.getMetadata().getName());
				}

				//job 상태에 따른 status 업데이트 및 노티 발송
				checkJobStatusAndUpdateStatus(afterJob);
			}
		}
	}

	@Override
	public void batchJobDeleteHandler(Job job) {
		if (!isAstragoResource(job)) {
			return;
		}

		K8SResourceMetadataDTO jobMetaData = getBatchWorkloadInfoFromResource(job);
		if (jobMetaData == null) {
			return;
		}

		updateDeleteJobStatusAndNoti(job);

		List<Volume> volumes = job.getSpec().getTemplate().getSpec().getVolumes();
		volumes.stream()
			.filter(volume -> volume.getPersistentVolumeClaim() != null)
			.forEach(volume -> deletePvAndPVC(job.getMetadata().getNamespace(), volume.getName(),
				volume.getPersistentVolumeClaim().getClaimName()));

		deleteServices(jobMetaData.getWorkspaceResourceName(), jobMetaData.getWorkloadResourceName());
	}

	@Override
	public void interactiveJobAddHandler(Deployment deployment) {
		if (isAstragoResource(deployment)) {
			log.info("interactive job {}가 생성되었습니다.", deployment.getMetadata().getName());
			String namespace = deployment.getMetadata().getNamespace();
			Container container = deployment.getSpec().getTemplate().getSpec().getContainers().get(0);
			K8SResourceMetadataDTO metadataFromResource = getInteractiveWorkloadInfoFromResource(deployment);
			// 잡 히스토리 저장
			if (metadataFromResource != null) {
				log.info(metadataFromResource.getCpuReq() + " " + metadataFromResource.getMemReq());
				saveJobHistory(namespace, container, metadataFromResource);
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

	@Override
	public void interactiveJobUpdateHandler(Deployment beforeDeployment, Deployment afterDeployment) {
		if (isAstragoResource(afterDeployment) && isResourceUpdate(beforeDeployment, afterDeployment)) {
			// 인터렉티브 상태 조회
			WorkloadStatus beforeStatus = getInteractiveWorkloadStatus(beforeDeployment.getStatus());
			WorkloadStatus afterStatus = getInteractiveWorkloadStatus(afterDeployment.getStatus());
			//status가 변경되었는지 체크
			if (isStatusChanged(beforeStatus, afterStatus)) {
				checkJobStatusAndUpdateStatus(afterDeployment);
			}
		}
	}

	@Override
	public void interactiveJobDeleteHandler(Deployment deployment) {
		if (!isAstragoResource(deployment)) {
			return;
		}

		K8SResourceMetadataDTO jobMetaData = getInteractiveWorkloadInfoFromResource(deployment);
		if (jobMetaData == null) {
			return;
		}

		updateDeleteJobStatusAndNoti(deployment);

		List<Volume> volumes = deployment.getSpec().getTemplate().getSpec().getVolumes();
		volumes.stream()
			.filter(volume -> volume.getPersistentVolumeClaim() != null)
			.forEach(volume -> deletePvAndPVC(deployment.getMetadata().getNamespace(), volume.getName(),
				volume.getPersistentVolumeClaim().getClaimName()));

		deleteServices(jobMetaData.getWorkspaceResourceName(), jobMetaData.getWorkloadResourceName());

		UserDTO.UserInfo userInfo = userService.getUserById(jobMetaData.getCreatorId());
		if (userInfo != null) {
			MailDTO mail = MailDTO.builder()
				.subject(String.format(MailAttribute.WORKLOAD_DELETE.getSubject(), jobMetaData.getWorkloadName()))
				.title(String.format(MailAttribute.WORKLOAD_DELETE.getTitle(), jobMetaData.getWorkloadName()))
				.subTitle(String.format(MailAttribute.WORKLOAD_DELETE.getSubTitle(),
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
				.footer(MailAttribute.WORKLOAD_DELETE.getFooter())
				.receiverEmail(userInfo.getEmail())
				.build();
			mailService.sendMail(mail);
		}
	}

	private boolean isStatusChanged(WorkloadStatus beforeStatus, WorkloadStatus afterStatus) {
		return beforeStatus != afterStatus;
	}

	private void checkJobStatusAndUpdateStatus(Job job) {
		WorkloadStatus workloadStatus = getBatchWorkloadStatus(job.getStatus());
		Optional<JobEntity> workload = workloadHistoryRepo.findByResourceName(job.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(workloadStatus, job.getMetadata().getName());
			handleNotificationsAndLog(wl, workloadStatus);
		});
	}

	private void updateDeleteJobStatusAndNoti(Job job) {
		Optional<JobEntity> workload = workloadHistoryRepo.findByResourceName(job.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(WorkloadStatus.END, job.getMetadata().getName());
			handleNotificationsAndLog(wl, WorkloadStatus.END);
		});
	}

	private void updateDeleteJobStatusAndNoti(Deployment deployment) {
		Optional<JobEntity> workload = workloadHistoryRepo.findByResourceName(deployment.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(WorkloadStatus.END,
				deployment.getMetadata().getName());
			handleNotificationsAndLog(wl, WorkloadStatus.END);
		});
	}

	private void checkJobStatusAndUpdateStatus(Deployment deployment) {
		WorkloadStatus workloadStatus = getInteractiveWorkloadStatus(deployment.getStatus());
		Optional<JobEntity> workload = workloadHistoryRepo.findByResourceName(deployment.getMetadata().getName());
		workload.ifPresent(wl -> {
			workloadHistoryRepo.updateWorkloadStatusByResourceName(workloadStatus, deployment.getMetadata().getName());
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

		MailAttribute mail = MailAttribute.WORKLOAD_END;
		MailDTO mailDTO = MailDTO.builder()
			.subject(String.format(mail.getSubject(), workload.getName()))
			.title(String.format(mail.getTitle(), workload.getName()))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(userService.getUserById(workload.getCreatorId()).getEmail())
			.build();

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_END, null, workload.getCreatorId(), emailTitle, title, message,
			workload.getWorkspaceResourceName(), pageNaviParam, mailDTO);

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

		MailAttribute mail = MailAttribute.WORKLOAD_START;

		MailDTO mailDTO = MailDTO.builder()
			.subject(String.format(mail.getSubject(), workloadName))
			.title(String.format(mail.getTitle(), workloadName))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(userService.getUserById(workload.getCreatorId()).getEmail())
			.build();

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

		MailAttribute mail = MailAttribute.WORKLOAD_ERROR;
		MailDTO mailDTO = MailDTO.builder()
			.subject(String.format(mail.getSubject(), workload.getName()))
			.title(String.format(mail.getTitle(), workload.getName()))
			.subTitle(String.format(mail.getSubTitle(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
			.footer(mail.getFooter())
			.receiverEmail(userService.getUserById(workload.getCreatorId()).getEmail())
			.build();

		WorkspaceUserAlertEvent workspaceUserAlertEvent = new WorkspaceUserAlertEvent(AlertRole.USER,
			AlertName.USER_WORKLOAD_ERROR,
			null, workload.getCreatorId(), emailTitle, title, message, workload.getWorkspaceResourceName(),
			pageNaviParam, mailDTO);

		publisher.publishEvent(workspaceUserAlertEvent);
	}

	private void saveJobHistory(String namespace, Container container,
		K8SResourceMetadataDTO metadataFromResource) {
		//이미 저장된 워크로드 일 경우 조기 리턴
		if (workloadHistoryRepo.findByResourceName(metadataFromResource.getWorkloadResourceName()).isPresent()) {
			return;
		}
		JobEntity jobEntity = JobEntity.jobBuilder()
			.uid(metadataFromResource.getUid())
			.name(metadataFromResource.getWorkloadName())
			.description(metadataFromResource.getDescription())
			.resourceName(metadataFromResource.getWorkloadResourceName())
			.workspaceName(metadataFromResource.getWorkspaceName())
			.workspaceResourceName(namespace)
			.envs(getEnvFromContainer(container))
			.ports(getPortFromContainer(container))
			.cpuReq(metadataFromResource.getCpuReq())
			.memReq(metadataFromResource.getMemReq())
			.gpuReq(metadataFromResource.getGpuReq())
			.workloadCmd(metadataFromResource.getCommand())
			.workingDir(metadataFromResource.getWorkingDir())
			.createdAt(metadataFromResource.getCreatedAt())
			.deletedAt(metadataFromResource.getDeletedAt())
			.creatorName(metadataFromResource.getCreatorUserName())
			.creatorId(metadataFromResource.getCreatorId())
			.creatorRealName(metadataFromResource.getCreatorFullName())
			.workloadType(metadataFromResource.getWorkloadType())
			.workspaceName(metadataFromResource.getWorkspaceName())
			.deleteYN(DeleteYN.N)
			.ide(metadataFromResource.getIde())
			.parameter(metadataFromResource.getParameter())
			.workloadStatus(WorkloadStatus.PENDING)
			.build();

		workloadHistoryRepo.save(jobEntity);

		// dataset, model mapping insert
		String datasetIds = metadataFromResource.getDatasetIds();
		saveDataMapping(getSplitIds(datasetIds), datasetRepository::findById, jobEntity, EntityMappingType.DATASET,
			metadataFromResource.getDatasetMountPathMap(), null);

		// 모델 mapping insert
		String modelIds = metadataFromResource.getModelIds();
		saveDataMapping(getSplitIds(modelIds), modelRepository::findById, jobEntity, EntityMappingType.MODEL,
			metadataFromResource.getModelMountPathMap(), null);

		RegUser regUser = new RegUser(metadataFromResource.getCreatorId(), metadataFromResource.getCreatorUserName(),
			metadataFromResource.getCreatorFullName());

		// 커스텀 소스코드 등록 후 코드 mapping insert
		String codeIds = saveCustomCode(regUser, namespace, metadataFromResource.getCodeIds(),
			metadataFromResource.getCodes());
		saveDataMapping(getSplitIds(codeIds), codeRepository::findById, jobEntity, EntityMappingType.CODE,
			null, metadataFromResource.getCodeMountPathMap());

		// 커스텀 이미지 등록 후 이미지 mapping insert
		Long imageId = saveCustomImage(regUser, metadataFromResource);
		saveDataMapping(getSplitIds(String.valueOf(imageId)), imageRepository::findById, jobEntity,
			EntityMappingType.IMAGE, null, null);
	}

	private void deleteServices(String workspaceResourceName, String workloadResourceName) {
		ServiceList serviceList = k8sSvcRepository.getServicesByResourceName(workspaceResourceName,
			workloadResourceName);
		serviceList.getItems()
			.forEach(service -> k8sSvcRepository.deleteServiceByResourceName(service.getMetadata().getName(),
				workspaceResourceName));
	}

	private void deletePvAndPVC(String workspaceResourceName, String pvName, String pvcName) {
		volumeRepository.deletePVC(pvcName, workspaceResourceName);
		volumeRepository.deletePV(pvName);
	}

	private String[] getSplitIds(String ids) {
		return ids != null ? ids.split(",") : null;
	}

	// 커스텀 소스코드 DB에 등록 후 ID 추가해서 반환
	private String saveCustomCode(RegUser regUser, String namespace, String codeIds,
		List<K8SResourceMetadataDTO.Code> codes) {
		StringBuilder result = StringUtils.hasText(codeIds) ? new StringBuilder(codeIds) : new StringBuilder();
		if (!CollectionUtils.isEmpty(codes)) {
			for (K8SResourceMetadataDTO.Code code : codes) {
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
					result.append(
						result.isEmpty() ? String.valueOf(savedCode.getId()) : "," + savedCode.getId());
				}
			}
		}

		return result.toString();
	}

	private Long saveCustomImage(RegUser regUser, K8SResourceMetadataDTO metadataFromResource) {
		Long id = metadataFromResource.getImageId();
		if (ValidUtils.isNullOrZero(metadataFromResource.getImageId()) &&
			metadataFromResource.getImageType() == ImageType.CUSTOM) {

			Optional<CredentialEntity> findCredential = Optional.empty();
			if (!ObjectUtils.isEmpty(metadataFromResource.getImageCredentialId())
				&& metadataFromResource.getImageCredentialId() > 0) {
				// 이미지 크레덴셜 조회
				findCredential = findCredentialById(metadataFromResource.getImageCredentialId());
			}

			CustomImageEntity customImageEntity = CustomImageEntity.informerBuilder()
				.regUser(regUser)
				.imageName(metadataFromResource.getImageName())
				.imageType(metadataFromResource.getImageType())
				.workloadType(metadataFromResource.getWorkloadType())
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

	private void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, JobEntity jobEntity,
		EntityMappingType type, Map<Long, String> mdAnddsMountPathMap, Map<String, Map<String, String>> codeInfoMap) {
		if (ids != null) {
			for (String id : ids) {
				if (StringUtils.hasText(id)) {
					Optional<?> optionalEntity = findByIdFunction.apply(Long.valueOf(id));
					optionalEntity.ifPresent(entity -> {
						if (type == EntityMappingType.DATASET) {
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
						} else if (type == EntityMappingType.CODE) {
							CodeEntity code = (CodeEntity)entity;
							Map<String, String> map = codeInfoMap.get(code.getCodeURL());

							CodeWorkLoadMappingEntity codeWorkLoadMappingEntity = CodeWorkLoadMappingEntity.builder()
								.workload(jobEntity)
								.code(code)
								.branch(map.getOrDefault("branch", ""))
								.mountPath(map.getOrDefault("mountPath", ""))
								.build();
							codeWorkLoadMappingRepository.save(codeWorkLoadMappingEntity);
						} else if (type == EntityMappingType.IMAGE) {
							ImageEntity image = (ImageEntity)entity;
							// 잡 엔티티 이미지 업데이트
							jobEntity.updateImage(image);
							workloadHistoryRepo.save(jobEntity);
							ImageWorkloadMappingEntity imageWorkloadMappingEntity = ImageWorkloadMappingEntity.builder()
								.workload(jobEntity)
								.image(image)
								.build();
							imageWorkloadMappingRepository.save(imageWorkloadMappingEntity);
						}
					});
				}
			}
		}
	}
}
