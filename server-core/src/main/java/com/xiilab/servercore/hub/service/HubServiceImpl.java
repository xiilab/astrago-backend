package com.xiilab.servercore.hub.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiilab.modulecommon.enums.ImageType;
import com.xiilab.modulecommon.enums.RepositoryAuthType;
import com.xiilab.modulecommon.enums.WorkloadType;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;

import com.xiilab.modulecommon.exception.errorcode.HubErrorCode;
import com.xiilab.modulek8sdb.image.entity.HubImageEntity;
import com.xiilab.modulek8sdb.image.entity.ImageEntity;
import com.xiilab.modulek8sdb.image.repository.ImageRepository;
import com.xiilab.servercore.hub.dto.HubReqDTO;
import com.xiilab.servercore.hub.dto.HubResDTO;
import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
import com.xiilab.modulek8sdb.hub.repository.HubCategoryMappingRepository;
import com.xiilab.modulek8sdb.hub.repository.HubRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.internal.util.MapUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {
	private final HubCategoryMappingRepository hubCategoryMappingRepository;
	private final HubRepository hubRepository;
	private final ImageRepository imageRepository;

	@Override
	public HubResDTO.FindHubs getHubList(String[] categoryNames, Pageable pageable) {
		if (ObjectUtils.isEmpty(categoryNames)) {
			return getHubAllList(pageable);
		} else {
			// 카테고리 네임으로 조회한 허브 목록
			return getHubListByCategoryNames(categoryNames, pageable);
		}
	}

	@Override
	public HubResDTO.FindHub getHubByHubId(Long hubId) {
		HubEntity hubEntity = hubRepository.findById(hubId)
			.orElseThrow(() -> new RestApiException(CommonErrorCode.HUB_NOT_FOUND));

		List<HubCategoryMappingEntity> hubCategoryMappingJoinFetchByHubId = hubCategoryMappingRepository.findHubCategoryMappingJoinFetchByHubId(hubId);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingJoinFetchByHubId);

		return HubResDTO.FindHub.from(hubEntity, typesMap);
	}

	@Override
	public HubResDTO.FindHubsInWorkload getHubListInWorkload(WorkloadType workloadType) {
		List<HubEntity> findAll = hubRepository.findByWorkloadType(workloadType);
		return HubResDTO.FindHubsInWorkload.from(findAll, findAll.size());
	}

	@Override
	@Transactional
	public void saveHub(HubReqDTO.SaveHub saveHubDTO) {
		try {
			String strEnvJson = serializeToJson(saveHubDTO.getEnvMap());
			HubImageEntity hubImageEntity = createHubImageEntity(saveHubDTO);
			HubEntity hubEntity = createHubEntity(saveHubDTO, strEnvJson, hubImageEntity);
			hubRepository.save(hubEntity);
		} catch (JsonProcessingException e) {
			throw new RestApiException(HubErrorCode.FAILED_ENV_MAP_TO_JSON);
		}
	}

	private static HubEntity createHubEntity(HubReqDTO.SaveHub saveHubDTO, String strEnvJson,
		HubImageEntity hubImageEntity) {
		return HubEntity.saveBuilder()
			.title(saveHubDTO.getTitle())
			.description(saveHubDTO.getDescription())
			.thumbnailURL(saveHubDTO.getThumbnailURL())
			.readmeURL(saveHubDTO.getReadmeURL())
			.sourceCodeUrl(saveHubDTO.getSourceCodeUrl())
			.sourceCodeBranch("master")
			.sourceCodeMountPath(saveHubDTO.getSourceCodeMountPath())
			.datasetMountPath(saveHubDTO.getDatasetMountPath())
			.modelMountPath(saveHubDTO.getModelMountPath())
			.envs(strEnvJson)
			.command(saveHubDTO.getCommand())
			.workloadType(saveHubDTO.getWorkloadType())
			.hubImageEntity(hubImageEntity)
			.build();
	}

	private static HubImageEntity createHubImageEntity(HubReqDTO.SaveHub saveHubDTO) {
		return HubImageEntity.builder()
			.imageName(saveHubDTO.getImageName())
			.repositoryAuthType(RepositoryAuthType.PUBLIC)
			.imageType(ImageType.HUB)
			.workloadType(saveHubDTO.getWorkloadType())
			.build();
	}

	/* 검색 조건 없을 때, List 반환 */
	private HubResDTO.FindHubs getHubAllList(Pageable pageable) {
		Page<HubEntity> findAll = hubRepository.findAll(pageable);
		long totalElements = findAll.getTotalElements();
		List<HubEntity> hubEntities = findAll.getContent();

		// 각 허브에 매핑되어 있는 타입 목록 조회
		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);

		return HubResDTO.FindHubs.from(hubEntities, totalElements, typesMap);
	}

	/* 카테고리 검색 List 반환 */
	private HubResDTO.FindHubs getHubListByCategoryNames(String[] categoryNames, Pageable pageable) {
		// 카테고리 이름으로 hub 목록 조회
		Page<HubCategoryMappingEntity> finByHubsByCategoryNames = hubCategoryMappingRepository.findHubs(
			Arrays.stream(categoryNames).toList(), null, pageable);

		List<HubCategoryMappingEntity> hubList = finByHubsByCategoryNames.getContent();
		long totalElements = finByHubsByCategoryNames.getTotalElements();
		// 각 허브에 등록되어 있는 모든 카테고리 목록 조회
		List<HubEntity> hubEntities = hubList.stream().map(HubCategoryMappingEntity::getHubEntity).toList();

		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);

		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);
		return HubResDTO.FindHubs.from(hubEntities, totalElements, typesMap);
	}

	/* 허브 카테고리 매핑 테이블 전체 조회 */
	private List<HubCategoryMappingEntity> getHubCategoryMappingEntityList(List<HubEntity> hubEntities) {
		// hubID만 추출
		List<Long> hubIds = hubEntities.stream()
			.map(HubEntity::getHubId)
			.toList();
		Page<HubCategoryMappingEntity> hubs = hubCategoryMappingRepository.findHubs(null, hubIds, null);
		return hubs.getContent();
	}

	/* 모델 타입 String으로 배열에 넣기(key: ID, value: [repositoryType, repositoryType]) */
	private Map<Long, Set<String>> getModelTypesMap(List<HubCategoryMappingEntity> hubCategoryMappingEntityList) {
		Map<Long, Set<String>> typesMap = new HashMap<>();
		for (HubCategoryMappingEntity hubCategoryMappingEntity : hubCategoryMappingEntityList) {
			Long hubId = hubCategoryMappingEntity.getHubEntity().getHubId();
			String typeName = hubCategoryMappingEntity.getHubCategoryEntity().getName();
			typesMap.computeIfAbsent(hubId, k -> new HashSet<>()).add(typeName);
		}

		return typesMap;
	}

	/* MAP -> JSON 문자열로 변환 */
	private String serializeToJson(Map<String, String> envMap) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return envMap != null && !envMap.isEmpty() ? objectMapper.writeValueAsString(envMap) : "{}";
	}
}
