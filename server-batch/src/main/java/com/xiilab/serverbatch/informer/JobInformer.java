package com.xiilab.serverbatch.informer;

import static com.xiilab.modulek8s.common.utils.K8sInfoPicker.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.RepositoryType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.util.NumberValidUtils;
import com.xiilab.modulek8s.common.dto.K8SResourceMetadataDTO;
import com.xiilab.modulek8s.common.enumeration.AnnotationField;
import com.xiilab.modulek8sdb.code.entity.CodeEntity;
import com.xiilab.modulek8sdb.code.entity.CodeWorkLoadMappingEntity;
import com.xiilab.modulek8sdb.code.repository.CodeRepository;
import com.xiilab.modulek8sdb.code.repository.CodeWorkLoadMappingRepository;
import com.xiilab.modulek8s.common.enumeration.EntityMappingType;
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
import com.xiilab.modulek8sdb.workload.history.entity.JobEntity;
import com.xiilab.modulek8sdb.workload.history.repository.WorkloadHistoryRepo;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Namespace;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Component
@Slf4j
public abstract class JobInformer {
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

	protected JobInformer(WorkloadHistoryRepo workloadHistoryRepo,
		DatasetWorkLoadMappingRepository datasetWorkLoadMappingRepository,
		ModelWorkLoadMappingRepository modelWorkLoadMappingRepository,
		CodeWorkLoadMappingRepository codeWorkLoadMappingRepository,
		ImageWorkloadMappingRepository imageWorkloadMappingRepository,
		DatasetRepository datasetRepository,
		ModelRepository modelRepository,
		CodeRepository codeRepository,
		ImageRepository imageRepository,
		CredentialRepository credentialRepository) {
		this.workloadHistoryRepo = workloadHistoryRepo;
		this.datasetWorkLoadMappingRepository = datasetWorkLoadMappingRepository;
		this.modelWorkLoadMappingRepository = modelWorkLoadMappingRepository;
		this.codeWorkLoadMappingRepository = codeWorkLoadMappingRepository;
		this.imageWorkloadMappingRepository = imageWorkloadMappingRepository;
		this.datasetRepository = datasetRepository;
		this.modelRepository = modelRepository;
		this.codeRepository = codeRepository;
		this.imageRepository = imageRepository;
		this.credentialRepository = credentialRepository;
	}

	@Transactional
	protected void saveJobHistory(String namespace, Namespace namespaceObject, Container container,
		K8SResourceMetadataDTO metadataFromResource) {
		// TODO metadataFromResource.getCpuReq() 확인 필요
		JobEntity jobEntity = JobEntity.jobBuilder()
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
			.createdAt(metadataFromResource.getCreatedAt())
			.deletedAt(metadataFromResource.getDeletedAt())
			.creatorName(metadataFromResource.getCreatorUserName())
			.creatorId(metadataFromResource.getCreatorId())
			.creatorRealName(metadataFromResource.getCreatorFullName())
			.workloadType(metadataFromResource.getWorkloadType())
			.workspaceName(metadataFromResource.getWorkspaceName())
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
		String codeIds = saveCustomCode(regUser, namespace, metadataFromResource.getCodeIds(), metadataFromResource.getCodes());
		saveDataMapping(getSplitIds(codeIds), codeRepository::findById, jobEntity, EntityMappingType.CODE,
			null, metadataFromResource.getCodeMountPathMap());

		// 커스텀 이미지 등록 후 이미지 mapping insert
		Long imageId = saveCustomImage(regUser, metadataFromResource);
		saveDataMapping(getSplitIds(String.valueOf(imageId)), imageRepository::findById, jobEntity,
			EntityMappingType.IMAGE, null, null);
	}

	// TODO 서비스, PV, PVC 삭제로직 필요

	private String[] getSplitIds(String ids) {
		return ids != null ? ids.split(",") : null;
	}

	// 커스텀 소스코드 DB에 등록 후 ID 추가해서 반환
	private String saveCustomCode(RegUser regUser, String namespace, String codeIds, List<K8SResourceMetadataDTO.Code> codes) {
		StringBuilder result = StringUtils.hasText(codeIds) ? new StringBuilder(codeIds) : new StringBuilder();
		if (!CollectionUtils.isEmpty(codes)) {
			for (K8SResourceMetadataDTO.Code code : codes) {
				// 커스텀 소스코드일 경우
				if (NumberValidUtils.isNullOrZero(code.getSourceCodeId())
					&& code.getRepositoryType() == RepositoryType.USER) {
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
		if (NumberValidUtils.isNullOrZero(metadataFromResource.getImageId()) &&
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
		if (!NumberValidUtils.isNullOrZero(id)) {
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
