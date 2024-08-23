package com.xiilab.modulek8sdb.plugin.service;

import java.util.List;

import com.xiilab.modulek8sdb.plugin.dto.PluginDTO;

public interface PluginService {
	void installPlugin(long id, boolean result);

	List<PluginDTO.ResponseDTO> getPluginList();
}
