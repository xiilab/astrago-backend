package com.xiilab.servercore.image.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.xiilab.modulek8s.workload.enums.WorkloadType;
import com.xiilab.modulek8sdb.image.dto.response.BuiltInImageResDTO;
import com.xiilab.modulek8sdb.image.entity.BuiltInImageEntity;
import com.xiilab.modulek8sdb.image.repository.BuiltInImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuiltInImageServiceImpl implements BuiltInImageService {
	private final BuiltInImageRepository builtInImageRepository;

	@Override
	public BuiltInImageResDTO getBuiltInImageById(Long id) {
		BuiltInImageEntity builtInImageEntity = builtInImageRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("조회하려는 빌트인 이미지 정보를 조회할 수 없습니다."));
		return BuiltInImageResDTO.builtInImageEntityToDTO(builtInImageEntity);
	}

	@Override
	public List<BuiltInImageResDTO> getBuiltInImageList(WorkloadType workloadType) {
		return builtInImageRepository.findByType(workloadType)
			.stream()
			.map(BuiltInImageResDTO::builtInImageEntityToDTO)
			.toList();
	}
}
