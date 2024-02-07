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

import com.xiilab.servercore.hub.dto.HubDTO;
import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.CommonErrorCode;
import com.xiilab.servercore.hub.dto.HubResDTO;
import com.xiilab.servercore.hub.entity.HubCategoryMappingEntity;
import com.xiilab.servercore.hub.entity.HubEntity;
import com.xiilab.servercore.hub.repository.HubCategoryMappingRepository;
import com.xiilab.servercore.hub.repository.HubRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubServiceImpl implements HubService {
	private final HubCategoryMappingRepository hubCategoryMappingRepository;
	private final HubRepository hubRepository;

	@Override
	public HubDTO.Response.HubsDto getHubList(String[] categoryNames, Pageable pageable) {
		if (ObjectUtils.isEmpty(categoryNames)) {
			return getHubList(pageable);
		} else {
			// 카테고리 네임으로 조회한 허브 목록
			return getHubListByCategoryNames(categoryNames, pageable);
		}

	}

	@Override
	public HubDTO.Response.HubDetailDto getHubByHubId(Long hubId) {
		HubEntity hubEntity = hubRepository.findById(hubId)
			.orElseThrow(() -> new RestApiException(CommonErrorCode.HUB_NOT_FOUND));

		List<HubCategoryMappingEntity> hubCategoryMappingJoinFetchByHubId = hubCategoryMappingRepository.findHubCategoryMappingJoinFetchByHubId(
			hubId);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingJoinFetchByHubId);

		return HubDTO.Response.HubDetailDto.hubEntityToHubDetailDto(hubEntity, typesMap);
	}

	/* 검색 조건 없을 때, List 반환 */
	private HubDTO.Response.HubsDto getHubList(Pageable pageable) {
		Page<HubEntity> findAll = hubRepository.findAll(pageable);
		List<HubEntity> hubEntities = findAll.getContent();
		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);

		long totalElements = findAll.getTotalElements();
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);
		return HubDTO.Response.HubsDto.hubEntitiesToDto(hubEntities, totalElements, typesMap);
	}

	/* 카테고리 검색 List 반환 */
	private HubDTO.Response.HubsDto getHubListByCategoryNames(String[] categoryNames, Pageable pageable) {
		Page<HubCategoryMappingEntity> finByHubsByCategoryNames = hubCategoryMappingRepository.finByHubsByCategoryNames(
				Arrays.stream(categoryNames).toList(), pageable);

		List<HubCategoryMappingEntity> hubList = finByHubsByCategoryNames.getContent();

		List<HubEntity> hubEntities = hubList.stream().map(HubCategoryMappingEntity::getHubEntity).toList();
		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = getHubCategoryMappingEntityList(hubEntities);

		long totalElements = finByHubsByCategoryNames.getTotalElements();
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityList);
		return HubDTO.Response.HubsDto.hubEntitiesToDto(hubEntities, totalElements, typesMap);
	}

	/* 허브 카테고리 매핑 테이블 전체 조회 */
	private List<HubCategoryMappingEntity> getHubCategoryMappingEntityList(List<HubEntity> hubEntities) {
		List<Long> hubIds = extractHubIds(hubEntities);
		List<HubCategoryMappingEntity> hubCategoryMappingEntityList = hubCategoryMappingRepository.findHcmJoinFetchByHubIds(hubIds);
		return hubCategoryMappingEntityList;
	}

	/*HUB ID만 추출*/
	private static List<Long> extractHubIds(List<HubEntity> hubList) {
		return hubList.stream()
			.map(HubEntity::getHubId)
			.toList();
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
}
