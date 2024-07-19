package com.xiilab.servercore.preset.service;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.exception.RestApiException;
import com.xiilab.modulecommon.exception.errorcode.ResourcePresetErrorCode;
import com.xiilab.modulecommon.util.TypeConversionUtils;
import com.xiilab.modulek8sdb.preset.entity.ResourcePresetEntity;
import com.xiilab.modulek8sdb.preset.repository.ResourcePresetRepository;
import com.xiilab.servercore.preset.dto.request.ResourcePresetReqDTO;
import com.xiilab.servercore.preset.dto.response.ResourcePresetResDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ResourcePresetServiceImpl implements ResourcePresetService {
	private final ResourcePresetRepository resourcePresetRepository;

	@Override
	public Long saveResourcePreset(ResourcePresetReqDTO.SaveResourcePreset saveResourcePreset) {
		ResourcePresetEntity resourcePresetEntity = ResourcePresetEntity.saveResourcePresetBuilder()
			.title(saveResourcePreset.getTitle())
			.description(saveResourcePreset.getDescription())
			.launcherCpuUsage(!Objects.isNull(saveResourcePreset.getLauncherCpuUsage())? BigDecimal.valueOf(saveResourcePreset.getLauncherCpuUsage()) : null)
			.launcherMemUsage(!Objects.isNull(saveResourcePreset.getLauncherMemUsage())? BigDecimal.valueOf(saveResourcePreset.getLauncherMemUsage()) : null)
			.cpuUsage(BigDecimal.valueOf(saveResourcePreset.getCpuUsage()))
			.memUsage(BigDecimal.valueOf(saveResourcePreset.getMemUsage()))
			.gpuUsage(saveResourcePreset.getGpuUsage())
			.nodeType(saveResourcePreset.getNodeType())
			.build();

		ResourcePresetEntity saveResourcePresetEntity = resourcePresetRepository.save(resourcePresetEntity);
		return saveResourcePresetEntity.getId();
	}

	@Override
	@Transactional(readOnly = true)
	public ResourcePresetResDTO.FindResourcePresetDetail findResourcePresetById(Long resourcePresetId) {
		ResourcePresetEntity resourcePresetEntity = resourcePresetRepository.findById(resourcePresetId)
			.orElseThrow(() -> new RestApiException(ResourcePresetErrorCode.NOT_FOUND_RESOURCE_PRESET));
		return ResourcePresetResDTO.FindResourcePresetDetail.from(resourcePresetEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public ResourcePresetResDTO.FindResourcePresets findResourcePresets(
		ResourcePresetReqDTO.FindSearchCondition findSearchCondition) {
		Page<ResourcePresetEntity> resourcePresets = resourcePresetRepository.findResourcePresets(
			findSearchCondition.getPage(), findSearchCondition.getSize(), findSearchCondition.getNodeType());
		return ResourcePresetResDTO.FindResourcePresets.from(resourcePresets.getContent(),
			resourcePresets.getTotalElements());
	}

	@Override
	public void updateResourcePreset(ResourcePresetReqDTO.UpdateResourcePreset updateResourcePreset) {
		if (!resourcePresetRepository.existsById(updateResourcePreset.getId())) {
			throw new RestApiException(ResourcePresetErrorCode.NOT_FOUND_RESOURCE_PRESET);
		}

		ResourcePresetEntity resourcePresetEntity = ResourcePresetEntity.updateResourcePresetBuilder()
			.id(updateResourcePreset.getId())
			.title(updateResourcePreset.getTitle())
			.description(updateResourcePreset.getDescription())
			.launcherCpuUsage(TypeConversionUtils.toBigDecimal(updateResourcePreset.getLauncherCpuUsage()))
			.launcherMemUsage(TypeConversionUtils.toBigDecimal(updateResourcePreset.getLauncherMemUsage()))
			.cpuUsage(TypeConversionUtils.toBigDecimal(updateResourcePreset.getCpuUsage()))
			.memUsage(TypeConversionUtils.toBigDecimal(updateResourcePreset.getMemUsage()))
			.gpuUsage(updateResourcePreset.getGpuUsage())
			.nodeType(updateResourcePreset.getNodeType())
			.build();

		resourcePresetRepository.save(resourcePresetEntity);
	}

	@Override
	public void deleteResourcePreset(Long resourcePresetId) {
		if (!resourcePresetRepository.existsById(resourcePresetId)) {
			throw new RestApiException(ResourcePresetErrorCode.NOT_FOUND_RESOURCE_PRESET);
		}

		resourcePresetRepository.deleteById(resourcePresetId);
	}
}
