package com.xiilab.servercore.preset.service;

import com.xiilab.servercore.preset.dto.request.ResourcePresetReqDTO;
import com.xiilab.servercore.preset.dto.response.ResourcePresetResDTO;

public interface ResourcePresetService {
	Long saveResourcePreset(ResourcePresetReqDTO.SaveResourcePreset saveResourcePreset);
	void updateResourcePreset(ResourcePresetReqDTO.UpdateResourcePreset updateResourcePreset);
	// 상세조회
	ResourcePresetResDTO.FindResourcePresetDetail findResourcePresetById(Long resourcePresetId);
	// 목록조회
	ResourcePresetResDTO.FindResourcePresets findResourcePresets(ResourcePresetReqDTO.FindSearchCondition findSearchCondition);
	void deleteResourcePreset(Long resourcePresetId);
}
