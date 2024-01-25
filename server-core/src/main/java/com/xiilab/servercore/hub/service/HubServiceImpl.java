package com.xiilab.servercore.hub.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	public Page<HubResDTO> getHubList(String[] categoryNames, Pageable pageable) {
		Page<HubCategoryMappingEntity> result = null;
		if (ObjectUtils.isEmpty(categoryNames)) {
			result = hubCategoryMappingRepository.findAll(pageable);
		} else {
			result = hubCategoryMappingRepository.finByHubsByCategoryNames(
				Arrays.stream(categoryNames).toList(), pageable);
		}

		List<Long> hubIds = result.getContent()
			.stream()
			.map(hubCategoryMappingEntity -> hubCategoryMappingEntity.getHubEntity().getHubId())
			.toList();
		List<HubCategoryMappingEntity> hubCategoryMappingEntityListList = hubCategoryMappingRepository.findHcmJoinFetchByHubIds(hubIds);
		// 모델 타입 String으로 배열에 넣기(key: ID, value: [type, type])
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingEntityListList);

		return Objects.requireNonNull(result)
			.map(hubCategoryMappingEntity -> new HubResDTO(hubCategoryMappingEntity.getHubEntity(), typesMap));
	}

	@Override
	public HubResDTO getHubByHubId(Long hubId) {
		HubEntity hubEntity = hubRepository.findById(hubId)
			.orElseThrow(() -> new RuntimeException("허브 상세정보가 존재하지 않습니다."));

		List<HubCategoryMappingEntity> hubCategoryMappingJoinFetchByHubId = hubCategoryMappingRepository.findHubCategoryMappingJoinFetchByHubId(
			hubId);
		Map<Long, Set<String>> typesMap = getModelTypesMap(hubCategoryMappingJoinFetchByHubId);

		return new HubResDTO(hubEntity, typesMap);
	}

	private Map<Long, Set<String>> getModelTypesMap(List<HubCategoryMappingEntity> hubCategoryMappingEntityListList) {
		Map<Long, Set<String>> typesMap = new HashMap<>();
		for (HubCategoryMappingEntity hubCategoryMappingEntity : hubCategoryMappingEntityListList) {
			Long hubId = hubCategoryMappingEntity.getHubEntity().getHubId();
			String typeName = hubCategoryMappingEntity.getHubCategoryEntity().getName();
			typesMap.computeIfAbsent(hubId, k -> new HashSet<>()).add(typeName);
		}

		return typesMap;
	}

}
