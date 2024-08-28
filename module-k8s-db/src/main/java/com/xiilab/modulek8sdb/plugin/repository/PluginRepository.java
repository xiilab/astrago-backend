package com.xiilab.modulek8sdb.plugin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xiilab.modulecommon.enums.StorageType;
import com.xiilab.modulek8sdb.plugin.entity.PluginEntity;

@Repository
public interface PluginRepository extends JpaRepository<PluginEntity, Long> {

	PluginEntity getPluginEntityByStorageType(StorageType type);
}
