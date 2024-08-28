package com.xiilab.modulek8sdb.plugin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.common.enums.DeleteYN;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;
import com.xiilab.modulek8sdb.plugin.entity.PluginEntity;
import com.xiilab.modulek8sdb.plugin.repository.PluginRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PluginServiceImpl implements PluginService {

	private final PluginRepository pluginRepository;

	@Override
	public List<PluginDTO.ResponseDTO> getPluginList() {
		List<PluginEntity> pluginEntities = pluginRepository.findAll();

		return pluginEntities.stream()
			.map(plugin -> PluginDTO.ResponseDTO.toDTOBuilder().plugin(plugin).build())
			.toList();
	}

	@Override
	public void pluginDeleteYN(StorageType type, boolean result) {
		PluginEntity pluginEntity = getPluginEntity(type);
		pluginEntity.setInstallYN(result ? DeleteYN.Y : DeleteYN.N);
	}

	private PluginEntity getPluginEntity(StorageType type) {
		return pluginRepository.getPluginEntityByStorageType(type);
	}
}
