package com.xiilab.modulek8sdb.plugin.service;

import java.util.List;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;

public interface PluginService {

	List<PluginDTO.ResponseDTO> getPluginList();

	void pluginDeleteYN(StorageType type, boolean result, PluginDTO pluginDTO);
}
