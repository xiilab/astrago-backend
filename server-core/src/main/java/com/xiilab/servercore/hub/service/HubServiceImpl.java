package com.xiilab.servercore.hub.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.xiilab.modulek8sdb.common.enums.NetworkCloseYN;
import com.xiilab.modulek8sdb.hub.entity.HubCategoryMappingEntity;
import com.xiilab.modulek8sdb.hub.entity.HubEntity;
import com.xiilab.modulek8sdb.hub.repository.HubCategoryMappingRepository;
import com.xiilab.modulek8sdb.hub.repository.HubRepository;
import com.xiilab.modulek8sdb.image.entity.HubImageEntity;
import com.xiilab.modulek8sdb.network.entity.NetworkEntity;
import com.xiilab.modulek8sdb.network.repository.NetworkRepository;
import com.xiilab.servercore.hub.dto.request.HubReqDTO;
import com.xiilab.servercore.hub.dto.response.FindHubCommonResDTO;
import com.xiilab.servercore.hub.dto.response.FindHubInWorkloadResDTO;
import com.xiilab.servercore.hub.dto.response.FindHubResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HubServiceImpl implements HubService {
	private final HubCategoryMappingRepository hubCategoryMappingRepository;
	private final HubRepository hubRepository;
	private final NetworkRepository networkRepository;
	@Value("${astrago.private-registry-url}")
	private String privateRegistryUrl;

	@Override
	public FindHubResDTO.Hubs getHubList(String searchText, String[] categoryNames, Pageable pageable) {
		if (ObjectUtils.isEmpty(categoryNames)) {
			return getHubAllList(searchText, pageable);
		} else {
			// 카테고리 네임으로 조회한 허브 목록
			return getHubListByCategoryNames(searchText, categoryNames, pageable);
		}
	}

	@Override
	public FindHubResDTO.HubDetail getHubByHubId(Long hubId) {
		HubEntity hubEntity = hubRepository.findById(hubId)
			.orElseThrow(() -> new RestApiException(CommonErrorCode.HUB_NOT_FOUND));

		List<HubCategoryMappingEntity> hubCategoryMappingJoinFetchByHubId = hubCategoryMappingRepository.findHubCategoryMappingJoinFetchByHubId(
			hubId);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingJoinFetchByHubId);

		//폐쇄망 체크 후 code, image url 세팅
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		NetworkCloseYN networkCloseYN = network.getNetworkCloseYN();
		FindHubResDTO.HubDetail hubDetail = FindHubResDTO.HubDetail.from(hubEntity, typesMap);

		return hubDetail;
	}

	@Override
	public FindHubInWorkloadResDTO.Hubs getHubListInWorkload(WorkloadType workloadType) {
		List<HubEntity> findAll = hubRepository.findByWorkloadType(workloadType);
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		NetworkCloseYN networkCloseYN = network.getNetworkCloseYN();

		return FindHubInWorkloadResDTO.Hubs.from(findAll, findAll.size(), networkCloseYN,privateRegistryUrl);
	}

	@Override
	@Transactional
	public void saveHub(HubReqDTO.SaveHub saveHubDTO) {
		try {
			String strEnvJson = serializeToJson(saveHubDTO.getEnvMap());
			String strParamJson = serializeToJson(saveHubDTO.getParameter());
			HubImageEntity hubImageEntity = createHubImageEntity(saveHubDTO);
			HubEntity hubEntity = createHubEntity(saveHubDTO, strEnvJson, strParamJson, hubImageEntity);
			hubRepository.save(hubEntity);
		} catch (JsonProcessingException e) {
			throw new RestApiException(HubErrorCode.FAILED_ENV_MAP_TO_JSON);
		}
	}
	@Override
	public String getHubReadMe(Long hubId) {
		FindHubResDTO.HubDetail hub = getHubByHubId(hubId);
		String readmeFileName = hub.getReadmeFileName();
		// ClassPathResource를 사용하여 resources 디렉토리 내의 파일을 읽음
		Resource resource = new ClassPathResource("static/hub/" + readmeFileName);
		try (InputStream inputStream = resource.getInputStream();
			 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return reader.lines().collect(Collectors.joining("\n"));
		}
		catch (IOException e) {
			log.error(e.toString());
			throw new RestApiException(HubErrorCode.GET_FAILED_HUB_REAM_ME);
		}
	}

	private static HubEntity createHubEntity(HubReqDTO.SaveHub saveHubDTO, String strEnvJson, String strParamJson,
		HubImageEntity hubImageEntity) {
		return HubEntity.saveBuilder()
			.title(saveHubDTO.getTitle())
			.description(saveHubDTO.getDescription())
			.sourceCodeMountPath(saveHubDTO.getSourceCodeMountPath())
			.datasetMountPath(saveHubDTO.getDatasetMountPath())
			.modelMountPath(saveHubDTO.getModelMountPath())
			.envs(strEnvJson)
			.command(saveHubDTO.getCommand())
			.thumbnailFileName("")
			.readmeFileName("")
			.parameter(strParamJson)
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
	// null 체크와 함께 isBlank를 수행하는 메서드
	public static boolean isBlankSafe(String str) {
		return str == null || str.isBlank();
	}
	/* 검색 조건 없을 때, List 반환 */
	private FindHubResDTO.Hubs getHubAllList(String searchText, Pageable pageable) {
		Page<HubEntity> hubs = hubRepository.findHubs(searchText, pageable);
		long totalElements = hubs.getTotalElements();
		List<HubEntity> hubEntities = hubs.getContent();

		// 각 허브에 매핑되어 있는 타입 목록 조회
		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);
		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		NetworkCloseYN networkCloseYN = network.getNetworkCloseYN();
		return FindHubResDTO.Hubs.from(hubEntities, totalElements, typesMap, networkCloseYN);
	}

	/* 카테고리 검색 List 반환 */
	private FindHubResDTO.Hubs getHubListByCategoryNames(String searchText, String[] categoryNames, Pageable pageable) {
		// 카테고리 이름으로 hub 목록 조회
		Page<HubCategoryMappingEntity> finByHubsByCategoryNames = hubCategoryMappingRepository.findHubCategoryMapping(
			searchText,
			Arrays.stream(categoryNames).toList(), null, pageable);

		List<HubCategoryMappingEntity> hubList = finByHubsByCategoryNames.getContent();
		long totalElements = finByHubsByCategoryNames.getTotalElements();
		// 각 허브에 등록되어 있는 모든 카테고리 목록 조회
		List<HubEntity> hubEntities = hubList.stream().map(HubCategoryMappingEntity::getHubEntity).toList();

		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);

		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);

		NetworkEntity network = networkRepository.findTopBy(Sort.by("networkId").descending());
		NetworkCloseYN networkCloseYN = network.getNetworkCloseYN();
		return FindHubResDTO.Hubs.from(hubEntities, totalElements, typesMap, networkCloseYN);
	}

	/* 허브 카테고리 매핑 테이블 전체 조회 */
	private List<HubCategoryMappingEntity> getHubCategoryMappingEntityList(List<HubEntity> hubEntities) {
		// hubID만 추출
		List<Long> hubIds = hubEntities.stream()
			.map(HubEntity::getHubId)
			.toList();
		Page<HubCategoryMappingEntity> hubs = hubCategoryMappingRepository.findHubCategoryMapping(null, null, hubIds,
			null);
		return hubs.getContent();
	}

	/* 모델 타입 String으로 배열에 넣기(key: ID, value: [hubCategoryType, hubCategoryType]) */
	private Map<Long, Set<String>> getModelTypesMap(List<HubCategoryMappingEntity> hubCategoryMappingEntityList) {
		Map<Long, Set<String>> typesMap = new HashMap<>();
		for (HubCategoryMappingEntity hubCategoryMappingEntity : hubCategoryMappingEntityList) {
			Long hubId = hubCategoryMappingEntity.getHubEntity().getHubId();
			String typeName = hubCategoryMappingEntity.getHubCategoryEntity().getHubLabelType().name();
			typesMap.computeIfAbsent(hubId, k -> new HashSet<>()).add(typeName);
		}

		return typesMap;
	}

	/* MAP -> JSON 문자열로 변환 */
	private String serializeToJson(Map<String, String> envMap) throws JsonProcessingException {
		return envMap != null && !envMap.isEmpty() ? new ObjectMapper().writeValueAsString(envMap) : "{}";
	}
}
