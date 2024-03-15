package com.xiilab.servercore.workload.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.WorkloadErrorCode;
import com.xiilab.modulek8s.common.dto.AgeDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleBatchJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleInteractiveJobResDTO;
import com.xiilab.modulek8s.workload.dto.response.ModuleWorkloadResDTO;
import com.xiilab.modulek8s.workload.enums.WorkloadStatus;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8s.common.enumeration.EntityMappingType;
import com.xiilab.modulek8sdb.dataset.entity.Dataset;
import com.xiilab.modulek8sdb.dataset.entity.DatasetWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.entity.ModelWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.dataset.repository.DatasetRepository;
import com.xiilab.modulek8sdb.dataset.repository.DatasetWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageWorkloadMappingEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.modulek8sdb.image.repository.ImageWorkloadMappingRepository;
import com.xiilab.modulek8sdb.model.entity.Model;
import com.xiilab.modulek8sdb.model.repository.ModelRepository;
import com.xiilab.modulek8sdb.model.repository.ModelWorkLoadMappingRepository;
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepoCusotm;
import com.xiilab.moduleuser.dto.UserInfoDTO;
import com.xiilab.servercore.workload.dto.request.WorkloadHistoryReqDTO;
import com.xiilab.servercore.workload.dto.response.FindWorkloadResDTO;
import com.xiilab.servercore.workload.dto.response.WorkloadHistoryResDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkloadHistoryServiceImpl implements WorkloadHistoryService {
	private final WorkloadHistoryRepo workloadHistoryRepo;
	private final WorkloadHistoryRepoCusotm workloadHistoryRepoCusotm;
	private final DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository;
	private final ModelWorkLoadMappingRepository modelWorkLoadMappingRepository;
	private final CodeWorkLoadMappingRepository codeWorkLoadMappingRepository;
	private final ImageWorkloadMappingRepository imageWorkloadMappingRepository;
	private final DatasetRepository datasetRepository;
	private final ModelRepository modelRepository;
	private final CodeRepository codeRepository;
	private final ImageRepository imageRepository;

	@Override
	public List<ModuleBatchJobResDTO> getBatchWorkloadHistoryList(String workspaceName, String searchName,
		String userId) {
		List<JobEntity> batchJobEntityList = workloadHistoryRepoCusotm.findBatchWorkloadHistoryByCondition(
			workspaceName, searchName, userId, WorkloadType.BATCH);
		return batchJobEntityList.stream().map(job -> ModuleBatchJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulecommon.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.remainTime(0)
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public List<ModuleInteractiveJobResDTO> getInteractiveWorkloadHistoryList(String workspaceName, String searchName,
		String userId) {
		List<JobEntity> batchJobEntityList = workloadHistoryRepoCusotm.findBatchWorkloadHistoryByCondition(
			workspaceName, searchName, userId, WorkloadType.INTERACTIVE);
		return batchJobEntityList.stream().map(job -> ModuleInteractiveJobResDTO.builder()
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulecommon.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.build())
			.collect(Collectors.toList());
	}

	@Override
	public ModuleWorkloadResDTO getWorkloadHistoryById(long id) {
		JobEntity job = workloadHistoryRepo.findById(id).orElseThrow();
		if (job.getWorkloadType() == WorkloadType.BATCH) {
			return ModuleBatchJobResDTO.builder()
				.uid(String.valueOf(job.getId()))
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulecommon.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.remainTime(0)
				.build();
		} else {
			return ModuleInteractiveJobResDTO.builder()
				.name(job.getName())
				.description(job.getDescription())
				.status(WorkloadStatus.END)
				.workspaceName(job.getWorkspaceName())
				.workspaceResourceName(job.getWorkspaceResourceName())
				.type(com.xiilab.modulecommon.enums.WorkloadType.BATCH)
				.createdAt(job.getCreatedAt())
				.deletedAt(job.getDeletedAt())
				.age(new AgeDTO(job.getCreatedAt()))
				.command(job.getWorkloadCMD())
				.cpuRequest(String.valueOf(job.getCpuRequest()))
				.memRequest(String.valueOf(job.getMemRequest()))
				.gpuRequest(String.valueOf(job.getGpuRequest()))
				.build();
		}
	}

	@Override
	public FindWorkloadResDTO.WorkloadDetail getWorkloadInfoByResourceName(String workspaceName, String workloadResourceName) {
		JobEntity jobEntity = workloadHistoryRepo.findByWorkspaceResourceNameAndResourceName(
			workspaceName, workloadResourceName).orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_LOAD_WORKLOAD_INFO));
		return FindWorkloadResDTO.WorkloadDetail.from(jobEntity);
	}

	@Override
	public void deleteWorkloadHistory(long id, UserInfoDTO userInfoDTO) {
		JobEntity jobEntity = workloadHistoryRepo.findById(id).orElseThrow();
		if (!jobEntity.getCreatorId().equals(userInfoDTO.getId())) {
			workloadHistoryRepo.deleteById(id);
		} else {
			throw new IllegalArgumentException("해당 유저는 워크스페이스 삭제 권한이 없습니다.");
		}
	}

	@Override
	public void saveWorkloadHistory(WorkloadHistoryReqDTO.CreateWorkloadHistory createWorkloadHistory) {
		// IMAGE 찾기
		long imageId = Long.parseLong(createWorkloadHistory.getImageId());
		ImageEntity imageEntity = imageRepository.findById(imageId).orElseThrow(() -> new RestApiException(WorkloadErrorCode.FAILED_SAVE_WORKLOAD_HISTORY));

		JobEntity jobEntity = JobEntity.jobBuilder()
			.name(createWorkloadHistory.getName())
			.description(createWorkloadHistory.getDescription())
			.resourceName(createWorkloadHistory.getResourceName())
			.workspaceName(createWorkloadHistory.getWorkspaceName())
			.workspaceResourceName(createWorkloadHistory.getWorkspaceResourceName())
			.ports(createWorkloadHistory.getPorts())
			.envs(createWorkloadHistory.getEnvs())
			.cpuReq(createWorkloadHistory.getCpuRequest())
			.memReq(createWorkloadHistory.getMemRequest())
			.gpuReq(createWorkloadHistory.getGpuRequest())
			.workloadCmd(createWorkloadHistory.getCmd())
			.createdAt(createWorkloadHistory.getCreatedAt())
			.creatorRealName(createWorkloadHistory.getCreatorRealName())
			.creatorName(createWorkloadHistory.getCreatorName())
			.creatorId(createWorkloadHistory.getCreatorId())
			.workloadType(WorkloadType.BATCH)
			.image(imageEntity)
			.build();
		JobEntity job = workloadHistoryRepo.save(jobEntity);

		// Dataset Mapping 엔티티 추가
		saveDataMapping(getSplitIds(createWorkloadHistory.getDatasetIds()), datasetRepository::findById, job, EntityMappingType.DATASET, createWorkloadHistory.getDatasetInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getModelIds()), modelRepository::findById, job, EntityMappingType.MODEL, createWorkloadHistory.getModelInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getCodeIds()), codeRepository::findById, job, EntityMappingType.CODE, createWorkloadHistory.getCodesInfoMap());
		saveDataMapping(getSplitIds(createWorkloadHistory.getImageId()), imageRepository::findById, job, EntityMappingType.IMAGE, null);

	}

	private String[] getSplitIds(String ids) {
		return ids != null? ids.split(",") : null;
	}

	// 데이터셋 또는 모델 정보를 저장하는 메서드
	private void saveDataMapping(String[] ids, Function<Long, Optional<?>> findByIdFunction, JobEntity jobEntity, EntityMappingType type, Map<Long, Map<String, String>> infoMap) {
		if (ids != null) {
			for (String id : ids) {
				if (StringUtils.hasText(id)) {
					Optional<?> optionalEntity = findByIdFunction.apply(Long.valueOf(id));
					optionalEntity.ifPresent(entity -> {
						if(type == EntityMappingType.DATASET){
							Dataset dataset = (Dataset)entity;
							Map<String, String> datasetInfoMap = infoMap.get(dataset.getDatasetId());
							DatasetWorkLoadMappingEntity datasetWorkLoadMappingEntity = DatasetWorkLoadMappingEntity.builder()
								.dataset(dataset)
								.workload(jobEntity)
								.mountPath(datasetInfoMap.getOrDefault("mountPath", ""))
								.build();
							datasetWorkLoadMappingRepository.save(datasetWorkLoadMappingEntity);
						}else if(type == EntityMappingType.MODEL){
							Model model = (Model)entity;
							Map<String, String> modelInfoMap = infoMap.get(model.getModelId());

							ModelWorkLoadMappingEntity modelWorkLoadMappingEntity = ModelWorkLoadMappingEntity.builder()
								.model(model)
								.workload(jobEntity)
								.mountPath(modelInfoMap.getOrDefault("mountPath", ""))
								.build();
							modelWorkLoadMappingRepository.save(modelWorkLoadMappingEntity);
						}else if(type == EntityMappingType.CODE){
							CodeEntity code = (CodeEntity)entity;
							Map<String, String> codeInfoMap = infoMap.get(code.getId());

							CodeWorkLoadMappingEntity codeWorkLoadMappingEntity = CodeWorkLoadMappingEntity.builder()
								.workload(jobEntity)
								.code(code)
								.branch(codeInfoMap.getOrDefault("branch", ""))
								.mountPath(codeInfoMap.getOrDefault("mountPath", ""))
								.build();
							codeWorkLoadMappingRepository.save(codeWorkLoadMappingEntity);
						} else if (type == EntityMappingType.IMAGE) {
							ImageEntity image = (ImageEntity) entity;

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
